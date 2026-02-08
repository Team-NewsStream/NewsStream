package com.yatik.infra.adapter.external;

import com.yatik.domain.entity.Article;
import com.yatik.domain.entity.Category;
import com.yatik.domain.port.EnrichmentPort;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class MockEnrichmentAdapter implements EnrichmentPort {

    private final Random random = new Random();

    @Override
    public Article enrichArticle(Article article) {
        // Simulate Network Delay + ML model inference delay
        try { Thread.sleep(5000); } catch (InterruptedException ignored) {}

        // 1. Mock Category (if missing)
        if (article.getCategory() == null) {
            // In reality, this string would be mapped to a Category Entity ID in the service layer
            // For now; we just set the string data
            Category category = Category.builder().name("General").build();
            article.setCategory(category); // Temporary field holder
        }

        // 2. Mock Sentiment
        boolean isPositive = random.nextBoolean();
        article.setSentiment(isPositive ? "POSITIVE" : "NEGATIVE");

        return article;
    }
}