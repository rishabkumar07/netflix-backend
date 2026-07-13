package com.netflix.backend.scheduler;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.backend.entity.Movie;
import com.netflix.backend.repository.MovieRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class MovieSyncScheduler {

    private final MovieRepository movieRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${app.tmdb.api-key}")
    private String tmdbApiKey;

    @Value("${app.tmdb.base-url}")
    private String tmdbBaseUrl;

    private static final Map<String, String> CATEGORY_ENDPOINTS = Map.of(
            "NOW_PLAYING", "/movie/now_playing",
            "POPULAR",     "/movie/popular",
            "TOP_RATED",   "/movie/top_rated",
            "UPCOMING",    "/movie/upcoming"
    );

    private static final int PAGES_TO_FETCH = 3;

    @Scheduled(cron = "0 0 2 1 * *")  // 2am on the 1st of every month
    @Transactional
    @CacheEvict(value = {"movies-by-category", "movies-by-id"}, allEntries = true)
    public void syncMovies() {
        log.info("Starting monthly movie sync...");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tmdbApiKey);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        Map<Integer, Movie> movieMap = new HashMap<>();

        for (Map.Entry<String, String> categoryEntry : CATEGORY_ENDPOINTS.entrySet()) {
            String category = categoryEntry.getKey();
            String endpoint = categoryEntry.getValue();

            try {
                fetchCategory(restTemplate, request, category, endpoint, movieMap);
            } catch (Exception e) {
                log.warn("Failed to sync category {}. Skipping. Reason: {}", category, e.getMessage());
                return;  // TMDB unreachable — abort entire sync, keep existing data
            }
        }

        log.info("Sync fetched {} unique movies. Saving to DB...", movieMap.size());

        for (String category : CATEGORY_ENDPOINTS.keySet()) {
            movieRepository.deleteCategoryAssociations(category);
        }

        movieRepository.saveAll(movieMap.values());
        log.info("Monthly movie sync complete.");
    }

    private void fetchCategory(RestTemplate restTemplate, HttpEntity<Void> request,
                               String category, String endpoint,
                               Map<Integer, Movie> movieMap) throws Exception {
        for (int page = 1; page <= PAGES_TO_FETCH; page++) {
            String url = tmdbBaseUrl + endpoint + "?page=" + page;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode results = root.get("results");

            for (JsonNode node : results) {
                TmdbMovieEntry entry = objectMapper.treeToValue(node, TmdbMovieEntry.class);
                if (entry.getId() == null) continue;

                Movie movie = movieMap.computeIfAbsent(entry.getId(), id -> toEntity(entry));
                movie.getCategories().add(category);
            }
        }
    }

    private Movie toEntity(TmdbMovieEntry entry) {
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
    private static class TmdbMovieEntry {
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
    }
}
