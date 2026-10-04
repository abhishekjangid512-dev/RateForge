package com.rateforge.apikey.controller;

import com.rateforge.apikey.dto.*;
import com.rateforge.apikey.service.ApiClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ApiClientController {
    private final ApiClientService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateClientResponse create(@Valid @RequestBody CreateClientRequest request) {
        return service.createClient(request);
    }

    @GetMapping("/validate")
    public ApiKeyValidationResponse validate(@RequestHeader(value = "X-API-KEY", required = false) String apiKey) {
        return service.validate(apiKey);
    }
}
