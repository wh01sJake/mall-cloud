package com.intelijake.mall;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test class to verify ProductApplication starts correctly
 * Tests the configuration and dependency injection
 */
@SpringBootTest
@ActiveProfiles("local")
class ProductApplicationTest {

    @Test
    void contextLoads() {
        // This test will pass if the Spring Boot context loads successfully
        // It validates that all configurations are correct and dependencies can be injected
    }
}
