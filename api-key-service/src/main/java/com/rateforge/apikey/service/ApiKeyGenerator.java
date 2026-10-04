package com.rateforge.apikey.service;

import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

@Component
public class ApiKeyGenerator {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int SECRET_BYTES = 32;
    private static final String PREFIX = "rf_live_";

    public String generate() {
        byte[] bytes = new byte[SECRET_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String hash(String rawApiKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(rawApiKey.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is unavailable", e);
        }
    }
}
