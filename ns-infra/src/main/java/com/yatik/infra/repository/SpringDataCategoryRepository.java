package com.yatik.infra.repository;

import com.yatik.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataCategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
