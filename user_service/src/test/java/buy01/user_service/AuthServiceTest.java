package buy01.user_service;

import buy01.user_service.exceptions.BadRequestException;
import buy01.user_service.model.Role;
import buy01.user_service.model.User;
import buy01.user_service.repo.UserRepository;
import buy01.user_service.security.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import buy01.user_service.producer.UserEventProducer;
import buy01.user_service.service.UserBlacklistService;
import buy01.user_service.service.UserService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserEventProducer producer;

    @Mock
    private UserBlacklistService blacklistService;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id("123")
                .username("testuser")
                .email("test@example.com")
                .password("encoded-password")
                .role(Role.CLIENT)
                .build();
    }

    @Test
    void login_validCredentials_shouldReturnToken() {

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password123",
                "encoded-password"
        )).thenReturn(true);

        when(jwtUtil.generateToken(user))
                .thenReturn("generated-token");

        Map<String, Object> result =
                userService.login(
                        "test@example.com",
                        "password123"
                );

        assertTrue((Boolean) result.get("success"));
        assertEquals("Login successful", result.get("message"));
        assertEquals(
                "generated-token",
                result.get("token")
        );
    }

    @Test
    void login_userDoesNotExist_shouldThrowException() {

        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () -> userService.login(
                                "missing@example.com",
                                "password123"
                        )
                );

        assertEquals(
                "User not found",
                exception.getMessage()
        );
    }

    @Test
    void login_wrongPassword_shouldThrowException() {

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrong",
                "encoded-password"
        )).thenReturn(false);

        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () -> userService.login(
                                "test@example.com",
                                "wrong"
                        )
                );

        assertEquals(
                "Invalid password",
                exception.getMessage()
        );

        verify(jwtUtil, never()).generateToken(any());
    }
}