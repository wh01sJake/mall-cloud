<script setup>
import {ref, reactive} from "vue";
import {User, Lock, View, Hide} from "@element-plus/icons-vue";
import adminApi from "@/api/admin.js";
import {ElMessage} from "element-plus";
import {useRouter} from "vue-router";
import {useTokenStore} from "@/store/token.js";

const tokenStore = useTokenStore()
const router = useRouter()

const loginFormRef = ref()
const loading = ref(false)

const user = ref({
    username: '',
    password: ''
})

const rules = reactive({
    username: [
        { required: true, message: 'Please enter username', trigger: 'blur' },
        { min: 3, max: 20, message: 'Username must be 3-20 characters', trigger: 'blur' }
    ],
    password: [
        { required: true, message: 'Please enter password', trigger: 'blur' },
        { min: 6, max: 50, message: 'Password must be at least 6 characters', trigger: 'blur' }
    ]
})

const login = async () => {
    if (!loginFormRef.value) return

    try {
        const valid = await loginFormRef.value.validate()
        if (!valid) return

        loading.value = true

        const result = await adminApi.login(user.value)

        if (result.code == 0) {
            ElMessage.success(result.data.message || 'Login successful')
            tokenStore.setToken(result.data.token)
            router.push('/')
        } else {
            ElMessage.error(result.msg || 'Login failed')
        }
    } catch (error) {
        console.error('Login error:', error)
        ElMessage.error('Login failed. Please try again.')
    } finally {
        loading.value = false
    }
}

const handleKeyPress = (event) => {
    if (event.key === 'Enter') {
        login()
    }
}
</script>

<template>
    <div class="login-container">
        <div class="login-background">
            <!-- Background decorative elements -->
            <div class="bg-decoration"></div>
            <div class="bg-circles">
                <div class="circle circle-1"></div>
                <div class="circle circle-2"></div>
                <div class="circle circle-3"></div>
            </div>
        </div>

        <div class="login-content">
            <!-- Logo and branding -->
            <div class="login-header">
                <div class="logo">
                    <div class="logo-icon">🛒</div>
                    <h1 class="logo-text">VapeMall</h1>
                </div>
                <p class="login-subtitle">Admin Dashboard</p>
            </div>

            <!-- Login form -->
            <el-form
                ref="loginFormRef"
                class="login-form"
                size="large"
                autocomplete="off"
                :model="user"
                :rules="rules"
                @keyup.enter="login"
            >
                <el-form-item prop="username">
                    <el-input
                        v-model="user.username"
                        :prefix-icon="User"
                        placeholder="Enter your username"
                        class="login-input"
                        @keypress="handleKeyPress"
                    />
                </el-form-item>

                <el-form-item prop="password">
                    <el-input
                        v-model="user.password"
                        :prefix-icon="Lock"
                        type="password"
                        placeholder="Enter your password"
                        class="login-input"
                        show-password
                        @keypress="handleKeyPress"
                    />
                </el-form-item>

                <el-form-item class="remember-forgot">
                    <div class="remember-me">
                        <el-checkbox>Remember me</el-checkbox>
                    </div>
                    <el-link type="primary" :underline="false" class="forgot-link">
                        Forgot Password?
                    </el-link>
                </el-form-item>

                <!-- Login button -->
                <el-form-item>
                    <el-button
                        class="login-button"
                        type="primary"
                        size="large"
                        :loading="loading"
                        @click="login"
                        block
                    >
                        {{ loading ? 'Signing In...' : 'Sign In' }}
                    </el-button>
                </el-form-item>
            </el-form>

            <!-- Footer -->
            <div class="login-footer">
                <p>&copy; 2025 VapeMall. All rights reserved.</p>
            </div>
        </div>
    </div>
</template>

<style scoped>
.login-container {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    overflow: hidden;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-background {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 1;
}

.bg-decoration {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="75" cy="75" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="50" cy="10" r="0.5" fill="rgba(255,255,255,0.05)"/><circle cx="10" cy="60" r="0.5" fill="rgba(255,255,255,0.05)"/><circle cx="90" cy="40" r="0.5" fill="rgba(255,255,255,0.05)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
    opacity: 0.3;
}

.bg-circles {
    position: absolute;
    width: 100%;
    height: 100%;
}

.circle {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    animation: float 6s ease-in-out infinite;
}

.circle-1 {
    width: 200px;
    height: 200px;
    top: 10%;
    left: 10%;
    animation-delay: 0s;
}

.circle-2 {
    width: 150px;
    height: 150px;
    top: 60%;
    right: 10%;
    animation-delay: 2s;
}

.circle-3 {
    width: 100px;
    height: 100px;
    bottom: 20%;
    left: 60%;
    animation-delay: 4s;
}

@keyframes float {
    0%, 100% { transform: translateY(0px) rotate(0deg); }
    50% { transform: translateY(-20px) rotate(180deg); }
}

.login-content {
    position: relative;
    z-index: 2;
    width: 100%;
    max-width: 420px;
    padding: 40px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
}

.login-header {
    text-align: center;
    margin-bottom: 40px;
}

.logo {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    margin-bottom: 8px;
}

.logo-icon {
    font-size: 32px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
}

.logo-text {
    font-size: 28px;
    font-weight: 700;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin: 0;
}

.login-subtitle {
    color: #666;
    font-size: 16px;
    font-weight: 500;
    margin: 0;
}

.login-form {
    margin-bottom: 30px;
}

.login-input {
    margin-bottom: 8px;
}

.login-input :deep(.el-input__wrapper) {
    border-radius: 12px;
    padding: 12px 16px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    border: 1px solid #e1e5e9;
    transition: all 0.3s ease;
}

.login-input :deep(.el-input__wrapper:hover) {
    border-color: #667eea;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.login-input :deep(.el-input__wrapper.is-focus) {
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.remember-forgot {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 20px 0;
}

.remember-me :deep(.el-checkbox__label) {
    color: #666;
    font-size: 14px;
}

.forgot-link {
    font-size: 14px;
    font-weight: 500;
}

.login-button {
    width: 100%;
    height: 48px;
    border-radius: 12px;
    font-size: 16px;
    font-weight: 600;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    transition: all 0.3s ease;
}

.login-button:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.login-button:active {
    transform: translateY(0);
}

.login-footer {
    text-align: center;
    color: #999;
    font-size: 12px;
    margin-top: 20px;
}

.login-footer p {
    margin: 0;
}

/* Responsive design */
@media (max-width: 480px) {
    .login-content {
        margin: 20px;
        padding: 30px 20px;
        max-width: none;
    }

    .logo-text {
        font-size: 24px;
    }

    .circle {
        display: none;
    }
}

/* Loading state */
.login-button.is-loading {
    pointer-events: none;
}

/* Form validation styles */
:deep(.el-form-item.is-error .el-input__wrapper) {
    border-color: #f56c6c !important;
    box-shadow: 0 0 0 3px rgba(245, 108, 108, 0.1) !important;
}

:deep(.el-form-item__error) {
    font-size: 12px;
    margin-top: 4px;
}
</style>