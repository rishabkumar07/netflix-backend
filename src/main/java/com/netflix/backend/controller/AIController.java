package com.netflix.backend.controller;

import com.netflix.backend.dto.request.AISuggestRequest;
import com.netflix.backend.dto.response.AISuggestResponse;
import com.netflix.backend.service.AIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AIController {

    private final AIService aiService;

    @PostMapping("/suggest")
    public ResponseEntity<AISuggestResponse> suggest(@Valid @RequestBody AISuggestRequest request,
                                                       @AuthenticationPrincipal UserDetails principal) {
        AISuggestResponse response = aiService.suggest(principal.getUsername(), request);
        return ResponseEntity.ok(response);
    }
}
