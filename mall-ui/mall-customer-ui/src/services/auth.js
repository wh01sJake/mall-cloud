import { apiClient } from './api'
import { TokenManager } from '@/utils/tokenManager'

export const authService = {
  /**
   * User login
   * @param {string} username
   * @param {string} password
   * @returns {Promise}
   */
  async login(username, password) {
    // Send JSON data instead of form data
    const loginData = {
      username,
      password
    }

    const response = await apiClient.post('/customer/login', loginData)

    // Handle JWT token response
    if (response.code === 0 && response.data) {
      const { token, refreshToken, user } = response.data

      // Store tokens and user info
      TokenManager.setTokens(token, refreshToken, user)
    }

    return response
  },

  /**
   * User registration
   * @param {Object} userData 
   * @returns {Promise}
   */
  async register(userData) {
    const { inviteCode, username, email, phone, password } = userData
    const formData = new URLSearchParams()
    formData.append('inviteCode', inviteCode)
    formData.append('username', username)
    formData.append('email', email)
    if (phone) formData.append('phone', phone)
    formData.append('password', password)

    return apiClient.post('/customer/register', formData, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    })
  },

  /**
   * User logout
   * @returns {Promise}
   */
  async logout() {
    try {
      // Call logout endpoint (optional for JWT)
      await apiClient.post('/customer/logout')
    } catch (error) {
      console.warn('Logout endpoint failed:', error)
    } finally {
      // Always clear tokens on logout
      TokenManager.clearTokens()
    }
  },

  /**
   * Get current session information
   * @returns {Promise}
   */
  async getSessionInfo() {
    return apiClient.get('/customer/session-info')
  },

  /**
   * Refresh JWT token
   * @returns {Promise}
   */
  async refreshToken() {
    const refreshToken = TokenManager.getRefreshToken()
    if (!refreshToken) {
      throw new Error('No refresh token available')
    }

    // Send JSON data instead of form data
    const tokenData = {
      refreshToken
    }

    const response = await apiClient.post('/customer/refresh-token', tokenData)

    // Handle new tokens
    if (response.code === 0 && response.data) {
      const { token, refreshToken: newRefreshToken } = response.data
      TokenManager.setTokens(token, newRefreshToken)
    }

    return response
  },

  /**
   * Check if user is authenticated
   * @returns {boolean}
   */
  isAuthenticated() {
    return TokenManager.isAuthenticated()
  },

  /**
   * Get current user info from token
   * @returns {Object|null}
   */
  getCurrentUser() {
    return TokenManager.getUserInfo()
  },

  /**
   * Get current user ID
   * @returns {number|null}
   */
  getCurrentUserId() {
    const token = TokenManager.getToken()
    return TokenManager.getUserIdFromToken(token)
  },
}
