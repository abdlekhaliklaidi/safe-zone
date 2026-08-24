package buy01.gateway_service;

import buy01.gateway_service.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET =
            "mysecretkeymysecretkeymysecretkey123456789012345678901234567890";

    private SecretKey key;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET);

        key = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );
    }

    // =========================================================
    // VALID TOKEN
    // =========================================================

    @Test
    void validateToken_shouldReturnTrueForValidToken() {

        String token = createToken(
                "john",
                "user-123",
                "CLIENT"
        );

        assertTrue(jwtService.validateToken(token));
    }

    // =========================================================
    // INVALID TOKEN
    // =========================================================

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {

        assertFalse(
                jwtService.validateToken("invalid.jwt.token")
        );
    }

    @Test
    void validateToken_shouldReturnFalseForEmptyToken() {

        assertFalse(
                jwtService.validateToken("")
        );
    }

    @Test
    void validateToken_shouldReturnFalseForNullToken() {

        assertFalse(
                jwtService.validateToken(null)
        );
    }

    // =========================================================
    // EXPIRED TOKEN
    // =========================================================

    @Test
    void validateToken_shouldReturnFalseForExpiredToken() {

        String token = Jwts.builder()
                .subject("john")
                .claim("userId", "user-123")
                .claim("role", "CLIENT")
                .issuedAt(new Date(System.currentTimeMillis() - 20000))
                .expiration(new Date(System.currentTimeMillis() - 10000))
                .signWith(key)
                .compact();

        assertFalse(
                jwtService.validateToken(token)
        );
    }

    // =========================================================
    // EXTRACT CLAIMS
    // =========================================================

    @Test
    void extractClaims_shouldReturnCorrectClaims() {

        String token = createToken(
                "john",
                "user-123",
                "SELLER"
        );

        Claims claims = jwtService.extractClaims(token);

        assertNotNull(claims);
        assertEquals("john", claims.getSubject());
        assertEquals("user-123", claims.get("userId", String.class));
        assertEquals("SELLER", claims.get("role", String.class));
    }

    @Test
    void extractClaims_shouldThrowForInvalidToken() {

        assertThrows(
                Exception.class,
                () -> jwtService.extractClaims("invalid-token")
        );
    }

    // =========================================================
    // WRONG SIGNATURE
    // =========================================================

    @Test
    void validateToken_shouldReturnFalseForWrongSignature() {

        SecretKey wrongKey = Keys.hmacShaKeyFor(
                "anothersecretkeyanothersecretkeyanothersecretkey123456"
                        .getBytes(StandardCharsets.UTF_8)
        );

        String token = Jwts.builder()
                .subject("john")
                .claim("userId", "user-123")
                .claim("role", "CLIENT")
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + 60000)
                )
                .signWith(wrongKey)
                .compact();

        assertFalse(
                jwtService.validateToken(token)
        );
    }

    // =========================================================
    // HELPER
    // =========================================================

    private String createToken(
            String username,
            String userId,
            String role) {

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + 60000)
                )
                .signWith(key)
                .compact();
    }
}