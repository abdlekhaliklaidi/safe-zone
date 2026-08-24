package buy01.product_service;


import buy01.product_service.model.Product;
import buy01.product_service.service.ProductService;
import buy01.product_service.controller.ProductController;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void getAllProducts_shouldReturnProducts() throws Exception {

        Product product = Product.builder()
                .name("Laptop")
                .description("A good laptop")
                .price(999.99)
                .quantity(10)
                .userId("seller-1")
                .build();

        when(productService.getAllProducts())
                .thenReturn(List.of(product));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].price").value(999.99));

        verify(productService).getAllProducts();
    }

    @Test
    void getProduct_shouldReturnProduct() throws Exception {

        Product product = Product.builder()
                .name("Phone")
                .description("A good phone")
                .price(500.0)
                .quantity(5)
                .userId("seller-1")
                .build();

        when(productService.getProduct("p1"))
                .thenReturn(product);

        mockMvc.perform(get("/api/products/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Phone"))
                .andExpect(jsonPath("$.quantity").value(5));

        verify(productService).getProduct("p1");
    }

    @Test
    void createProduct_shouldReturnProduct() throws Exception {

        Product product = Product.builder()
                .name("Laptop")
                .description("A good laptop")
                .price(1000.0)
                .quantity(10)
                .userId("seller-1")
                .build();

        when(productService.createProduct(
                anyString(),
                anyString(),
                anyDouble(),
                anyInt(),
                any(),
                anyString(),
                anyString()
        )).thenReturn(product);

        mockMvc.perform(
                multipart("/api/products")
                        .param("name", "Laptop")
                        .param("description", "A good laptop")
                        .param("price", "1000.0")
                        .param("quantity", "10")
                        .header("X-User-Id", "seller-1")
                        .header("X-Role", "SELLER")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Laptop"))
        .andExpect(jsonPath("$.price").value(1000.0));

        verify(productService).createProduct(
                eq("Laptop"),
                eq("A good laptop"),
                eq(1000.0),
                eq(10),
                isNull(),
                eq("seller-1"),
                eq("SELLER")
        );
    }

    @Test
    void createProduct_shouldAcceptImage() throws Exception {

        Product product = Product.builder()
                .name("Laptop")
                .description("A good laptop")
                .price(1000.0)
                .quantity(10)
                .userId("seller-1")
                .build();

        when(productService.createProduct(
                anyString(),
                anyString(),
                anyDouble(),
                anyInt(),
                any(),
                anyString(),
                anyString()
        )).thenReturn(product);

        mockMvc.perform(
                multipart("/api/products")
                        .file(
                                "images",
                                "test.jpg".getBytes()
                        )
                        .param("name", "Laptop")
                        .param("description", "A good laptop")
                        .param("price", "1000.0")
                        .param("quantity", "10")
                        .header("X-User-Id", "seller-1")
                        .header("X-Role", "SELLER")
        )
        .andExpect(status().isOk());
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct() throws Exception {

        Product product = Product.builder()
                .name("Updated Laptop")
                .description("Updated description")
                .price(1500.0)
                .quantity(20)
                .userId("seller-1")
                .build();

        when(productService.updateProduct(
                anyString(),
                anyString(),
                anyString(),
                anyDouble(),
                anyInt(),
                any(),
                any(),
                anyString(),
                anyString()
        )).thenReturn(product);

        mockMvc.perform(
                multipart("/api/products/p1")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .param("name", "Updated Laptop")
                        .param("description", "Updated description")
                        .param("price", "1500.0")
                        .param("quantity", "20")
                        .header("X-User-Id", "seller-1")
                        .header("X-Role", "SELLER")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Laptop"))
        .andExpect(jsonPath("$.price").value(1500.0));

        verify(productService).updateProduct(
                eq("p1"),
                eq("Updated Laptop"),
                eq("Updated description"),
                eq(1500.0),
                eq(20),
                isNull(),
                isNull(),
                eq("seller-1"),
                eq("SELLER")
        );
    }

    @Test
    void deleteProduct_shouldReturnSuccess() throws Exception {

        doNothing().when(productService)
                .deleteProduct("p1", "seller-1", "SELLER");

        mockMvc.perform(
                delete("/api/products/p1")
                        .header("X-User-Id", "seller-1")
                        .header("X-Role", "SELLER")
        )
        .andExpect(status().isOk());

        verify(productService)
                .deleteProduct("p1", "seller-1", "SELLER");
    }
}