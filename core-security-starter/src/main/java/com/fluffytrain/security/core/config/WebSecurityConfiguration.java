package com.fluffytrain.security.core.config;

import com.fluffytrain.security.core.jwt.JwtAuthenticationFilter;
import com.fluffytrain.security.core.web.AuthenticatedUserLoggingFilter;
import com.fluffytrain.security.core.web.JwtAccessDeniedHandler;
import com.fluffytrain.security.core.web.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SecurityProperties securityProperties,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticatedUserLoggingFilter authenticatedUserLoggingFilter,
            JwtAuthenticationEntryPoint authenticationEntryPoint,
            JwtAccessDeniedHandler accessDeniedHandler
    ) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.logout(AbstractHttpConfigurer::disable);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(authenticatedUserLoggingFilter, JwtAuthenticationFilter.class);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(securityProperties.getPublicPathPatterns().toArray(String[]::new)).permitAll()
                .requestMatchers(securityProperties.getAdminPathPatterns().toArray(String[]::new)).hasRole("ADMIN")
                .requestMatchers(securityProperties.getUserPathPatterns().toArray(String[]::new)).authenticated()
                .anyRequest().authenticated());

        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler));

        return http.build();
    }
}
