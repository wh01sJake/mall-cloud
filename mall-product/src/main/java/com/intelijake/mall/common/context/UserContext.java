package com.intelijake.mall.common.context;

/**
 * User Context Management using ThreadLocal
 * Provides thread-safe storage for current user information
 *
 * @author Likun.Fang
 * @version 1.0
 * @since 2025-07-25
 */
public class UserContext {

    private static final ThreadLocal<Integer> currentUserId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentUsername = new ThreadLocal<>();
    private static final ThreadLocal<String> currentUserEmail = new ThreadLocal<>();

    /**
     * Set current user information from JWT token data
     * @param userId User ID
     * @param username Username
     */
    public static void setCurrentUser(Integer userId, String username) {
        currentUserId.set(userId);
        currentUsername.set(username);
    }

    /**
     * Set current user information with email
     * @param userId User ID
     * @param username Username
     * @param email User email
     */
    public static void setCurrentUser(Integer userId, String username, String email) {
        currentUserId.set(userId);
        currentUsername.set(username);
        currentUserEmail.set(email);
    }

    /**
     * Get the current user ID for the current thread
     * @return User ID or null if not set
     */
    public static Integer getCurrentUserId() {
        return currentUserId.get();
    }

    /**
     * Get the current username for the current thread
     * @return Username or null if not set
     */
    public static String getCurrentUsername() {
        return currentUsername.get();
    }

    /**
     * Get the current user email for the current thread
     * @return Email or null if not set
     */
    public static String getCurrentUserEmail() {
        return currentUserEmail.get();
    }

    /**
     * Check if a user is currently set in the context
     * @return true if user is set, false otherwise
     */
    public static boolean hasCurrentUser() {
        return currentUserId.get() != null;
    }

    /**
     * Clear the current user context for the current thread
     * This should be called after request processing to prevent memory leaks
     */
    public static void clear() {
        currentUserId.remove();
        currentUsername.remove();
        currentUserEmail.remove();
    }

    /**
     * Get current user ID with validation
     * @return User ID
     * @throws RuntimeException if no user is set in context
     */
    public static Integer requireCurrentUserId() {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("No user found in current context. Please ensure user is authenticated.");
        }
        return userId;
    }

    /**
     * Get current username with validation
     * @return Username
     * @throws RuntimeException if no user is set in context
     */
    public static String requireCurrentUsername() {
        String username = getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("No user found in current context. Please ensure user is authenticated.");
        }
        return username;
    }
}
