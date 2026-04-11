package com.yatik.infra.adapter.external.ml;

import com.yatik.domain.entity.Article;
import com.yatik.domain.entity.Category;
import com.yatik.domain.port.out.EnrichmentPort;
import com.yatik.infra.adapter.external.ml.dto.MlEnrichmentRequest;
import com.yatik.infra.adapter.external.ml.dto.MlEnrichmentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * Outbound adapter that delegates article enrichment to an external ML microservice.
 *
 * <p>Implements {@link EnrichmentPort} by sending a batch of article texts to the ML
 * FastAPI service's {@code /predict} endpoint and mapping the predicted categories and
 * sentiments back onto the supplied {@link Article} entities. Response order is
 * guaranteed to match the request order by the ML service contract.
 *
 * <p>If the ML service is unavailable or returns a mismatched response, the adapter
 * falls back to safe defaults ({@code "General"} category, {@code "NEUTRAL"} sentiment)
 * so that downstream DB constraints are not violated.
 */
@Component
@Slf4j
public class MlAdapter implements EnrichmentPort {

    private final RestClient restClient;

    public MlAdapter(@Value("${ml-service.base-url}") String mlServiceBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(mlServiceBaseUrl)
                .build();
    }

    /**
     * Enriches a batch of articles with ML-predicted categories and sentiments.
     *
     * <p>Each article's text is derived from its title and description, then sent as a
     * single batch request to {@code POST /predict}. On success, the returned category
     * and sentiment values are applied to the corresponding articles in index order.
     *
     * <p>If {@code articles} is {@code null} or empty the list is returned unchanged.
     * On a response size mismatch or a {@link RestClientException}, defaults are applied
     * via {@link #applyDefaults(List)}.
     *
     * @param articles the articles to enrich; may be {@code null} or empty
     * @return the same list with {@code category} and {@code sentiment} populated on
     *         each article, or with defaults applied on failure
     */
    @Override
    public List<Article> enrichArticles(List<Article> articles) {
        if (articles == null || articles.isEmpty()) return articles;

        // Prepare the batch text (Title + Description provides good ML context)
        List<String> textsToAnalyze = articles.stream()
                .map(a -> a.getTitle() + ". " + (a.getDescription() != null ? a.getDescription() : ""))
                .toList();

        MlEnrichmentRequest requestPayload = new MlEnrichmentRequest(textsToAnalyze);

        try {
            List<MlEnrichmentResponse> responses = restClient.post()
                    .uri("/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestPayload)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            // Zip the results back to the original entities
            // ML FastAPI service uses zip(texts, sentiments, categories),
            // so the order is guaranteed to match our input list.
            if (responses != null && responses.size() == articles.size()) {
                for (int i = 0; i < articles.size(); i++) {
                    Article article = articles.get(i);
                    MlEnrichmentResponse response = responses.get(i);

                    Category category = Category.builder().name(response.category()).build();
                    article.setCategory(category);
                    article.setSentiment(response.sentiment());
                }
            } else {
                log.warn("ML response size mismatch. Expected {}, got {}", articles.size(), responses == null ? 0 : responses.size());
                applyDefaults(articles);
            }

        } catch (RestClientException e) {
            log.error("Failed to execute batch ML enrichment: {}", e.getMessage());
            applyDefaults(articles);
        }

        return articles;
    }

    /**
     * Applies safe default values to articles whose enrichment fields are unset.
     *
     * <p>Sets the category name to {@code "General"} and the sentiment to
     * {@code "NEUTRAL"} for any article missing these values. Called when the ML
     * service is unreachable or returns an unexpected response.
     *
     * @param articles the articles to apply defaults to
     */
    private void applyDefaults(List<Article> articles) {
        // Fallback to ensure DB constraints don't fail if ML service is down
        articles.forEach(article -> {
            if (article.getCategory().getName() == null) article.getCategory().setName("General");
            if (article.getSentiment() == null) article.setSentiment("NEUTRAL");
        });
    }
}