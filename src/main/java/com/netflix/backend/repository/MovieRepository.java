package com.netflix.backend.repository;

import com.netflix.backend.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    @Query("SELECT m FROM Movie m JOIN m.categories c WHERE c = :category")
    List<Movie> findByCategory(@Param("category") String category);

    @Query("SELECT COUNT(m) > 0 FROM Movie m JOIN m.categories c WHERE c = :category")
    boolean existsByCategory(@Param("category") String category);

    @Modifying
    @Query(value = "DELETE FROM movie_categories WHERE category = :category", nativeQuery = true)
    void deleteCategoryAssociations(@Param("category") String category);
}
