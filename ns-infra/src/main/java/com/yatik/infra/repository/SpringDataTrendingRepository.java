package com.yatik.infra.repository;

import com.yatik.domain.entity.Trending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataTrendingRepository extends JpaRepository<Trending, Long> {
}
