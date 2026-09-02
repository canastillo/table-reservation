package com.restaurant.reservation.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtUtils {

    private final String jwtSecret;
    private final int jwtExpirationMs;

    public JwtUtils(
            @Value("${app.jwt.secret}") String jwtSecret,
            @Value("${app.jwt.expirationMs}") int jwtExpirationMs
        ) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtToken(UserDetails userDetails, Long userId) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("uid", userId)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    /**
     * Extracts the subject from the JWT token.
     * <p>
     * Note: In this system the subject is the user's email, which serves as the
     * unique identifier for authentication. Despite the method name using
     * "username", the returned value is actually an email address.
     *
     * @param authToken the JWT token
     * @return the email address stored as the subject claim
     */
    public String getUsernameFromJwtToken(String authToken) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(authToken)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(authToken);
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("Invalid JWT token: {}", ex.getMessage());
            return false;
        }

        return true;
    }
}
