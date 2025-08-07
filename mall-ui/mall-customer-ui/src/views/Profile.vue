<template>
  <div class="profile-page">
    <div class="container">
      <div class="page-header">
        <h1>My Profile</h1>
        <p class="page-subtitle">Manage your account information and settings</p>
      </div>

      <!-- Loading State -->
      <div v-if="userStore.loading && !userStore.profile" class="loading-container">
        <el-skeleton :rows="4" animated />
      </div>

      <!-- Profile Content -->
      <div v-else class="profile-content">
        <!-- Profile Overview Card -->
        <div class="profile-card">
          <div class="profile-header">
            <div class="avatar-section">
              <div class="avatar-container">
                <img
                  :src="userStore.avatarUrl"
                  :alt="userStore.displayName"
                  class="avatar"
                  @error="handleAvatarError"
                />
                <div class="avatar-overlay">
                  <el-button
                    type="primary"
                    size="small"
                    @click="showAvatarUpload = true"
                  >
                    Change
                  </el-button>
                </div>
              </div>
            </div>
            <div class="profile-info">
              <h2>{{ userStore.displayName }}</h2>
              <p class="user-email">{{ userStore.profile?.email || 'No email set' }}</p>
              <p class="member-since">Member since {{ userStore.formattedMemberSince }}</p>
              <div class="profile-status">
                <el-tag
                  :type="userStore.isProfileComplete ? 'success' : 'warning'"
                  size="small"
                >
                  {{ userStore.isProfileComplete ? 'Profile Complete' : 'Profile Incomplete' }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>

        <!-- Profile Stats Card -->
        <div class="stats-card">
          <h3>Account Statistics</h3>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-value">{{ userStore.stats.totalOrders || 0 }}</div>
              <div class="stat-label">Total Orders</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">¥{{ (userStore.stats.totalSpent || 0).toFixed(2) }}</div>
              <div class="stat-label">Total Spent</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ userStore.stats.favoriteProducts || 0 }}</div>
              <div class="stat-label">Favorite Products</div>
            </div>
          </div>
        </div>

        <!-- Profile Details Card -->
        <div class="details-card">
          <div class="card-header">
            <h3>Profile Information</h3>
            <el-button
              type="primary"
              size="small"
              @click="showEditProfile = true"
            >
              Edit Profile
            </el-button>
          </div>
          <div class="details-grid">
            <div class="detail-item">
              <label>Username:</label>
              <span>{{ userStore.profile?.username || 'Not set' }}</span>
            </div>
            <div class="detail-item">
              <label>Email:</label>
              <span>{{ userStore.profile?.email || 'Not set' }}</span>
            </div>
            <div class="detail-item">
              <label>Phone:</label>
              <span>{{ userStore.profile?.phone || 'Not set' }}</span>
            </div>
            <div class="detail-item">
              <label>Nickname:</label>
              <span>{{ userStore.profile?.nickname || 'Not set' }}</span>
            </div>
            <div class="detail-item">
              <label>Gender:</label>
              <span>{{ userStore.formattedGender }}</span>
            </div>
            <div class="detail-item">
              <label>Birthday:</label>
              <span>{{ userStore.formattedBirthday || 'Not set' }}</span>
            </div>
          </div>
        </div>

        <!-- Account Actions Card -->
        <div class="actions-card">
          <h3>Account Actions</h3>
          <div class="action-buttons">
            <el-button
              @click="showChangePassword = true"
              icon="Lock"
            >
              Change Password
            </el-button>
            <el-button
              @click="$router.push('/orders')"
              icon="List"
            >
              View Orders
            </el-button>
            <el-button
              type="danger"
              @click="handleDeleteAccount"
              icon="Delete"
            >
              Delete Account
            </el-button>
          </div>
        </div>
      </div>

      <!-- Edit Profile Dialog -->
      <el-dialog
        v-model="showEditProfile"
        title="Edit Profile"
        width="90%"
        :max-width="600"
        :close-on-click-modal="false"
      >
        <ProfileForm
          :profile="userStore.profile"
          :loading="formLoading"
          @submit="handleUpdateProfile"
          @cancel="showEditProfile = false"
        />
      </el-dialog>

      <!-- Change Password Dialog -->
      <el-dialog
        v-model="showChangePassword"
        title="Change Password"
        width="90%"
        :max-width="600"
        :close-on-click-modal="false"
      >
        <PasswordChangeForm
          :loading="formLoading"
          @submit="handleChangePassword"
          @cancel="showChangePassword = false"
        />
      </el-dialog>

      <!-- Avatar Upload Dialog -->
      <el-dialog
        v-model="showAvatarUpload"
        title="Change Avatar"
        width="90%"
        :max-width="400"
        :close-on-click-modal="false"
      >
        <div class="avatar-upload">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleAvatarChange"
            accept="image/*"
            drag
          >
            <div class="upload-content">
              <el-icon class="upload-icon"><Upload /></el-icon>
              <div class="upload-text">
                <p>Drop image here or <em>click to upload</em></p>
                <p class="upload-hint">JPG, PNG files up to 5MB</p>
              </div>
            </div>
          </el-upload>
          <div v-if="selectedAvatar" class="avatar-preview">
            <img :src="avatarPreview" alt="Avatar preview" />
          </div>
        </div>

        <template #footer>
          <div class="dialog-footer">
            <el-button @click="handleCancelAvatar">Cancel</el-button>
            <el-button
              type="primary"
              @click="handleUploadAvatar"
              :loading="formLoading"
              :disabled="!selectedAvatar"
            >
              Upload
            </el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAuthStore } from '@/stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import ProfileForm from '@/components/profile/ProfileForm.vue'
import PasswordChangeForm from '@/components/profile/PasswordChangeForm.vue'

const router = useRouter()
const userStore = useUserStore()
const authStore = useAuthStore()

// Reactive data
const showEditProfile = ref(false)
const showChangePassword = ref(false)
const showAvatarUpload = ref(false)
const formLoading = ref(false)
const selectedAvatar = ref(null)
const avatarPreview = ref('')
const uploadRef = ref()

// Methods
const handleAvatarError = (event) => {
  event.target.src = '/default-avatar.png'
}

const handleUpdateProfile = async (profileData) => {
  try {
    formLoading.value = true
    await userStore.updateProfile(profileData)
    showEditProfile.value = false
  } catch (error) {
    console.error('Failed to update profile:', error)
  } finally {
    formLoading.value = false
  }
}

const handleChangePassword = async (passwordData) => {
  try {
    formLoading.value = true
    await userStore.changePassword(passwordData)
    showChangePassword.value = false
  } catch (error) {
    console.error('Failed to change password:', error)
  } finally {
    formLoading.value = false
  }
}

const handleAvatarChange = (file) => {
  selectedAvatar.value = file.raw

  // Create preview
  const reader = new FileReader()
  reader.onload = (e) => {
    avatarPreview.value = e.target.result
  }
  reader.readAsDataURL(file.raw)
}

const handleUploadAvatar = async () => {
  if (!selectedAvatar.value) return

  try {
    formLoading.value = true
    await userStore.uploadAvatar(selectedAvatar.value)
    handleCancelAvatar()
  } catch (error) {
    console.error('Failed to upload avatar:', error)
  } finally {
    formLoading.value = false
  }
}

const handleCancelAvatar = () => {
  showAvatarUpload.value = false
  selectedAvatar.value = null
  avatarPreview.value = ''
  uploadRef.value?.clearFiles()
}

const handleDeleteAccount = async () => {
  try {
    const { value: password } = await ElMessageBox.prompt(
      'This action cannot be undone. Please enter your password to confirm account deletion.',
      'Delete Account',
      {
        confirmButtonText: 'Delete Account',
        cancelButtonText: 'Cancel',
        inputType: 'password',
        inputPlaceholder: 'Enter your password',
        inputValidator: (value) => {
          if (!value) {
            return 'Password is required'
          }
          return true
        },
        type: 'error',
      }
    )

    await ElMessageBox.confirm(
      'Are you absolutely sure you want to delete your account? This action cannot be undone.',
      'Final Confirmation',
      {
        confirmButtonText: 'Yes, Delete My Account',
        cancelButtonText: 'Cancel',
        type: 'error',
      }
    )

    formLoading.value = true
    await userStore.deleteAccount(password)

    // Logout and redirect to home
    await authStore.logout()
    router.push({ name: 'home' })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete account:', error)
    }
  } finally {
    formLoading.value = false
  }
}

// Lifecycle
onMounted(async () => {
  await Promise.all([
    userStore.fetchProfile(),
    userStore.fetchStats()
  ])
})
</script>

<style scoped>
.profile-page {
  padding: 20px 0 40px;
  min-height: 60vh;
  background: #f5f7fa;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 16px;
  color: #909399;
  margin: 0;
}

.loading-container {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.profile-card,
.stats-card,
.details-card,
.actions-card {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.profile-header {
  display: flex;
  gap: 20px;
  align-items: center;
}

.avatar-section {
  flex-shrink: 0;
}

.avatar-container {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.avatar-container:hover .avatar-overlay {
  opacity: 1;
}

.profile-info {
  flex: 1;
  min-width: 0;
}

.profile-info h2 {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.user-email {
  font-size: 16px;
  color: #606266;
  margin: 0 0 4px 0;
}

.member-since {
  font-size: 14px;
  color: #909399;
  margin: 0 0 12px 0;
}

.profile-status {
  display: flex;
  gap: 8px;
}

.stats-card h3,
.details-card h3,
.actions-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
}

.card-header h3 {
  margin: 0;
  padding: 0;
  border: none;
}

.details-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-item label {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
}

.detail-item span {
  font-size: 16px;
  color: #303133;
}

.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.avatar-upload {
  text-align: center;
}

.upload-content {
  padding: 40px 20px;
}

.upload-icon {
  font-size: 48px;
  color: #c0c4cc;
  margin-bottom: 16px;
}

.upload-text p {
  margin: 8px 0;
  color: #606266;
}

.upload-hint {
  font-size: 12px;
  color: #909399;
}

.avatar-preview {
  margin-top: 20px;
}

.avatar-preview img {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e4e7ed;
}

.dialog-footer {
  display: flex;
  gap: 12px;
  justify-content: center;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .profile-page {
    padding: 16px 0 32px;
  }

  .container {
    padding: 0 16px;
  }

  .page-header {
    margin-bottom: 20px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .page-subtitle {
    font-size: 14px;
  }

  .profile-card,
  .stats-card,
  .details-card,
  .actions-card {
    padding: 16px;
  }

  .profile-header {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }

  .avatar-container {
    width: 80px;
    height: 80px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .details-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .card-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    width: 100%;
  }

  .dialog-footer {
    flex-direction: column;
  }

  .dialog-footer .el-button {
    width: 100%;
  }
}
</style>
