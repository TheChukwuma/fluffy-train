package com.fluffytrain.security.core.jwt;

import com.fluffytrain.security.core.web.SecurityJsonResponseWriter;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityJsonResponseWriter jsonResponseWriter;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, SecurityJsonResponseWriter jsonResponseWriter) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jsonResponseWriter = jsonResponseWriter;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = extractBearerToken(header);
        if (token != null) {
            try {
                Authentication authentication = jwtTokenProvider.buildAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (ExpiredJwtException e) {
                jsonResponseWriter.write(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized",
                        "Token expired");
                return;
            } catch (JwtException e) {
                jsonResponseWriter.write(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized",
                        "Invalid or malformed token");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * RFC 7235 scheme is case-insensitive; only the "Bearer " prefix is stripped.
     */
    private static String extractBearerToken(String header) {
        if (!StringUtils.hasText(header)) {
            return null;
        }
        String trimmed = header.trim();
        if (trimmed.length() < 7) {
            return null;
        }
        if (!trimmed.substring(0, 7).equalsIgnoreCase("Bearer ")) {
            return null;
        }
        return trimmed.substring(7).trim();
    }
}
