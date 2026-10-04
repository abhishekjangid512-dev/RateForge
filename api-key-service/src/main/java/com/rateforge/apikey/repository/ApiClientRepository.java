package com.rateforge.apikey.repository;

import com.rateforge.apikey.entity.ApiClient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ApiClientRepository extends JpaRepository<ApiClient, String> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<ApiClient> findByApiKeyHashAndActiveTrue(String apiKeyHash);
}
