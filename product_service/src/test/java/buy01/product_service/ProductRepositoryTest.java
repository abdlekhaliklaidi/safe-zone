package buy01.product_service;


import buy01.product_service.model.Product;
import buy01.product_service.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;


    @Test
    void findByUserId() {

        Product product = Product.builder()
                .id("p1")
                .name("Laptop")
                .description("Gaming laptop")
                .price(1000.0)
                .quantity(5)
                .userId("user1")
                .build();

        productRepository.save(product);

        List<Product> result =
                productRepository.findByUserId("user1");

        assertEquals(1, result.size());
        assertEquals("p1", result.get(0).getId());
        assertEquals("user1", result.get(0).getUserId());
    }


    @Test
    void deleteByUserId() {

        Product product = Product.builder()
                .id("p2")
                .name("Phone")
                .description("Smart phone")
                .price(500.0)
                .quantity(2)
                .userId("user2")
                .build();

        productRepository.save(product);

        productRepository.deleteByUserId("user2");

        assertTrue(
                productRepository.findByUserId("user2").isEmpty()
        );
    }
}