package com.fluffytrain.security.core.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "core.security")
public class SecurityProperties {

    private final Jwt jwt = new Jwt();
    private List<String> publicPathPatterns = List.of("/api/public/**", "/api/auth/**");
    private List<String> adminPathPatterns = List.of("/api/admin/**");
    private List<String> userPathPatterns = List.of("/api/user/**");

    public Jwt getJwt() {
        return jwt;
    }

    public List<String> getPublicPathPatterns() {
        return publicPathPatterns;
    }

    public void setPublicPathPatterns(List<String> publicPathPatterns) {
        this.publicPathPatterns = publicPathPatterns;
    }

    public List<String> getAdminPathPatterns() {
        return adminPathPatterns;
    }

    public void setAdminPathPatterns(List<String> adminPathPatterns) {
        this.adminPathPatterns = adminPathPatterns;
    }

    public List<String> getUserPathPatterns() {
        return userPathPatterns;
    }

    public void setUserPathPatterns(List<String> userPathPatterns) {
        this.userPathPatterns = userPathPatterns;
    }

    public static class Jwt {

        /**
         * HMAC secret (HS256). Must be at least 256 bits (32 bytes) for production use.
         */
        @NotBlank
        private String secret;

        @Positive
        private long expirationMs = 86_400_000L;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getExpirationMs() {
            return expirationMs;
        }

        public void setExpirationMs(long expirationMs) {
            this.expirationMs = expirationMs;
        }
    }
}
