package com.restaurant.reservation.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilsTest {
    private JwtUtils jwtUtils;
    private final String JWT_SECRET = "secret-jwt-secure-32-bytes-signature";
    private final int JWT_EXPIRATION_MS = 60_000;
    private final String INVALID_SECRET = "another-32-bytes-super-secure-secret";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(JWT_SECRET,  JWT_EXPIRATION_MS);
    }

    @Test
    void generateJwtToken_shouldGenerateJwtTokenWithExpectedClaims() {
        UserDetails userDetails = User.withUsername("user@example.com")
                .password("password")
                .authorities(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"))
                .build();

        Long userId = 10L;
        long before = System.currentTimeMillis();
        String token = jwtUtils.generateJwtToken(userDetails, userId);
        long after = System.currentTimeMillis();

        assertNotNull(token);
        assertFalse(token.isBlank());

        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        assertEquals("user@example.com", claims.getSubject());
        assertEquals(10L, claims.get("uid", Long.class));

        assertThat(claims.get("roles")).asInstanceOf(LIST).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        //assertTrue(claims.getIssuedAt().getTime() >= before && claims.getIssuedAt().getTime() <= after);

        long expectedExpiration = claims.getIssuedAt().getTime() + JWT_EXPIRATION_MS;
        assertEquals(expectedExpiration,claims.getExpiration().getTime());
    }

    @Test
    void getUsernameFromJwtToken_successful() {
        UserDetails userDetails = User.withUsername("user@example.com")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        String token = jwtUtils.generateJwtToken(userDetails, 1L);
        String username = jwtUtils.getUsernameFromJwtToken(token);

        assertEquals("user@example.com", username);
    }

    @Test
    void validateJwtToken_successful() {
        UserDetails userDetails = User.withUsername("user@example.com")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        String token = jwtUtils.generateJwtToken(userDetails, 1L);
        boolean result = jwtUtils.validateJwtToken(token);

        assertTrue(result);
    }

    @Test
    void validateJwtToken_shouldRejectTokenSignedWithAnotherSecret() {
        SecretKey anotherKey = Keys.hmacShaKeyFor(INVALID_SECRET.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder().subject("user@example.com")
                .claim("uid", 1L)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000)).signWith(anotherKey)
                .compact();

        boolean result = jwtUtils.validateJwtToken(token);

        assertFalse(result);
    }

    @Test
    void validateJwtToken_shouldRejectMalformedToken() {
        boolean result = jwtUtils.validateJwtToken("invalid-token");
        assertFalse(result);
    }

    @Test
    void validateJwtToken_shouldRejectEmptyToken() {
        assertFalse(jwtUtils.validateJwtToken(""));
    }

    @Test
    void validateJwtToken_shouldRejectNullToken() {
        assertFalse(jwtUtils.validateJwtToken(null));
    }

    @Test
    void validateJwtToken_shouldRejectExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        String expiredToken = Jwts.builder()
                .subject("user@example.com")
                .claim("uid", 1L)
                .issuedAt(new Date(System.currentTimeMillis() - 120_000))
                .expiration(new Date(System.currentTimeMillis() - 60_000))
                .signWith(key)
                .compact();

        boolean result = jwtUtils.validateJwtToken(expiredToken);

        assertFalse(result);
    }

    @Test
    void getUsernameFromJwtToken_shouldRejectTokenWithInvalidSignature() {
        SecretKey anotherKey = Keys.hmacShaKeyFor(INVALID_SECRET.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("user@example.com")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(anotherKey)
                .compact();

        assertThrows(Exception.class, () -> jwtUtils.getUsernameFromJwtToken(token));
    }
}