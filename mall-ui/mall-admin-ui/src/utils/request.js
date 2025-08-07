import axios from 'axios'

// Environment-specific configuration
const getBaseURL = () => {
  // In development, use the proxy configuration
  if (import.meta.env.DEV) {
    return '/api'
  }
  
  // In production, use environment variable or default to deployed gateway URL
  return import.meta.env.VITE_API_BASE_URL || 'https://vapemall-gateway-989f7ea980e6.herokuapp.com/api'
}

const baseURL = getBaseURL()

//这个service和axios具有相同的功能
const service = axios.create({
  baseURL,
  timeout: import.meta.env.VITE_API_TIMEOUT || 10000
})


import {useTokenStore} from '@/store/token.js'
import {ElMessage} from "element-plus";
import router from "@/router/index.js";
//添加请求拦截器
service.interceptors.request.use(
    (config) => {
        //请求前的回调
        //添加token
        const tokenStore = useTokenStore();
        //判断有没有token
        if (tokenStore.token) {
            config.headers.Authorization = `Bearer ${tokenStore.token}`
        }
        return config;
    },
    (error) => {
        //请求错误的回调
        Promise.reject(error)
    }
)



//添加响应的拦截器
service.interceptors.response.use(
    response => {
        //返回result
        return response.data
    },
    error => {
        //判断响应状态码并处理不同的错误情况
        const status = error.response?.status
        const message = error.response?.data?.message || error.message
        
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

        // Check if error is related to token expiration
        const errorData = error.response?.data
        const errorMessage = errorData?.error || errorData?.message || message || ''

        const isTokenExpired = status === 401 ||
                              status === 403 ||
                              (status === 500 && (
                                  errorMessage.toLowerCase().includes('token') ||
                                  errorMessage.toLowerCase().includes('expired') ||
                                  errorMessage.toLowerCase().includes('unauthorized') ||
                                  errorMessage.toLowerCase().includes('jwt') ||
                                  errorMessage.toLowerCase().includes('authentication error')
                              ))

        if (isTokenExpired) {
            // Clear token and redirect to login
            const tokenStore = useTokenStore()
            tokenStore.removeToken()

            // Show user-friendly message
            if (errorMessage.toLowerCase().includes('expired')) {
                ElMessage.error('🔐 Your session has expired. Please login again.')
            } else {
                ElMessage.error('🔐 Authentication failed. Please login again.')
            }

            // Redirect to login page
            router.push('/login')
            return Promise.reject(error)
        }

        switch (status) {
            case 401:
                ElMessage.error('Please login')
                router.push('/login')
                break
            case 403:
                ElMessage.error('Access denied')
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
                ElMessage.error(message || 'Something went wrong...')
        }

        return Promise.reject(error);//异步的状态转化成失败的状态
    }
)

export default service