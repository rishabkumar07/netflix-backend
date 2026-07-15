package com.netflix.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AISuggestRequest {

    @NotBlank(message = "query is required")
    private String query;
}
