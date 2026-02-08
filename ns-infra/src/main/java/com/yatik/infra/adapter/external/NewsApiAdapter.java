package com.yatik.infra.adapter.external;

import com.yatik.infra.adapter.external.dto.NewsApiArticle;
import com.yatik.infra.adapter.external.dto.NewsApiDto;
import com.yatik.domain.entity.Article;
import com.yatik.domain.entity.Source;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import com.yatik.domain.port.NewsProviderPort;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter implementation for integrating with the NewsAPI to fetch news articles.
 * This class acts as a bridge between the external NewsAPI and the internal application domain,
 * converting the API responses into domain-specific objects.
 *
 * The adapter communicates with NewsAPI using a {@link RestClient} and retrieves articles based on
 * a provided category. The articles returned are mapped into a list of {@link Article} domain objects.
 *
 * Dependencies such as the API key and base URL are injected via configuration properties.
 */
@Component
public class NewsApiAdapter implements NewsProviderPort {

    private final RestClient restClient;
    private final String apiKey;

    public NewsApiAdapter(@Value("${newsapi.apiKey}") String apiKey, @Value("${newsapi.baseUrl}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.apiKey = apiKey;
    }

    /**
     * Fetches a list of articles from an external news API based on the specified category.
     * The method retrieves top headlines from the API, maps them into domain-specific {@link Article} objects,
     * and returns them as a list. In case the API response is null or contains no articles, an empty list
     * is returned.
     *
     * @param category The category of articles to fetch (e.g., "technology", "sports", "general").
     * @return A list of {@link Article} objects containing the fetched articles mapped from the API response.
     */
    @Override
    public List<Article> fetchArticles(String category) {
        NewsApiDto response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/top-headlines")
                        .queryParam("category", category)
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .body(NewsApiDto.class);

        if (response == null || response.getArticles() == null) {
            return List.of();
        }
        return Arrays.stream(response.getArticles()).map(this::mapToArticle).collect(Collectors.toList());
    }

    /**
     * Maps a NewsApiArticle DTO to an Article domain object.
     *
     * @param dto the NewsApiArticle object containing data fetched from the external API
     * @return the mapped Article domain object with transformed fields
     */
    private Article mapToArticle(NewsApiArticle dto) {
        Source domainSource = dto.getSource() != null ? Source.builder().name(dto.getSource().getName()).build() : null;
        return Article.builder()
                .title(dto.getTitle())
                .url(dto.getUrl())
                .content(dto.getContent())
                .source(domainSource)
                .publishedAt(LocalDateTime.parse(dto.getPublishedAt(), DateTimeFormatter.ISO_DATE_TIME))
                .build();
    }
}
