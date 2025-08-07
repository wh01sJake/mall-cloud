package com.intelijake.mall.gateway.filter;


import com.intelijake.mall.common.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * JWT Authentication Gateway Filter Factory
 * Validates JWT tokens and adds user context headers for downstream services
 * 
 * @author Likun.Fang
 * @version 1.0
 * @since 2025-07-25
 */
@Component
public class JwtAuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilterFactory.Config> {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationGatewayFilterFactory.class);

    public JwtAuthenticationGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            String method = request.getMethod().toString();

            logger.info("🔐 JWT Filter: Processing {} {}", method, path);

            // Skip authentication for public endpoints
            if (isPublicEndpoint(path, config)) {
                logger.info("✅ JWT Filter: Public endpoint, skipping authentication for {}", path);
                return chain.filter(exchange);
            }

            // Extract JWT token from Authorization header
            String authHeader = request.getHeaders().getFirst("Authorization");
            logger.info("🔍 JWT Filter: Authorization header present: {}", authHeader != null);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("❌ JWT Filter: Missing or invalid Authorization header for {} {}", method, path);
                return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            try {
                String token = authHeader.substring(7);
                logger.info("🔑 JWT Filter: Extracted token (length: {})", token.length());

                // Validate JWT token using JwtUtil
                JwtUtil.validateToken(token);
                logger.info("✅ JWT Filter: Token validation successful");

                // Check if token is expired
                if (JwtUtil.isTokenExpired(token)) {
                    logger.warn("⏰ JWT Filter: Token expired for {} {}", method, path);
                    return onError(exchange, "Token expired", HttpStatus.UNAUTHORIZED);
                }

                // Extract user information from token
                Map<String, Object> userInfo = JwtUtil.extractUserInfo(token);
                String userId = userInfo.get("userId") != null ? userInfo.get("userId").toString() : null;
                String username = (String) userInfo.get("username");
                String email = userInfo.get("email") != null ? userInfo.get("email").toString() : "";

                logger.info("👤 JWT Filter: Extracted user info - ID: {}, Username: {}, Email: {}", userId, username, email);

                // Create modified request with user context headers
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-Auth-Token", token)  // Keep original token for backward compatibility
                        .header("X-User-Id", userId)
                        .header("X-Username", username)
                        .header("X-User-Email", email)
                        .build();

                logger.info("🚀 JWT Filter: Forwarding request with user context headers to {}", path);

                // Continue with the modified request
                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                logger.error("💥 JWT Filter: Authentication error for {} {}: {}", method, path, e.getMessage(), e);

                // Check if it's a JWT-related exception
                if (e.getMessage() != null && (e.getMessage().contains("JWT") || e.getMessage().contains("token"))) {
                    logger.warn("🚫 JWT Filter: Invalid JWT token for {} {}", method, path);
                    return onError(exchange, "Invalid JWT token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
                }
                logger.error("🔥 JWT Filter: Internal server error for {} {}", method, path);
                return onError(exchange, "Authentication error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        };
    }

    /**
     * Check if the endpoint is public and doesn't require authentication
     */
    private boolean isPublicEndpoint(String path, Config config) {
        // Default public endpoints
        boolean isDefaultPublic = path.startsWith("/api/customer/login") ||
               path.startsWith("/api/customer/register") ||
               path.startsWith("/api/customer/refresh-token") ||
               path.startsWith("/customer/login") ||
               path.startsWith("/customer/register") ||
               path.startsWith("/customer/refresh-token") ||
               path.startsWith("/api/admin/login") ||
               path.startsWith("/admin/login") ||
               path.startsWith("/actuator/") ||
               path.equals("/") ||
               path.startsWith("/static/") ||
               path.startsWith("/public/");

        if (isDefaultPublic) {
            return true;
        }

        // Check configured exclude paths
        if (config.getExcludePaths() != null && !config.getExcludePaths().isEmpty()) {
            for (String excludePath : config.getExcludePaths()) {
                if (path.endsWith(excludePath) || path.contains(excludePath)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Handle authentication errors
     */
    private Mono<Void> onError(org.springframework.web.server.ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        
        String body = String.format("{\"error\": \"%s\", \"status\": %d}", message, status.value());
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * Configuration class for the filter
     */
    public static class Config {
        private java.util.List<String> excludePaths = new java.util.ArrayList<>();

        public java.util.List<String> getExcludePaths() {
            return excludePaths;
        }

        public void setExcludePaths(java.util.List<String> excludePaths) {
            this.excludePaths = excludePaths;
        }

        public void setExcludePaths(String excludePaths) {
            if (excludePaths != null && !excludePaths.trim().isEmpty()) {
                this.excludePaths = java.util.Arrays.asList(excludePaths.split(","));
            }
        }
    }
}
