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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;



//securityFilterChain: Configures request authorization
// (e.g., /api/auth/** is public, all others are private), disables CSRF, sets sessions to STATELESS, and injects the jwtFilter.
// passwordEncoder: Defines the BCryptPasswordEncoder to securely hash and verify passwords.
// authenticationManager: Exposes the central authentication component.
// authenticationProvider:Configures the DaoAuthenticationProvider to know how to authenticate (using the UserDetailsService and PasswordEncoder)
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
//define the security rules, beans, and overall behavior of the application's security.
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection (careful in production)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(request -> request
                                .requestMatchers("/api/email/**").permitAll()
                                .requestMatchers("/api/auth/**").permitAll() // Allow open access to auth endpoints
//                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/users/**")
//                        .hasAuthority("ROLE_ADMIN")  // Only users with role ADMIN can DELETE on /users/**
                                .anyRequest().authenticated() // All other requests require authentication
                )
                .httpBasic(Customizer.withDefaults()) // Enable basic auth (useful for testing with Postman)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless sessions for JWT
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT filter before Spring Security auth filter
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173", // React dev server
                "https://coding-factory.apps.gov.gr",
                "https://test-coding-factory.apps.gov.gr"
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

//    @Bean
//    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
//                                                         PasswordEncoder passwordEncoder) {
//        // Sets up DaoAuthenticationProvider, which is an AuthenticationProvider
//        // that retrieves user details from the UserDetailsService and uses the PasswordEncoder to verify passwords.
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//
//        // Inject the UserDetailsService (your custom user lookup)
//        authProvider.setUserDetailsService(userDetailsService);
//
//        // Inject the PasswordEncoder to hash and verify passwords securely
//        authProvider.setPasswordEncoder(passwordEncoder);
//
//        return authProvider;
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Returns a BCryptPasswordEncoder with strength 12
        // BCrypt is a strong hashing algorithm recommended for storing passwords securely.
        return new BCryptPasswordEncoder(12);
    }
}
