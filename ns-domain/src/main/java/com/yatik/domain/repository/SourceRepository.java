package com.yatik.domain.repository;

import com.yatik.domain.entity.Source;
import java.util.Optional;

public interface SourceRepository {
    Source save(Source source);
    Optional<Source> findByName(String name);
}