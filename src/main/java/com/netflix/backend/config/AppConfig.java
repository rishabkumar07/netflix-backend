package com.netflix.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    /**
     * No timeout was set before, so a blocked TMDB connection (e.g. an ISP blocking
     * api.themoviedb.org) would hang for the OS-level default (~20s+) per request
     * instead of failing fast. 3s to connect, 5s to read — TMDB normally responds
     * in well under a second, so this only bites when TMDB is genuinely unreachable.
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);
        return new RestTemplate(factory);
    }
}
