package com.fluffytrain.security.core.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final SecurityJsonResponseWriter jsonResponseWriter;

    public JwtAuthenticationEntryPoint(SecurityJsonResponseWriter jsonResponseWriter) {
        this.jsonResponseWriter = jsonResponseWriter;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        String message = authException instanceof BadCredentialsException
                ? "Invalid username or password"
                : "Authentication required";
        jsonResponseWriter.write(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", message);
    }
}
