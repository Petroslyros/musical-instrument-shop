package com.musical_instrument_shop.security;

import com.musical_instrument_shop.authentication.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


//This custom filter runs before Spring Security's built-in filters for every request that requires authentication (i.e., every endpoint except /api/auth).
//Core Action	1. Extracts the JWT from the Authorization: Bearer <token> header.
// 2. Uses JwtService to extract the username and validate the token.
// 3. Loads the full UserDetails via the UserDetailsService.
// 4.If valid, it creates an Authentication object and places it into the SecurityContextHolder, allowing the request to proceed as an authenticated user.
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;               // Service for JWT token handling (creation, validation, extraction)
    private final UserDetailsService userDetailsService; // Spring Security service to load user details by username

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization"); // Extract "Authorization" header from request
        final String jwt;
        final String username;

        // If header is missing or doesn't start with "Bearer ", just continue filter chain without auth
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token by removing "Bearer " prefix
        jwt = authHeader.substring(7).trim();

        try {
            // Extract username (subject) from JWT token
            username = jwtService.extractSubject(jwt);

            // If username is not null and user not yet authenticated in this context
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details from DB (or wherever) by username
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Validate token is still valid and matches user details
                if (!jwtService.isTokenValid(jwt, userDetails)) {
                    throw new BadCredentialsException("Invalid Token");
                }

                // Create authentication token for Spring Security context with user details and authorities
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                // Set authentication into the SecurityContext (mark user as authenticated)
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (ExpiredJwtException e) {
            // JWT expired: trigger 401 Unauthorized response through AuthenticationEntryPoint
            throw new AuthenticationCredentialsNotFoundException("Expired token", e);
        } catch (JwtException | IllegalArgumentException e) {
            // JWT invalid or malformed: trigger 401 Unauthorized
            throw new BadCredentialsException("Invalid token");
        } catch (Exception e) {
            // Any other error: trigger 403 Forbidden response
            throw new AccessDeniedException("Token validation failed");
        }

        // Continue with the filter chain (request processing)
        filterChain.doFilter(request, response);
    }
}

