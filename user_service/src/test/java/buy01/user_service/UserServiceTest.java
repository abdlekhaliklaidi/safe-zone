package buy01.user_service;

import buy01.user_service.model.User;
import buy01.user_service.repo.UserRepository;
import buy01.user_service.producer.UserEventProducer;
import buy01.user_service.security.JwtUtil;
import buy01.user_service.exceptions.BadRequestException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;
import buy01.user_service.service.UserService;
import buy01.user_service.service.UserBlacklistService;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

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


    @Test
    void shouldRegisterUser() {

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");


        Map<String, Object> result = userService.register(
                "abdel",
                "abdel@test.com",
                "pass123",
                "SELLER",
                null
        );


        assertTrue((Boolean) result.get("success"));

        verify(userRepository)
                .save(any(User.class));
    }
}