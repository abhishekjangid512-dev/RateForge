package com.rateforge.apikey.dto;

import com.rateforge.apikey.enums.Plan;

public record ApiKeyValidationResponse(boolean valid, String clientId, Plan plan, int requestsPerMinute) {}
