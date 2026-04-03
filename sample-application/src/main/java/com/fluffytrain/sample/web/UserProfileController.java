package com.fluffytrain.sample.web;

import com.fluffytrain.security.core.jwt.JwtPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    @RequestMapping(value = "/me", method = {RequestMethod.GET, RequestMethod.POST})
    public MeResponse me(@AuthenticationPrincipal JwtPrincipal principal) {
        List<String> roles = principal.getAuthorities().stream()
                .map(a -> a.getAuthority().startsWith("ROLE_")
                        ? a.getAuthority().substring("ROLE_".length())
                        : a.getAuthority())
                .toList();
        return new MeResponse(principal.getUserId(), principal.getUsername(), roles);
    }

    public record MeResponse(String userId, String username, List<String> roles) {
    }
}
