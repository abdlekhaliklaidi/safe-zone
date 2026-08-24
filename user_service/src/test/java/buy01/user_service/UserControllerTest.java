package buy01.user_service;


import buy01.user_service.dto.ProfileRequest;
import buy01.user_service.dto.ProfileResponse;
import buy01.user_service.model.Role;
import buy01.user_service.service.UserService;
import buy01.user_service.controller.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;


    // =========================
    // REGISTER
    // =========================

    @Test
    void shouldRegisterUser() throws Exception {

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User registered successfully");

        when(userService.register(
                eq("abdel"),
                eq("abdel@test.com"),
                eq("pass123"),
                eq("CLIENT"),
                isNull()
        )).thenReturn(response);

        String request = """
                {
                    "username": "abdel",
                    "email": "abdel@test.com",
                    "password": "pass123",
                    "role": "CLIENT"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("User registered successfully"));

        verify(userService).register(
                "abdel",
                "abdel@test.com",
                "pass123",
                "CLIENT",
                null
        );
    }


    @Test
    void shouldRejectRegisterWithInvalidEmail() throws Exception {

        String request = """
                {
                    "username": "abdel",
                    "email": "invalid-email",
                    "password": "pass123",
                    "role": "CLIENT"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verify(userService, never()).register(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any()
        );
    }


    @Test
    void shouldRejectRegisterWithInvalidUsername() throws Exception {

        String request = """
                {
                    "username": "ab",
                    "email": "abdel@test.com",
                    "password": "pass123",
                    "role": "CLIENT"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verify(userService, never()).register(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any()
        );
    }


    @Test
    void shouldRejectRegisterWithInvalidPassword() throws Exception {

        String request = """
                {
                    "username": "abdel",
                    "email": "abdel@test.com",
                    "password": "123",
                    "role": "CLIENT"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }


    // =========================
    // LOGIN
    // =========================

    @Test
    void shouldLoginSuccessfully() throws Exception {

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login successful");
        response.put("token", "jwt-token");

        when(userService.login(
                "abdel@test.com",
                "pass123"
        )).thenReturn(response);

        String request = """
                {
                    "email": "abdel@test.com",
                    "password": "pass123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Login successful"))
                .andExpect(jsonPath("$.token")
                        .value("jwt-token"));

        verify(userService)
                .login("abdel@test.com", "pass123");
    }


    @Test
    void shouldRejectLoginWithInvalidEmail() throws Exception {

        String request = """
                {
                    "email": "invalid-email",
                    "password": "pass123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verify(userService, never())
                .login(anyString(), anyString());
    }


    // =========================
    // GET PROFILE
    // =========================

    @Test
    void shouldGetProfile() throws Exception {

        ProfileResponse profile = new ProfileResponse(
                "123",
                "abdel",
                "abdel@test.com",
                Role.CLIENT,
                "avatar.png",
                "2026-08-24T10:00:00"
        );

        when(userService.getProfile("123"))
                .thenReturn(profile);

        mockMvc.perform(get("/api/auth/profile")
                        .header("X-User-Id", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.username").value("abdel"))
                .andExpect(jsonPath("$.email")
                        .value("abdel@test.com"))
                .andExpect(jsonPath("$.role")
                        .value("CLIENT"))
                .andExpect(jsonPath("$.avatarUrl")
                        .value("avatar.png"));

        verify(userService).getProfile("123");
    }


    @Test
    void shouldRejectProfileWithoutUserIdHeader() throws Exception {

        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isBadRequest());

        verify(userService, never())
                .getProfile(anyString());
    }


    // =========================
    // UPDATE PROFILE
    // =========================

    @Test
    void shouldUpdateProfile() throws Exception {

        ProfileRequest request = new ProfileRequest();
        request.setUsername("newabdel");
        request.setEmail("new@test.com");
        request.setAvatarUrl("new-avatar.png");
        request.setRole(Role.CLIENT);

        ProfileResponse response = new ProfileResponse(
                "123",
                "newabdel",
                "new@test.com",
                Role.CLIENT,
                "new-avatar.png",
                "2026-08-24T10:00:00"
        );

        when(userService.updateProfile(
                eq("123"),
                any(ProfileRequest.class)
        )).thenReturn(response);

        mockMvc.perform(put("/api/auth/profile")
                        .header("X-User-Id", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.username")
                        .value("newabdel"))
                .andExpect(jsonPath("$.email")
                        .value("new@test.com"));

        verify(userService).updateProfile(
                eq("123"),
                any(ProfileRequest.class)
        );
    }


    @Test
    void shouldRejectUpdateProfileWithoutUserId() throws Exception {

        ProfileRequest request = new ProfileRequest();
        request.setUsername("abdel");
        request.setEmail("abdel@test.com");

        mockMvc.perform(put("/api/auth/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never())
                .updateProfile(anyString(), any(ProfileRequest.class));
    }


    // =========================
    // DELETE PROFILE
    // =========================

    @Test
    void shouldDeleteProfile() throws Exception {

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User deleted successfully");

        when(userService.deleteProfile("123"))
                .thenReturn(response);

        mockMvc.perform(delete("/api/auth/profile")
                        .header("X-User-Id", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("User deleted successfully"));

        verify(userService).deleteProfile("123");
    }


    @Test
    void shouldRejectDeleteWithoutUserId() throws Exception {

        mockMvc.perform(delete("/api/auth/profile"))
                .andExpect(status().isBadRequest());

        verify(userService, never())
                .deleteProfile(anyString());
    }
}
