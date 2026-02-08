package com.yatik.domain.repository;

import com.yatik.domain.entity.Trending;
import java.util.List;

public interface TrendingRepository {
    // Used to refresh the list
    void deleteAll();
    void saveAll(List<Trending> trendingItems);
}
