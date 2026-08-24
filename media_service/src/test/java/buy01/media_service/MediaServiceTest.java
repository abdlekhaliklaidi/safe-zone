package buy01.media_service;


import buy01.media_service.service.MediaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


import static org.junit.jupiter.api.Assertions.*;

class MediaServiceTest {

    private MediaService service;

    @BeforeEach
    void setUp() {
        service = new MediaService();
    }

    @Test
    void shouldRejectEmptyImage() {
        MultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                new byte[0]
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.uploadSingleAvatar(file)
        );
    }

    @Test
    void shouldRejectInvalidImageType() {
        MultipartFile file = new MockMultipartFile(
                "image",
                "test.txt",
                "text/plain",
                "hello".getBytes()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.uploadSingleAvatar(file)
        );
    }

    @Test
    void shouldRejectTooLargeImage() {
        byte[] data = new byte[2 * 1024 * 1024 + 1];

        MultipartFile file = new MockMultipartFile(
                "image",
                "big.jpg",
                "image/jpeg",
                data
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.uploadSingleAvatar(file)
        );
    }

    @Test
    void shouldUploadSingleAvatar() {
        MultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "fake-image".getBytes()
        );

        String result = service.uploadSingleAvatar(file);

        assertNotNull(result);
        assertTrue(result.startsWith("/uploads/"));
    }

    @Test
    void shouldRejectEmptyImagesArray() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.upload(new MultipartFile[0])
        );
    }

    @Test
    void shouldUploadMultipleImages() {
        MultipartFile file1 = new MockMultipartFile(
                "images",
                "one.jpg",
                "image/jpeg",
                "image1".getBytes()
        );

        MultipartFile file2 = new MockMultipartFile(
                "images",
                "two.png",
                "image/png",
                "image2".getBytes()
        );

        var result = service.upload(
                new MultipartFile[]{file1, file2}
        );

        assertEquals(2, result.size());
        assertTrue(result.get(0).startsWith("/uploads/"));
        assertTrue(result.get(1).startsWith("/uploads/"));
    }

    @Test
    void shouldDeleteImage() {
        MultipartFile file = new MockMultipartFile(
                "image",
                "delete.jpg",
                "image/jpeg",
                "image".getBytes()
        );

        String url = service.uploadSingleAvatar(file);

        assertTrue(service.deleteImage(url));
    }

    @Test
    void shouldReturnFalseWhenDeletingBlankFilename() {
        assertFalse(service.deleteImage(""));
    }

    @Test
    void shouldReturnFalseWhenDeletingMissingFile() {
        assertFalse(
                service.deleteImage(
                        "this-file-does-not-exist.jpg"
                )
        );
    }
}