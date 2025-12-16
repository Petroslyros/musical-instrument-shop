package com.musical_instrument_shop.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

//define the security rules, beans, and overall behavior of the application's security.

//securityFilterChain: Configures request authorization
// (e.g., /api/auth/** is public, all others are private), disables CSRF, sets sessions to STATELESS, and injects the jwtFilter.
// passwordEncoder: Defines the BCryptPasswordEncoder to securely hash and verify passwords.
// authenticationManager: Exposes the central authentication component.
// authenticationProvider:Configures the DaoAuthenticationProvider to know how to authenticate (using the UserDetailsService and PasswordEncoder)
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity

public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection (careful in production)
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(request -> request
                                .requestMatchers("/api/email/**").permitAll()
                                .requestMatchers("/api/auth/**").permitAll() // Allow open access to auth endpoints
//                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/users/**")
//                        .hasAuthority("ROLE_ADMIN")  // Only users with role ADMIN can DELETE on /users/**
                                .requestMatchers(
                                        "/swagger-ui.html",        // The old Swagger UI HTML (if used)
                                        "/swagger-ui/**",          // All Swagger UI resources (JS, CSS, etc.)
                                        "/v3/api-docs/**",         // The API JSON docs
                                        "/v3/api-docs.yaml",       // YAML version of the docs
                                        "/swagger-resources/**",   // Swagger resource descriptors
                                        "/configuration/**"        // Swagger configuration endpoints
                                ).permitAll()
                                .anyRequest().authenticated() // All other requests require authentication
                )

                .httpBasic(Customizer.withDefaults()) // Enable basic auth (useful for testing with Postman)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless sessions for JWT
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT filter before Spring Security auth filter
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(myCustomAuthenticationEntryPoint())
                        .accessDeniedHandler(myCustomAccessDeniedHandler()))

                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173" // React dev server
        ));
        configuration.setAllowedMethods(List.of("*")); // allow all HTTP methods
        configuration.setAllowedHeaders(List.of("*")); // allow all headers
        configuration.setAllowCredentials(true); // allow Authorization header
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // Retrieves the AuthenticationManager from the AuthenticationConfiguration.
        // This manager is used to authenticate user credentials.
        return config.getAuthenticationManager();
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        // Returns a BCryptPasswordEncoder with strength 12
        // BCrypt is a strong hashing algorithm recommended for storing passwords securely.
        return new BCryptPasswordEncoder(12);
    }
    @Bean
    public AccessDeniedHandler myCustomAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    // AuthenticationEntryPoint (Handles 401 Unauthorized)
    // Triggered when an unauthenticated user tries to access a secured resource.
    // Default behavior: Redirects to login page (for web apps) or returns HTTP 401 (for APIs).
    // Want to return to a structured JSON response for APIs:
    @Bean
    public AuthenticationEntryPoint myCustomAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }
}
