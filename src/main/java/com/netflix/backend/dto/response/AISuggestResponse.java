package com.netflix.backend.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AISuggestResponse {

    private List<String> movies;
}
