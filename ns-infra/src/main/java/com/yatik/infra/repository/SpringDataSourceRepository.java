package com.yatik.infra.repository;

import com.yatik.domain.entity.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataSourceRepository extends JpaRepository<Source, Long> {
    Optional<Source> findByName(String name);
}
