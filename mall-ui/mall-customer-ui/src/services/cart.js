import { apiClient } from './api'

export const cartService = {
  /**
   * Get cart items for current user
   * @param {number} userId 
   * @returns {Promise}
   */
  async getCartItems(userId) {
    const params = userId ? { customerId: userId } : {}
    return apiClient.get('/customer/cart/list', { params })
  },

  /**
   * Add item to cart
   * @param {number} productId 
   * @param {number} quantity 
   * @param {number} price 
   * @param {number} userId 
   * @returns {Promise}
   */
  async addToCart(productId, quantity = 1, price, userId) {
    const cartItem = {
      productId,
      quantity,
      price,
      userId,
      isChecked: 1,
      status: 1
    }

    return apiClient.post('/customer/cart/add', cartItem, {
      headers: {
        'Content-Type': 'application/json',
      },
    })
  },

  /**
   * Update cart item
   * @param {Object} cartItem 
   * @returns {Promise}
   */
  async updateCartItem(cartItem) {
    return apiClient.put('/customer/cart/update', cartItem, {
      headers: {
        'Content-Type': 'application/json',
      },
    })
  },

  /**
   * Remove item from cart
   * @param {number} id 
   * @returns {Promise}
   */
  async removeFromCart(id) {
    return apiClient.delete(`/customer/cart/remove/${id}`)
  },

  /**
   * Remove multiple items from cart
   * @param {Array} ids 
   * @returns {Promise}
   */
  async removeMultipleFromCart(ids) {
    return apiClient.delete(`/customer/cart/removeAll/${ids.join(',')}`)
  },

  /**
   * Update item checked status
   * @param {number} id 
   * @param {boolean} isChecked 
   * @returns {Promise}
   */
  async updateItemChecked(id, isChecked) {
    return apiClient.put(`/customer/cart/updateChecked/${id}/${isChecked ? 1 : 0}`)
  },

  /**
   * Clear cart for user
   * @param {number} userId 
   * @returns {Promise}
   */
  async clearCart(userId) {
    return apiClient.delete(`/customer/cart/clear/${userId}`)
  },

  /**
   * Get cart count for user
   * @param {number} userId 
   * @returns {Promise}
   */
  async getCartCount(userId) {
    return apiClient.get(`/customer/cart/count/${userId}`)
  },
}
