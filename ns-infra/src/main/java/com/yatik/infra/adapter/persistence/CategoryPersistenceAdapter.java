package com.yatik.infra.adapter.persistence;

import com.yatik.domain.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.yatik.domain.repository.CategoryRepository;
import com.yatik.infra.repository.SpringDataCategoryRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Adapter implementation of the {@link CategoryRepository} interface responsible for persistence operations
 * related to the {@link Category} entity. This class acts as a bridge between the domain layer of
 * the application and the data access layer by delegating database operations to the
 * {@link SpringDataCategoryRepository}.
 *
 * The adapter leverages Spring Data JPA to handle CRUD operations and queries. This ensures a clean separation
 * of concerns by allowing the application to interact with the domain repository interface, while the actual
 * implementation details are encapsulated within this class.
 */
@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepository {

    private final SpringDataCategoryRepository jpaRepository;

    @Override
    public List<Category> findByNames(Collection<String> names) {
        return jpaRepository.findByNames(names);
    }

    @Override
    public List<Category> saveAll(List<Category> categories) {
        return jpaRepository.saveAll(categories);
    }

    @Override
    public Category save(Category category) {
        return jpaRepository.save(category);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return jpaRepository.findByName(name);
    }

    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll();
    }
}
