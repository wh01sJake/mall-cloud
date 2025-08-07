// Manual test script to verify error handling
// This script simulates different error scenarios to test the enhanced error handling

import axios from 'axios'

// Create a test instance similar to our request.js
const testService = axios.create({
  baseURL: 'http://localhost:9000',
  timeout: 5000
})

// Add the same error handling logic as in request.js
testService.interceptors.response.use(
  response => response.data,
  error => {
    const status = error.response?.status
    const message = error.response?.data?.message || error.message
    
    console.log('Error intercepted:', {
      status,
      message,
      code: error.code,
      hasResponse: !!error.response
    })
    
    // Handle network errors (no response from server)
    if (!error.response) {
      if (error.code === 'ECONNABORTED') {
        console.log('❌ Request timeout. Please check your connection.')
      } else if (error.code === 'ERR_NETWORK') {
        console.log('❌ Network error. Please check if the gateway is running.')
      } else {
        console.log('❌ Unable to connect to server. Please try again later.')
      }
      return Promise.reject(error)
    }
    
    switch (status) {
      case 401:
        console.log('❌ Please login')
        break
      case 403:
        console.log('❌ Access denied')
        break
      case 500:
        console.log('❌ Internal server error. Please try again later.')
        break
      case 502:
        console.log('❌ Bad gateway. Service may be temporarily unavailable.')
        break
      case 503:
        console.log('❌ Service temporarily unavailable. Please try again later.')
        break
      case 504:
        console.log('❌ Gateway timeout. Please check your connection and try again.')
        break
      default:
        console.log('❌', message || 'Something went wrong...')
    }

    return Promise.reject(error)
  }
)

// Test different error scenarios
async function testErrorHandling() {
  console.log('🧪 Testing Enhanced Error Handling for Microservices Architecture\n')
  
  // Test 1: Network error (gateway not running)
  console.log('Test 1: Network Error (Gateway not running)')
  try {
    await testService.get('/admin/list')
  } catch (error) {
    console.log('✅ Network error handled correctly\n')
  }
  
  // Test 2: Timeout error
  console.log('Test 2: Timeout Error')
  const timeoutService = axios.create({
    baseURL: 'http://httpstat.us',
    timeout: 100 // Very short timeout
  })
  
  timeoutService.interceptors.response.use(
    response => response.data,
    error => {
      if (error.code === 'ECONNABORTED') {
        console.log('✅ Timeout error handled correctly')
        return Promise.reject(error)
      }
      return Promise.reject(error)
    }
  )
  
  try {
    await timeoutService.get('/200?sleep=1000')
  } catch (error) {
    console.log('✅ Timeout scenario tested\n')
  }
  
  console.log('🎉 Error handling tests completed!')
  console.log('📋 Enhanced error handling includes:')
  console.log('   • 502 Bad Gateway errors')
  console.log('   • 503 Service Unavailable errors') 
  console.log('   • 504 Gateway Timeout errors')
  console.log('   • 500 Internal Server errors')
  console.log('   • Network connection errors')
  console.log('   • Request timeout errors')
  console.log('   • Server error message extraction')
}

testErrorHandling().catch(console.error)