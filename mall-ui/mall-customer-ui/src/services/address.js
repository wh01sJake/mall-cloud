import { apiClient } from './api'

export const addressService = {
  /**
   * Get all addresses for current user
   * @returns {Promise}
   */
  async getAddresses() {
    return apiClient.get('/customer/address/list')
  },

  /**
   * Get address by ID
   * @param {number} id - Address ID
   * @returns {Promise}
   */
  async getAddressById(id) {
    return apiClient.get(`/customer/address/selectById/${id}`)
  },

  /**
   * Create a new address
   * @param {Object} addressData - Address data
   * @param {string} addressData.receiverName - Receiver name
   * @param {string} addressData.receiverPhone - Receiver phone
   * @param {string} addressData.province - Province
   * @param {string} addressData.city - City
   * @param {string} addressData.region - Region/District
   * @param {string} addressData.detailAddress - Detailed address
   * @param {string} addressData.postCode - Postal code (optional)
   * @param {boolean} addressData.defaultStatus - Is default address
   * @returns {Promise}
   */
  async createAddress(addressData) {
    const {
      receiverName,
      receiverPhone,
      province,
      city,
      region,
      detailAddress,
      postCode,
      defaultStatus
    } = addressData

    const addressPayload = {
      receiverName,
      receiverPhone,
      receiverMobile: receiverPhone, // Map to mobile field as well
      townCity: city, // Map city to townCity
      addressLine1: detailAddress, // Map detailAddress to addressLine1
      addressLine2: region || '', // Map region to addressLine2
      eircode: postCode || '', // Map postCode to eircode
      isDefault: defaultStatus ? 1 : 0 // Map defaultStatus to isDefault
    }

    return apiClient.post('/customer/address/add', addressPayload, {
      headers: {
        'Content-Type': 'application/json',
      },
    })
  },

  /**
   * Update an existing address
   * @param {number} id - Address ID
   * @param {Object} addressData - Address data
   * @returns {Promise}
   */
  async updateAddress(id, addressData) {
    const {
      receiverName,
      receiverPhone,
      province,
      city,
      region,
      detailAddress,
      postCode,
      defaultStatus
    } = addressData

    const addressPayload = {
      id,
      receiverName,
      receiverPhone,
      receiverMobile: receiverPhone, // Map to mobile field as well
      townCity: city, // Map city to townCity
      addressLine1: detailAddress, // Map detailAddress to addressLine1
      addressLine2: region || '', // Map region to addressLine2
      eircode: postCode || '', // Map postCode to eircode
      isDefault: defaultStatus ? 1 : 0 // Map defaultStatus to isDefault
    }

    return apiClient.put('/customer/address/update', addressPayload, {
      headers: {
        'Content-Type': 'application/json',
      },
    })
  },

  /**
   * Delete an address
   * @param {number} id - Address ID
   * @returns {Promise}
   */
  async deleteAddress(id) {
    return apiClient.delete(`/customer/address/delete/${id}`)
  },

  /**
   * Set an address as default
   * @param {number} id - Address ID
   * @returns {Promise}
   */
  async setDefaultAddress(id) {
    return apiClient.put(`/customer/address/setDefault/${id}`, {}, {
      headers: {
        'Content-Type': 'application/json',
      },
    })
  },

  /**
   * Get default address
   * @returns {Promise}
   */
  async getDefaultAddress() {
    return apiClient.get('/customer/address/default')
  },

  /**
   * Validate address data
   * @param {Object} addressData - Address data to validate
   * @returns {Object} Validation result
   */
  validateAddress(addressData) {
    const errors = {}

    if (!addressData.receiverName?.trim()) {
      errors.receiverName = 'Receiver name is required'
    }

    if (!addressData.receiverPhone?.trim()) {
      errors.receiverPhone = 'Phone number is required'
    }

    if (!addressData.province?.trim()) {
      errors.province = 'Province is required'
    }

    if (!addressData.city?.trim()) {
      errors.city = 'City is required'
    }

    if (!addressData.region?.trim()) {
      errors.region = 'District/Region is required'
    }

    if (!addressData.detailAddress?.trim()) {
      errors.detailAddress = 'Detailed address is required'
    }

    // Postal code validation removed - accepting any format

    return {
      isValid: Object.keys(errors).length === 0,
      errors
    }
  },

  /**
   * Format address for display
   * @param {Object} address - Address object
   * @returns {string} Formatted address string
   */
  formatAddress(address) {
    if (!address) return ''

    const parts = [
      address.province,
      address.city,
      address.region,
      address.detailAddress
    ].filter(Boolean)

    return parts.join(' ')
  },

  /**
   * Format address with receiver info
   * @param {Object} address - Address object
   * @returns {string} Formatted address with receiver info
   */
  formatFullAddress(address) {
    if (!address) return ''

    const addressStr = this.formatAddress(address)
    const receiverInfo = `${address.receiverName} ${address.receiverPhone}`

    return `${receiverInfo}\n${addressStr}`
  }
}
