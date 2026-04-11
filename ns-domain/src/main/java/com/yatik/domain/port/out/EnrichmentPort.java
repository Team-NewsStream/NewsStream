package com.yatik.domain.port.out;

import com.yatik.domain.entity.Article;
import java.util.List;

public interface EnrichmentPort {
    /**
     * Sends a batch of articles to the ML Service to get Sentiment & Category.
     * @param articles The batch of articles to enrich.
     * @return The same list, but with ML fields populated.
     */
    List<Article> enrichArticles(List<Article> articles);
}