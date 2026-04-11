package com.yatik.infra.adapter.external.scraper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SourceResponseDto(
        String name,
        @JsonProperty("logo_url") String logoUrl
) {}