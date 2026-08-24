package buy01.user_service;


import buy01.user_service.dto.ProfileRequest;
import buy01.user_service.dto.ProfileResponse;
import buy01.user_service.model.Role;
import buy01.user_service.service.UserService;
import buy01.user_service.controller.UserController;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // =========================
    // REGISTER
    // =========================

    @Test
    void register_shouldReturnSuccess() throws Exception {

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put(
                "message",
                "User registered successfully"
        );

        when(userService.register(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any()
        )).thenReturn(response);

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "username": "john123",
                              "email": "john@example.com",
                              "password": "pass1234",
                              "role": "CLIENT",
                              "avatarUrl": "avatar.png"
                            }
                        """)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message")
                .value("User registered successfully"));
    }

    @Test
    void register_invalidUsername_shouldReturnBadRequest()
            throws Exception {

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "username": "x",
                              "email": "john@example.com",
                              "password": "pass1234",
                              "role": "CLIENT"
                            }
                        """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void register_invalidEmail_shouldReturnBadRequest()
            throws Exception {

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "username": "john123",
                              "email": "invalid-email",
                              "password": "pass1234",
                              "role": "CLIENT"
                            }
                        """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void register_invalidPassword_shouldReturnBadRequest()
            throws Exception {

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "username": "john123",
                              "email": "john@example.com",
                              "password": "123",
                              "role": "CLIENT"
                            }
                        """)
        )
        .andExpect(status().isBadRequest());
    }

    // =========================
    // LOGIN
    // =========================

    @Test
    void login_shouldReturnSuccess() throws Exception {

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login successful");
        response.put("token", "jwt-token");

        when(userService.login(
                "john@example.com",
                "password123"
        )).thenReturn(response);

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "email": "john@example.com",
                              "password": "password123"
                            }
                        """)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.token")
                .value("jwt-token"));
    }

    @Test
    void login_invalidEmail_shouldReturnBadRequest()
            throws Exception {

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "email": "wrong",
                              "password": "password123"
                            }
                        """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void login_emptyPassword_shouldReturnBadRequest()
            throws Exception {

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "email": "john@example.com",
                              "password": ""
                            }
                        """)
        )
        .andExpect(status().isBadRequest());
    }

    // =========================
    // PROFILE
    // =========================

    @Test
    void profile_shouldReturnUserProfile()
            throws Exception {

        ProfileResponse response = new ProfileResponse(
                "123",
                "john",
                "john@example.com",
                Role.CLIENT,
                "avatar.png",
                "2026-01-01"
        );

        when(userService.getProfile("123"))
                .thenReturn(response);

        mockMvc.perform(
                get("/api/auth/profile")
                        .header("X-User-Id", "123")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("123"))
        .andExpect(jsonPath("$.username").value("john"))
        .andExpect(jsonPath("$.email")
                .value("john@example.com"));
    }

    @Test
    void updateProfile_shouldReturnUpdatedProfile()
            throws Exception {

        ProfileResponse response = new ProfileResponse(
                "123",
                "john-updated",
                "john@example.com",
                Role.CLIENT,
                "new.png",
                "2026-01-01"
        );

        when(userService.updateProfile(
                eq("123"),
                any(ProfileRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                put("/api/auth/profile")
                        .header("X-User-Id", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "username": "john-updated",
                              "email": "john@example.com",
                              "avatarUrl": "new.png",
                              "role": "CLIENT"
                            }
                        """)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username")
                .value("john-updated"));
    }

    @Test
    void deleteProfile_shouldReturnSuccess()
            throws Exception {

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put(
                "message",
                "User deleted successfully"
        );

        when(userService.deleteProfile("123"))
                .thenReturn(response);

        mockMvc.perform(
                delete("/api/auth/profile")
                        .header("X-User-Id", "123")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message")
                .value("User deleted successfully"));
    }
}