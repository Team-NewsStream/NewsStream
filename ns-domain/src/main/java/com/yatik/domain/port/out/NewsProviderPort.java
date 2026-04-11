package com.yatik.domain.port.out;

import com.yatik.domain.model.IncomingArticle;

import java.util.List;

/**
 * Port interface for retrieving news articles from external or internal news providers.
 * <p>
 * Implementations of this interface act as adapters to specific news data sources,
 * enabling pagination via a cursor-based approach using item UUIDs.
 * </p>
 */
public interface NewsProviderPort {

    /**
     * Port interface for retrieving news articles from external or internal news providers.
     * <p>
     * Implementations of this interface act as adapters to specific news data sources,
     * enabling pagination via a cursor-based approach using item UUIDs.
     * </p>
     */
    List<IncomingArticle> fetchLatestNews(String lastItemUuid, int limit);

    /**
     * Fetches trending news articles, ordered by popularity.
     *
     * @param lastItemUuid the UUID of the last article from the previous page,
     *                     used as a cursor for pagination; pass {@code null} to
     *                     fetch from the beginning
     * @param limit        the maximum number of articles to return
     * @return a list of trending {@link IncomingArticle} objects, never {@code null}
     */
    List<IncomingArticle> fetchTrendingNews(String lastItemUuid, int limit);
}