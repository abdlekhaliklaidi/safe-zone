package buy01.product_service;


import buy01.product_service.client.MediaClient;
import buy01.product_service.exceptions.ForbiddenException;
import buy01.product_service.model.Product;
import buy01.product_service.repository.ProductRepository;
import buy01.product_service.service.ProductService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private MediaClient mediaClient;

    @InjectMocks
    private ProductService service;


    private Product product() {
        return Product.builder()
                .id("product1")
                .name("Laptop")
                .description("Gaming laptop 16GB RAM")
                .price(1000.0)
                .quantity(5)
                .userId("user1")
                .imageUrls(Collections.emptyList())
                .build();
    }


    // =========================
    // GET ALL
    // =========================

    @Test
    void shouldGetAllProducts() {

        when(repository.findAll())
                .thenReturn(List.of(product()));

        List<Product> result = service.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());

        verify(repository).findAll();
    }


    // =========================
    // GET BY ID
    // =========================

    @Test
    void shouldGetProduct() {

        when(repository.findById("product1"))
                .thenReturn(Optional.of(product()));

        Product result = service.getProduct("product1");

        assertEquals("product1", result.getId());

        verify(repository).findById("product1");
    }


    @Test
    void shouldThrowWhenProductNotFound() {

        when(repository.findById("bad-id"))
                .thenReturn(Optional.empty());

        ResponseStatusException ex =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.getProduct("bad-id")
                );

        assertEquals(404, ex.getStatusCode().value());
    }


    // =========================
    // CREATE
    // =========================

    @Test
    void shouldCreateProduct() {

        Product saved = product();

        when(repository.save(any(Product.class)))
                .thenReturn(saved);

        Product result = service.createProduct(
                " Laptop ",
                " Gaming laptop 16GB RAM ",
                1000.0,
                5,
                null,
                "user1",
                "SELLER"
        );

        assertEquals("Laptop", result.getName());
        assertEquals("Gaming laptop 16GB RAM", result.getDescription());
        assertEquals("user1", result.getUserId());

        verify(repository).save(any(Product.class));
    }


    // =========================
    // CREATE WITH IMAGE
    // =========================

    @Test
    void shouldCreateProductWithImage() {

        MockMultipartFile image =
                new MockMultipartFile(
                        "image",
                        "laptop.png",
                        "image/png",
                        "image-data".getBytes()
                );

        when(mediaClient.uploadImages(any()))
                .thenReturn(List.of("http://image.com/laptop.png"));

        when(repository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product result = service.createProduct(
                "Laptop",
                "Gaming laptop 16GB RAM",
                1000.0,
                5,
                new MockMultipartFile[]{image},
                "user1",
                "SELLER"
        );

        assertEquals(1, result.getImageUrls().size());
        assertEquals(
                "http://image.com/laptop.png",
                result.getImageUrls().get(0)
        );

        verify(mediaClient).uploadImages(any());
        verify(repository).save(any(Product.class));
    }


    // =========================
    // CREATE WRONG ROLE
    // =========================

    @Test
    void shouldRejectCreateWithWrongRole() {

        assertThrows(
                ForbiddenException.class,
                () -> service.createProduct(
                        "Laptop",
                        "Gaming laptop 16GB RAM",
                        1000.0,
                        5,
                        null,
                        "user1",
                        "CLIENT"
                )
        );

        verify(repository, never()).save(any());
    }


    // =========================
    // CREATE WITHOUT USER
    // =========================

    @Test
    void shouldRejectCreateWithoutUser() {

        ResponseStatusException ex =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.createProduct(
                                "Laptop",
                                "Gaming laptop 16GB RAM",
                                1000.0,
                                5,
                                null,
                                null,
                                "SELLER"
                        )
                );

        assertEquals(401, ex.getStatusCode().value());

        verify(repository, never()).save(any());
    }


    // =========================
    // INVALID PRODUCT DATA
    // =========================

    @Test
    void shouldRejectInvalidProductName() {

        ResponseStatusException ex =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.createProduct(
                                "ab",
                                "Gaming laptop 16GB RAM",
                                1000.0,
                                5,
                                null,
                                "user1",
                                "SELLER"
                        )
                );

        assertEquals(400, ex.getStatusCode().value());
    }


    @Test
    void shouldRejectInvalidDescription() {

        ResponseStatusException ex =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.createProduct(
                                "Laptop",
                                "short",
                                1000.0,
                                5,
                                null,
                                "user1",
                                "SELLER"
                        )
                );

        assertEquals(400, ex.getStatusCode().value());
    }


    @Test
    void shouldRejectInvalidPrice() {

        ResponseStatusException ex =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.createProduct(
                                "Laptop",
                                "Gaming laptop 16GB RAM",
                                0.0,
                                5,
                                null,
                                "user1",
                                "SELLER"
                        )
                );

        assertEquals(400, ex.getStatusCode().value());
    }


    @Test
    void shouldRejectInvalidQuantity() {

        ResponseStatusException ex =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.createProduct(
                                "Laptop",
                                "Gaming laptop 16GB RAM",
                                1000.0,
                                -1,
                                null,
                                "user1",
                                "SELLER"
                        )
                );

        assertEquals(400, ex.getStatusCode().value());
    }


    // =========================
    // INVALID IMAGE TYPE
    // =========================

    @Test
    void shouldRejectInvalidImageType() {

        MockMultipartFile file =
                new MockMultipartFile(
                        "image",
                        "file.txt",
                        "text/plain",
                        "hello".getBytes()
                );

        ResponseStatusException ex =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.createProduct(
                                "Laptop",
                                "Gaming laptop 16GB RAM",
                                1000.0,
                                5,
                                new MockMultipartFile[]{file},
                                "user1",
                                "SELLER"
                        )
                );

        assertEquals(400, ex.getStatusCode().value());

        verify(mediaClient, never()).uploadImages(any());
    }


    // =========================
    // UPDATE
    // =========================

    @Test
    void shouldUpdateProduct() {

        Product existing = product();

        when(repository.findById("product1"))
                .thenReturn(Optional.of(existing));

        when(repository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product result = service.updateProduct(
                "product1",
                "Updated Laptop",
                "Updated gaming laptop description",
                1200.0,
                10,
                List.of("old-image.jpg"),
                null,
                "user1",
                "SELLER"
        );

        assertEquals("Updated Laptop", result.getName());
        assertEquals(1200.0, result.getPrice());
        assertEquals(10, result.getQuantity());
        assertEquals(1, result.getImageUrls().size());

        verify(repository).findById("product1");
        verify(repository).save(existing);
    }


    // =========================
    // UPDATE WITH NEW IMAGE
    // =========================

    @Test
    void shouldUpdateProductWithNewImage() {

        Product existing = product();

        MockMultipartFile image =
                new MockMultipartFile(
                        "image",
                        "new.png",
                        "image/png",
                        "data".getBytes()
                );

        when(repository.findById("product1"))
                .thenReturn(Optional.of(existing));

        when(mediaClient.uploadImages(any()))
                .thenReturn(List.of("new-image.png"));

        when(repository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product result = service.updateProduct(
                "product1",
                "Laptop",
                "Gaming laptop 16GB RAM",
                1000.0,
                5,
                List.of("old-image.png"),
                new MockMultipartFile[]{image},
                "user1",
                "SELLER"
        );

        assertEquals(2, result.getImageUrls().size());

        verify(mediaClient).uploadImages(any());
        verify(repository).save(existing);
    }


    // =========================
    // UPDATE UNAUTHORIZED OWNER
    // =========================

    @Test
    void shouldRejectUpdateForAnotherUser() {

        when(repository.findById("product1"))
                .thenReturn(Optional.of(product()));

        ResponseStatusException ex =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.updateProduct(
                                "product1",
                                "Laptop",
                                "Gaming laptop 16GB RAM",
                                1000.0,
                                5,
                                null,
                                null,
                                "user2",
                                "SELLER"
                        )
                );

        assertEquals(403, ex.getStatusCode().value());

        verify(repository, never()).save(any());
    }


    // =========================
    // DELETE
    // =========================

    @Test
    void shouldDeleteProduct() {

        when(repository.findById("product1"))
                .thenReturn(Optional.of(product()));

        service.deleteProduct(
                "product1",
                "user1",
                "SELLER"
        );

        verify(repository).findById("product1");
        verify(repository).delete(any(Product.class));
    }


    // =========================
    // DELETE UNAUTHORIZED
    // =========================

    @Test
    void shouldRejectDeleteForAnotherUser() {

        when(repository.findById("product1"))
                .thenReturn(Optional.of(product()));

        ResponseStatusException ex =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.deleteProduct(
                                "product1",
                                "user2",
                                "SELLER"
                        )
                );

        assertEquals(403, ex.getStatusCode().value());

        verify(repository, never()).delete(any());
    }


    // =========================
    // DELETE PRODUCTS BY USER
    // =========================

    @Test
    void shouldDeleteProductsByUserId() {

        service.deleteProductsByUserId("user1");

        verify(repository).deleteByUserId("user1");
    }


    @Test
    void shouldNotDeleteProductsWhenUserIdIsEmpty() {

        service.deleteProductsByUserId("");

        verify(repository, never()).deleteByUserId(anyString());
    }
}