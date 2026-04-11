package com.yatik.domain.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record IncomingArticle(
        String uuid,
        String title,
        String url,
        String urlToImage,
        String description,
        String sourceName,
        LocalDateTime publishedAt,
        boolean isTrending
) {}