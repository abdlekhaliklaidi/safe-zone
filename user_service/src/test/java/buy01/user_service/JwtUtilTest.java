package buy01.user_service;

import buy01.user_service.model.Role;
import buy01.user_service.model.User;
import buy01.user_service.security.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String SECRET_KEY =
            "mysecretkeymysecretkeymysecretkey123456789012345678901234567890";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }


    @Test
    void shouldGenerateValidToken() {

        User user = User.builder()
                .id("123")
                .username("abdel")
                .email("abdel@test.com")
                .role(Role.CLIENT)
                .build();

        String token = jwtUtil.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }


    @Test
    void shouldGenerateTokenWithCorrectUsername() {

        User user = User.builder()
                .id("123")
                .username("abdel")
                .email("abdel@test.com")
                .role(Role.CLIENT)
                .build();

        String token = jwtUtil.generateToken(user);

        Claims claims = parseToken(token);

        assertEquals("abdel", claims.getSubject());
    }


    @Test
    void shouldGenerateTokenWithCorrectUserId() {

        User user = User.builder()
                .id("123")
                .username("abdel")
                .email("abdel@test.com")
                .role(Role.CLIENT)
                .build();

        String token = jwtUtil.generateToken(user);

        Claims claims = parseToken(token);

        assertEquals(
                "123",
                claims.get("userId", String.class)
        );
    }


    @Test
    void shouldGenerateTokenWithCorrectRole() {

        User user = User.builder()
                .id("123")
                .username("abdel")
                .email("abdel@test.com")
                .role(Role.SELLER)
                .build();

        String token = jwtUtil.generateToken(user);

        Claims claims = parseToken(token);

        assertEquals(
                "SELLER",
                claims.get("role", String.class)
        );
    }


    @Test
    void shouldContainIssuedAt() {

        User user = User.builder()
                .id("123")
                .username("abdel")
                .email("abdel@test.com")
                .role(Role.CLIENT)
                .build();

        String token = jwtUtil.generateToken(user);

        Claims claims = parseToken(token);

        assertNotNull(claims.getIssuedAt());
    }


    @Test
    void shouldContainExpirationDate() {

        User user = User.builder()
                .id("123")
                .username("abdel")
                .email("abdel@test.com")
                .role(Role.CLIENT)
                .build();

        String token = jwtUtil.generateToken(user);

        Claims claims = parseToken(token);

        assertNotNull(claims.getExpiration());
        assertTrue(
                claims.getExpiration()
                        .after(claims.getIssuedAt())
        );
    }


    @Test
    void shouldGenerateDifferentTokensForDifferentUsers() {

        User user1 = User.builder()
                .id("123")
                .username("abdel")
                .email("abdel@test.com")
                .role(Role.CLIENT)
                .build();

        User user2 = User.builder()
                .id("456")
                .username("seller")
                .email("seller@test.com")
                .role(Role.SELLER)
                .build();

        String token1 = jwtUtil.generateToken(user1);
        String token2 = jwtUtil.generateToken(user2);

        assertNotEquals(token1, token2);
    }


    private Claims parseToken(String token) {

        SecretKey key = Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
