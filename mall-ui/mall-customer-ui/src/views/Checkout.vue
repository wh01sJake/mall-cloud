<template>
  <div class="checkout-page">
    <div class="container">
      <div class="page-header">
        <el-button type="text" @click="goBack" class="back-button">
          <el-icon>
            <ArrowLeft />
          </el-icon>
          Back to Cart
        </el-button>
        <h1>Checkout</h1>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="4" animated />
      </div>

      <!-- Checkout Content -->
      <div v-else class="checkout-content">
        <!-- Address Selection -->
        <div class="checkout-section">
          <AddressSelector :selected-address-id="selectedAddress?.id" @address-selected="handleAddressSelected" />
        </div>

        <!-- Order Items -->
        <div class="checkout-section">
          <div class="section-header">
            <h3>Order Items</h3>
            <span class="item-count">{{ checkedItems.length }} item(s)</span>
          </div>

          <div class="order-items">
            <div v-for="item in checkedItems" :key="item.id" class="order-item">
              <div class="item-image">
                <img :src="item.productMainImage || item.productPic || '/placeholder-product.jpg'"
                  :alt="item.productName" @error="handleImageError" />
              </div>
              <div class="item-info">
                <h4 class="item-name">{{ item.productName }}</h4>
                <p v-if="item.productSubtitle" class="item-specs">{{ item.productSubtitle }}</p>
                <div class="item-price-qty">
                  <span class="item-price">€{{ Number(item.productPrice || item.price || 0).toFixed(2) }}</span>
                  <span class="item-qty">x{{ item.quantity }}</span>
                  <span class="item-total">€{{ (Number(item.productPrice || item.price || 0) * item.quantity).toFixed(2)
                    }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Order Remark -->
        <div class="checkout-section">
          <h3>Order Remark</h3>
          <el-input v-model="orderRemark" type="textarea" :rows="3"
            placeholder="Enter any special instructions for your order (optional)" maxlength="200" show-word-limit />
        </div>

        <!-- Order Summary -->
        <div class="checkout-section">
          <div class="order-summary">
            <h3>Order Summary</h3>
            <div class="summary-rows">
              <div class="summary-row">
                <span>Subtotal ({{ checkedItems.length }} items):</span>
                <span>€{{ cartStore.totalAmount.toFixed(2) }}</span>
              </div>
              <div class="summary-row">
                <span>Shipping to Ireland:</span>
                <span>{{ shippingFee > 0 ? '€' + shippingFee.toFixed(2) : 'Free' }}</span>
              </div>
              <div v-if="discount > 0" class="summary-row">
                <span>Discount:</span>
                <span class="discount">-€{{ discount.toFixed(2) }}</span>
              </div>
              <div class="summary-row total-row">
                <span>Total:</span>
                <span class="total-amount">€{{ totalAmount.toFixed(2) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Checkout Actions -->
        <div class="checkout-actions">
          <div class="payment-info">
            <span class="payment-label">Payment Method:</span>
            <el-select v-model="paymentMethod" class="payment-select">
              <el-option label="Credit/Debit Card" value="CARD" />
              <el-option label="PayPal" value="PAYPAL" />
              <el-option label="Bank Transfer" value="BANK_TRANSFER" />
            </el-select>
          </div>

          <div class="action-buttons">
            <div class="total-display">
              <span class="total-label">Total: </span>
              <span class="total-price">€{{ totalAmount.toFixed(2) }}</span>
            </div>
            <el-button type="primary" size="large" @click="handleSubmitOrder" :loading="submitting"
              :disabled="!canSubmit">
              Place Order
            </el-button>
          </div>
        </div>
      </div>

      <!-- Empty Cart State -->
      <div v-if="!loading && checkedItems.length === 0" class="empty-state">
        <el-empty :image-size="120" description="No items selected for checkout">
          <el-button type="primary" @click="goBack">
            Back to Cart
          </el-button>
        </el-empty>
      </div>
    </div>

    <!-- Order Success Dialog -->
    <el-dialog v-model="showSuccessDialog" title="Order Placed Successfully!" width="90%" :max-width="500"
      :close-on-click-modal="false" :show-close="false">
      <div class="success-content">
        <el-icon class="success-icon" color="#67c23a" size="60">
          <CircleCheck />
        </el-icon>
        <h3>Thank you for your order!</h3>
        <p>Order Number: <strong>{{ createdOrderNo }}</strong></p>
        <p>We'll process your order and send you updates via email.</p>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="goToOrders">View Orders</el-button>
          <el-button type="primary" @click="continueShopping">Continue Shopping</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useOrderStore } from '@/stores/order'
import { useAddressStore } from '@/stores/address'
import { useAuthStore } from '@/stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, CircleCheck } from '@element-plus/icons-vue'
import AddressSelector from '@/components/address/AddressSelector.vue'

const router = useRouter()
const cartStore = useCartStore()
const orderStore = useOrderStore()
const addressStore = useAddressStore()
const authStore = useAuthStore()

// Reactive data
const loading = ref(true)
const submitting = ref(false)
const selectedAddress = ref(null)
const orderRemark = ref('')
const paymentMethod = ref('CARD')
const shippingFee = ref(0) // Free shipping for now
const discount = ref(0) // No discount for now
const showSuccessDialog = ref(false)
const createdOrderNo = ref('')

// Computed properties
const checkedItems = computed(() => cartStore.checkedItems)

const totalAmount = computed(() => {
  return cartStore.totalAmount + shippingFee.value - discount.value
})

const canSubmit = computed(() => {
  return selectedAddress.value &&
    checkedItems.value.length > 0 &&
    !submitting.value
})

// Methods
const goBack = () => {
  router.push({ name: 'cart' })
}

const handleAddressSelected = (address) => {
  selectedAddress.value = address
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

const handleSubmitOrder = async () => {
  // Check authentication first and validate session
  const sessionValid = await authStore.checkSession()
  if (!sessionValid || !authStore.isAuthenticated || !authStore.currentUser) {
    ElMessage.error('Your session has expired. Please login again.')
    router.push('/login')
    return
  }

  if (!selectedAddress.value) {
    ElMessage.error('Please select a shipping address')
    return
  }

  if (checkedItems.value.length === 0) {
    ElMessage.error('No items selected for checkout')
    return
  }

  try {
    await ElMessageBox.confirm(
      `Confirm order with total amount €${totalAmount.value.toFixed(2)}?`,
      'Confirm Order',
      {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'info',
      }
    )

    submitting.value = true

    // Prepare order data
    const orderData = {
      cartIds: checkedItems.value.map(item => item.id),
      addressId: selectedAddress.value.id,
      remark: orderRemark.value.trim() || undefined,
      paymentMethod: paymentMethod.value,
      totalAmount: totalAmount.value
    }

    // Create order
    const result = await orderStore.createOrder(orderData)

    if (result) {
      createdOrderNo.value = result.orderNo || result.id || 'Unknown'
      showSuccessDialog.value = true

      // Refresh cart to remove ordered items
      await cartStore.fetchCart()
    }
  } catch (error) {
    if (error !== 'cancel') {
      // Handle authentication errors specifically
      if (error.message === 'AUTHENTICATION_REQUIRED') {
        // Don't log authentication errors as they're expected
        console.log('Session expired, redirecting to login')
        router.push('/login')
        return
      } else if (error.message && error.message.includes('Please login first')) {
        console.log('Authentication required, redirecting to login')
        router.push('/login')
        return
      }

      // Log other errors
      console.error('Order submission failed:', error)
      ElMessage.error('Failed to create order. Please try again.')
    }
  } finally {
    submitting.value = false
  }
}

const goToOrders = () => {
  showSuccessDialog.value = false
  router.push({ name: 'orders' })
}

const continueShopping = () => {
  showSuccessDialog.value = false
  router.push({ name: 'home' })
}

// Lifecycle
onMounted(async () => {
  try {
    // Always check session to ensure it's still valid
    const sessionValid = await authStore.checkSession()

    if (!sessionValid || !authStore.isAuthenticated || !authStore.currentUser) {
      ElMessage.error('Please login to access checkout')
      router.push('/login')
      return
    }

    // Load cart and addresses
    await Promise.all([
      cartStore.fetchCart(),
      addressStore.fetchAddresses()
    ])

    // Check if there are checked items
    if (cartStore.checkedItems.length === 0) {
      ElMessage.warning('No items selected for checkout')
      router.push({ name: 'cart' })
      return
    }

    // Auto-select default address
    const defaultAddress = addressStore.getDefaultAddress
    if (defaultAddress) {
      selectedAddress.value = defaultAddress
    }
  } catch (error) {
    console.error('Failed to load checkout data:', error)
    ElMessage.error('Failed to load checkout information')

    // If it's an authentication error, redirect to login
    if (error.message && error.message.includes('login')) {
      router.push('/login')
    }
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.checkout-page {
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
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 24px;
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

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.loading-container {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.checkout-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.checkout-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
}

.section-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.item-count {
  font-size: 14px;
  color: #909399;
}

.order-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
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

.item-total {
  font-weight: 600;
  color: #303133;
}

.order-summary h3 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
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

.checkout-actions {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  bottom: 20px;
}

.payment-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.payment-label {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
}

.payment-select {
  width: 150px;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-display {
  display: flex;
  align-items: center;
  gap: 8px;
}

.total-label {
  font-size: 16px;
  color: #606266;
}

.total-price {
  font-size: 20px;
  font-weight: 600;
  color: #e6a23c;
}

.empty-state {
  background: white;
  border-radius: 8px;
  padding: 40px 20px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.success-content {
  text-align: center;
  padding: 20px 0;
}

.success-icon {
  margin-bottom: 16px;
}

.success-content h3 {
  font-size: 20px;
  color: #303133;
  margin: 0 0 16px 0;
}

.success-content p {
  font-size: 16px;
  color: #606266;
  margin: 8px 0;
  line-height: 1.5;
}

.dialog-footer {
  display: flex;
  gap: 12px;
  justify-content: center;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .checkout-page {
    padding: 16px 0 32px;
  }

  .container {
    padding: 0 16px;
  }

  .page-header h1 {
    font-size: 20px;
  }

  .checkout-section {
    padding: 16px;
  }

  .section-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }

  .order-item {
    flex-direction: column;
    gap: 8px;
  }

  .item-image {
    width: 50px;
    height: 50px;
    align-self: flex-start;
  }

  .item-price-qty {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .checkout-actions {
    padding: 16px;
    position: static;
  }

  .payment-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .payment-select {
    width: 100%;
  }

  .action-buttons {
    flex-direction: column;
    gap: 12px;
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
