<template>
  <div class="cart-page">
    <div class="container">
      <h1>Shopping Cart</h1>
      
      <div v-if="cartStore.items.length > 0" class="cart-content">
        <div class="cart-items">
          <div v-for="item in cartStore.items" :key="item.id" class="cart-item">
            <el-checkbox 
              :model-value="item.isChecked === 1"
              @change="cartStore.toggleItemChecked(item.id)"
            />
            <img :src="item.productMainImage || item.productImage || '/placeholder-product.svg'" :alt="item.productName" />
            <div class="item-info">
              <h3>{{ item.productName }}</h3>
              <p class="price">€{{ Number(item.productPrice || item.price || 0).toFixed(2) }}</p>
            </div>
            <div class="quantity-controls">
              <el-input-number 
                v-model="item.quantity"
                :min="1"
                @change="updateQuantity(item.id, item.quantity)"
              />
            </div>
            <div class="subtotal">
              <span class="subtotal-label">Subtotal:</span>
              <span class="subtotal-amount">€{{ (Number(item.productPrice || item.price || 0) * item.quantity).toFixed(2) }}</span>
            </div>
            <el-button 
              type="danger" 
              text
              @click="removeItem(item.id)"
            >
              Remove
            </el-button>
          </div>
        </div>
        
        <div class="cart-summary">
          <div class="summary-content">
            <h3>Order Summary</h3>
            <div class="summary-line">
              <span>Selected Items ({{ cartStore.checkedCount }}):</span>
              <span>€{{ cartStore.totalAmount.toFixed(2) }}</span>
            </div>
            <div class="summary-line">
              <span>Shipping:</span>
              <span>Free</span>
            </div>
            <div class="summary-total">
              <span>Total:</span>
              <span>€{{ cartStore.totalAmount.toFixed(2) }}</span>
            </div>
            <el-button 
              type="primary" 
              size="large"
              :disabled="cartStore.checkedCount === 0"
              @click="$router.push('/checkout')"
              block
            >
              Proceed to Checkout
            </el-button>
          </div>
        </div>
      </div>
      
      <div v-else class="empty-cart">
        <el-empty description="Your cart is empty">
          <el-button type="primary" @click="$router.push('/products')">
            Start Shopping
          </el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useCartStore } from '@/stores/cart'

const cartStore = useCartStore()

// Wrapper functions that include scroll-to-top behavior
const updateQuantity = async (itemId, quantity) => {
  await cartStore.updateQuantity(itemId, quantity)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const removeItem = async (itemId) => {
  await cartStore.removeItem(itemId)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  cartStore.fetchCart()
})
</script>

<style scoped>
.cart-page {
  padding: 40px 0;
  min-height: 60vh;
}

.cart-page h1 {
  margin-bottom: 30px;
  color: #333;
}

.cart-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 40px;
}

.cart-item {
  display: grid;
  grid-template-columns: auto 80px 1fr auto auto auto;
  gap: 20px;
  align-items: center;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 15px;
}

.cart-item img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}

.item-info h3 {
  margin-bottom: 5px;
  color: #333;
}

.price {
  color: #409eff;
  font-weight: bold;
}

.subtotal {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.subtotal-label {
  font-size: 12px;
  color: #666;
}

.subtotal-amount {
  font-weight: bold;
  color: #333;
  font-size: 16px;
}

.cart-summary {
  background: #f8f9fa;
  padding: 30px;
  border-radius: 12px;
  height: fit-content;
}

.summary-content h3 {
  margin-bottom: 20px;
  color: #333;
}

.summary-line {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  font-size: 1.2rem;
  font-weight: bold;
  margin: 20px 0;
  padding-top: 20px;
  border-top: 1px solid #ddd;
}

@media (max-width: 768px) {
  .cart-content {
    grid-template-columns: 1fr;
  }
  
  .cart-item {
    grid-template-columns: auto 60px 1fr;
    gap: 15px;
  }
  
  .quantity-controls,
  .subtotal {
    grid-column: 1 / -1;
    justify-self: start;
  }
}
</style>
