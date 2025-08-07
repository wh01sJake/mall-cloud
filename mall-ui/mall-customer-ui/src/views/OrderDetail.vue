<template>
  <div class="order-detail-page">
    <div class="container">
      <!-- Loading State -->
      <div v-if="orderStore.loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <!-- Order Detail Content -->
      <div v-else-if="order" class="order-detail">
        <!-- Header -->
        <div class="detail-header">
          <div class="header-left">
            <el-button
              type="text"
              @click="goBack"
              class="back-button"
            >
              <el-icon><ArrowLeft /></el-icon>
              Back to Orders
            </el-button>
            <h1>Order Details</h1>
          </div>
          <div class="header-right">
            <el-tag :type="getStatusType(order.status)" size="large">
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>
        </div>

        <!-- Order Info Card -->
        <div class="info-card">
          <h3>Order Information</h3>
          <div class="info-grid">
            <div class="info-item">
              <label>Order Number:</label>
              <span>{{ order.orderNo }}</span>
            </div>
            <div class="info-item">
              <label>Order Date:</label>
              <span>{{ formatDate(order.createTime) }}</span>
            </div>
            <div class="info-item">
              <label>Payment Method:</label>
              <span>{{ getPaymentMethodText(order.paymentType) }}</span>
            </div>
            <div class="info-item">
              <label>Total Amount:</label>
              <span class="amount">€{{ order.paymentAmount || '0.00' }}</span>
            </div>
          </div>
          <div v-if="order.remark" class="order-remark">
            <label>Order Remark:</label>
            <p>{{ order.remark }}</p>
          </div>
        </div>

        <!-- Shipping Address Card -->
        <div v-if="order.receiverAddress" class="info-card">
          <h3>Shipping Address</h3>
          <div class="address-info">
            <div class="address-line">
              <strong>{{ order.receiverName }}</strong>
              <span class="phone">{{ order.receiverPhone }}</span>
            </div>
            <div class="address-line">
              {{ order.receiverAddress }}
            </div>
            <div v-if="order.receiverPostCode" class="address-line">
              Postal Code: {{ order.receiverPostCode }}
            </div>
          </div>
        </div>

        <!-- Order Items Card -->
        <div class="info-card">
          <h3>Order Items</h3>
          <div class="items-list">
            <div
              v-for="item in order.orderItems"
              :key="item.id"
              class="item-row"
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
                <p v-if="item.productAttr" class="item-specs">{{ item.productAttr }}</p>
                <div class="item-details">
                  <span class="item-price">€{{ item.currentUnitPrice }}</span>
                  <span class="item-qty">Qty: {{ item.quantity }}</span>
                  <span class="item-total">Subtotal: €{{ item.totalPrice }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Order Summary Card -->
        <div class="info-card">
          <h3>Order Summary</h3>
          <div class="summary-rows">
            <div class="summary-row">
              <span>Subtotal:</span>
              <span>€{{ calculateSubtotal() }}</span>
            </div>
            <div class="summary-row">
              <span>Shipping:</span>
              <span>€{{ order.postageFee || '0.00' }}</span>
            </div>
            <div v-if="order.discountAmount" class="summary-row">
              <span>Discount:</span>
              <span class="discount">-€{{ order.discountAmount }}</span>
            </div>
            <div class="summary-row total-row">
              <span>Total:</span>
              <span class="total-amount">€{{ order.paymentAmount || '0.00' }}</span>
            </div>
          </div>
        </div>

        <!-- Order Actions -->
        <div class="order-actions">
          <el-button
            v-if="order.status === 1"
            type="primary"
            size="large"
            @click="handlePay"
            :loading="actionLoading"
          >
            Pay Now
          </el-button>
          <el-button
            v-if="order.status === 1"
            size="large"
            @click="handleCancel"
            :loading="actionLoading"
          >
            Cancel Order
          </el-button>
          <el-button
            v-if="order.status === 3"
            type="primary"
            size="large"
            @click="handleConfirm"
            :loading="actionLoading"
          >
            Confirm Receipt
          </el-button>
        </div>
      </div>

      <!-- Error State -->
      <div v-else class="error-state">
        <el-empty
          :image-size="120"
          description="Order not found"
        >
          <el-button type="primary" @click="goBack">
            Back to Orders
          </el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/order'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const orderStore = useOrderStore()

// Reactive data
const actionLoading = ref(false)

// Computed properties
const order = computed(() => orderStore.currentOrder)

// Methods
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

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

const getPaymentMethodText = (paymentType) => {
  const paymentMap = {
    1: 'Stripe',
    2: 'PayPal',
    3: 'Credit Card',
    4: 'Cash on Delivery'
  }
  return paymentMap[paymentType] || 'Not specified'
}

const calculateSubtotal = () => {
  if (!order.value?.orderItems) return '0.00'
  const subtotal = order.value.orderItems.reduce((total, item) => {
    return total + (parseFloat(item.totalPrice) || 0)
  }, 0)
  return subtotal.toFixed(2)
}

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

const goBack = () => {
  router.push({ name: 'orders' })
}

const handlePay = async () => {
  try {
    actionLoading.value = true
    await orderStore.payOrder(order.value.orderNo)
    // Refresh order detail
    await orderStore.fetchOrderDetail(route.params.orderNo)
  } catch (error) {
    console.error('Payment failed:', error)
  } finally {
    actionLoading.value = false
  }
}

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

    actionLoading.value = true
    await orderStore.cancelOrder(order.value.orderNo)
    // Refresh order detail
    await orderStore.fetchOrderDetail(route.params.orderNo)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Cancel failed:', error)
    }
  } finally {
    actionLoading.value = false
  }
}

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

    actionLoading.value = true
    await orderStore.confirmOrder(order.value.orderNo)
    // Refresh order detail
    await orderStore.fetchOrderDetail(route.params.orderNo)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Confirm failed:', error)
    }
  } finally {
    actionLoading.value = false
  }
}

// Lifecycle
onMounted(async () => {
  const orderNo = route.params.orderNo
  if (orderNo) {
    try {
      await orderStore.fetchOrderDetail(orderNo)
    } catch (error) {
      console.error('Failed to load order detail:', error)
    }
  }
})
</script>

<style scoped>
.order-detail-page {
  padding: 20px 0 40px;
  min-height: 60vh;
  background: #f5f7fa;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.loading-container {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.back-button {
  align-self: flex-start;
  padding: 0;
  font-size: 14px;
  color: #409eff;
}

.back-button:hover {
  color: #66b1ff;
}

.detail-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.info-card {
  background: white;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.info-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item label {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
}

.info-item span {
  font-size: 16px;
  color: #303133;
}

.info-item .amount {
  font-size: 18px;
  font-weight: 600;
  color: #e6a23c;
}

.order-remark {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
}

.order-remark label {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
  display: block;
  margin-bottom: 8px;
}

.order-remark p {
  font-size: 16px;
  color: #303133;
  margin: 0;
  line-height: 1.5;
}

.address-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.address-line {
  font-size: 16px;
  color: #303133;
  line-height: 1.5;
}

.address-line strong {
  font-weight: 600;
  margin-right: 12px;
}

.phone {
  color: #909399;
  font-size: 14px;
}

.items-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.item-row {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.item-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
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
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.item-specs {
  font-size: 14px;
  color: #909399;
  margin: 0 0 12px 0;
}

.item-details {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.item-price {
  font-size: 16px;
  font-weight: 600;
  color: #e6a23c;
}

.item-qty {
  font-size: 14px;
  color: #606266;
}

.item-total {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.summary-rows {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
}

.summary-row.total-row {
  padding-top: 12px;
  border-top: 1px solid #e4e7ed;
  font-weight: 600;
  font-size: 18px;
}

.discount {
  color: #67c23a;
}

.total-amount {
  color: #e6a23c;
  font-size: 20px;
}

.order-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 30px;
}

.error-state {
  background: white;
  border-radius: 8px;
  padding: 40px 20px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* Mobile responsive */
@media (max-width: 768px) {
  .order-detail-page {
    padding: 16px 0 32px;
  }

  .container {
    padding: 0 16px;
  }

  .detail-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .detail-header h1 {
    font-size: 20px;
  }

  .info-card {
    padding: 16px;
    margin-bottom: 16px;
  }

  .info-card h3 {
    font-size: 16px;
  }

  .info-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .item-row {
    flex-direction: column;
    gap: 12px;
    padding: 12px;
  }

  .item-image {
    width: 60px;
    height: 60px;
    align-self: flex-start;
  }

  .item-details {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .order-actions {
    flex-direction: column;
    gap: 12px;
  }

  .order-actions .el-button {
    width: 100%;
  }
}
</style>
