package com.intelijake.mall.common.interceptor;

import com.intelijake.mall.common.context.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * User Context Interceptor
 * Extracts user information from gateway headers and sets it in UserContext
 * 
 * @author Likun.Fang
 * @version 1.0
 * @since 2025-07-25
 */
public class UserContextInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(UserContextInterceptor.class);

    // Header names that the gateway will use to forward user information
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USERNAME_HEADER = "X-Username";
    private static final String USER_EMAIL_HEADER = "X-User-Email";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        logger.info("🔍 UserContext: Processing {} {} in Order Service", method, requestPath);

        // Extract user information from headers set by the gateway
        String userIdStr = request.getHeader(USER_ID_HEADER);
        String username = request.getHeader(USERNAME_HEADER);
        String email = request.getHeader(USER_EMAIL_HEADER);

        logger.info("📋 UserContext: Headers received - X-User-Id: {}, X-Username: {}, X-User-Email: {}",
                   userIdStr, username, email);

        // Set user context if user information is available
        if (userIdStr != null && username != null) {
            try {
                Integer userId = Integer.valueOf(userIdStr);
                UserContext.setCurrentUser(userId, username, email);
                logger.info("✅ UserContext: Successfully set user context - ID: {}, Username: {}, Email: {}",
                           userId, username, email);
            } catch (NumberFormatException e) {
                logger.error("❌ UserContext: Invalid user ID in header: {} - {}", userIdStr, e.getMessage());
            }
        } else {
            logger.warn("⚠️ UserContext: Missing required headers - UserID: {}, Username: {}",
                       userIdStr != null, username != null);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Clear user context after request processing to prevent memory leaks
        UserContext.clear();
    }
}
