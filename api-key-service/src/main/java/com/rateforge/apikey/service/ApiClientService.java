package com.rateforge.apikey.service;

import com.rateforge.apikey.dto.*;
import com.rateforge.apikey.entity.ApiClient;
import com.rateforge.apikey.exception.DuplicateClientException;
import com.rateforge.apikey.repository.ApiClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApiClientService {
    private final ApiClientRepository repository;
    private final ApiKeyGenerator apiKeyGenerator;

    @Transactional
    public CreateClientResponse createClient(CreateClientRequest request) {
        if (repository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateClientException("Client already exists for email: " + request.email());
        }
        String rawApiKey = apiKeyGenerator.generate();
        ApiClient client = new ApiClient();
        client.setClientName(request.clientName());
        client.setEmail(request.email().toLowerCase());
        client.setPlan(request.plan());
        client.setApiKeyHash(apiKeyGenerator.hash(rawApiKey));
        ApiClient saved = repository.save(client);
        return new CreateClientResponse(saved.getId(), saved.getClientName(), saved.getEmail(), saved.getPlan(), rawApiKey);
    }

    @Transactional(readOnly = true)
    public ApiKeyValidationResponse validate(String rawApiKey) {
        if (rawApiKey == null || rawApiKey.isBlank()) return new ApiKeyValidationResponse(false, null, null, 0);
        String hash = apiKeyGenerator.hash(rawApiKey);
        return repository.findByApiKeyHashAndActiveTrue(hash)
                .map(c -> new ApiKeyValidationResponse(true, c.getId(), c.getPlan(), c.getPlan().getRequestsPerMinute()))
                .orElseGet(() -> new ApiKeyValidationResponse(false, null, null, 0));
    }
}
