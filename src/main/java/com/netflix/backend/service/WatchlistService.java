package com.netflix.backend.service;

import com.netflix.backend.dto.request.WatchlistRequest;
import com.netflix.backend.dto.response.MovieDTO;
import com.netflix.backend.dto.response.WatchlistItemDTO;
import com.netflix.backend.entity.User;
import com.netflix.backend.entity.WatchlistItem;
import com.netflix.backend.exception.ResourceNotFoundException;
import com.netflix.backend.repository.UserRepository;
import com.netflix.backend.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final UserRepository userRepository;
    private final MovieService movieService;

    @Transactional
    public WatchlistItemDTO add(String email, WatchlistRequest request) {
        User user = getUserByEmail(email);
        Integer movieId = request.getMovieId();

        if (watchlistRepository.existsByUserIdAndMovieId(user.getId(), movieId)) {
            throw new IllegalArgumentException("Movie already in watchlist: " + movieId);
        }

        MovieDTO movie = movieService.getById(movieId);

        WatchlistItem item = WatchlistItem.builder()
                .user(user)
                .movieId(movie.getId())
                .title(movie.getTitle())
                .posterPath(movie.getPosterPath())
                .build();

        WatchlistItem saved = watchlistRepository.save(item);
        log.info("User {} added movie {} to watchlist", email, movieId);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<WatchlistItemDTO> getByUser(String email) {
        User user = getUserByEmail(email);
        return watchlistRepository.findByUserId(user.getId())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void remove(String email, Integer movieId) {
        User user = getUserByEmail(email);
        long deleted = watchlistRepository.deleteByUserIdAndMovieId(user.getId(), movieId);

        if (deleted == 0) {
            throw new ResourceNotFoundException("Movie not in watchlist: " + movieId);
        }
        log.info("User {} removed movie {} from watchlist", email, movieId);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private WatchlistItemDTO toDTO(WatchlistItem item) {
        return WatchlistItemDTO.builder()
                .movieId(item.getMovieId())
                .title(item.getTitle())
                .posterPath(item.getPosterPath())
                .addedAt(item.getAddedAt())
                .build();
    }
}
