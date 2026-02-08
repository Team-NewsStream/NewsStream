package com.yatik.infra.adapter.external.dto;

import lombok.Data;

@Data
public class NewsApiDto {
    private String status;
    private long totalResults;
    private NewsApiArticle[] articles;
}
