package buy01.media_service;

import buy01.media_service.controller.MediaController;
import buy01.media_service.service.MediaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MediaController.class)
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MediaService mediaService;

    @Test
    void shouldUploadPublicAvatar() throws Exception {

        when(mediaService.uploadSingleAvatar(any()))
                .thenReturn("/uploads/avatar.jpg");

        MockMultipartFile file = new MockMultipartFile(
                "avatar",
                "avatar.jpg",
                "image/jpeg",
                "image".getBytes()
        );

        mockMvc.perform(
                        multipart("/api/media/avatars/public")
                                .file(file)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.avatarUrl")
                        .value("/uploads/avatar.jpg"));
    }

    @Test
    void shouldRejectInvalidAvatar() throws Exception {

        when(mediaService.uploadSingleAvatar(any()))
                .thenThrow(new IllegalArgumentException("Invalid image"));

        MockMultipartFile file = new MockMultipartFile(
                "avatar",
                "test.txt",
                "text/plain",
                "hello".getBytes()
        );

        mockMvc.perform(
                        multipart("/api/media/avatars/public")
                                .file(file)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Invalid image"));
    }

    @Test
    void shouldUploadImages() throws Exception {

        when(mediaService.upload(any()))
                .thenReturn(List.of(
                        "/uploads/a.jpg",
                        "/uploads/b.jpg"
                ));

        MockMultipartFile file1 = new MockMultipartFile(
                "images",
                "a.jpg",
                "image/jpeg",
                "a".getBytes()
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "images",
                "b.jpg",
                "image/jpeg",
                "b".getBytes()
        );

        mockMvc.perform(
                        multipart("/api/media/images")
                                .file(file1)
                                .file(file2)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldDeleteImage() throws Exception {

        when(mediaService.deleteImage("test.jpg"))
                .thenReturn(true);

        mockMvc.perform(
                        delete("/api/media/images/test.jpg")
                )
                .andExpect(status().isNoContent());

        verify(mediaService).deleteImage("test.jpg");
    }

    @Test
    void shouldReturnNotFoundWhenDeleteFails() throws Exception {

        when(mediaService.deleteImage("missing.jpg"))
                .thenReturn(false);

        mockMvc.perform(
                        delete("/api/media/images/missing.jpg")
                )
                .andExpect(status().isNotFound());
    }
}