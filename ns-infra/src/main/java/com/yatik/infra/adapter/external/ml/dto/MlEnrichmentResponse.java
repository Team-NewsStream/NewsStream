package com.yatik.infra.adapter.external.ml.dto;

public record MlEnrichmentResponse(
        String text,
        String sentiment,
        String category
) {}
