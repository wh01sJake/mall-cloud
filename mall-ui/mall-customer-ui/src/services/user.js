import { apiClient } from './api'

export const userService = {
  /**
   * Get user profile information
   * @returns {Promise}
   */
  async getUserProfile() {
    return apiClient.get('/customer/profile')
  },

  /**
   * Update user profile information
   * @param {Object} profileData - Profile data
   * @param {string} profileData.username - Username
   * @param {string} profileData.email - Email
   * @param {string} profileData.phone - Phone number
   * @param {string} profileData.nickname - Nickname
   * @param {string} profileData.gender - Gender (0: unknown, 1: male, 2: female)
   * @param {string} profileData.birthday - Birthday
   * @returns {Promise}
   */
  async updateProfile(profileData) {
    const {
      username,
      email,
      phone,
      nickname,
      gender,
      birthday
    } = profileData

    const formData = new FormData()
    if (username) formData.append('username', username)
    if (email) formData.append('email', email)
    if (phone) formData.append('phone', phone)
    if (nickname) formData.append('nickname', nickname)
    if (gender !== undefined) formData.append('gender', gender)
    if (birthday) formData.append('birthday', birthday)

    return apiClient.post('/customer/profile/update', formData, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    })
  },

  /**
   * Change password
   * @param {Object} passwordData - Password data
   * @param {string} passwordData.oldPassword - Current password
   * @param {string} passwordData.newPassword - New password
   * @param {string} passwordData.confirmPassword - Confirm new password
   * @returns {Promise}
   */
  async changePassword(passwordData) {
    const { oldPassword, newPassword, confirmPassword } = passwordData

    const formData = new FormData()
    formData.append('oldPassword', oldPassword)
    formData.append('newPassword', newPassword)
    formData.append('confirmPassword', confirmPassword)

    return apiClient.post('/customer/password/change', formData, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    })
  },

  /**
   * Upload avatar with image optimization
   * @param {File} file - Avatar file
   * @returns {Promise}
   */
  async uploadAvatar(file) {
    const formData = new FormData()
    formData.append('file', file) // Changed from 'avatar' to 'file' to match backend

    return apiClient.post('/upload/user/enhanced', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  },

  /**
   * Get user statistics
   * @returns {Promise}
   */
  async getUserStats() {
    return apiClient.get('/customer/stats')
  },

  /**
   * Delete user account
   * @param {string} password - Current password for confirmation
   * @returns {Promise}
   */
  async deleteAccount(password) {
    const formData = new FormData()
    formData.append('password', password)

    return apiClient.post('/customer/account/delete', formData, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    })
  },

  /**
   * Validate profile data
   * @param {Object} profileData - Profile data to validate
   * @returns {Object} Validation result
   */
  validateProfile(profileData) {
    const errors = {}

    if (!profileData.username?.trim()) {
      errors.username = 'Username is required'
    } else if (profileData.username.trim().length < 3) {
      errors.username = 'Username must be at least 3 characters'
    } else if (profileData.username.trim().length > 20) {
      errors.username = 'Username must be less than 20 characters'
    }

    if (!profileData.email?.trim()) {
      errors.email = 'Email is required'
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(profileData.email.trim())) {
      errors.email = 'Please enter a valid email address'
    }

    if (profileData.phone && !/^1[3-9]\d{9}$/.test(profileData.phone.trim())) {
      errors.phone = 'Please enter a valid phone number'
    }

    if (profileData.nickname && profileData.nickname.trim().length > 30) {
      errors.nickname = 'Nickname must be less than 30 characters'
    }

    return {
      isValid: Object.keys(errors).length === 0,
      errors
    }
  },

  /**
   * Validate password change data
   * @param {Object} passwordData - Password data to validate
   * @returns {Object} Validation result
   */
  validatePasswordChange(passwordData) {
    const errors = {}

    if (!passwordData.oldPassword?.trim()) {
      errors.oldPassword = 'Current password is required'
    }

    if (!passwordData.newPassword?.trim()) {
      errors.newPassword = 'New password is required'
    } else if (passwordData.newPassword.trim().length < 6) {
      errors.newPassword = 'Password must be at least 6 characters'
    } else if (passwordData.newPassword.trim().length > 20) {
      errors.newPassword = 'Password must be less than 20 characters'
    }

    if (!passwordData.confirmPassword?.trim()) {
      errors.confirmPassword = 'Please confirm your new password'
    } else if (passwordData.newPassword !== passwordData.confirmPassword) {
      errors.confirmPassword = 'Passwords do not match'
    }

    if (passwordData.oldPassword === passwordData.newPassword) {
      errors.newPassword = 'New password must be different from current password'
    }

    return {
      isValid: Object.keys(errors).length === 0,
      errors
    }
  },

  /**
   * Format gender for display
   * @param {number} gender - Gender value (0: unknown, 1: male, 2: female)
   * @returns {string} Gender text
   */
  formatGender(gender) {
    const genderMap = {
      0: 'Not specified',
      1: 'Male',
      2: 'Female'
    }
    return genderMap[gender] || 'Not specified'
  },

  /**
   * Format date for display
   * @param {string} dateString - Date string
   * @returns {string} Formatted date
   */
  formatDate(dateString) {
    if (!dateString) return ''
    const date = new Date(dateString)
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    })
  }
}
