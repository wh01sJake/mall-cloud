package com.intelijake.mall.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Utility Class for Authentication
 * Enhanced version with Customer-specific functionality
 *
 * @author Likun.Fang
 * @version 2.0
 * @since 2025-07-25
 */
public class JwtUtil {

    // JWT secret key - should be externalized to configuration in production
    private static final String SECRET_KEY = "mall-cloud-jwt-secret-key-2025";

    // Token expiration time - 1 hour (3600 seconds)
    private static final long EXPIRATION_TIME = 3600 * 1000;

    // Refresh token expiration time - 7 days
    private static final long REFRESH_EXPIRATION_TIME = 7 * 24 * 3600 * 1000;

    // JWT issuer
    private static final String ISSUER = "mall-system";

    // Algorithm for signing
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY);

    /**
     * Generate JWT token for user
     * @param userId User ID
     * @param username Username
     * @param email Email
     * @param status User status
     * @return JWT token string
     */
    public static String generateToken(Integer userId, String username, String email, Integer status) {
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(userId.toString())
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withClaim("email", email != null ? email : "")
                .withClaim("status", status != null ? status : 1)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(ALGORITHM);
    }

    /**
     * Generate refresh token for user
     * @param userId User ID
     * @return Refresh token string
     */
    public static String generateRefreshToken(Integer userId) {
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(userId.toString())
                .withClaim("userId", userId)
                .withClaim("type", "refresh")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .sign(ALGORITHM);
    }

    /**
     * Validate JWT token and return decoded JWT
     * @param token JWT token string
     * @return DecodedJWT object
     * @throws JWTVerificationException if token is invalid
     */
    public static DecodedJWT validateToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(ALGORITHM)
                .withIssuer(ISSUER)
                .build();
        return verifier.verify(token);
    }

    /**
     * Check if token is expired
     * @param token JWT token string
     * @return true if expired, false otherwise
     */
    public static boolean isTokenExpired(String token) {
        try {
            DecodedJWT decodedJWT = validateToken(token);
            return decodedJWT.getExpiresAt().before(new Date());
        } catch (JWTVerificationException e) {
            return true;
        }
    }

    /**
     * Extract user information from JWT token
     * @param token JWT token string
     * @return Map with user information
     */
    public static Map<String, Object> extractUserInfo(String token) {
        try {
            DecodedJWT decodedJWT = validateToken(token);
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", decodedJWT.getClaim("userId").asInt());
            userInfo.put("username", decodedJWT.getClaim("username").asString());
            userInfo.put("email", decodedJWT.getClaim("email").asString());
            userInfo.put("status", decodedJWT.getClaim("status").asInt());
            userInfo.put("type", decodedJWT.getClaim("type").asString());
            return userInfo;
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    /**
     * Extract user ID from JWT token
     * @param token JWT token string
     * @return User ID
     */
    public static Integer extractUserId(String token) {
        try {
            DecodedJWT decodedJWT = validateToken(token);
            return decodedJWT.getClaim("userId").asInt();
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    /**
     * Extract username from JWT token
     * @param token JWT token string
     * @return Username
     */
    public static String extractUsername(String token) {
        try {
            DecodedJWT decodedJWT = validateToken(token);
            return decodedJWT.getClaim("username").asString();
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    // Legacy methods for backward compatibility

    /**
     * Legacy method - Create token from map (for backward compatibility)
     * @param map Claims map
     * @return JWT token string
     * @deprecated Use generateToken(Customer) instead
     */
    @Deprecated
    public static String createToken(Map<String, Object> map) {
        return JWT.create()
                .withClaim("claims", map)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(ALGORITHM);
    }

    /**
     * Legacy method - Parse token to map (for backward compatibility)
     * @param token JWT token string
     * @return Claims map
     * @deprecated Use validateToken(String) and extract methods instead
     */
    @Deprecated
    public static Map<String, Object> parseToken(String token) {
        return JWT.require(ALGORITHM)
                .build()
                .verify(token)
                .getClaim("claims")
                .asMap();
    }
}