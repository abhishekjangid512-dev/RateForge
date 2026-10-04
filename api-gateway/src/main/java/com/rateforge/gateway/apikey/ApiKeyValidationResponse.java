package com.rateforge.gateway.apikey;

public record ApiKeyValidationResponse(
        boolean valid,
        String clientId,
        String plan,
        long requestsPerMinute) {
}
