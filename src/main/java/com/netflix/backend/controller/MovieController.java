package com.netflix.backend.controller;

import com.netflix.backend.dto.response.MovieDTO;
import com.netflix.backend.service.ImageProxyService;
import com.netflix.backend.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Slf4j
public class MovieController {

    private final MovieService movieService;
    private final ImageProxyService imageProxyService;

    @GetMapping("/now-playing")
    public ResponseEntity<List<MovieDTO>> getNowPlaying() {
        return ResponseEntity.ok(movieService.getByCategory("NOW_PLAYING"));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<MovieDTO>> getPopular() {
        return ResponseEntity.ok(movieService.getByCategory("POPULAR"));
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<MovieDTO>> getTopRated() {
        return ResponseEntity.ok(movieService.getByCategory("TOP_RATED"));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<MovieDTO>> getUpcoming() {
        return ResponseEntity.ok(movieService.getByCategory("UPCOMING"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(movieService.getById(id));
    }

    @GetMapping("/image")
    public ResponseEntity<byte[]> getImage(@RequestParam String path) {
        return imageProxyService.fetchImage(path);
    }
}
