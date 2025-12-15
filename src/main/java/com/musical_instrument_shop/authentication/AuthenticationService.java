package com.musical_instrument_shop.authentication;

import com.musical_instrument_shop.dto.AuthenticationRequestDTO;
import com.musical_instrument_shop.dto.AuthenticationResponseDTO;
import com.musical_instrument_shop.model.User;
import com.musical_instrument_shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


//application-level service for handling the login request. It takes the username/password DTO.
//delegates to the AuthenticationManager to verify credentials, and if successful, it asks the JwtService to generate the token.
//Called by the AuthRestController upon a login request.
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;                     // JWT utility service to generate tokens
    private final UserRepository userRepository;                   // User repository for database access (not used here directly)
    private final AuthenticationManager authenticationManager; // Spring Security component to perform authentication

    /**
     * Authenticates user credentials and returns AuthenticationResponseDTO with JWT token.
     */
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO dto) {
        // Try authenticating the user with username and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        // If authentication succeeds, get authenticated User object from Authentication principal
        User user = (User) authentication.getPrincipal();

        // Generate JWT token with username and role info
        String token = jwtService.generateToken(authentication.getName(), user.getRole().name());

        // Return response DTO with user's first and last names plus the JWT token
        return new AuthenticationResponseDTO(user.getFirstname(), user.getLastname(), token);
    }
}


