package buy01.user_service;

import buy01.user_service.dto.ProfileRequest;
import buy01.user_service.dto.ProfileResponse;
import buy01.user_service.event.UserDeletedEvent;
import buy01.user_service.exceptions.BadRequestException;
import buy01.user_service.model.Role;
import buy01.user_service.model.User;
import buy01.user_service.producer.UserEventProducer;
import buy01.user_service.repo.UserRepository;
import buy01.user_service.security.JwtUtil;
import buy01.user_service.service.UserBlacklistService;
import buy01.user_service.service.UserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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


    // =====================================================
    // REGISTER
    // =====================================================

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

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("pass123");
    }


    @Test
    void shouldRegisterClientWhenRoleIsNotSeller() {

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded");

        Map<String, Object> result = userService.register(
                "client1",
                "client@test.com",
                "pass123",
                "CLIENT",
                null
        );

        assertTrue((Boolean) result.get("success"));

        verify(userRepository).save(argThat(user ->
                user.getRole() == Role.CLIENT
        ));
    }


    @Test
    void shouldRejectExistingEmail() {

        User user = User.builder()
                .email("test@test.com")
                .build();

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        assertThrows(
                BadRequestException.class,
                () -> userService.register(
                        "john",
                        "test@test.com",
                        "pass123",
                        "CLIENT",
                        null
                )
        );

        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void shouldRejectExistingUsername() {

        when(userRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.empty());

        User user = User.builder()
                .username("john")
                .build();

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        assertThrows(
                BadRequestException.class,
                () -> userService.register(
                        "john",
                        "john@test.com",
                        "pass123",
                        "CLIENT",
                        null
                )
        );

        verify(userRepository, never()).save(any(User.class));
    }


    // =====================================================
    // LOGIN
    // =====================================================

    @Test
    void shouldLoginUser() {

        User user = User.builder()
                .id("user1")
                .email("john@test.com")
                .password("encoded")
                .role(Role.CLIENT)
                .build();

        when(userRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("pass123", "encoded"))
                .thenReturn(true);

        when(jwtUtil.generateToken(user))
                .thenReturn("fake-token");

        Map<String, Object> result =
                userService.login("john@test.com", "pass123");

        assertTrue((Boolean) result.get("success"));
        assertEquals("fake-token", result.get("token"));

        verify(jwtUtil).generateToken(user);
    }


    @Test
    void shouldRejectUnknownUser() {

        when(userRepository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                BadRequestException.class,
                () -> userService.login(
                        "unknown@test.com",
                        "pass123"
                )
        );

        verify(jwtUtil, never()).generateToken(any());
    }


    @Test
    void shouldRejectWrongPassword() {

        User user = User.builder()
                .email("john@test.com")
                .password("encoded")
                .build();

        when(userRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong", "encoded"))
                .thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> userService.login(
                        "john@test.com",
                        "wrong"
                )
        );

        verify(jwtUtil, never()).generateToken(any());
    }


    // =====================================================
    // GET PROFILE
    // =====================================================

    @Test
    void shouldGetProfile() {

        User user = User.builder()
                .id("user1")
                .username("john")
                .email("john@test.com")
                .role(Role.CLIENT)
                .avatarUrl("avatar.png")
                .createdAt("2026-08-24")
                .build();

        when(userRepository.findById("user1"))
                .thenReturn(Optional.of(user));

        ProfileResponse result =
                userService.getProfile("user1");

        assertNotNull(result);

        verify(userRepository).findById("user1");
    }


    @Test
    void shouldRejectProfileWhenUserNotFound() {

        when(userRepository.findById("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> userService.getProfile("unknown")
        );
    }


    // =====================================================
    // UPDATE PROFILE
    // =====================================================

    @Test
    void shouldUpdateProfile() {

        User user = User.builder()
                .id("user1")
                .username("old")
                .email("old@test.com")
                .role(Role.CLIENT)
                .build();

        ProfileRequest request = new ProfileRequest();
        request.setUsername("john");
        request.setEmail("john@test.com");
        request.setAvatarUrl("avatar.png");
        request.setRole(Role.CLIENT);

        when(userRepository.findById("user1"))
                .thenReturn(Optional.of(user));

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        ProfileResponse result =
                userService.updateProfile("user1", request);

        assertNotNull(result);

        verify(userRepository).save(user);
    }


    @Test
    void shouldRejectUpdateWhenUserNotFound() {

        when(userRepository.findById("user1"))
                .thenReturn(Optional.empty());

        ProfileRequest request = new ProfileRequest();

        assertThrows(
                RuntimeException.class,
                () -> userService.updateProfile(
                        "user1",
                        request
                )
        );

        verify(userRepository, never()).save(any());
    }


    // =====================================================
    // DELETE PROFILE
    // =====================================================

    @Test
    void shouldDeleteProfile() {

        User user = User.builder()
                .id("user1")
                .username("john")
                .build();

        when(userRepository.findById("user1"))
                .thenReturn(Optional.of(user));

        Map<String, Object> result =
                userService.deleteProfile("user1");

        assertTrue((Boolean) result.get("success"));

        verify(userRepository).delete(user);

        verify(producer)
                .sendUserDeletedEvent(any(UserDeletedEvent.class));
    }


    @Test
    void shouldRejectDeleteWhenUserNotFound() {

        when(userRepository.findById("user1"))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> userService.deleteProfile("user1")
        );

        verify(userRepository, never()).delete(any());
        verify(producer, never())
                .sendUserDeletedEvent(any());
    }
}