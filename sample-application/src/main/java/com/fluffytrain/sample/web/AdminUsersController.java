package com.fluffytrain.sample.web;

import com.fluffytrain.sample.domain.Role;
import com.fluffytrain.sample.domain.UserAccount;
import com.fluffytrain.sample.persistence.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminUsersController {

    private final UserRepository userRepository;

    public AdminUsersController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<UserSummary> listUsers() {
        return userRepository.findAll().stream()
                .map(UserSummary::fromEntity)
                .toList();
    }

    public record UserSummary(String id, String username, List<String> roles) {
        static UserSummary fromEntity(UserAccount user) {
            List<String> roles = user.getRoles().stream().map(Role::name).toList();
            return new UserSummary(String.valueOf(user.getId()), user.getUsername(), roles);
        }
    }
}
