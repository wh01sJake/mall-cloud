package com.intelijake.mall.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Password utility class for secure password hashing and verification
 * Uses BCrypt for secure password storage
 * 
 * @author Jake
 * @since 2025-08-04
 */
public class PasswordUtil {
    
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * Hash a plain text password using BCrypt
     * 
     * @param plainPassword The plain text password to hash
     * @return The BCrypt hashed password
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        String hashedPassword = passwordEncoder.encode(plainPassword);
        System.out.println("🔐 Password hashed successfully (length: " + hashedPassword.length() + ")");
        return hashedPassword;
    }
    
    /**
     * Verify a plain text password against a BCrypt hashed password
     * 
     * @param plainPassword The plain text password to verify
     * @param hashedPassword The BCrypt hashed password to verify against
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            System.out.println("❌ Password verification failed: null password or hash");
            return false;
        }
        
        boolean matches = passwordEncoder.matches(plainPassword, hashedPassword);
        System.out.println("🔍 Password verification: " + (matches ? "✅ SUCCESS" : "❌ FAILED"));
        return matches;
    }
    
    /**
     * Check if a password is already BCrypt hashed
     * BCrypt hashes start with $2a$, $2b$, $2x$, or $2y$
     * 
     * @param password The password to check
     * @return true if the password appears to be BCrypt hashed
     */
    public static boolean isBCryptHashed(String password) {
        if (password == null || password.length() < 60) {
            return false;
        }
        
        return password.startsWith("$2a$") || 
               password.startsWith("$2b$") || 
               password.startsWith("$2x$") || 
               password.startsWith("$2y$");
    }
    
    /**
     * Migrate plain text password to BCrypt hash
     * This method is useful for migrating existing plain text passwords
     * 
     * @param plainPassword The plain text password
     * @return The BCrypt hashed password
     */
    public static String migratePlainTextPassword(String plainPassword) {
        if (isBCryptHashed(plainPassword)) {
            System.out.println("⚠️ Password is already hashed, no migration needed");
            return plainPassword;
        }
        
        System.out.println("🔄 Migrating plain text password to BCrypt hash");
        return hashPassword(plainPassword);
    }
}
