package com.yatik.domain.service;

import com.yatik.domain.entity.Article;
import com.yatik.domain.entity.Category;
import com.yatik.domain.entity.Source;
import com.yatik.domain.entity.Trending;
import com.yatik.domain.model.IncomingArticle;
import com.yatik.domain.port.out.EnrichmentPort;
import com.yatik.domain.port.out.NewsProviderPort;
import com.yatik.domain.repository.ArticleRepository;
import com.yatik.domain.repository.CategoryRepository;
import com.yatik.domain.repository.SourceRepository;
import com.yatik.domain.repository.TrendingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Domain service responsible for fetching, deduplicating, enriching, and persisting
 * news articles from external providers.
 *
 * <p>Orchestrates the full ingestion pipeline:
 * <ol>
 *   <li>Fetches the latest global and trending articles via {@link NewsProviderPort}.</li>
 *   <li>Deduplicates against already-persisted URLs.</li>
 *   <li>Resolves or creates {@link Source} and {@link Category} records in batch.</li>
 *   <li>Delegates ML-based category and sentiment enrichment to {@link EnrichmentPort}.</li>
 *   <li>Persists new articles and, for trending feeds, creates {@link Trending} links.</li>
 * </ol>
 */
@RequiredArgsConstructor
@Slf4j
public class NewsIngestionService {

    private final ArticleRepository articleRepo;
    private final CategoryRepository categoryRepo;
    private final SourceRepository sourceRepo;
    private final TrendingRepository trendingRepo;
    private final NewsProviderPort newsProvider;
    private final EnrichmentPort enrichmentPort;

    /**
     * Synchronizes the database with the latest global and trending news.
     *
     * <p>Fetches up to 100 global articles (resuming from the most recently stored UUID)
     * and up to 50 trending articles, then processes each batch independently.
     */
    public void syncAllNews() {
        // Sync Global News
        String latestUuid = articleRepo.findLatestArticleUuid().orElse(null);
        List<IncomingArticle> globalNews = newsProvider.fetchLatestNews(latestUuid, 100);
        processBatch(globalNews, false);

        // Sync Trending News
        List<IncomingArticle> trendingNews = newsProvider.fetchTrendingNews(null, 50);
        processBatch(trendingNews, true);
    }

    /**
     * Processes a batch of incoming articles: deduplicates, enriches, and persists them.
     *
     * <p>Articles whose URLs already exist in the database are silently skipped.
     * New articles are built with a default {@code "General"} category (overridden by
     * ML enrichment), then saved. If {@code isTrendingFeed} is {@code true}, a
     * {@link Trending} record is also created for each saved article.
     *
     * <p>Returns immediately if {@code incomingBatch} is {@code null}, empty, or contains
     * only already-known URLs.
     *
     * @param incomingBatch  the raw articles returned by the news provider; may be
     *                       {@code null} or empty
     * @param isTrendingFeed {@code true} if this batch originates from the trending feed,
     *                       causing {@link Trending} records to be created alongside articles
     */
    private void processBatch(List<IncomingArticle> incomingBatch, boolean isTrendingFeed) {
        if (incomingBatch == null || incomingBatch.isEmpty()) return;

        // Batch deduplication
        Set<String> incomingUrls = incomingBatch.stream()
                .map(IncomingArticle::url)
                .collect(Collectors.toSet());

        Set<String> existingUrls = articleRepo.findExistingUrls(incomingUrls);

        List<IncomingArticle> newArticles = incomingBatch.stream()
                .filter(a -> !existingUrls.contains(a.url()))
                .toList();

        if (newArticles.isEmpty()) return;

        // Batch Resolve Sources
        Map<String, Source> sourceMap = resolveSources(newArticles);

        // Batch Resolve Categories (Post-Enrichment)
        // Note: In reality, enrichment happens in batch too, but mock
        // enrichmentPort is still a single-item for now.
        // We will assign a default category for the sake of the DB relations.
        Map<String, Category> categoryMap = resolveCategories();

        // Build Entities
        List<Article> entitiesToSave = new ArrayList<>();

        for (IncomingArticle incoming : newArticles) {
            Article article = Article.builder()
                    .uuid(incoming.uuid())
                    .title(incoming.title())
                    .url(incoming.url())
                    .urlToImage(incoming.urlToImage())
                    .description(incoming.description())
                    .publishedAt(incoming.publishedAt())
                    .source(sourceMap.get(incoming.sourceName()))
                    // Assuming ML enrichment sets the category later, default to General
                    .category(categoryMap.get("General"))
                    .build();
            entitiesToSave.add(article);
        }

        entitiesToSave = enrichmentPort.enrichArticles(entitiesToSave);

        List<Article> savedArticles = articleRepo.saveAll(entitiesToSave);
        log.info("Bulk inserted {} new articles.", savedArticles.size());

        if (isTrendingFeed) {
            List<Trending> trendingItems = savedArticles.stream()
                    .map(article -> Trending.builder().article(article).build())
                    .toList();
            trendingRepo.saveAll(trendingItems);
            log.info("Bulk inserted {} trending links.", trendingItems.size());
        }
    }

    /**
     * Resolves {@link Source} entities for a batch of incoming articles, creating any
     * that do not yet exist in the database.
     *
     * <p>Performs a single read to fetch all known sources by name, then a single writing
     * for any missing ones, minimizing round-trips.
     *
     * @param articles the incoming articles whose source names need to be resolved
     * @return a map from source name to the corresponding (possibly newly created)
     *         {@link Source} entity
     */
    private Map<String, Source> resolveSources(List<IncomingArticle> articles) {
        Set<String> sourceNames = articles.stream()
                .map(IncomingArticle::sourceName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Fetch existing
        List<Source> existingSources = sourceRepo.findByNames(sourceNames);
        Map<String, Source> sourceMap = existingSources.stream()
                .collect(Collectors.toMap(Source::getName, Function.identity()));

        // Identify and save missing
        List<Source> newSources = sourceNames.stream()
                .filter(name -> !sourceMap.containsKey(name))
                .map(name -> Source.builder().name(name).build())
                .toList();

        if (!newSources.isEmpty()) {
            List<Source> savedSources = sourceRepo.saveAll(newSources);
            savedSources.forEach(s -> sourceMap.put(s.getName(), s));
        }

        return sourceMap;
    }

    /**
     * Ensures the {@code "General"} {@link Category} exists in the database and returns
     * it in a name-keyed map.
     *
     * <p>The {@code "General"} category serves as the default assigned to articles before
     * ML enrichment overrides it. Using this method guarantees the category record is
     * present, preventing FK constraint violations during the initial save.
     *
     * @return a map containing at least the {@code "General"} category entry
     */
    private Map<String, Category> resolveCategories() {
        // Since ML determines category, and we default to "General" to avoid nulls,
        // we ensure "General" exists in this batch.
        Set<String> categoryNames = Set.of("General");

        List<Category> existingCategories = categoryRepo.findByNames(categoryNames);
        Map<String, Category> categoryMap = existingCategories.stream()
                .collect(Collectors.toMap(Category::getName, Function.identity()));

        if (!categoryMap.containsKey("General")) {
            Category newCat = categoryRepo.saveAll(List.of(Category.builder().name("General").build())).get(0);
            categoryMap.put(newCat.getName(), newCat);
        }

        return categoryMap;
    }
}