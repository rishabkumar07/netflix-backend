package com.netflix.backend.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {

    private Integer id;
    private String title;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private LocalDate releaseDate;
    private BigDecimal voteAverage;
    private Integer voteCount;
    private BigDecimal popularity;
    private List<Integer> genreIds;
    private String trailerKey;
}
