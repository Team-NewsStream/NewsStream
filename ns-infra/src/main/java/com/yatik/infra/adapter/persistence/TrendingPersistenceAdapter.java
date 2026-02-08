package com.yatik.infra.adapter.persistence;

import com.yatik.domain.entity.Trending;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.yatik.infra.repository.SpringDataTrendingRepository;
import com.yatik.domain.repository.TrendingRepository;

import java.util.List;

/**
 * Implementation of the {@link TrendingRepository} interface responsible for managing persistence
 * operations related to the {@link Trending} entity. This adapter facilitates the interaction
 * between the application's domain layer and the persistence layer.
 *
 * This class utilizes the {@link SpringDataTrendingRepository} for all database operations and ensures
 * a clean separation of concerns by delegating domain-specific operations to the repository.
 *
 * Use cases for this adapter include saving and deleting all trending data entries in the persistence layer.
 */
@Component
@RequiredArgsConstructor
public class TrendingPersistenceAdapter implements TrendingRepository {

    private final SpringDataTrendingRepository jpaRepository;

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }

    @Override
    public void saveAll(List<Trending> trendingItems) {
        jpaRepository.saveAll(trendingItems);
    }
}
