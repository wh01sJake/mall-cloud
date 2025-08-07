package com.intelijake.mall.gateway;

import com.intelijake.mall.common.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for JWT Authentication functionality
 * Tests the complete JWT flow including token generation, validation, and user info extraction
 * 
 * @author Likun.Fang
 * @version 1.0
 * @since 2025-07-27
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.cloud.nacos.discovery.enabled=false",
    "spring.cloud.nacos.config.enabled=false"
})
public class JwtAuthenticationIntegrationTest {

    @Test
    public void testJwtTokenGeneration() {
        // Test data
        Integer userId = 123;
        String username = "testuser";
        String email = "test@example.com";
        Integer status = 1;

        // Generate JWT token
        String token = JwtUtil.generateToken(userId, username, email, status);
        
        // Verify token is not null and not empty
        assertNotNull(token, "JWT token should not be null");
        assertFalse(token.isEmpty(), "JWT token should not be empty");
        assertTrue(token.startsWith("eyJ"), "JWT token should start with 'eyJ' (Base64 encoded header)");
    }

    @Test
    public void testJwtTokenValidation() {
        // Test data
        Integer userId = 456;
        String username = "validuser";
        String email = "valid@example.com";
        Integer status = 1;

        // Generate JWT token
        String token = JwtUtil.generateToken(userId, username, email, status);
        
        // Validate token - should not throw exception
        assertDoesNotThrow(() -> {
            JwtUtil.validateToken(token);
        }, "Valid JWT token should pass validation");
    }

    @Test
    public void testJwtUserInfoExtraction() {
        // Test data
        Integer userId = 789;
        String username = "extractuser";
        String email = "extract@example.com";
        Integer status = 1;

        // Generate JWT token
        String token = JwtUtil.generateToken(userId, username, email, status);
        
        // Extract user information
        Map<String, Object> userInfo = JwtUtil.extractUserInfo(token);
        
        // Verify extracted information
        assertNotNull(userInfo, "User info should not be null");
        assertEquals(userId, userInfo.get("userId"), "User ID should match");
        assertEquals(username, userInfo.get("username"), "Username should match");
        assertEquals(email, userInfo.get("email"), "Email should match");
        assertEquals(status, userInfo.get("status"), "Status should match");
    }

    @Test
    public void testJwtRefreshToken() {
        // Test data
        Integer userId = 999;

        // Generate refresh token
        String refreshToken = JwtUtil.generateRefreshToken(userId);
        
        // Verify refresh token is not null and not empty
        assertNotNull(refreshToken, "Refresh token should not be null");
        assertFalse(refreshToken.isEmpty(), "Refresh token should not be empty");
        
        // Extract user information from refresh token
        Map<String, Object> userInfo = JwtUtil.extractUserInfo(refreshToken);
        assertEquals(userId, userInfo.get("userId"), "User ID should match in refresh token");
        assertEquals("refresh", userInfo.get("type"), "Token type should be 'refresh'");
    }

    @Test
    public void testJwtTokenExpiration() {
        // Test data
        Integer userId = 111;
        String username = "expireduser";
        String email = "expired@example.com";
        Integer status = 1;

        // Generate JWT token
        String token = JwtUtil.generateToken(userId, username, email, status);
        
        // Check if token is expired (should be false for a newly generated token)
        assertFalse(JwtUtil.isTokenExpired(token), "Newly generated token should not be expired");
    }

    @Test
    public void testInvalidJwtToken() {
        // Test with invalid token
        String invalidToken = "invalid.jwt.token";
        
        // Should throw exception for invalid token
        assertThrows(Exception.class, () -> {
            JwtUtil.validateToken(invalidToken);
        }, "Invalid JWT token should throw exception");
    }

    @Test
    public void testJwtWithNullValues() {
        // Test with null email
        Integer userId = 222;
        String username = "nulluser";
        String email = null;
        Integer status = null;

        // Generate JWT token with null values
        String token = JwtUtil.generateToken(userId, username, email, status);
        
        // Extract user information
        Map<String, Object> userInfo = JwtUtil.extractUserInfo(token);
        
        // Verify handling of null values
        assertEquals(userId, userInfo.get("userId"), "User ID should match");
        assertEquals(username, userInfo.get("username"), "Username should match");
        assertEquals("", userInfo.get("email"), "Null email should be converted to empty string");
        assertEquals(1, userInfo.get("status"), "Null status should default to 1");
    }
}
