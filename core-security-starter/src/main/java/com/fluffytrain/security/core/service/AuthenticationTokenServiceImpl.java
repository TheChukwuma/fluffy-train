package com.fluffytrain.security.core.service;

import com.fluffytrain.security.core.auth.LoginRequest;
import com.fluffytrain.security.core.auth.LoginResponse;
import com.fluffytrain.security.core.config.SecurityProperties;
import com.fluffytrain.security.core.jwt.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationTokenServiceImpl implements AuthenticationTokenService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityProperties securityProperties;

    public AuthenticationTokenServiceImpl(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            SecurityProperties securityProperties
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.securityProperties = securityProperties;
    }

    @Override
    public LoginResponse authenticateAndIssueToken(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        UserDetails user = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(user);
        long expiresInSeconds = Math.max(1L, securityProperties.getJwt().getExpirationMs() / 1000L);
        return new LoginResponse(token, "Bearer", expiresInSeconds);
    }
}
