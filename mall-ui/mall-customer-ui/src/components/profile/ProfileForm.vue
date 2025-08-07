<template>
  <el-form
    ref="formRef"
    :model="formData"
    :rules="formRules"
    label-width="120px"
    class="profile-form"
  >
    <el-form-item label="Username" prop="username">
      <el-input
        v-model="formData.username"
        placeholder="Enter username"
        maxlength="20"
        show-word-limit
      />
    </el-form-item>

    <el-form-item label="Email" prop="email">
      <el-input
        v-model="formData.email"
        type="email"
        placeholder="Enter email address"
        maxlength="50"
      />
    </el-form-item>

    <el-form-item label="Phone" prop="phone">
      <el-input
        v-model="formData.phone"
        placeholder="Enter phone number"
        maxlength="11"
      />
    </el-form-item>

    <el-form-item label="Nickname" prop="nickname">
      <el-input
        v-model="formData.nickname"
        placeholder="Enter nickname (optional)"
        maxlength="30"
        show-word-limit
      />
    </el-form-item>

    <el-form-item label="Gender" prop="gender">
      <el-radio-group v-model="formData.gender">
        <el-radio :label="0">Not specified</el-radio>
        <el-radio :label="1">Male</el-radio>
        <el-radio :label="2">Female</el-radio>
      </el-radio-group>
    </el-form-item>

    <el-form-item label="Birthday" prop="birthday">
      <el-date-picker
        v-model="formData.birthday"
        type="date"
        placeholder="Select birthday"
        format="YYYY-MM-DD"
        value-format="YYYY-MM-DD"
        :disabled-date="disabledDate"
      />
    </el-form-item>

    <el-form-item>
      <div class="form-actions">
        <el-button @click="handleCancel">Cancel</el-button>
        <el-button 
          type="primary" 
          @click="handleSubmit"
          :loading="loading"
        >
          Update Profile
        </el-button>
      </div>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  profile: {
    type: Object,
    default: null
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['submit', 'cancel'])

const userStore = useUserStore()
const formRef = ref()

// Form data
const formData = reactive({
  username: '',
  email: '',
  phone: '',
  nickname: '',
  gender: 0,
  birthday: ''
})

// Form validation rules
const formRules = {
  username: [
    { required: true, message: 'Please enter username', trigger: 'blur' },
    { min: 3, max: 20, message: 'Username should be 3-20 characters', trigger: 'blur' }
  ],
  email: [
    { required: true, message: 'Please enter email address', trigger: 'blur' },
    { type: 'email', message: 'Please enter a valid email address', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: 'Please enter a valid phone number', trigger: 'blur' }
  ],
  nickname: [
    { max: 30, message: 'Nickname should be less than 30 characters', trigger: 'blur' }
  ]
}

// Disable future dates for birthday
const disabledDate = (time) => {
  return time.getTime() > Date.now()
}

// Handle form submission
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    emit('submit', { ...formData })
  } catch (error) {
    console.error('Form validation failed:', error)
  }
}

// Handle cancel
const handleCancel = () => {
  emit('cancel')
}

// Initialize form data when profile prop changes
watch(() => props.profile, (newProfile) => {
  if (newProfile) {
    Object.assign(formData, {
      username: newProfile.username || '',
      email: newProfile.email || '',
      phone: newProfile.phone || '',
      nickname: newProfile.nickname || '',
      gender: newProfile.gender || 0,
      birthday: newProfile.birthday || ''
    })
  }
}, { immediate: true })

// Reset form
const resetForm = () => {
  Object.assign(formData, {
    username: '',
    email: '',
    phone: '',
    nickname: '',
    gender: 0,
    birthday: ''
  })
  formRef.value?.clearValidate()
}

// Expose reset method
defineExpose({
  resetForm
})
</script>

<style scoped>
.profile-form {
  max-width: 600px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .profile-form {
    max-width: 100%;
  }
  
  .profile-form :deep(.el-form-item__label) {
    width: 100px !important;
  }
  
  .form-actions {
    justify-content: center;
  }
  
  .form-actions .el-button {
    flex: 1;
  }
}
</style>
