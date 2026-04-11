package com.yatik.infra.adapter.persistence;

import com.yatik.domain.entity.Source;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.yatik.domain.repository.SourceRepository;
import com.yatik.infra.repository.SpringDataSourceRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Adapter implementation of the {@link SourceRepository} interface responsible for persistence operations
 * related to the {@link Source} entity. This class acts as a bridge between the application's domain layer
 * and the data access layer by delegating database operations to the {@link SpringDataSourceRepository}.
 *
 * The adapter utilizes Spring Data JPA to manage CRUD operations for the {@link Source} entity, ensuring a clean
 * separation of concerns between the domain and persistence layers.
 *
 * This class uses Dependency Injection to integrate with the Spring Data repository and provides concrete
 * implementations for methods defined in the {@link SourceRepository} interface.
 */
@Component
@RequiredArgsConstructor
public class SourcePersistenceAdapter implements SourceRepository {

    private final SpringDataSourceRepository jpaRepository;

    @Override
    public Source save(Source source) {
        return jpaRepository.save(source);
    }

    @Override
    public List<Source> saveAll(List<Source> sources) {
        return jpaRepository.saveAll(sources);
    }

    @Override
    public Optional<Source> findByName(String name) {
        return jpaRepository.findByName(name);
    }

    @Override
    public List<Source> findByNames(Collection<String> names) {
        return jpaRepository.findByNames(names);
    }
}
