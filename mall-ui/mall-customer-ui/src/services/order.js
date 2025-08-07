import { apiClient } from './api'

export const orderService = {
  /**
   * Create a new order from cart items
   * @param {Object} orderData - Order creation data
   * @param {Array} orderData.cartIds - Array of cart item IDs to order
   * @param {number} orderData.addressId - Shipping address ID
   * @param {string} orderData.remark - Optional order remark
   * @param {string} orderData.paymentMethod - Payment method (CARD, PAYPAL, BANK_TRANSFER)
   * @param {number} orderData.totalAmount - Total amount including shipping and discounts
   * @returns {Promise}
   */
  async createOrder(orderData) {
    const { cartIds, addressId, remark, paymentMethod, totalAmount } = orderData

    // Map payment method strings to integers
    const paymentTypeMap = {
      'CARD': 3,           // Credit Card
      'PAYPAL': 2,         // PayPal
      'BANK_TRANSFER': 4,  // Cash on Delivery (closest match)
      'STRIPE': 1          // Stripe
    }

    const orderPayload = {
      cartIds: cartIds.join(','), // Convert array to comma-separated string
      shippingId: addressId, // Map addressId to shippingId
      remark: remark || '',
      paymentType: paymentTypeMap[paymentMethod] || 3, // Default to Credit Card
      totalAmount: totalAmount
    }

    return apiClient.post('/order/add', orderPayload, {
      headers: {
        'Content-Type': 'application/json',
      },
    })
  },

  /**
   * Get order list for current user
   * @param {Object} params - Query parameters
   * @param {number} params.page - Page number (default: 1)
   * @param {number} params.size - Page size (default: 10)
   * @param {string} params.status - Order status filter (optional)
   * @returns {Promise}
   */
  async getOrderList(params = {}) {
    const { page = 1, size = 10, status } = params
    const queryParams = { page, size }
    if (status) queryParams.status = status

    return apiClient.get('/order/customer/list', { params: queryParams })
  },

  /**
   * Get order details by order number
   * @param {string} orderNo - Order number
   * @returns {Promise}
   */
  async getOrderDetail(orderNo) {
    return apiClient.get(`/order/customer/detail/${orderNo}`)
  },

  /**
   * Cancel an order
   * @param {string} orderNo - Order number
   * @returns {Promise}
   */
  async cancelOrder(orderNo) {
    return apiClient.post(`/order/cancel/${orderNo}`)
  },

  /**
   * Confirm order receipt
   * @param {string} orderNo - Order number
   * @returns {Promise}
   */
  async confirmOrder(orderNo) {
    return apiClient.post(`/order/confirm/${orderNo}`)
  },

  /**
   * Get order statistics for user
   * @returns {Promise}
   */
  async getOrderStats() {
    return apiClient.get('/order/stats')
  },

  /**
   * Pay for an order
   * @param {string} orderNo - Order number
   * @param {string} paymentMethod - Payment method
   * @returns {Promise}
   */
  async payOrder(orderNo, paymentMethod = 'CARD') {
    const paymentPayload = {
      orderNo,
      paymentMethod
    }

    return apiClient.post('/order/pay', paymentPayload, {
      headers: {
        'Content-Type': 'application/json',
      },
    })
  },
}
