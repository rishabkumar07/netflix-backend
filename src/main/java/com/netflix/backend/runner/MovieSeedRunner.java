package com.netflix.backend.runner;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.backend.entity.Movie;
import com.netflix.backend.repository.MovieRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class MovieSeedRunner implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        if (movieRepository.count() > 0) {
            log.info("Movies already seeded ({} rows), skipping.", movieRepository.count());
            return;
        }

        log.info("Starting movie seed from seed-movies.json...");

        InputStream inputStream = getClass().getResourceAsStream("/seed-movies.json");
        if (inputStream == null) {
            log.warn("seed-movies.json not found in classpath. Skipping seed.");
            return;
        }

        Map<String, List<MovieSeedEntry>> seedData = objectMapper.readValue(
                inputStream,
                new TypeReference<Map<String, List<MovieSeedEntry>>>() {}
        );

        Map<Integer, Movie> movieMap = new HashMap<>();

        for (Map.Entry<String, List<MovieSeedEntry>> entry : seedData.entrySet()) {
            String category = entry.getKey();
            for (MovieSeedEntry seedEntry : entry.getValue()) {
                Movie movie = movieMap.computeIfAbsent(seedEntry.getId(), id -> toEntity(seedEntry));
                movie.getCategories().add(category);
            }
        }

        movieRepository.saveAll(movieMap.values());
        log.info("Seeded {} unique movies successfully.", movieMap.size());
    }

    private Movie toEntity(MovieSeedEntry entry) {
        return Movie.builder()
                .id(entry.getId())
                .title(entry.getTitle())
                .overview(entry.getOverview())
                .posterPath(entry.getPosterPath())
                .backdropPath(entry.getBackdropPath())
                .releaseDate(parseDate(entry.getReleaseDate()))
                .voteAverage(entry.getVoteAverage())
                .voteCount(entry.getVoteCount())
                .popularity(entry.getPopularity())
                .genreIds(entry.getGenreIds())
                .trailerKey(entry.getTrailerKey())
                .categories(new HashSet<>())
                .lastSynced(LocalDateTime.now())
                .build();
    }

    private LocalDate parseDate(String date) {
        if (date == null || date.isBlank()) return null;
        try {
            return LocalDate.parse(date);
        } catch (Exception e) {
            return null;
        }
    }

    @Data
    @NoArgsConstructor
    private static class MovieSeedEntry {
        private Integer id;
        private String title;
        private String overview;
        @JsonProperty("poster_path")
        private String posterPath;
        @JsonProperty("backdrop_path")
        private String backdropPath;
        @JsonProperty("release_date")
        private String releaseDate;
        @JsonProperty("vote_average")
        private BigDecimal voteAverage;
        @JsonProperty("vote_count")
        private Integer voteCount;
        private BigDecimal popularity;
        @JsonProperty("genre_ids")
        private List<Integer> genreIds;
        @JsonProperty("trailer_key")
        private String trailerKey;
    }
}
