package com.netflix.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "movies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column(name = "poster_path")
    private String posterPath;

    @Column(name = "backdrop_path")
    private String backdropPath;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "vote_average", precision = 3, scale = 1)
    private BigDecimal voteAverage;

    @Column(name = "vote_count")
    private Integer voteCount;

    @Column(precision = 10, scale = 3)
    private BigDecimal popularity;

    @Convert(converter = GenreIdsConverter.class)
    @Column(name = "genre_ids", columnDefinition = "json")
    private List<Integer> genreIds;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "movie_categories", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "category")
    private Set<String> categories;

    @Column(name = "trailer_key")
    private String trailerKey;

    @Column(name = "last_synced")
    private LocalDateTime lastSynced;
}
