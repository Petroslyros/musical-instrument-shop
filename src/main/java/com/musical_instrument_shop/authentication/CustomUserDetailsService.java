package com.musical_instrument_shop.authentication;

import com.musical_instrument_shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


//This class implements the core Spring Security interface, UserDetailsService. Its single purpose
// is to retrieve a user's entire account information (UserDetails object, which your User entity implements)
// from the database using the provided username.
//This is called by the AuthenticationManager during the login process to load the user and get the hashed password for comparison.
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // This method is called by Spring Security during the authentication process.
        // It tries to find a User entity by the username provided during login.

        return userRepository.findByUsername(username)    // Query the database for a user with the given username
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
