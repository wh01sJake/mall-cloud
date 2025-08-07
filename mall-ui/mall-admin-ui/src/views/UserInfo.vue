<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import adminApi from '@/api/admin.js'
import { useAdminInfoStore } from '@/store/adminInfo.js'

const adminInfoStore = useAdminInfoStore()
const userInfo = ref({})
const loading = ref(false)

// Load current user info
onMounted(() => {
    loadUserInfo()
})

const loadUserInfo = () => {
    loading.value = true
    adminApi.adminInfo().then(result => {
        if (result.code === 0) {
            userInfo.value = result.data
            adminInfoStore.setAdminInfo(result.data)
        } else {
            ElMessage.error('Failed to load user info')
        }
    }).finally(() => {
        loading.value = false
    })
}

const updateUserInfo = () => {
    loading.value = true
    adminApi.updateById(userInfo.value).then(result => {
        if (result.code === 0) {
            ElMessage.success('User info updated successfully')
            adminInfoStore.setAdminInfo(userInfo.value)
        } else {
            ElMessage.error(result.msg || 'Failed to update user info')
        }
    }).finally(() => {
        loading.value = false
    })
}
</script>

<template>
    <el-card class="user-info-card">
        <template #header>
            <div class="card-header">
                <span>基本资料 (Basic Info)</span>
            </div>
        </template>
        
        <el-form :model="userInfo" label-width="120px" v-loading="loading">
            <el-form-item label="用户名">
                <el-input v-model="userInfo.username" placeholder="请输入用户名" />
            </el-form-item>
            
            <el-form-item label="邮箱">
                <el-input v-model="userInfo.email" placeholder="请输入邮箱" />
            </el-form-item>
            
            <el-form-item label="电话">
                <el-input v-model="userInfo.phone" placeholder="请输入电话号码" />
            </el-form-item>
            
            <el-form-item label="角色">
                <el-select v-model="userInfo.role" placeholder="请选择角色">
                    <el-option label="管理员" :value="0" />
                    <el-option label="普通员工" :value="1" />
                </el-select>
            </el-form-item>
            
            <el-form-item label="状态">
                <el-select v-model="userInfo.status" placeholder="请选择状态">
                    <el-option label="启用" :value="1" />
                    <el-option label="禁用" :value="0" />
                </el-select>
            </el-form-item>
            
            <el-form-item>
                <el-button type="primary" @click="updateUserInfo" :loading="loading">
                    更新信息
                </el-button>
                <el-button @click="loadUserInfo">
                    重置
                </el-button>
            </el-form-item>
        </el-form>
    </el-card>
</template>

<style scoped>
.user-info-card {
    max-width: 600px;
    margin: 0 auto;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>
