package com.yatik.infra.repository;


import com.yatik.domain.entity.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SpringDataArticleRepository extends JpaRepository<Article, Long> {

    Optional<Article> findByUuid(String uuid);
    Optional<Article> findByUrl(String url);
    boolean existsByUrl(String url);

    @Query("""
        SELECT t.article FROM Trending t
        WHERE (:lastId IS NULL OR t.id < :lastId)
        ORDER BY t.id DESC
    """)
    List<Article> findTrendingAll(Long lastId, Pageable pageable);

    @Query("""
        SELECT t.article FROM Trending t
        WHERE (:lastId IS NULL OR t.id < :lastId)
        AND t.article.sentiment != 'NEGATIVE'
        ORDER BY t.id DESC
    """)
    List<Article> findTrendingPositive(Long lastId, Pageable pageable);

    @Query("""
        SELECT a FROM Article a
        WHERE (:lastId IS NULL OR a.id < :lastId)
        ORDER BY a.id DESC
    """)
    List<Article> findLatest(Long lastId, Pageable pageable);


    @Query("""
        SELECT a FROM Article a
        WHERE a.category.id = :catId
        AND (:lastId IS NULL OR a.id < :lastId)
        ORDER BY a.id DESC
    """)
    List<Article> findByCategory(Long catId, Long lastId, Pageable pageable);

    @Query("""
        SELECT a FROM Article a
        WHERE a.category.name = :categoryName
        AND (:lastId IS NULL or a.id < :lastId)
        ORDER BY a.id DESC
    """)
    List<Article> findByCategoryName(String categoryName, Long lastId, int limit);

    @Query("""
        SELECT a.url FROM Article a
        WHERE a.url IN :urls
    """)
    Set<String> findExistingUrls(@Param("urls") Collection<String> urls);

    @Query("""
        SELECT a.uuid FROM Article a
        ORDER BY a.id DESC LIMIT 1
    """)
    Optional<String> findLatestArticleUuid();
}
