package com.rateforge.gateway.apikey;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ApiKeyValidationClient {

    private final WebClient webClient;

    public ApiKeyValidationClient(WebClient.Builder builder){
        this.webClient=builder
                .baseUrl("http://localhost:8081")
                .build();
    }

    public Mono<ApiKeyValidationResponse> validate(String apiKey) {

        return webClient
                .get()
                .uri("/api/clients/validate")
                .header("X-API-KEY", apiKey)
                .retrieve()
                .bodyToMono(ApiKeyValidationResponse.class);
    }
}
