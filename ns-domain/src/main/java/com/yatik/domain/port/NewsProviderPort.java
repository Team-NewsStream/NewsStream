package com.yatik.domain.port;

import com.yatik.domain.entity.Article;
import java.util.List;

public interface NewsProviderPort {
    /**
     * Fetches raw articles from an external source (News APIs, Scraper, etc.).
     * @param category The category to fetch (e.g., "technology", "general").
     * @return List of Domain Articles (mapped from external JSON).
     */
    List<Article> fetchArticles(String category);
}
