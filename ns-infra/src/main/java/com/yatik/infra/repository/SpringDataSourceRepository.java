package com.yatik.infra.repository;

import com.yatik.domain.entity.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataSourceRepository extends JpaRepository<Source, Long> {
    Optional<Source> findByName(String name);

    @Query("SELECT s FROM Source s WHERE s.name IN :names")
    List<Source> findByNames(Collection<String> names);
}
