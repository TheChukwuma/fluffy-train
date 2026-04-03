package com.fluffytrain.sample.bootstrap;

import com.fluffytrain.sample.domain.Role;
import com.fluffytrain.sample.domain.UserAccount;
import com.fluffytrain.sample.persistence.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.EnumSet;
import java.util.Set;

@Configuration
public class UserDataLoader {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }
            Set<Role> userRoles = EnumSet.of(Role.USER);
            Set<Role> adminRoles = EnumSet.of(Role.USER, Role.ADMIN);
            userRepository.save(new UserAccount(
                    "alice",
                    passwordEncoder.encode("alice-secret"),
                    userRoles));
            userRepository.save(new UserAccount(
                    "bob",
                    passwordEncoder.encode("bob-secret"),
                    adminRoles));
        };
    }
}
