<template>
  <div class="register-page">
    <div class="container">
      <div class="register-container">
        <div class="register-header">
          <h1>Create Account</h1>
          <p>Join VapeMall and start shopping</p>
        </div>

        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="register-form"
          @submit.prevent="handleRegister"
        >
          <el-form-item prop="inviteCode">
            <el-input
              v-model="registerForm.inviteCode"
              placeholder="Invite Code"
              size="large"
              prefix-icon="Key"
            />
          </el-form-item>

          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="Username"
              size="large"
              prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="email">
            <el-input
              v-model="registerForm.email"
              type="email"
              placeholder="Email Address"
              size="large"
              prefix-icon="Message"
            />
          </el-form-item>

          <el-form-item prop="phone">
            <el-input
              v-model="registerForm.phone"
              placeholder="Phone Number (Optional)"
              size="large"
              prefix-icon="Phone"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="Password"
              size="large"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="Confirm Password"
              size="large"
              prefix-icon="Lock"
              show-password
              @keyup.enter="handleRegister"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="authStore.loading"
              @click="handleRegister"
              class="register-button"
            >
              Create Account
            </el-button>
          </el-form-item>
        </el-form>

        <div class="register-footer">
          <p>
            Already have an account?
            <router-link to="/login" class="login-link">
              Sign in here
            </router-link>
          </p>
        </div>

        <!-- Invite Codes Info -->
        <div class="invite-info">
          <el-alert
            title="Valid Invite Codes"
            type="success"
            :closable="false"
            show-icon
          >
            <p>Use one of these invite codes to register:</p>
            <ul>
              <li><strong>VAPE2024</strong></li>
              <li><strong>WELCOME123</strong></li>
              <li><strong>NEWUSER2024</strong></li>
              <li><strong>VAPEHUB2024</strong></li>
              <li><strong>INVITE123</strong></li>
            </ul>
          </el-alert>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const registerFormRef = ref()
const registerForm = reactive({
  inviteCode: '',
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('Passwords do not match'))
  } else {
    callback()
  }
}

const registerRules = {
  inviteCode: [
    { required: true, message: 'Please enter an invite code', trigger: 'blur' }
  ],
  username: [
    { required: true, message: 'Please enter a username', trigger: 'blur' },
    { min: 3, message: 'Username must be at least 3 characters', trigger: 'blur' },
    { max: 20, message: 'Username cannot exceed 20 characters', trigger: 'blur' }
  ],
  email: [
    { required: true, message: 'Please enter an email address', trigger: 'blur' },
    { type: 'email', message: 'Please enter a valid email address', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^[\d\s\-\+\(\)]*$/, message: 'Please enter a valid phone number', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Please enter a password', trigger: 'blur' },
    { min: 6, message: 'Password must be at least 6 characters', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: 'Please confirm your password', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    const valid = await registerFormRef.value.validate()
    if (!valid) return
    
    const success = await authStore.register(registerForm)
    
    if (success) {
      // Redirect to login page
      router.push('/login')
    }
  } catch (error) {
    console.error('Registration validation error:', error)
  }
}
</script>

<style scoped>
.register-page {
  min-height: calc(100vh - 160px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 40px 0;
}

.register-container {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 450px;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h1 {
  color: #333;
  margin-bottom: 10px;
  font-size: 2rem;
}

.register-header p {
  color: #666;
  font-size: 1rem;
}

.register-form {
  margin-bottom: 20px;
}

.register-button {
  width: 100%;
  height: 48px;
  font-size: 1rem;
  font-weight: 600;
}

.register-footer {
  text-align: center;
  margin-bottom: 20px;
}

.register-footer p {
  color: #666;
}

.login-link {
  color: #409eff;
  text-decoration: none;
  font-weight: 600;
}

.login-link:hover {
  text-decoration: underline;
}

.invite-info {
  margin-top: 20px;
}

.invite-info :deep(.el-alert__content) {
  line-height: 1.6;
}

.invite-info ul {
  margin: 10px 0 0 20px;
}

.invite-info li {
  margin: 5px 0;
}

/* Mobile Styles */
@media (max-width: 768px) {
  .register-container {
    margin: 0 20px;
    padding: 30px 20px;
  }
  
  .register-header h1 {
    font-size: 1.5rem;
  }
}
</style>
