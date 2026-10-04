package com.rateforge.apikey.dto;

import com.rateforge.apikey.enums.Plan;

public record CreateClientResponse(String id, String clientName, String email, Plan plan, String apiKey) {}
