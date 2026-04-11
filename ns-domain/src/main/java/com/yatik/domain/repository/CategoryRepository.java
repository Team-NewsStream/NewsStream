package com.yatik.domain.repository;

import com.yatik.domain.entity.Category;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    List<Category> findByNames(Collection<String> names);

    List<Category> saveAll(List<Category> categories);

    Category save(Category category);

    Optional<Category> findByName(String name);

    List<Category> findAll();
}