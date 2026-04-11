package com.yatik.domain.port.out;

import com.yatik.domain.entity.Article;

public interface EnrichmentPort {
    /**
     * Sends an article to the ML Service (or Mock) to get Sentiment & Category.
     * @param article The article with title/description/content.
     * @return The same article, but with 'sentiment', 'sentimentScore', and 'category' filled.
     */
    Article enrichArticle(Article article);
}
