import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { TokenManager } from '@/utils/tokenManager'

// Environment-specific configuration
const getBaseURL = () => {
  // In development, use the proxy configuration
  if (import.meta.env.DEV) {
    return '/api'
  }

  // In production, use environment variable or default to deployed gateway URL
  return import.meta.env.VITE_API_BASE_URL || 'https://vapemall-gateway-989f7ea980e6.herokuapp.com/api'
}

// Create axios instance
const api = axios.create({
  baseURL: getBaseURL(),
  timeout: 10000,
  withCredentials: false, // Changed to false for JWT-based auth
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor
api.interceptors.request.use(
  async (config) => {
    // Add JWT token to requests
    const token = TokenManager.getToken()

    if (token) {
      // Check if token needs refresh before making request
      if (TokenManager.needsRefresh(token)) {
        try {
          await refreshTokenIfNeeded()
          // Get the new token after refresh
          const newToken = TokenManager.getToken()
          if (newToken) {
            config.headers.Authorization = `Bearer ${newToken}`
          }
        } catch (error) {
          console.error('Token refresh failed:', error)
          // Continue with existing token
          config.headers.Authorization = `Bearer ${token}`
        }
      } else {
        config.headers.Authorization = `Bearer ${token}`
      }
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
api.interceptors.response.use(
  (response) => {
    // Return the response data directly (like admin-ui)
    return response.data
  },
  (error) => {
    // Handle network errors (no response from server)
    if (!error.response) {
      if (error.code === 'ECONNABORTED') {
        ElMessage.error('Request timeout. Please check your connection.')
      } else if (error.code === 'ERR_NETWORK') {
        ElMessage.error('Network error. Please check if the gateway is running.')
      } else {
        ElMessage.error('Unable to connect to server. Please try again later.')
      }
      return Promise.reject(error)
    }

    const status = error.response?.status
    const message = error.response?.data?.message || error.message

    switch (status) {
      case 401:
        ElMessage.error('Please login first')
        // Clear tokens on authentication failure
        TokenManager.clearTokens()
        // Only redirect to login for auth-required endpoints
        if (!window.location.pathname.includes('/login')) {
          router.push('/login')
        }
        break
      case 403:
        ElMessage.error('Access denied')
        break
      case 404:
        // Don't show error for 404s, let components handle gracefully
        console.warn('Resource not found:', error.config?.url)
        break
      case 500:
        ElMessage.error('Internal server error. Please try again later.')
        break
      case 502:
        ElMessage.error('Bad gateway. Service may be temporarily unavailable.')
        break
      case 503:
        ElMessage.error('Service temporarily unavailable. Please try again later.')
        break
      case 504:
        ElMessage.error('Gateway timeout. Please check your connection and try again.')
        break
      default:
        // Use server message if available, otherwise generic message
        if (status !== 404) {
          ElMessage.error(message || 'Something went wrong...')
        }
    }

    return Promise.reject(error)
  }
)

// Token refresh function
let isRefreshing = false
let refreshPromise = null

async function refreshTokenIfNeeded() {
  // Prevent multiple simultaneous refresh attempts
  if (isRefreshing) {
    return refreshPromise
  }

  const refreshToken = TokenManager.getRefreshToken()
  if (!refreshToken) {
    throw new Error('No refresh token available')
  }

  isRefreshing = true

  refreshPromise = axios.post(`${getBaseURL()}/customer/refresh-token`,
    { refreshToken },
    {
      headers: {
        'Content-Type': 'application/json',
      },
    }
  ).then(response => {
    const data = response.data
    if (data.code === 0 && data.data) {
      // Store new tokens
      TokenManager.setTokens(data.data.token, data.data.refreshToken)
      return data.data.token
    } else {
      throw new Error('Token refresh failed')
    }
  }).catch(error => {
    console.error('Token refresh error:', error)
    // Clear tokens on refresh failure
    TokenManager.clearTokens()
    // Redirect to login
    if (!window.location.pathname.includes('/login')) {
      router.push('/login')
    }
    throw error
  }).finally(() => {
    isRefreshing = false
    refreshPromise = null
  })

  return refreshPromise
}

// API methods
export const apiClient = {
  get: (url, config) => api.get(url, config),
  post: (url, data, config) => api.post(url, data, config),
  put: (url, data, config) => api.put(url, data, config),
  delete: (url, config) => api.delete(url, config),
  patch: (url, data, config) => api.patch(url, data, config),
}

export default api
