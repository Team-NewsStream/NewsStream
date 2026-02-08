package com.yatik.infra.config;

import com.yatik.domain.port.EnrichmentPort;
import com.yatik.domain.port.NewsProviderPort;
import com.yatik.domain.repository.ArticleRepository;
import com.yatik.domain.repository.CategoryRepository;
import com.yatik.domain.repository.SourceRepository;
import com.yatik.domain.service.NewsIngestionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for domain-related beans.
 * This class defines and provides all domain-level dependencies and their configurations
 * required to foster the business processes of the application.
 */
@Configuration
public class DomainConfig {

    /**
     * Provides an instance of the NewsIngestionService bean, which orchestrates the process of fetching,
     * enriching, and persisting news articles from external sources.
     *
     * @param articleRepo Repository for managing article entities.
     * @param categoryRepo Repository for managing category entities.
     * @param sourceRepo Repository for managing source entities.
     * @param newsProvider External port for fetching raw news articles.
     * @param enrichmentPort Port for enriching articles with additional metadata.
     * @return A fully initialized NewsIngestionService instance.
     */
    @Bean
    public NewsIngestionService newsIngestionService(
            ArticleRepository articleRepo,
            CategoryRepository categoryRepo,
            SourceRepository sourceRepo,
            NewsProviderPort newsProvider,
            EnrichmentPort enrichmentPort
    ) {
        return new NewsIngestionService(
                articleRepo,
                categoryRepo,
                sourceRepo,
                newsProvider,
                enrichmentPort
        );
    }
}
