package com.fluffytrain.security.core.auth;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds
) {
}
