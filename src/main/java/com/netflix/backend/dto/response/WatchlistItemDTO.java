package com.netflix.backend.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistItemDTO {

    private Integer movieId;
    private String title;
    private String posterPath;
    private LocalDateTime addedAt;
}
