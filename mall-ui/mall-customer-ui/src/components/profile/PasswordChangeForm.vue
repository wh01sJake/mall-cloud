<template>
  <el-form
    ref="formRef"
    :model="formData"
    :rules="formRules"
    label-width="140px"
    class="password-form"
  >
    <el-form-item label="Current Password" prop="oldPassword">
      <el-input
        v-model="formData.oldPassword"
        type="password"
        placeholder="Enter current password"
        show-password
        maxlength="20"
      />
    </el-form-item>

    <el-form-item label="New Password" prop="newPassword">
      <el-input
        v-model="formData.newPassword"
        type="password"
        placeholder="Enter new password"
        show-password
        maxlength="20"
      />
      <div class="password-tips">
        <p>Password requirements:</p>
        <ul>
          <li>At least 6 characters long</li>
          <li>Must be different from current password</li>
        </ul>
      </div>
    </el-form-item>

    <el-form-item label="Confirm Password" prop="confirmPassword">
      <el-input
        v-model="formData.confirmPassword"
        type="password"
        placeholder="Confirm new password"
        show-password
        maxlength="20"
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
          Change Password
        </el-button>
      </div>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, reactive } from 'vue'

const props = defineProps({
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['submit', 'cancel'])

const formRef = ref()

// Form data
const formData = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// Custom validator for confirm password
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('Please confirm your new password'))
  } else if (value !== formData.newPassword) {
    callback(new Error('Passwords do not match'))
  } else {
    callback()
  }
}

// Custom validator for new password
const validateNewPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('Please enter new password'))
  } else if (value.length < 6) {
    callback(new Error('Password must be at least 6 characters'))
  } else if (value === formData.oldPassword) {
    callback(new Error('New password must be different from current password'))
  } else {
    callback()
    // Re-validate confirm password if it has been entered
    if (formData.confirmPassword !== '') {
      formRef.value.validateField('confirmPassword')
    }
  }
}

// Form validation rules
const formRules = {
  oldPassword: [
    { required: true, message: 'Please enter current password', trigger: 'blur' }
  ],
  newPassword: [
    { validator: validateNewPassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
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
  resetForm()
  emit('cancel')
}

// Reset form
const resetForm = () => {
  Object.assign(formData, {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
  formRef.value?.clearValidate()
}

// Expose reset method
defineExpose({
  resetForm
})
</script>

<style scoped>
.password-form {
  max-width: 600px;
}

.password-tips {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.password-tips p {
  margin: 0 0 4px 0;
  font-weight: 500;
}

.password-tips ul {
  margin: 0;
  padding-left: 16px;
}

.password-tips li {
  margin-bottom: 2px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .password-form {
    max-width: 100%;
  }
  
  .password-form :deep(.el-form-item__label) {
    width: 120px !important;
  }
  
  .form-actions {
    justify-content: center;
  }
  
  .form-actions .el-button {
    flex: 1;
  }
}
</style>
