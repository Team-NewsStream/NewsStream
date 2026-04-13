package com.yatik.web.controller.v1;

import com.yatik.domain.entity.Article;
import com.yatik.domain.repository.ArticleRepository;
import com.yatik.domain.repository.CategoryRepository;
import com.yatik.web.dto.CategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "News", description = "Endpoints for fetching news articles and categories")
public class NewsController {

    private final ArticleRepository articleRepo;
    private final CategoryRepository categoryRepo;

    @GetMapping("/category-news/all")
    @Operation(summary = "Fetch all news", description = "Retrieves a paginated list of all news articles.")
    public ResponseEntity<List<Article>> fetchUnfilteredNews(
            @RequestParam(required = false) Long lastItemId,
            @RequestParam(defaultValue = "25") int pageSize
    ) {
        int limit = Math.min(pageSize, 50);
        return ResponseEntity.ok(articleRepo.findAllNews(lastItemId, limit));
    }

    @GetMapping("/category-news/{categoryName}")
    @Operation(summary = "Fetch news by category", description = "Retrieves a paginated list of news articles for a specific category.")
    public ResponseEntity<List<Article>> fetchCategoryArticles(
            @PathVariable String categoryName,
            @RequestParam(required = false) Long lastItemId,
            @RequestParam(defaultValue = "25") int pageSize
    ) {
        int limit = Math.min(pageSize, 50);
        return ResponseEntity.ok(articleRepo.findByCategoryName(categoryName, lastItemId, limit));
    }

    @GetMapping("/trending-topics")
    @Operation(summary = "Fetch trending topics", description = "Retrieves a list of trending news articles.")
    public ResponseEntity<List<Article>> fetchTrendingTopics(
            @RequestParam(required = false) Long lastItemId,
            @RequestParam(defaultValue = "25") int pageSize,
            @RequestParam(defaultValue = "false") boolean omitNegativeSentiment
    ) {
        int limit = Math.min(pageSize, 50);
        return ResponseEntity.ok(articleRepo.findTrending(lastItemId, limit, omitNegativeSentiment));
    }

    @GetMapping("/categories")
    @Operation(summary = "Fetch categories", description = "Retrieves a list of all available news categories.")
    public ResponseEntity<List<CategoryResponse>> fetchCategories() {
        List<CategoryResponse> response = categoryRepo.findAll().stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}
