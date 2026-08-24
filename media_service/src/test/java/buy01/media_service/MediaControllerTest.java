package buy01.media_service;

import buy01.media_service.controller.MediaController;
import buy01.media_service.service.MediaService;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MediaController.class)
@AutoConfigureMockMvc(addFilters = false)
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MediaService mediaService;


    // =========================
    // PUBLIC AVATAR - SUCCESS
    // =========================

    @Test
    void shouldUploadPublicAvatar() throws Exception {

        MockMultipartFile avatar =
                new MockMultipartFile(
                        "avatar",
                        "avatar.jpg",
                        "image/jpeg",
                        "fake-image".getBytes()
                );

        when(mediaService.uploadSingleAvatar(any()))
                .thenReturn("/uploads/avatar.jpg");

        mockMvc.perform(
                multipart("/api/media/avatars/public")
                        .file(avatar)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.avatarUrl")
                .value("/uploads/avatar.jpg"));

        verify(mediaService).uploadSingleAvatar(any());
    }


    // =========================
    // PUBLIC AVATAR - BAD REQUEST
    // =========================

    @Test
    void shouldRejectInvalidAvatar() throws Exception {

        MockMultipartFile avatar =
                new MockMultipartFile(
                        "avatar",
                        "test.txt",
                        "text/plain",
                        "invalid".getBytes()
                );

        when(mediaService.uploadSingleAvatar(any()))
                .thenThrow(new IllegalArgumentException("Invalid image"));

        mockMvc.perform(
                multipart("/api/media/avatars/public")
                        .file(avatar)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error")
                .value("Invalid image"));
    }


    // =========================
    // PUBLIC AVATAR - SERVER ERROR
    // =========================

    @Test
    void shouldReturn500WhenAvatarUploadFails() throws Exception {

        MockMultipartFile avatar =
                new MockMultipartFile(
                        "avatar",
                        "avatar.jpg",
                        "image/jpeg",
                        "image".getBytes()
                );

        when(mediaService.uploadSingleAvatar(any()))
                .thenThrow(new RuntimeException("Storage error"));

        mockMvc.perform(
                multipart("/api/media/avatars/public")
                        .file(avatar)
        )
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error")
                .value("Failed to upload avatar: Storage error"));
    }


    // =========================
    // MULTIPLE IMAGES - SUCCESS
    // =========================

    @Test
    void shouldUploadImages() throws Exception {

        MockMultipartFile image =
                new MockMultipartFile(
                        "images",
                        "image.jpg",
                        "image/jpeg",
                        "image".getBytes()
                );

        when(mediaService.upload(any(MultipartFile[].class)))
                .thenReturn(List.of(
                        "/uploads/image.jpg"
                ));

        mockMvc.perform(
                multipart("/api/media/images")
                        .file(image)
        )
        .andExpect(status().isOk());

        verify(mediaService).upload(any(MultipartFile[].class));
    }


    // =========================
    // DELETE - SUCCESS
    // =========================

    @Test
    void shouldDeleteImage() throws Exception {

        when(mediaService.deleteImage("image.jpg"))
                .thenReturn(true);

        mockMvc.perform(
                delete("/api/media/images/image.jpg")
        )
        .andExpect(status().isNoContent());

        verify(mediaService).deleteImage("image.jpg");
    }


    // =========================
    // DELETE - NOT FOUND
    // =========================

    @Test
    void shouldReturnNotFoundWhenDeleteFails() throws Exception {

        when(mediaService.deleteImage("missing.jpg"))
                .thenReturn(false);

        mockMvc.perform(
                delete("/api/media/images/missing.jpg")
        )
        .andExpect(status().isNotFound());

        verify(mediaService).deleteImage("missing.jpg");
    }
}