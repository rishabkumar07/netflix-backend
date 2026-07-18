package com.netflix.backend.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.backend.dto.response.MovieSearchResultDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Live TMDB search proxy — same shape as ImageProxyService: no DB, no @Cacheable.
 * Search queries are unbounded free text, so caching here would just grow forever
 * with a low hit rate. Unlike MovieSyncScheduler, a failed lookup here just returns
 * an empty list instead of aborting anything — this is a single user's search, not a sync job.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TmdbSearchService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.tmdb.api-key}")
    private String tmdbApiKey;

    @Value("${app.tmdb.base-url}")
    private String tmdbBaseUrl;

    public List<MovieSearchResultDTO> search(String title) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(tmdbBaseUrl + "/search/movie")
                    .queryParam("query", title)
                    .queryParam("include_adult", false)
                    .queryParam("language", "en-US")
                    .queryParam("page", 1)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(tmdbApiKey);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            TmdbSearchResponse parsed = objectMapper.readValue(response.getBody(), TmdbSearchResponse.class);

            if (parsed.getResults() == null) {
                return Collections.emptyList();
            }

            return parsed.getResults().stream()
                    .map(r -> MovieSearchResultDTO.builder()
                            .id(r.getId())
                            .title(r.getTitle())
                            .posterPath(r.getPosterPath())
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.warn("TMDB search failed for title '{}': {}", title, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Data
    @NoArgsConstructor
    private static class TmdbSearchResponse {
        private List<TmdbSearchEntry> results;
    }

    @Data
    @NoArgsConstructor
    private static class TmdbSearchEntry {
        private Integer id;
        private String title;
        @JsonProperty("poster_path")
        private String posterPath;
    }
}
