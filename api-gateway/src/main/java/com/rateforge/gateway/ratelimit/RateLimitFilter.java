package com.rateforge.gateway.ratelimit;

import com.rateforge.gateway.apikey.ApiKeyValidationClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private final RateLimiterService rateLimiterService;
    private final ApiKeyValidationClient apiKeyValidationClient;

    public RateLimitFilter(RateLimiterService rateLimiterService,ApiKeyValidationClient apiKeyValidationClient) {
        this.rateLimiterService = rateLimiterService;
        this.apiKeyValidationClient=apiKeyValidationClient;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
         String path=  exchange.getRequest().getURI().getPath();

         //add the rate limiting on the protected end points
        if (!path.startsWith("/api/demo")){
            return chain.filter(exchange);
        }

        String apiKey=exchange.getRequest().getHeaders().getFirst("X-API-KEY");

        if (apiKey == null || apiKey.isBlank()){
            return writeError(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    "API key is required"
            );
        }
        return apiKeyValidationClient
                .validate(apiKey)
                .flatMap(client -> {

                    if (!client.valid()) {
                        return writeError(
                                exchange,
                                HttpStatus.UNAUTHORIZED,
                                "Invalid API key"
                        );
                    }

                    return rateLimiterService
                            .checkLimit(
                                    client.clientId(),
                                    client.requestsPerMinute(),
                                    60
                            )
                            .flatMap(result -> {

                                exchange.getResponse()
                                        .getHeaders()
                                        .set(
                                                "X-RateLimit-Limit",
                                                String.valueOf(result.limit())
                                        );

                                exchange.getResponse()
                                        .getHeaders()
                                        .set(
                                                "X-RateLimit-Remaining",
                                                String.valueOf(result.remaining())
                                        );

                                exchange.getResponse()
                                        .getHeaders()
                                        .set(
                                                "X-RateLimit-Plan",
                                                client.plan()
                                        );

                                if (!result.allowed()) {
                                    return writeRateLimitResponse(
                                            exchange,
                                            result
                                    );
                                }

                                return chain.filter(exchange);
                            });
                });


    }

    @Override
    public int getOrder() {

        return -1;
    }
    private Mono<Void> writeRateLimitResponse(
            ServerWebExchange exchange,
            RateLimitResult result) {

        exchange.getResponse()
                .setStatusCode(HttpStatus.TOO_MANY_REQUESTS);

        exchange.getResponse()
                .getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {
                  "status": 429,
                  "error": "Too Many Requests",
                  "message": "Rate limit exceeded",
                  "limit": %d,
                  "remaining": %d
                }
                """.formatted(
                result.limit(),
                result.remaining()
        );

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse()
                .writeWith(Mono.just(buffer));
    }
    private Mono<Void> writeError(
            ServerWebExchange exchange,
            HttpStatus status,
            String message) {

        exchange.getResponse()
                .setStatusCode(status);

        exchange.getResponse()
                .getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {
                  "status": %d,
                  "error": "%s"
                }
                """.formatted(
                status.value(),
                message
        );

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse()
                .writeWith(Mono.just(buffer));
    }
}
