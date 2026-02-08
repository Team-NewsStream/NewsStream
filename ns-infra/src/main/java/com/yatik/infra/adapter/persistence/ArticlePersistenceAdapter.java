package com.yatik.infra.adapter.persistence;

import com.yatik.domain.entity.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import com.yatik.domain.repository.ArticleRepository;
import com.yatik.infra.repository.SpringDataArticleRepository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementation of the {@link ArticleRepository} interface responsible for persistence operations
 * related to the {@link Article} entity. This class acts as a bridge between the application's domain layer
 * and the data access layer by delegating database operations to the {@link SpringDataArticleRepository}.
 *
 * The adapter uses Dependency Injection to integrate with the Spring Data JPA repository and provides
 * concrete implementations for various querying and persistence methods defined in the domain repository.
 */
@Component
@RequiredArgsConstructor
public class ArticlePersistenceAdapter implements ArticleRepository {

    private final SpringDataArticleRepository jpaRepository;

    @Override
    public Article save(Article article) {
        return jpaRepository.save(article);
    }

    @Override
    public Optional<Article> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Article> findByUuid(String uuid) {
        return jpaRepository.findByUuid(uuid);
    }

    @Override
    public Optional<Article> findByUrl(String url) {
        return jpaRepository.findByUrl(url);
    }

    @Override
    public boolean existsByUrl(String url) {
        return jpaRepository.existsByUrl(url);
    }

    @Override
    public List<Article> findTrending(Long lastSeenId, int limit, boolean omitNegative) {

        // PageRequest of 0 means "Get the first chunk of results"
        // The WHERE clause handles the "Cursor" logic for us.
        // Since this implementation uses a "last seen ID" (cursor) to handle pagination,
        // it always requests the "first" page of results that match the criteria defined in the query.
        PageRequest pageable = PageRequest.of(0, limit);

        if (omitNegative) return jpaRepository.findTrendingPositive(lastSeenId, pageable);
        return jpaRepository.findTrendingAll(lastSeenId, pageable);
    }

    @Override
    public List<Article> findAllNews(Long lastSeenId, int limit) {
        PageRequest pageable = PageRequest.of(0, limit);
        return jpaRepository.findLatest(lastSeenId, pageable);
    }

    @Override
    public List<Article> findByCategory(Long categoryId, Long lastSeenId, int limit) {
        PageRequest pageable = PageRequest.of(0, limit);
        return jpaRepository.findByCategory(categoryId, lastSeenId, pageable);
    }

    @Override
    public List<Article> findByCategoryName(String categoryName, Long lastSeenId, int limit) {
        return jpaRepository.findByCategoryName(categoryName, lastSeenId, limit);
    }
}
