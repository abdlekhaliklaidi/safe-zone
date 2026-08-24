package buy01.user_service;


import buy01.user_service.dto.ProfileRequest;
import buy01.user_service.dto.ProfileResponse;
import buy01.user_service.service.UserService;
import buy01.user_service.controller.UserController;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService us;


    // =========================
    // REGISTER
    // =========================

    @Test
    void register() throws Exception {

        when(us.register(
                eq("john123"),
                eq("john@test.com"),
                eq("pass123"),
                eq("CLIENT"),
                isNull()
        )).thenReturn(
                Map.of(
                        "message", "User registered",
                        "success", true
                )
        );

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "username": "john123",
                                "email": "john@test.com",
                                "password": "pass123",
                                "role": "CLIENT"
                            }
                        """)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("User registered"))
        .andExpect(jsonPath("$.success").value(true));

        verify(us).register(
                eq("john123"),
                eq("john@test.com"),
                eq("pass123"),
                eq("CLIENT"),
                isNull()
        );
    }


    // =========================
    // LOGIN
    // =========================

    @Test
    void login() throws Exception {

        when(us.login(
                "john@test.com",
                "pass123"
        )).thenReturn(
                Map.of(
                        "token", "fake-token",
                        "success", true
                )
        );

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": "john@test.com",
                                "password": "pass123"
                            }
                        """)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("fake-token"))
        .andExpect(jsonPath("$.success").value(true));

        verify(us).login(
                "john@test.com",
                "pass123"
        );
    }


    // =========================
    // GET PROFILE
    // =========================

    @Test
    void getProfile() throws Exception {

        ProfileResponse response = new ProfileResponse();

        when(us.getProfile("user1"))
                .thenReturn(response);

        mockMvc.perform(
                get("/api/auth/profile")
                        .header("X-User-Id", "user1")
        )
        .andExpect(status().isOk());

        verify(us).getProfile("user1");
    }


    // =========================
    // UPDATE PROFILE
    // =========================

    @Test
    void updateProfile() throws Exception {

        ProfileRequest request = new ProfileRequest();

        ProfileResponse response = new ProfileResponse();

        when(us.updateProfile(
                eq("user1"),
                any(ProfileRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                put("/api/auth/profile")
                        .header("X-User-Id", "user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {}
                        """)
        )
        .andExpect(status().isOk());

        verify(us).updateProfile(
                eq("user1"),
                any(ProfileRequest.class)
        );
    }


    // =========================
    // DELETE PROFILE
    // =========================

    @Test
    void deleteProfile() throws Exception {

        when(us.deleteProfile("user1"))
                .thenReturn(
                        Map.of(
                                "message", "Profile deleted",
                                "success", true
                        )
                );

        mockMvc.perform(
                delete("/api/auth/profile")
                        .header("X-User-Id", "user1")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Profile deleted"))
        .andExpect(jsonPath("$.success").value(true));

        verify(us).deleteProfile("user1");
    }
}