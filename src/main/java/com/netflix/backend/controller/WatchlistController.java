package com.netflix.backend.controller;

import com.netflix.backend.dto.request.WatchlistRequest;
import com.netflix.backend.dto.response.WatchlistItemDTO;
import com.netflix.backend.service.WatchlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/watchlist")
@RequiredArgsConstructor
@Slf4j
public class WatchlistController {

    private final WatchlistService watchlistService;

    @PostMapping
    public ResponseEntity<WatchlistItemDTO> add(@Valid @RequestBody WatchlistRequest request,
                                                 @AuthenticationPrincipal UserDetails principal) {
        WatchlistItemDTO dto = watchlistService.add(principal.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<WatchlistItemDTO>> getWatchlist(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(watchlistService.getByUser(principal.getUsername()));
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Void> remove(@PathVariable Integer movieId,
                                        @AuthenticationPrincipal UserDetails principal) {
        watchlistService.remove(principal.getUsername(), movieId);
        return ResponseEntity.noContent().build();
    }
}
