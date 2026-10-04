package com.rateforge.apikey.dto;

import com.rateforge.apikey.enums.Plan;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateClientRequest(
        @NotBlank String clientName,
        @NotBlank @Email String email,
        @NotNull Plan plan
) {}
