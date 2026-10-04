package com.rateforge.apikey.entity;

import com.rateforge.apikey.enums.Plan;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_clients", indexes = @Index(name = "idx_api_clients_api_key_hash", columnList = "api_key_hash", unique = true))
@Getter @Setter @NoArgsConstructor
public class ApiClient {
    @Id
    private String id;

    @Column(name = "client_name", nullable = false, length = 120)
    private String clientName;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Plan plan;

    @Column(name = "api_key_hash", nullable = false, unique = true, length = 64)
    private String apiKeyHash;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID().toString();
        if (createdAt == null) createdAt = LocalDateTime.now();
        active = true;
    }
}
