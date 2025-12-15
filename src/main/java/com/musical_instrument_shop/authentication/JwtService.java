package com.musical_instrument_shop.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;



//Responsible for creating the JWT token upon successful authentication, embedding claims like the username (subject) and role,
// and signing it with a secret key.
//Provides methods to parse, extract claims (like username), and validate the token's signature and expiration date on subsequent requests.
//Used by AuthenticationService to issue the token and by JwtAuthenticationFilter to validate tokens.
@Service
public class JwtService {

    @Value("${app.security.secret-key}")
    private String secretKey; // Base64 encoded secret key for signing JWT tokens

    @Value("${app.security.jwt-expiration}")
    private long jwtExpiration; // Token validity duration in milliseconds

    /**
     * Generates a JWT token containing username as subject and role as claim.
     */
    public String generateToken(String username, String role) {
        var claims = new HashMap<String, Object>();
        claims.put("role", role);  // Add user role to the token payload

        return Jwts.builder()
                .setIssuer("self")  // Issuer identifier (optional)
                .setClaims(claims)   // Set custom claims
                .setSubject(username) // Set username as the subject
                .setIssuedAt(new Date(System.currentTimeMillis())) // Token creation time
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Expiration time
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Sign token using HMAC-SHA256 and secret key
                .compact();
    }

    /**
     * Validates if the token matches the user and is not expired.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String subject = extractSubject(token);
        return (subject.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Extracts the username (subject) from the JWT token.
     */
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Generic method to extract any claim from the token using a claims resolver function.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Checks if the token is expired by comparing expiration date with current date.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts expiration date from token claims.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Parses the JWT token and extracts all claims.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) // Set signing key to validate signature
                .build()
                .parseClaimsJws(token)          // Parses and validates the JWT signature
                .getBody();                     // Returns the claims/payload
    }

    /**
     * Decodes the base64 secret key and returns a Key object used for signing JWTs.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
