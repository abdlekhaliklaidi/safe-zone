package buy01.product_service;

import buy01.product_service.client.MediaClient;
import buy01.product_service.model.Product;
import buy01.product_service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import buy01.product_service.service.ProductService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private MediaClient mediaClient;

    @InjectMocks
    private ProductService service;

    @Test
    void shouldCreateProduct() {

        Product product = Product.builder()
                .name("Laptop")
                .description("Gaming laptop 16GB RAM")
                .price(1000.0)
                .quantity(5)
                .userId("user1")
                .imageUrls(Collections.emptyList())
                .build();

        when(repository.save(any(Product.class))).thenReturn(product);

        Product result = service.createProduct(
                "Laptop",
                "Gaming laptop 16GB RAM",
                1000.0,
                5,
                null,
                "user1",
                "SELLER"
        );

        assertEquals("Laptop", result.getName());

        verify(repository).save(any(Product.class));
    }
}