package com.yatik.domain.repository;

import com.yatik.domain.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findByName(String name);

    List<Category> findAll();
}