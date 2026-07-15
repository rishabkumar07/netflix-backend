package com.netflix.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WatchlistRequest {

    @NotNull(message = "movieId is required")
    private Integer movieId;
}
