package com.yatik.domain.repository;

import com.yatik.domain.entity.Article;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ArticleRepository {
    Article save(Article article);
    Optional<Article> findById(Long id);
    Optional<Article> findByUuid(String uuid);
    Optional<Article> findByUrl(String url);
    boolean existsByUrl(String url);

    /** Finds which of the provided URLs already exist in the DB */
    Set<String> findExistingUrls(Collection<String> urls);

    /** Bulk insert */
    List<Article> saveAll(List<Article> articles);

    Optional<String> findLatestArticleUuid();

    /**
     * Fetches trending articles for the home page.
     * * @param lastSeenId The ID of the last item in the list (for pagination).
     * If null, fetch the very top (first page).
     * @param limit How many items to fetch (pageSize).
     * @param omitNegative If true, filter out NEGATIVE sentiment articles.
     */
    List<Article> findTrending(Long lastSeenId, int limit, boolean omitNegative);

    /**
     * Case A: The "All" Chip.
     * Fetches the global timeline of news (mixed categories).
     * * @param lastSeenId The cursor (ID of the last item user saw).
     * @param limit Page size.
     */
    List<Article> findAllNews(Long lastSeenId, int limit);

    /**
     * Case B: Specific Category Chip id.
     * Fetches news strictly for one category.
     * * @param categoryId The DB ID of the category (e.g., "Tech").
     * @param lastSeenId The cursor.
     * @param limit Page size.
     */
    List<Article> findByCategory(Long categoryId, Long lastSeenId, int limit);

    /**
     * Case C: Specific Category Chip name.
     * Fetches news strictly for one category.
     * * @param categoryName The name of the category (e.g., "Tech").
     * @param lastSeenId The cursor.
     * @param limit Page size.
     */
    List<Article> findByCategoryName(String categoryName, Long lastSeenId, int limit);
}
