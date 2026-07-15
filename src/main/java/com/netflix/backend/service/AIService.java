package com.netflix.backend.service;

import com.netflix.backend.dto.request.AISuggestRequest;
import com.netflix.backend.dto.response.AISuggestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AIService {

    private final ChatClient chatClient;

    public AIService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /** Shape Spring AI coerces the model's JSON response into. Never exposed outside this class. */
    private record MovieSuggestions(List<String> titles) {}

    public AISuggestResponse suggest(String email, AISuggestRequest request) {
        String query = request.getQuery();
        log.info("User {} requested AI movie suggestions for query: {}", email, query);

        String promptText = """
                You are a movie recommendation system. Suggest exactly 5 well-known,
                real movie titles closely related to: "%s".
                Only include movies that actually exist — never invent a title.
                """.formatted(query);

        MovieSuggestions result = chatClient.prompt()
                .user(promptText)
                .call()
                .entity(MovieSuggestions.class);

        return AISuggestResponse.builder()
                .movies(result.titles())
                .build();
    }
}
