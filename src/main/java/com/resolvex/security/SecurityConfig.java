package com.resolvex.security;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtConverter =
                new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(
                authoritiesConverter);

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/login").permitAll()

                    .requestMatchers(
                            "/roles", "/roles/**",
                            "/users", "/users/**")
                        .hasRole("ADMIN")

                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/departments", "/api/departments/**",
                            "/api/categories", "/api/categories/**")
                        .authenticated()

                    .requestMatchers(
                            "/api/departments", "/api/departments/**",
                            "/api/categories", "/api/categories/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers("/api/dashboard", "/api/dashboard/**")
                        .hasAnyRole("ADMIN", "MANAGER")
                        
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/issues/*/assign")
                            .hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/issues/*/status")
                            .hasAnyRole("ADMIN", "MANAGER", "SUPPORT")
                        
                    .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt
                            .jwtAuthenticationConverter(jwtConverter)));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}