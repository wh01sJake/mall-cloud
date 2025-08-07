import { defineStore } from 'pinia'
import { orderService } from '@/services/order'
import { ElMessage } from 'element-plus'

export const useOrderStore = defineStore('order', {
  state: () => ({
    orders: [],
    currentOrder: null,
    orderStats: {
      totalOrders: 0,
      pendingPayment: 0,
      pendingShipment: 0,
      pendingReceipt: 0,
      completed: 0,
    },
    loading: false,
    pagination: {
      page: 1,
      size: 10,
      total: 0,
    },
  }),

  getters: {
    /**
     * Get order by order number
     */
    getOrderByNo: (state) => (orderNo) => {
      return state.orders.find(order => order.orderNo === orderNo)
    },

    /**
     * Get orders by status
     */
    getOrdersByStatus: (state) => (status) => {
      return state.orders.filter(order => order.status === status)
    },

    /**
     * Check if there are pending orders
     */
    hasPendingOrders: (state) => {
      return state.orderStats.pendingPayment > 0 || 
             state.orderStats.pendingShipment > 0 || 
             state.orderStats.pendingReceipt > 0
    },

    /**
     * Get total order count
     */
    totalOrderCount: (state) => {
      return state.orders.length
    },
  },

  actions: {
    /**
     * Create a new order
     * @param {Object} orderData - Order creation data
     */
    async createOrder(orderData) {
      this.loading = true
      try {
        const response = await orderService.createOrder(orderData)
        
        // Check for successful response
        if (response && response.code === 0 && response.data) {
          ElMessage.success('Order created successfully!')
          // Refresh order list and stats
          await this.fetchOrders()
          await this.fetchOrderStats()
          return response.data
        } else if (response && response.code === 1) {
          // Handle authentication error
          if (response.msg === 'Please login first') {
            ElMessage.error('Your session has expired. Please login again.')
            // Import auth store and clear auth state
            const { useAuthStore } = await import('@/stores/auth')
            const authStore = useAuthStore()
            authStore.clearAuth()
            // Throw a specific error that the component can handle
            const authError = new Error('AUTHENTICATION_REQUIRED')
            authError.originalMessage = response.msg
            throw authError
          } else {
            ElMessage.error(response.msg || 'Failed to create order')
          }
          throw new Error(response.msg || 'Failed to create order')
        } else {
          ElMessage.error('Failed to create order')
          throw new Error('Failed to create order')
        }
      } catch (error) {
        console.error('Error creating order:', error)
        
        // Handle different types of errors
        if (error.message === 'AUTHENTICATION_REQUIRED') {
          // This is already handled above, just re-throw for component to handle redirect
          throw error
        } else if (error.response) {
          const errorData = error.response.data
          if (errorData && errorData.code === 1 && errorData.msg === 'Please login first') {
            ElMessage.error('Your session has expired. Please login again.')
            // Import auth store and clear auth state
            const { useAuthStore } = await import('@/stores/auth')
            const authStore = useAuthStore()
            authStore.clearAuth()
            // Throw authentication error for component to handle
            const authError = new Error('AUTHENTICATION_REQUIRED')
            authError.originalMessage = errorData.msg
            throw authError
          } else {
            ElMessage.error(errorData?.msg || error.response.statusText || 'Failed to create order')
          }
        } else if (error.message) {
          // Don't show error message again if we already showed it above
          if (!error.message.includes('Please login first') && !error.message.includes('Failed to create order')) {
            ElMessage.error(error.message)
          }
        } else {
          ElMessage.error('Network error. Please try again.')
        }
        
        throw error
      } finally {
        this.loading = false
      }
    },

    /**
     * Fetch order list
     * @param {Object} params - Query parameters
     */
    async fetchOrders(params = {}) {
      this.loading = true
      try {
        const queryParams = {
          page: params.page || this.pagination.page,
          size: params.size || this.pagination.size,
          status: params.status,
        }

        const response = await orderService.getOrderList(queryParams)
        if (response.data) {
          // Handle both array and paginated response
          if (Array.isArray(response.data)) {
            this.orders = response.data
            this.pagination.total = response.data.length
          } else {
            this.orders = response.data.records || []
            this.pagination.total = response.data.total || 0
            this.pagination.page = response.data.current || 1
            this.pagination.size = response.data.size || 10
          }
        }
      } catch (error) {
        console.error('Error fetching orders:', error)
        ElMessage.error('Failed to load orders')
      } finally {
        this.loading = false
      }
    },

    /**
     * Fetch order details
     * @param {string} orderNo - Order number
     */
    async fetchOrderDetail(orderNo) {
      this.loading = true
      try {
        const response = await orderService.getOrderDetail(orderNo)
        if (response.data) {
          this.currentOrder = response.data
          return response.data
        }
      } catch (error) {
        console.error('Error fetching order detail:', error)
        ElMessage.error('Failed to load order details')
        throw error
      } finally {
        this.loading = false
      }
    },

    /**
     * Cancel an order
     * @param {string} orderNo - Order number
     */
    async cancelOrder(orderNo) {
      this.loading = true
      try {
        const response = await orderService.cancelOrder(orderNo)
        if (response.data) {
          ElMessage.success('Order cancelled successfully!')
          // Update order in list
          const orderIndex = this.orders.findIndex(order => order.orderNo === orderNo)
          if (orderIndex !== -1) {
            this.orders[orderIndex].status = 'CANCELLED'
          }
          // Update current order if it's the same
          if (this.currentOrder?.orderNo === orderNo) {
            this.currentOrder.status = 'CANCELLED'
          }
          // Refresh stats
          await this.fetchOrderStats()
          return response.data
        }
      } catch (error) {
        console.error('Error cancelling order:', error)
        ElMessage.error(error.response?.data?.message || 'Failed to cancel order')
        throw error
      } finally {
        this.loading = false
      }
    },

    /**
     * Confirm order receipt
     * @param {string} orderNo - Order number
     */
    async confirmOrder(orderNo) {
      this.loading = true
      try {
        const response = await orderService.confirmOrder(orderNo)
        if (response.data) {
          ElMessage.success('Order confirmed successfully!')
          // Update order in list
          const orderIndex = this.orders.findIndex(order => order.orderNo === orderNo)
          if (orderIndex !== -1) {
            this.orders[orderIndex].status = 'COMPLETED'
          }
          // Update current order if it's the same
          if (this.currentOrder?.orderNo === orderNo) {
            this.currentOrder.status = 'COMPLETED'
          }
          // Refresh stats
          await this.fetchOrderStats()
          return response.data
        }
      } catch (error) {
        console.error('Error confirming order:', error)
        ElMessage.error(error.response?.data?.message || 'Failed to confirm order')
        throw error
      } finally {
        this.loading = false
      }
    },

    /**
     * Pay for an order
     * @param {string} orderNo - Order number
     * @param {string} paymentMethod - Payment method
     */
    async payOrder(orderNo, paymentMethod = 'ALIPAY') {
      this.loading = true
      try {
        const response = await orderService.payOrder(orderNo, paymentMethod)
        if (response.data) {
          ElMessage.success('Payment initiated successfully!')
          // Update order status
          const orderIndex = this.orders.findIndex(order => order.orderNo === orderNo)
          if (orderIndex !== -1) {
            this.orders[orderIndex].status = 'PAID'
          }
          // Update current order if it's the same
          if (this.currentOrder?.orderNo === orderNo) {
            this.currentOrder.status = 'PAID'
          }
          // Refresh stats
          await this.fetchOrderStats()
          return response.data
        }
      } catch (error) {
        console.error('Error paying order:', error)
        ElMessage.error(error.response?.data?.message || 'Payment failed')
        throw error
      } finally {
        this.loading = false
      }
    },

    /**
     * Fetch order statistics
     */
    async fetchOrderStats() {
      try {
        const response = await orderService.getOrderStats()
        if (response.data) {
          this.orderStats = response.data
        }
      } catch (error) {
        console.error('Error fetching order stats:', error)
        // Don't show error message for stats as it's not critical
      }
    },

    /**
     * Clear current order
     */
    clearCurrentOrder() {
      this.currentOrder = null
    },

    /**
     * Reset store state
     */
    resetStore() {
      this.orders = []
      this.currentOrder = null
      this.orderStats = {
        totalOrders: 0,
        pendingPayment: 0,
        pendingShipment: 0,
        pendingReceipt: 0,
        completed: 0,
      }
      this.pagination = {
        page: 1,
        size: 10,
        total: 0,
      }
    },
  },
})
