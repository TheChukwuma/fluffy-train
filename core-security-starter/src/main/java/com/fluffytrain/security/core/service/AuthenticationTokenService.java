package com.fluffytrain.security.core.service;

import com.fluffytrain.security.core.auth.LoginRequest;
import com.fluffytrain.security.core.auth.LoginResponse;

public interface AuthenticationTokenService {

    LoginResponse authenticateAndIssueToken(LoginRequest request);
}
