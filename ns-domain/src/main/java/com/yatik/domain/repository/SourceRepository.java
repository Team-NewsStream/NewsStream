package com.yatik.domain.repository;

import com.yatik.domain.entity.Source;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SourceRepository {
    Source save(Source source);
    List<Source> saveAll(List<Source> sources);
    Optional<Source> findByName(String name);
    List<Source> findByNames(Collection<String> names);
}