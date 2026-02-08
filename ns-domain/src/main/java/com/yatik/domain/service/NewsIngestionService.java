package com.yatik.domain.service;

import com.yatik.domain.entity.Article;
import com.yatik.domain.entity.Category;
import com.yatik.domain.entity.Source;
import com.yatik.domain.port.EnrichmentPort;
import com.yatik.domain.port.NewsProviderPort;
import com.yatik.domain.repository.ArticleRepository;
import com.yatik.domain.repository.CategoryRepository;
import com.yatik.domain.repository.SourceRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class NewsIngestionService {

    private final ArticleRepository articleRepo;
    private final CategoryRepository categoryRepo;
    private final SourceRepository sourceRepo;
    private final NewsProviderPort newsProvider;
    private final EnrichmentPort enrichmentPort;

    /**
     * The Main Orchestrator Method.
     * Triggered by a Scheduler (cron job) later.
     */
    public void fetchAndProcessNews(String targetCategoryName) {
        List<Article> rawArticles = newsProvider.fetchArticles(targetCategoryName);

        for (Article rawArticle : rawArticles) {
            processSingleArticle(rawArticle, targetCategoryName);
        }
    }

    private void processSingleArticle(Article article, String categoryName) {
        if (articleRepo.existsByUrl(article.getUrl())) {
            return; // Skip duplicate
        }

        String sourceName = article.getSource().getName();
        Source sourceEntity = sourceRepo.findByName(sourceName)
                .orElseGet(() -> sourceRepo.save(
                        Source.builder().name(sourceName).build()
                ));
        article.setSource(sourceEntity);

        Category categoryEntity = categoryRepo.findByName(categoryName)
                .orElseGet(() -> categoryRepo.save(
                        Category.builder().name(categoryName).build()
                ));

        article.setCategory(categoryEntity);
        article = enrichmentPort.enrichArticle(article);
        articleRepo.save(article);
    }
}