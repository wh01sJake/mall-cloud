import { defineStore } from 'pinia'
import { authService } from '@/services/auth'
import { TokenManager } from '@/utils/tokenManager'
import { ElMessage } from 'element-plus'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    isLoggedIn: false,
    loading: false,
  }),

  getters: {
    currentUser: (state) => state.user,
    isAuthenticated: (state) => state.isLoggedIn && TokenManager.isAuthenticated(),
  },

  actions: {
    /**
     * Login user
     * @param {Object} credentials 
     * @returns {Promise<boolean>}
     */
    async login(credentials) {
      this.loading = true
      try {
        const response = await authService.login(credentials.username, credentials.password)

        if (response && response.code === 0 && response.data) {
          // JWT login successful - tokens are already stored by authService
          const { user } = response.data
          this.user = user
          this.isLoggedIn = true
          ElMessage.success('Login successful!')
          return true
        } else {
          ElMessage.error(response?.msg || 'Login failed')
          return false
        }
      } catch (error) {
        console.error('Login error:', error)
        ElMessage.error('Login failed. Please try again.')
        return false
      } finally {
        this.loading = false
      }
    },

    /**
     * Register new user
     * @param {Object} userData 
     * @returns {Promise<boolean>}
     */
    async register(userData) {
      this.loading = true
      try {
        const response = await authService.register(userData)

        if (response && response.code === 0) {
          ElMessage.success('Registration successful! Please login with your credentials.')
          return true
        } else {
          ElMessage.error(response?.msg || 'Registration failed')
          return false
        }
      } catch (error) {
        console.error('Registration error:', error)
        ElMessage.error('Registration failed. Please try again.')
        return false
      } finally {
        this.loading = false
      }
    },

    /**
     * Logout user
     */
    async logout() {
      try {
        await authService.logout()
        this.user = null
        this.isLoggedIn = false
        ElMessage.success('Logged out successfully')
      } catch (error) {
        console.error('Logout error:', error)
        // Clear local state even if API call fails
        this.user = null
        this.isLoggedIn = false
      }
    },

    /**
     * Check current session/token validity
     */
    async checkSession() {
      try {
        // First check if we have a valid token
        if (!TokenManager.isAuthenticated()) {
          this.clearAuth()
          return false
        }

        // Try to get user info from stored data first
        const storedUser = TokenManager.getUserInfo()
        if (storedUser) {
          this.user = storedUser
          this.isLoggedIn = true
          return true
        }

        // If no stored user info, get it from the server
        const response = await authService.getSessionInfo()

        if (response && response.code === 0 && response.data) {
          this.user = {
            id: response.data.userId,
            username: response.data.username,
            email: response.data.email,
            status: response.data.status,
          }
          this.isLoggedIn = true

          // Update stored user info
          TokenManager.setTokens(null, null, this.user)
          return true
        }

        this.clearAuth()
        return false
      } catch (error) {
        console.error('Session check error:', error)
        this.clearAuth()
        return false
      }
    },

    /**
     * Initialize auth state from stored tokens
     */
    initializeAuth() {
      if (TokenManager.isAuthenticated()) {
        const storedUser = TokenManager.getUserInfo()
        if (storedUser) {
          this.user = storedUser
          this.isLoggedIn = true
          return true
        }
      }
      return false
    },

    /**
     * Clear auth state
     */
    clearAuth() {
      this.user = null
      this.isLoggedIn = false
      TokenManager.clearTokens()
    },
  },
})
