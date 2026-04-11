package com.yatik.infra.adapter.external.scraper;

import com.yatik.domain.model.IncomingArticle;
import com.yatik.domain.port.out.NewsProviderPort;
import com.yatik.infra.adapter.external.scraper.dto.ArticleResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FastApiScraperAdapter implements NewsProviderPort {

    private final RestClient restClient;

    public FastApiScraperAdapter(@Value("${scraper.base-url}") String scraperBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(scraperBaseUrl)
                .build();
    }

    @Override
    public List<IncomingArticle> fetchLatestNews(String lastItemUuid, int limit) {
        return fetchFromEndpoint("/fetch_news", lastItemUuid, limit);
    }

    @Override
    public List<IncomingArticle> fetchTrendingNews(String lastItemUuid, int limit) {
        return fetchFromEndpoint("/fetch_trending_news", lastItemUuid, limit);
    }

    private List<IncomingArticle> fetchFromEndpoint(String path, String lastItemUuid, int limit) {
        String uri = UriComponentsBuilder.fromPath(path)
                .queryParamIfPresent("last_item_uuid", Optional.ofNullable(lastItemUuid))
                .queryParam("limit", limit)
                .toUriString();

        List<ArticleResponseDto> responses = restClient.get()
                .uri(uri)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (responses == null) return List.of();

        return responses.stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    private IncomingArticle mapToDomain(ArticleResponseDto dto) {
        return IncomingArticle.builder()
                .uuid(dto.uuid())
                .title(dto.title())
                .url(dto.url())
                .urlToImage(dto.urlToImage())
                .description(dto.description())
                .sourceName(dto.source().name())
                .publishedAt(dto.publishedAt())
                // We pass the trending flag so the domain service knows what to do with it
                .isTrending(dto.isTrending())
                .build();
    }
}