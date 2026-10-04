package com.rateforge.gateway.ratelimit;

public record RateLimitResult(
        boolean allowed,
        long currentCount,
        long limit,
        long remaining
) {
}
