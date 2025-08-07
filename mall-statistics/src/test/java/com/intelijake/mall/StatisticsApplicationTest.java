package com.intelijake.mall;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test class to verify StatisticsApplication starts correctly
 */
@SpringBootTest
@ActiveProfiles("local")
class StatisticsApplicationTest {

    @Test
    void contextLoads() {
        // This test will pass if the Spring Boot context loads successfully
    }
}