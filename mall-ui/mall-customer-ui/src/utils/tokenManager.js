/**
 * JWT Token Manager
 * Handles secure storage, retrieval, and validation of JWT tokens
 * 
 * @author Likun.Fang
 * @version 1.0
 * @since 2025-07-25
 */

const TOKEN_KEY = 'jwt_token'
const REFRESH_TOKEN_KEY = 'jwt_refresh_token'
const USER_INFO_KEY = 'user_info'

export class TokenManager {
  /**
   * Store JWT token securely
   * @param {string} token - JWT access token
   * @param {string} refreshToken - JWT refresh token
   * @param {Object} userInfo - User information
   */
  static setTokens(token, refreshToken, userInfo) {
    if (token) {
      localStorage.setItem(TOKEN_KEY, token)
    }
    if (refreshToken) {
      localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken)
    }
    if (userInfo) {
      localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo))
    }
  }

  /**
   * Get JWT access token
   * @returns {string|null} JWT token or null if not found
   */
  static getToken() {
    return localStorage.getItem(TOKEN_KEY)
  }

  /**
   * Get JWT refresh token
   * @returns {string|null} Refresh token or null if not found
   */
  static getRefreshToken() {
    return localStorage.getItem(REFRESH_TOKEN_KEY)
  }

  /**
   * Get stored user information
   * @returns {Object|null} User info object or null if not found
   */
  static getUserInfo() {
    const userInfo = localStorage.getItem(USER_INFO_KEY)
    return userInfo ? JSON.parse(userInfo) : null
  }

  /**
   * Remove all tokens and user info (logout)
   */
  static clearTokens() {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(REFRESH_TOKEN_KEY)
    localStorage.removeItem(USER_INFO_KEY)
  }

  /**
   * Check if user is authenticated (has valid token)
   * @returns {boolean} True if authenticated, false otherwise
   */
  static isAuthenticated() {
    const token = this.getToken()
    if (!token) return false
    
    // Check if token is expired
    return !this.isTokenExpired(token)
  }

  /**
   * Check if token is expired
   * @param {string} token - JWT token to check
   * @returns {boolean} True if expired, false otherwise
   */
  static isTokenExpired(token) {
    if (!token) return true
    
    try {
      // Decode JWT payload (base64)
      const payload = JSON.parse(atob(token.split('.')[1]))
      const currentTime = Math.floor(Date.now() / 1000)
      
      // Check if token is expired (with 30 second buffer)
      return payload.exp < (currentTime + 30)
    } catch (error) {
      console.error('Error parsing token:', error)
      return true
    }
  }

  /**
   * Get token expiration time
   * @param {string} token - JWT token
   * @returns {Date|null} Expiration date or null if invalid
   */
  static getTokenExpiration(token) {
    if (!token) return null
    
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      return new Date(payload.exp * 1000)
    } catch (error) {
      console.error('Error parsing token:', error)
      return null
    }
  }

  /**
   * Get time until token expires (in minutes)
   * @param {string} token - JWT token
   * @returns {number} Minutes until expiration, or 0 if expired/invalid
   */
  static getTimeUntilExpiration(token) {
    const expiration = this.getTokenExpiration(token)
    if (!expiration) return 0
    
    const now = new Date()
    const diffMs = expiration.getTime() - now.getTime()
    return Math.max(0, Math.floor(diffMs / (1000 * 60)))
  }

  /**
   * Check if token needs refresh (expires within 5 minutes)
   * @param {string} token - JWT token
   * @returns {boolean} True if needs refresh, false otherwise
   */
  static needsRefresh(token) {
    return this.getTimeUntilExpiration(token) <= 5
  }

  /**
   * Get Authorization header value
   * @returns {string|null} Bearer token string or null
   */
  static getAuthHeader() {
    const token = this.getToken()
    return token ? `Bearer ${token}` : null
  }

  /**
   * Extract user ID from token
   * @param {string} token - JWT token
   * @returns {number|null} User ID or null if invalid
   */
  static getUserIdFromToken(token) {
    if (!token) return null
    
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      return payload.userId || payload.sub
    } catch (error) {
      console.error('Error parsing token:', error)
      return null
    }
  }

  /**
   * Extract username from token
   * @param {string} token - JWT token
   * @returns {string|null} Username or null if invalid
   */
  static getUsernameFromToken(token) {
    if (!token) return null
    
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      return payload.username
    } catch (error) {
      console.error('Error parsing token:', error)
      return null
    }
  }

  /**
   * Debug: Log token information
   * @param {string} token - JWT token
   */
  static debugToken(token) {
    if (!token) {
      console.log('No token provided')
      return
    }
    
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      console.log('Token payload:', payload)
      console.log('Expires at:', new Date(payload.exp * 1000))
      console.log('Time until expiration:', this.getTimeUntilExpiration(token), 'minutes')
      console.log('Needs refresh:', this.needsRefresh(token))
    } catch (error) {
      console.error('Error parsing token:', error)
    }
  }
}

export default TokenManager
