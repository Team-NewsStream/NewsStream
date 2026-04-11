package com.yatik.infra.adapter.external.scraper.dto;

import java.time.LocalDateTime;

public record ArticleResponseDto(
        int id,
        String uuid,
        String title,
        String url,
        String urlToImage,
        String description,
        LocalDateTime publishedAt,
        SourceResponseDto source,
        boolean isTrending
) {}