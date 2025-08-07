package com.intelijake.mall.order.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class to verify endpoint mapping consistency
 * Ensures all endpoints follow the correct prefix patterns
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("local")
class EndpointMappingTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testOrderEndpointMapping() throws Exception {
        // Test that order endpoints are accessible under /order/ prefix
        // This test verifies the mapping exists, not the business logic
        mockMvc.perform(get("/order/list"))
                .andExpect(status().isOk());
    }

    @Test
    void testOrderItemEndpointMapping() throws Exception {
        // Test that order item endpoints are accessible under /order/item/ prefix
        // This verifies our consistency update from /order-item to /order/item
        mockMvc.perform(get("/order/item/list"))
                .andExpect(status().isOk());
    }
}
