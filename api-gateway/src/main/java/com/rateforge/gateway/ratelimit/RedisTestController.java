package com.rateforge.gateway.ratelimit;

import com.rateforge.gateway.apikey.ApiKeyValidationClient;
import com.rateforge.gateway.apikey.ApiKeyValidationResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/redis-test")
public class RedisTestController {

    private final RedisPracticeService redisPracticeService;

    private final RateLimiterService rateLimiterService;

    private final ApiKeyValidationClient apiKeyValidationClient;

    public RedisTestController(RedisPracticeService redisPracticeService,RateLimiterService rateLimiterService,ApiKeyValidationClient apiKeyValidationClient) {
        this.redisPracticeService = redisPracticeService;
        this.rateLimiterService= rateLimiterService;
        this.apiKeyValidationClient=apiKeyValidationClient;
    }
    @GetMapping("/api-key-test")
    public Mono<ApiKeyValidationResponse> test(
            @RequestHeader("X-API-KEY") String apiKey) {

        return apiKeyValidationClient.validate(apiKey);
    }





    @GetMapping("/rate/{clientId}")
    public Mono<RateLimitResult> check(
            @PathVariable String clientId) {

        return rateLimiterService.checkLimit(
                clientId,
                3,
                60
        );
    }

    @GetMapping("/{clientId}")
    public Mono<Long> increment(@PathVariable String clientId) {
        return redisPracticeService.increment(clientId);
    }

}
