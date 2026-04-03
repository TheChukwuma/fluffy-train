package com.fluffytrain.security.core.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluffytrain.security.core.api.ApiError;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;

public final class SecurityJsonResponseWriter {

    private final ObjectMapper objectMapper;

    public SecurityJsonResponseWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void write(HttpServletResponse response, int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ApiError body = ApiError.of(status, error, message);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
