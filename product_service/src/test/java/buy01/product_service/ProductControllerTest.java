package buy01.product_service;

import buy01.product_service.controller.ProductController;
import buy01.product_service.model.Product;
import buy01.product_service.service.ProductService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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


    private Product product() {
        return Product.builder()
                .id("product1")
                .name("Laptop")
                .description("Gaming laptop")
                .price(1000.0)
                .quantity(5)
                .userId("user1")
                .imageUrls(Collections.emptyList())
                .build();
    }


    @Test
    void getAllProducts() throws Exception {

        when(productService.getAllProducts())
                .thenReturn(List.of(product()));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("product1"))
                .andExpect(jsonPath("$[0].name").value("Laptop"));

        verify(productService).getAllProducts();
    }


    @Test
    void getAllProductsEmpty() throws Exception {

        when(productService.getAllProducts())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(productService).getAllProducts();
    }


    @Test
    void getProductById() throws Exception {

        when(productService.getProduct("product1"))
                .thenReturn(product());

        mockMvc.perform(get("/api/products/product1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("product1"))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1000.0));

        verify(productService).getProduct("product1");
    }


    @Test
    void createProduct() throws Exception {

        when(productService.createProduct(
                eq("Laptop"),
                eq("Gaming laptop"),
                eq(1000.0),
                eq(5),
                any(),
                eq("user1"),
                eq("SELLER")
        )).thenReturn(product());

        mockMvc.perform(
                multipart("/api/products")
                        .param("name", "Laptop")
                        .param("description", "Gaming laptop")
                        .param("price", "1000.0")
                        .param("quantity", "5")
                        .header("X-User-Id", "user1")
                        .header("X-Role", "SELLER")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("product1"))
        .andExpect(jsonPath("$.name").value("Laptop"));

        verify(productService).createProduct(
                eq("Laptop"),
                eq("Gaming laptop"),
                eq(1000.0),
                eq(5),
                any(),
                eq("user1"),
                eq("SELLER")
        );
    }


    @Test
    void updateProduct() throws Exception {

        when(productService.updateProduct(
                eq("product1"),
                eq("Laptop Updated"),
                eq("New description"),
                eq(1200.0),
                eq(10),
                any(),
                any(),
                eq("user1"),
                eq("SELLER")
        )).thenReturn(product());

        mockMvc.perform(
                multipart("/api/products/product1")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .param("name", "Laptop Updated")
                        .param("description", "New description")
                        .param("price", "1200.0")
                        .param("quantity", "10")
                        .header("X-User-Id", "user1")
                        .header("X-Role", "SELLER")
        )
        .andExpect(status().isOk());

        verify(productService).updateProduct(
                eq("product1"),
                eq("Laptop Updated"),
                eq("New description"),
                eq(1200.0),
                eq(10),
                any(),
                any(),
                eq("user1"),
                eq("SELLER")
        );
    }


    @Test
    void deleteProduct() throws Exception {

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
}