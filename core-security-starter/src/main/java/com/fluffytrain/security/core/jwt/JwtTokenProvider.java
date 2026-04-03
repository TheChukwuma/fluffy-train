package com.fluffytrain.security.core.jwt;

import com.fluffytrain.security.core.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final String USERNAME_CLAIM = "username";
    private static final String ROLES_CLAIM = "roles";

    private final SecurityProperties securityProperties;
    private final SecretKey secretKey;

    public JwtTokenProvider(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        byte[] keyBytes = securityProperties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "core.security.jwt.secret must be at least 32 bytes (256 bits) for HS256");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + securityProperties.getJwt().getExpirationMs());
        String userId = userDetails instanceof UserDetailsWithId withId
                ? withId.getUserId()
                : userDetails.getUsername();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.startsWith("ROLE_") ? a.substring("ROLE_".length()) : a)
                .collect(Collectors.toList());
        return Jwts.builder()
                .subject(userId)
                .claim(USERNAME_CLAIM, userDetails.getUsername())
                .claim(ROLES_CLAIM, roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public Authentication buildAuthentication(String token) {
        Claims claims = parseClaims(token);
        String username = claims.get(USERNAME_CLAIM, String.class);
        if (username == null || username.isBlank()) {
            throw new JwtException("Missing username claim");
        }
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get(ROLES_CLAIM, List.class);
        if (roles == null) {
            throw new JwtException("Missing roles claim");
        }
        Collection<GrantedAuthority> authorities = roles.stream()
                .map(r -> "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        JwtPrincipal principal = new JwtPrincipal(claims.getSubject(), username, authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (JwtException e) {
            throw new JwtException("Invalid JWT", e);
        }
    }
}
