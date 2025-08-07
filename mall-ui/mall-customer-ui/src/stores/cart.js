import { defineStore } from 'pinia'
import { cartService } from '@/services/cart'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: [],
    loading: false,
  }),

  getters: {
    totalCount: (state) => {
      return state.items.reduce((total, item) => total + item.quantity, 0)
    },
    
    checkedItems: (state) => {
      return state.items.filter(item => item.isChecked === 1)
    },
    
    checkedCount: (state) => {
      return state.items.filter(item => item.isChecked === 1).length
    },
    
    totalAmount: (state) => {
      return state.items
        .filter(item => item.isChecked === 1)
        .reduce((total, item) => {
          // Handle both CartVO (productPrice) and ShoppingCart (price) formats
          const price = item.productPrice || item.price || 0
          const numPrice = Number(price)
          const quantity = Number(item.quantity) || 0
          
          if (isNaN(numPrice) || isNaN(quantity)) {
            console.warn('Invalid price or quantity:', { price, quantity, item })
            return total
          }
          
          return total + (numPrice * quantity)
        }, 0)
    },
    
    allChecked: (state) => {
      return state.items.length > 0 && state.items.every(item => item.isChecked === 1)
    },
  },

  actions: {
    /**
     * Fetch cart items
     */
    async fetchCart() {
      this.loading = true
      try {
        const authStore = useAuthStore()
        
        // Only fetch cart if user is logged in
        if (!authStore.isAuthenticated || !authStore.currentUser) {
          this.items = []
          return
        }
        
        const response = await cartService.getCartItems(authStore.currentUser.id)
        if (response && response.code === 0 && response.data) {
          // Handle both array and paginated response
          this.items = Array.isArray(response.data) ? response.data : response.data.records || []
          
          // Debug: Log the first item to see the structure
          if (this.items.length > 0) {
            console.log('Cart item structure:', this.items[0])
          }
        } else {
          this.items = []
        }
      } catch (error) {
        console.error('Error fetching cart:', error)
        this.items = []
      } finally {
        this.loading = false
      }
    },

    /**
     * Add product to cart
     * @param {Object} product 
     * @param {number} quantity 
     */
    async addToCart(product, quantity = 1) {
      try {
        const authStore = useAuthStore()
        
        // Check if user is logged in
        if (!authStore.isAuthenticated || !authStore.currentUser) {
          ElMessage.error('Please login to add items to cart')
          return
        }
        
        const response = await cartService.addToCart(
          product.id, 
          quantity, 
          product.price, 
          authStore.currentUser.id
        )
        
        if (response && response.code === 0) {
          // Refresh cart to get updated data
          await this.fetchCart()
          ElMessage.success('Added to cart successfully!')
        } else {
          ElMessage.error('Failed to add item to cart')
        }
      } catch (error) {
        console.error('Error adding to cart:', error)
        ElMessage.error('Failed to add item to cart')
      }
    },

    /**
     * Update item quantity
     * @param {number} id 
     * @param {number} quantity 
     */
    async updateQuantity(id, quantity) {
      try {
        const item = this.items.find(item => item.id === id)
        if (!item) return
        
        const updatedItem = { ...item, quantity }
        await cartService.updateCartItem(updatedItem)
        
        // Update local state
        item.quantity = quantity
        const price = item.productPrice || item.price || 0
        item.subtotal = Number(price) * quantity
        
      } catch (error) {
        console.error('Error updating quantity:', error)
      }
    },

    /**
     * Remove item from cart
     * @param {number} id 
     */
    async removeItem(id) {
      try {
        await cartService.removeFromCart(id)
        
        // Update local state
        const index = this.items.findIndex(item => item.id === id)
        if (index > -1) {
          this.items.splice(index, 1)
        }
        
        ElMessage.success('Item removed from cart')
      } catch (error) {
        console.error('Error removing item:', error)
      }
    },

    /**
     * Remove multiple items
     * @param {Array} ids 
     */
    async removeMultipleItems(ids) {
      try {
        await cartService.removeMultipleFromCart(ids)
        
        // Update local state
        this.items = this.items.filter(item => !ids.includes(item.id))
        
        ElMessage.success('Items removed from cart')
      } catch (error) {
        console.error('Error removing items:', error)
      }
    },

    /**
     * Toggle item checked status
     * @param {number} id 
     */
    async toggleItemChecked(id) {
      const item = this.items.find(item => item.id === id)
      if (!item) return
      
      try {
        const newCheckedStatus = item.isChecked === 1 ? 0 : 1
        await cartService.updateItemChecked(id, newCheckedStatus === 1)
        
        // Update local state
        item.isChecked = newCheckedStatus
      } catch (error) {
        console.error('Error toggling item checked:', error)
      }
    },

    /**
     * Toggle all items checked status
     * @param {boolean} checked 
     */
    async toggleSelectAll(checked) {
      try {
        await cartService.updateCheckedAll(checked)
        
        // Update local state
        this.items.forEach(item => {
          item.isChecked = checked ? 1 : 0
        })
      } catch (error) {
        console.error('Error toggling select all:', error)
      }
    },

    /**
     * Clear cart
     */
    async clearCart() {
      try {
        const authStore = useAuthStore()
        
        if (authStore.isAuthenticated && authStore.currentUser) {
          await cartService.clearCart(authStore.currentUser.id)
        }
        
        this.items = []
      } catch (error) {
        console.error('Error clearing cart:', error)
        // Clear local state anyway
        this.items = []
      }
    },

    /**
     * Get checked items for checkout
     */
    getCheckedItems() {
      return this.checkedItems
    },
  },
})
