package com.netflix.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageProxyService {

    private final RestTemplate restTemplate;

    @Value("${app.tmdb.image-base-url}")
    private String imageBaseUrl;

    public ResponseEntity<byte[]> fetchImage(String path) {
        String url = imageBaseUrl + path;
        try {
            ResponseEntity<byte[]> tmdbResponse = restTemplate.getForEntity(url, byte[].class);

            MediaType contentType = tmdbResponse.getHeaders().getContentType();
            if (contentType == null) {
                contentType = MediaType.IMAGE_JPEG;
            }

            return ResponseEntity.ok()
                    .contentType(contentType)
                    .body(tmdbResponse.getBody());

        } catch (Exception e) {
            log.warn("Image proxy failed for path: {} — {}", path, e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }
}
