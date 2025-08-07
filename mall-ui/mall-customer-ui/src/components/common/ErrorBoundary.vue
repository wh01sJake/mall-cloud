<template>
  <div v-if="hasError" class="error-boundary">
    <el-alert
      title="Something went wrong"
      type="error"
      :description="errorMessage"
      show-icon
      :closable="false"
    />
    <div class="error-actions">
      <el-button type="primary" @click="retry">
        Try Again
      </el-button>
      <el-button @click="goHome">
        Go to Home
      </el-button>
    </div>
  </div>
  <slot v-else />
</template>

<script setup>
import { ref, onErrorCaptured } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const hasError = ref(false)
const errorMessage = ref('')

onErrorCaptured((error, instance, info) => {
  console.error('Error caught by boundary:', error, info)
  hasError.value = true
  errorMessage.value = error.message || 'An unexpected error occurred'
  return false // Prevent error from propagating
})

const retry = () => {
  hasError.value = false
  errorMessage.value = ''
  // Force component re-render
  window.location.reload()
}

const goHome = () => {
  hasError.value = false
  errorMessage.value = ''
  router.push('/')
}
</script>

<style scoped>
.error-boundary {
  padding: 20px;
  text-align: center;
}

.error-actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
  justify-content: center;
}
</style>
