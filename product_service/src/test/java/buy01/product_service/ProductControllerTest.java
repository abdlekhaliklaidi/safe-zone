package buy01.product_service;


import buy01.product_service.model.Product;
import buy01.product_service.service.ProductService;
import buy01.product_service.controller.ProductController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
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


    // =====================================================
    // GET ALL PRODUCTS
    // =====================================================

    @Test
    void shouldGetAllProducts() throws Exception {

        Product product = Product.builder()
                .id("product1")
                .name("Laptop")
                .description("Gaming laptop 16GB RAM")
                .price(1000.0)
                .quantity(5)
                .userId("user1")
                .imageUrls(Collections.emptyList())
                .build();

        when(productService.getAllProducts())
                .thenReturn(List.of(product));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("product1"))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].description")
                        .value("Gaming laptop 16GB RAM"))
                .andExpect(jsonPath("$[0].price").value(1000.0))
                .andExpect(jsonPath("$[0].quantity").value(5));

        verify(productService).getAllProducts();
    }


    @Test
    void shouldReturnEmptyListWhenNoProducts() throws Exception {

        when(productService.getAllProducts())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(productService).getAllProducts();
    }


    // =====================================================
    // GET PRODUCT BY ID
    // =====================================================

    @Test
    void shouldGetProductById() throws Exception {

        Product product = Product.builder()
                .id("product1")
                .name("Laptop")
                .description("Gaming laptop 16GB RAM")
                .price(1000.0)
                .quantity(5)
                .userId("user1")
                .imageUrls(Collections.emptyList())
                .build();

        when(productService.getProduct("product1"))
                .thenReturn(product);

        mockMvc.perform(get("/api/products/product1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("product1"))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1000.0))
                .andExpect(jsonPath("$.quantity").value(5));

        verify(productService).getProduct("product1");
    }


    // =====================================================
    // CREATE PRODUCT
    // =====================================================

    @Test
    void shouldCreateProduct() throws Exception {

        Product product = Product.builder()
                .id("product1")
                .name("Laptop")
                .description("Gaming laptop 16GB RAM")
                .price(1000.0)
                .quantity(5)
                .userId("user1")
                .imageUrls(Collections.emptyList())
                .build();

        when(productService.createProduct(
                eq("Laptop"),
                eq("Gaming laptop 16GB RAM"),
                eq(1000.0),
                eq(5),
                any(),
                eq("user1"),
                eq("SELLER")
        )).thenReturn(product);

        mockMvc.perform(
                        multipart("/api/products")
                                .file("images", new byte[0])
                                .param("name", "Laptop")
                                .param(
                                        "description",
                                        "Gaming laptop 16GB RAM"
                                )
                                .param("price", "1000.0")
                                .param("quantity", "5")
                                .header("X-User-Id", "user1")
                                .header("X-Role", "SELLER")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value("product1"))
                .andExpect(jsonPath("$.name")
                        .value("Laptop"))
                .andExpect(jsonPath("$.price")
                        .value(1000.0))
                .andExpect(jsonPath("$.quantity")
                        .value(5));

        verify(productService).createProduct(
                eq("Laptop"),
                eq("Gaming laptop 16GB RAM"),
                eq(1000.0),
                eq(5),
                any(),
                eq("user1"),
                eq("SELLER")
        );
    }


    @Test
    void shouldRejectCreateWithoutUserId() throws Exception {

        mockMvc.perform(
                        multipart("/api/products")
                                .param("name", "Laptop")
                                .param(
                                        "description",
                                        "Gaming laptop 16GB RAM"
                                )
                                .param("price", "1000.0")
                                .param("quantity", "5")
                                .header("X-Role", "SELLER")
                )
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(
                anyString(),
                anyString(),
                anyDouble(),
                anyInt(),
                any(),
                anyString(),
                anyString()
        );
    }


    @Test
    void shouldRejectCreateWithoutRole() throws Exception {

        mockMvc.perform(
                        multipart("/api/products")
                                .param("name", "Laptop")
                                .param(
                                        "description",
                                        "Gaming laptop 16GB RAM"
                                )
                                .param("price", "1000.0")
                                .param("quantity", "5")
                                .header("X-User-Id", "user1")
                )
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(
                anyString(),
                anyString(),
                anyDouble(),
                anyInt(),
                any(),
                anyString(),
                anyString()
        );
    }


    // =====================================================
    // UPDATE PRODUCT
    // =====================================================

    @Test
    void shouldUpdateProduct() throws Exception {

        Product product = Product.builder()
                .id("product1")
                .name("Updated Laptop")
                .description("Updated gaming laptop description")
                .price(1200.0)
                .quantity(10)
                .userId("user1")
                .imageUrls(Collections.emptyList())
                .build();

        when(productService.updateProduct(
                eq("product1"),
                eq("Updated Laptop"),
                eq("Updated gaming laptop description"),
                eq(1200.0),
                eq(10),
                any(),
                any(),
                eq("user1"),
                eq("SELLER")
        )).thenReturn(product);

        mockMvc.perform(
                        multipart("/api/products/product1")
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                                .param("name", "Updated Laptop")
                                .param(
                                        "description",
                                        "Updated gaming laptop description"
                                )
                                .param("price", "1200.0")
                                .param("quantity", "10")
                                .header("X-User-Id", "user1")
                                .header("X-Role", "SELLER")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value("product1"))
                .andExpect(jsonPath("$.name")
                        .value("Updated Laptop"))
                .andExpect(jsonPath("$.price")
                        .value(1200.0))
                .andExpect(jsonPath("$.quantity")
                        .value(10));

        verify(productService).updateProduct(
                eq("product1"),
                eq("Updated Laptop"),
                eq("Updated gaming laptop description"),
                eq(1200.0),
                eq(10),
                any(),
                any(),
                eq("user1"),
                eq("SELLER")
        );
    }


    // =====================================================
    // DELETE PRODUCT
    // =====================================================

    @Test
    void shouldDeleteProduct() throws Exception {

        doNothing().when(productService)
                .deleteProduct(
                        "product1",
                        "user1",
                        "SELLER"
                );

        mockMvc.perform(
                        delete("/api/products/product1")
                                .header("X-User-Id", "user1")
                                .header("X-Role", "SELLER")
                )
                .andExpect(status().isOk());

        verify(productService).deleteProduct(
                "product1",
                "user1",
                "SELLER"
        );
    }


    @Test
    void shouldRejectDeleteWithoutUserId() throws Exception {

        mockMvc.perform(
                        delete("/api/products/product1")
                                .header("X-Role", "SELLER")
                )
                .andExpect(status().isBadRequest());

        verify(productService, never())
                .deleteProduct(
                        anyString(),
                        anyString(),
                        anyString()
                );
    }


    @Test
    void shouldRejectDeleteWithoutRole() throws Exception {

        mockMvc.perform(
                        delete("/api/products/product1")
                                .header("X-User-Id", "user1")
                )
                .andExpect(status().isBadRequest());

        verify(productService, never())
                .deleteProduct(
                        anyString(),
                        anyString(),
                        anyString()
                );
    }
}
