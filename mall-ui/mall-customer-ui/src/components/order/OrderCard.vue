<template>
  <div class="order-card">
    <div class="order-header">
      <div class="order-info">
        <span class="order-no">Order #{{ order.orderNo }}</span>
        <span class="order-date">{{ formatDate(order.createTime) }}</span>
      </div>
      <div class="order-status">
        <el-tag :type="getStatusType(order.status)">
          {{ getStatusText(order.status) }}
        </el-tag>
      </div>
    </div>

    <div class="order-items">
      <div 
        v-for="item in order.orderItems" 
        :key="item.id"
        class="order-item"
      >
        <div class="item-image">
          <img
            :src="item.productImage || '/placeholder-product.jpg'"
            :alt="item.productName"
            @error="handleImageError"
          />
        </div>
        <div class="item-info">
          <h4 class="item-name">{{ item.productName }}</h4>
          <p class="item-specs" v-if="item.productAttr">{{ item.productAttr }}</p>
          <div class="item-price-qty">
            <span class="item-price">€{{ item.currentUnitPrice }}</span>
            <span class="item-qty">x{{ item.quantity }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="order-footer">
      <div class="order-total">
        <span class="total-label">Total: </span>
        <span class="total-amount">€{{ order.paymentAmount || '0.00' }}</span>
      </div>
      <div class="order-actions">
        <el-button
          v-if="order.status === 1"
          type="primary"
          size="small"
          @click="handlePay"
          :loading="loading"
        >
          Pay Now
        </el-button>
        <el-button
          v-if="order.status === 1"
          size="small"
          @click="handleCancel"
          :loading="loading"
        >
          Cancel
        </el-button>
        <el-button
          v-if="order.status === 3"
          type="primary"
          size="small"
          @click="handleConfirm"
          :loading="loading"
        >
          Confirm Receipt
        </el-button>
        <el-button 
          size="small"
          @click="viewDetail"
        >
          View Details
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps({
  order: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['refresh'])

const router = useRouter()
const orderStore = useOrderStore()
const loading = ref(false)

// Format date
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// Get status type for tag color
const getStatusType = (status) => {
  const statusMap = {
    0: 'danger',    // Cancelled
    1: 'warning',   // Pending Payment
    2: 'info',      // Paid
    3: 'primary',   // Shipped
    4: 'success',   // Completed
    5: 'info'       // Closed
  }
  return statusMap[status] || 'info'
}

// Get status text
const getStatusText = (status) => {
  const statusMap = {
    0: 'Cancelled',
    1: 'Pending Payment',
    2: 'Paid',
    3: 'Shipped',
    4: 'Completed',
    5: 'Closed'
  }
  return statusMap[status] || `Status ${status}`
}

// Handle image error
const handleImageError = (event) => {
  // Prevent infinite loop by checking if we already tried the placeholder
  if (event.target.src.includes('placeholder-product.jpg')) {
    // Remove the error handler to prevent further attempts
    event.target.onerror = null
    // Set a data URL for a simple gray placeholder
    event.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjYwIiBoZWlnaHQ9IjYwIiBmaWxsPSIjRjVGNUY1Ii8+CjxwYXRoIGQ9Ik0yMCAyMEg0MFY0MEgyMFYyMFoiIGZpbGw9IiNEREREREQiLz4KPC9zdmc+'
  } else {
    event.target.src = '/placeholder-product.jpg'
  }
}

// Handle payment
const handlePay = async () => {
  try {
    loading.value = true
    await orderStore.payOrder(props.order.orderNo)
    emit('refresh')
  } catch (error) {
    console.error('Payment failed:', error)
  } finally {
    loading.value = false
  }
}

// Handle cancel
const handleCancel = async () => {
  try {
    await ElMessageBox.confirm(
      'Are you sure you want to cancel this order?',
      'Confirm Cancellation',
      {
        confirmButtonText: 'Yes, Cancel',
        cancelButtonText: 'No',
        type: 'warning',
      }
    )
    
    loading.value = true
    await orderStore.cancelOrder(props.order.orderNo)
    emit('refresh')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Cancel failed:', error)
    }
  } finally {
    loading.value = false
  }
}

// Handle confirm receipt
const handleConfirm = async () => {
  try {
    await ElMessageBox.confirm(
      'Confirm that you have received this order?',
      'Confirm Receipt',
      {
        confirmButtonText: 'Yes, Received',
        cancelButtonText: 'Not Yet',
        type: 'info',
      }
    )
    
    loading.value = true
    await orderStore.confirmOrder(props.order.orderNo)
    emit('refresh')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Confirm failed:', error)
    }
  } finally {
    loading.value = false
  }
}

// View order detail
const viewDetail = () => {
  router.push({ name: 'order-detail', params: { orderNo: props.order.orderNo } })
}
</script>

<style scoped>
.order-card {
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 16px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e4e7ed;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-no {
  font-weight: 600;
  color: #303133;
}

.order-date {
  font-size: 14px;
  color: #909399;
}

.order-items {
  padding: 16px 20px;
}

.order-item {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.order-item:last-child {
  margin-bottom: 0;
}

.item-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-specs {
  font-size: 12px;
  color: #909399;
  margin: 0 0 8px 0;
}

.item-price-qty {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-price {
  font-weight: 600;
  color: #e6a23c;
}

.item-qty {
  font-size: 14px;
  color: #909399;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f8f9fa;
  border-top: 1px solid #e4e7ed;
}

.order-total {
  display: flex;
  align-items: center;
  gap: 8px;
}

.total-label {
  color: #606266;
}

.total-amount {
  font-size: 18px;
  font-weight: 600;
  color: #e6a23c;
}

.order-actions {
  display: flex;
  gap: 8px;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .order-header {
    padding: 12px 16px;
  }
  
  .order-items {
    padding: 12px 16px;
  }
  
  .order-footer {
    flex-direction: column;
    gap: 12px;
    padding: 12px 16px;
  }
  
  .order-actions {
    width: 100%;
    justify-content: center;
  }
  
  .item-image {
    width: 50px;
    height: 50px;
  }
  
  .item-name {
    font-size: 13px;
  }
}
</style>
