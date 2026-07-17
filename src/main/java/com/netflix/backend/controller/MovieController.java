package com.netflix.backend.controller;

import com.netflix.backend.dto.response.MovieDTO;
import com.netflix.backend.dto.response.PageResponse;
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
    public ResponseEntity<PageResponse<MovieDTO>> getNowPlaying(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(pagedResponse("NOW_PLAYING", page, size));
    }

    @GetMapping("/popular")
    public ResponseEntity<PageResponse<MovieDTO>> getPopular(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(pagedResponse("POPULAR", page, size));
    }

    @GetMapping("/top-rated")
    public ResponseEntity<PageResponse<MovieDTO>> getTopRated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(pagedResponse("TOP_RATED", page, size));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<PageResponse<MovieDTO>> getUpcoming(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(pagedResponse("UPCOMING", page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(movieService.getById(id));
    }

    @GetMapping("/image")
    public ResponseEntity<byte[]> getImage(@RequestParam String path) {
        return imageProxyService.fetchImage(path);
    }

    /**
     * Fetches the full cached category list through movieService (a different bean,
     * so this correctly goes through its proxy — @Cacheable still applies), then slices it.
     * Deliberately not in MovieService itself — see the self-invocation note from that file.
     */
    private PageResponse<MovieDTO> pagedResponse(String category, int page, int size) {
        List<MovieDTO> fullList = movieService.getByCategory(category);
        return PageResponse.of(fullList, page, size);
    }
}
