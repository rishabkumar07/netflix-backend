package com.netflix.backend.service;

import com.netflix.backend.dto.response.MovieDTO;
import com.netflix.backend.entity.Movie;
import com.netflix.backend.exception.ResourceNotFoundException;
import com.netflix.backend.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    @Cacheable(value = "movies-by-category", key = "#category")
    public List<MovieDTO> getByCategory(String category) {
        log.info("Fetching movies from DB for category: {}", category);
        return movieRepository.findByCategory(category)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "movies-by-id", key = "#id")
    public MovieDTO getById(Integer id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        return toDTO(movie);
    }

    private MovieDTO toDTO(Movie movie) {
        return MovieDTO.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .overview(movie.getOverview())
                .posterPath(movie.getPosterPath())
                .backdropPath(movie.getBackdropPath())
                .releaseDate(movie.getReleaseDate())
                .voteAverage(movie.getVoteAverage())
                .voteCount(movie.getVoteCount())
                .popularity(movie.getPopularity())
                .genreIds(movie.getGenreIds())
                .trailerKey(movie.getTrailerKey())
                .build();
    }
}
