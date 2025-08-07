import { defineStore } from 'pinia'
import { userService } from '@/services/user'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', {
  state: () => ({
    profile: null,
    stats: {
      totalOrders: 0,
      totalSpent: 0,
      favoriteProducts: 0,
      memberSince: null
    },
    loading: false,
  }),

  getters: {
    /**
     * Get user's full name or username
     */
    displayName: (state) => {
      if (!state.profile) return ''
      return state.profile.nickname || state.profile.username || 'User'
    },

    /**
     * Get user's avatar URL
     */
    avatarUrl: (state) => {
      return state.profile?.avatar || '/default-avatar.png'
    },

    /**
     * Check if profile is complete
     */
    isProfileComplete: (state) => {
      if (!state.profile) return false
      return !!(state.profile.username && 
                state.profile.email && 
                state.profile.phone)
    },

    /**
     * Get formatted gender
     */
    formattedGender: (state) => {
      return userService.formatGender(state.profile?.gender)
    },

    /**
     * Get formatted birthday
     */
    formattedBirthday: (state) => {
      return userService.formatDate(state.profile?.birthday)
    },

    /**
     * Get formatted member since date
     */
    formattedMemberSince: (state) => {
      return userService.formatDate(state.stats?.memberSince || state.profile?.createTime)
    },
  },

  actions: {
    /**
     * Fetch user profile
     */
    async fetchProfile() {
      this.loading = true
      try {
        const response = await userService.getUserProfile()
        if (response.data) {
          this.profile = response.data
        }
      } catch (error) {
        console.error('Error fetching profile:', error)
        ElMessage.error('Failed to load profile information')
      } finally {
        this.loading = false
      }
    },

    /**
     * Update user profile
     * @param {Object} profileData - Profile data
     */
    async updateProfile(profileData) {
      // Validate profile data
      const validation = userService.validateProfile(profileData)
      if (!validation.isValid) {
        const firstError = Object.values(validation.errors)[0]
        ElMessage.error(firstError)
        throw new Error(firstError)
      }

      this.loading = true
      try {
        const response = await userService.updateProfile(profileData)
        if (response.data) {
          ElMessage.success('Profile updated successfully!')
          // Update local profile data
          this.profile = { ...this.profile, ...profileData }
          return response.data
        }
      } catch (error) {
        console.error('Error updating profile:', error)
        ElMessage.error(error.response?.data?.message || 'Failed to update profile')
        throw error
      } finally {
        this.loading = false
      }
    },

    /**
     * Change password
     * @param {Object} passwordData - Password data
     */
    async changePassword(passwordData) {
      // Validate password data
      const validation = userService.validatePasswordChange(passwordData)
      if (!validation.isValid) {
        const firstError = Object.values(validation.errors)[0]
        ElMessage.error(firstError)
        throw new Error(firstError)
      }

      this.loading = true
      try {
        const response = await userService.changePassword(passwordData)
        if (response.data) {
          ElMessage.success('Password changed successfully!')
          return response.data
        }
      } catch (error) {
        console.error('Error changing password:', error)
        ElMessage.error(error.response?.data?.message || 'Failed to change password')
        throw error
      } finally {
        this.loading = false
      }
    },

    /**
     * Upload avatar
     * @param {File} file - Avatar file
     */
    async uploadAvatar(file) {
      // Validate file
      if (!file) {
        ElMessage.error('Please select a file')
        throw new Error('No file selected')
      }

      if (!file.type.startsWith('image/')) {
        ElMessage.error('Please select an image file')
        throw new Error('Invalid file type')
      }

      if (file.size > 5 * 1024 * 1024) { // 5MB
        ElMessage.error('File size must be less than 5MB')
        throw new Error('File too large')
      }

      this.loading = true
      try {
        const response = await userService.uploadAvatar(file)
        if (response.data) {
          ElMessage.success('Avatar updated successfully!')
          // Update local profile avatar - handle enhanced upload response
          if (this.profile) {
            if (response.data.urls) {
              // Use thumbnail for avatar display
              this.profile.avatar = response.data.urls.thumbnail || response.data.urls.medium || response.data.urls.original
              console.log('🖼️ Avatar uploaded with variants:', response.data.urls)
            } else {
              this.profile.avatar = response.data.avatarUrl || response.data.url
            }
          }
          return response.data
        }
      } catch (error) {
        console.error('Error uploading avatar:', error)
        ElMessage.error(error.response?.data?.message || 'Failed to upload avatar')
        throw error
      } finally {
        this.loading = false
      }
    },

    /**
     * Fetch user statistics
     */
    async fetchStats() {
      try {
        const response = await userService.getUserStats()
        if (response.data) {
          this.stats = response.data
        }
      } catch (error) {
        console.error('Error fetching user stats:', error)
        // Don't show error message for stats as it's not critical
      }
    },

    /**
     * Delete user account
     * @param {string} password - Current password
     */
    async deleteAccount(password) {
      if (!password?.trim()) {
        ElMessage.error('Password is required')
        throw new Error('Password required')
      }

      this.loading = true
      try {
        const response = await userService.deleteAccount(password)
        if (response.data) {
          ElMessage.success('Account deleted successfully')
          // Clear all user data
          this.resetStore()
          return response.data
        }
      } catch (error) {
        console.error('Error deleting account:', error)
        ElMessage.error(error.response?.data?.message || 'Failed to delete account')
        throw error
      } finally {
        this.loading = false
      }
    },

    /**
     * Reset store state
     */
    resetStore() {
      this.profile = null
      this.stats = {
        totalOrders: 0,
        totalSpent: 0,
        favoriteProducts: 0,
        memberSince: null
      }
    },

    /**
     * Validate profile data
     * @param {Object} profileData - Profile data to validate
     * @returns {Object} Validation result
     */
    validateProfileData(profileData) {
      return userService.validateProfile(profileData)
    },

    /**
     * Validate password change data
     * @param {Object} passwordData - Password data to validate
     * @returns {Object} Validation result
     */
    validatePasswordData(passwordData) {
      return userService.validatePasswordChange(passwordData)
    },
  },
})
