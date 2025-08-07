package com.intelijake.mall.product.config;


import org.springframework.context.annotation.Configuration;

/**
 * Product Service Configuration
 * Enables Feign clients for inter-service communication
 * 
 * @author Jake
 * @since 2025-07-22
 */
@Configuration

public class ProductServiceConfig {
    
    // Configuration for product service
    // Feign clients will be automatically discovered and configured
    // Circuit breakers and retry mechanisms are configured in application.yml
}
