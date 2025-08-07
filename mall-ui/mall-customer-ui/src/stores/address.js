import { defineStore } from 'pinia'
import { addressService } from '@/services/address'
import { ElMessage } from 'element-plus'

export const useAddressStore = defineStore('address', {
  state: () => ({
    addresses: [],
    currentAddress: null,
    defaultAddress: null,
    loading: false,
  }),

  getters: {
    /**
     * Get address by ID
     */
    getAddressById: (state) => (id) => {
      return state.addresses.find(address => address.id === id)
    },

    /**
     * Get default address
     */
    getDefaultAddress: (state) => {
      return state.addresses.find(address => address.defaultStatus === 1) || state.defaultAddress
    },

    /**
     * Get non-default addresses
     */
    getNonDefaultAddresses: (state) => {
      return state.addresses.filter(address => address.defaultStatus !== 1)
    },

    /**
     * Check if user has any addresses
     */
    hasAddresses: (state) => {
      return state.addresses.length > 0
    },

    /**
     * Get total address count
     */
    addressCount: (state) => {
      return state.addresses.length
    },

    /**
     * Format address for display
     */
    formatAddress: () => (address) => {
      return addressService.formatAddress(address)
    },

    /**
     * Format full address with receiver info
     */
    formatFullAddress: () => (address) => {
      return addressService.formatFullAddress(address)
    },
  },

  actions: {
    /**
     * Fetch all addresses
     */
    async fetchAddresses() {
      this.loading = true
      try {
        const response = await addressService.getAddresses()
        if (response.data) {
          // Handle both array and paginated response
          this.addresses = Array.isArray(response.data) ? response.data : response.data.records || []
          
          // Update default address reference
          this.defaultAddress = this.addresses.find(addr => addr.defaultStatus === 1)
        }
      } catch (error) {
        console.error('Error fetching addresses:', error)
        ElMessage.error('Failed to load addresses')
      } finally {
        this.loading = false
      }
    },

    /**
     * Fetch address by ID
     * @param {number} id - Address ID
     */
    async fetchAddressById(id) {
      this.loading = true
      try {
        const response = await addressService.getAddressById(id)
        if (response.data) {
          this.currentAddress = response.data
          return response.data
        }
      } catch (error) {
        console.error('Error fetching address:', error)
        ElMessage.error('Failed to load address details')
        throw error
      } finally {
        this.loading = false
      }
    },

    /**
     * Create a new address
     * @param {Object} addressData - Address data
     */
    async createAddress(addressData) {
      // Skip frontend validation - let backend handle it
      this.loading = true
      try {
        const response = await addressService.createAddress(addressData)
        if (response.data) {
          ElMessage.success('Address added successfully!')
          // Refresh address list
          await this.fetchAddresses()
          return response.data
        }
      } catch (error) {
        console.error('Error creating address:', error)
        ElMessage.error(error.response?.data?.message || 'Failed to add address')
        throw error
      } finally {
        this.loading = false
      }
    },

    /**
     * Update an existing address
     * @param {number} id - Address ID
     * @param {Object} addressData - Address data
     */
    async updateAddress(id, addressData) {
      // Skip frontend validation - let backend handle it
      this.loading = true
      try {
        const response = await addressService.updateAddress(id, addressData)
        if (response.data) {
          ElMessage.success('Address updated successfully!')
          // Update address in list
          const index = this.addresses.findIndex(addr => addr.id === id)
          if (index !== -1) {
            this.addresses[index] = { ...this.addresses[index], ...addressData }
          }
          // Update current address if it's the same
          if (this.currentAddress?.id === id) {
            this.currentAddress = { ...this.currentAddress, ...addressData }
          }
          // Refresh address list to get updated data
          await this.fetchAddresses()
          return response.data
        }
      } catch (error) {
        console.error('Error updating address:', error)
        ElMessage.error(error.response?.data?.message || 'Failed to update address')
        throw error
      } finally {
        this.loading = false
      }
    },

    /**
     * Delete an address
     * @param {number} id - Address ID
     */
    async deleteAddress(id) {
      this.loading = true
      try {
        const response = await addressService.deleteAddress(id)
        if (response.data) {
          ElMessage.success('Address deleted successfully!')
          // Remove address from list
          this.addresses = this.addresses.filter(addr => addr.id !== id)
          // Clear current address if it's the same
          if (this.currentAddress?.id === id) {
            this.currentAddress = null
          }
          // Update default address reference
          this.defaultAddress = this.addresses.find(addr => addr.defaultStatus === 1)
          return response.data
        }
      } catch (error) {
        console.error('Error deleting address:', error)
        ElMessage.error(error.response?.data?.message || 'Failed to delete address')
        throw error
      } finally {
        this.loading = false
      }
    },

    /**
     * Set an address as default
     * @param {number} id - Address ID
     */
    async setDefaultAddress(id) {
      this.loading = true
      try {
        const response = await addressService.setDefaultAddress(id)
        if (response.data) {
          ElMessage.success('Default address updated!')
          // Update addresses in list
          this.addresses = this.addresses.map(addr => ({
            ...addr,
            defaultStatus: addr.id === id ? 1 : 0
          }))
          // Update default address reference
          this.defaultAddress = this.addresses.find(addr => addr.id === id)
          return response.data
        }
      } catch (error) {
        console.error('Error setting default address:', error)
        ElMessage.error(error.response?.data?.message || 'Failed to set default address')
        throw error
      } finally {
        this.loading = false
      }
    },

    /**
     * Fetch default address
     */
    async fetchDefaultAddress() {
      try {
        const response = await addressService.getDefaultAddress()
        if (response.data) {
          this.defaultAddress = response.data
          return response.data
        }
      } catch (error) {
        console.error('Error fetching default address:', error)
        // Don't show error message as this might be called frequently
      }
    },

    /**
     * Clear current address
     */
    clearCurrentAddress() {
      this.currentAddress = null
    },

    /**
     * Reset store state
     */
    resetStore() {
      this.addresses = []
      this.currentAddress = null
      this.defaultAddress = null
    },

    /**
     * Validate address data
     * @param {Object} addressData - Address data to validate
     * @returns {Object} Validation result
     */
    validateAddressData(addressData) {
      return addressService.validateAddress(addressData)
    },
  },
})
