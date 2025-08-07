package com.intelijake.mall.common.interceptor;

import com.intelijake.mall.common.context.UserContext;
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

    // Header names that the gateway will use to forward user information
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USERNAME_HEADER = "X-Username";
    private static final String USER_EMAIL_HEADER = "X-User-Email";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Extract user information from headers set by the gateway
        String userIdStr = request.getHeader(USER_ID_HEADER);
        String username = request.getHeader(USERNAME_HEADER);
        String email = request.getHeader(USER_EMAIL_HEADER);

        // Set user context if user information is available
        if (userIdStr != null && username != null) {
            try {
                Integer userId = Integer.valueOf(userIdStr);
                UserContext.setCurrentUser(userId, username, email);
            } catch (NumberFormatException e) {
                // Log the error but don't fail the request
                System.err.println("Invalid user ID in header: " + userIdStr);
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Clear user context after request processing to prevent memory leaks
        UserContext.clear();
    }
}
