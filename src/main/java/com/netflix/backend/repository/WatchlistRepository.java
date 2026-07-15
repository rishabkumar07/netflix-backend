package com.netflix.backend.repository;

import com.netflix.backend.entity.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<WatchlistItem, Long> {

    List<WatchlistItem> findByUserId(Long userId);

    boolean existsByUserIdAndMovieId(Long userId, Integer movieId);

    long deleteByUserIdAndMovieId(Long userId, Integer movieId);
}
