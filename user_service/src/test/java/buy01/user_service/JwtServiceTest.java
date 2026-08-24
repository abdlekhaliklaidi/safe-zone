package buy01.user_service;


import buy01.user_service.model.Role;
import buy01.user_service.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import buy01.user_service.security.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtUtil jwtUtil;

    private User user;

    private static final String SECRET_KEY =
            "mysecretkeymysecretkeymysecretkey123456789012345678901234567890";

    @BeforeEach
    void setUp() {

        jwtUtil = new JwtUtil();

        user = User.builder()
                .id("user-123")
                .username("john")
                .email("john@example.com")
                .password("encoded-password")
                .role(Role.CLIENT)
                .build();
    }

    @Test
    void generateToken_shouldGenerateValidJwt() {

        String token = jwtUtil.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());

        assertEquals(
                3,
                token.split("\\.").length
        );
    }

    @Test
    void generateToken_shouldContainUsernameAsSubject() {

        String token = jwtUtil.generateToken(user);

        SecretKey key = io.jsonwebtoken.security.Keys
                .hmacShaKeyFor(
                        SECRET_KEY.getBytes(StandardCharsets.UTF_8)
                );

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(
                "john",
                claims.getSubject()
        );
    }

    @Test
    void generateToken_shouldContainUserId() {

        String token = jwtUtil.generateToken(user);

        SecretKey key = io.jsonwebtoken.security.Keys
                .hmacShaKeyFor(
                        SECRET_KEY.getBytes(StandardCharsets.UTF_8)
                );

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(
                "user-123",
                claims.get("userId", String.class)
        );
    }

    @Test
    void generateToken_shouldContainRole() {

        String token = jwtUtil.generateToken(user);

        SecretKey key = io.jsonwebtoken.security.Keys
                .hmacShaKeyFor(
                        SECRET_KEY.getBytes(StandardCharsets.UTF_8)
                );

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(
                "CLIENT",
                claims.get("role", String.class)
        );
    }

    @Test
    void generateToken_shouldContainIssuedAt() {

        String token = jwtUtil.generateToken(user);

        SecretKey key = io.jsonwebtoken.security.Keys
                .hmacShaKeyFor(
                        SECRET_KEY.getBytes(StandardCharsets.UTF_8)
                );

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertNotNull(claims.getIssuedAt());
    }

    @Test
    void generateToken_shouldContainExpiration() {

        String token = jwtUtil.generateToken(user);

        SecretKey key = io.jsonwebtoken.security.Keys
                .hmacShaKeyFor(
                        SECRET_KEY.getBytes(StandardCharsets.UTF_8)
                );

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertNotNull(claims.getExpiration());

        assertTrue(
                claims.getExpiration()
                        .after(claims.getIssuedAt())
        );
    }

    @Test
    void generateToken_shouldContainCorrectRoleForSeller()
            {

        user.setRole(Role.SELLER);

        String token = jwtUtil.generateToken(user);

        SecretKey key = io.jsonwebtoken.security.Keys
                .hmacShaKeyFor(
                        SECRET_KEY.getBytes(StandardCharsets.UTF_8)
                );

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(
                "SELLER",
                claims.get("role", String.class)
        );
    }
}