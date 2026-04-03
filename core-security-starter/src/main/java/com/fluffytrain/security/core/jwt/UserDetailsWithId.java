package com.fluffytrain.security.core.jwt;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Optional contract for applications that embed a stable user identifier in JWT claims.
 */
public interface UserDetailsWithId extends UserDetails {

    String getUserId();
}
