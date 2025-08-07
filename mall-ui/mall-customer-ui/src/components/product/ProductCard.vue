<template>
  <div class="product-card">
    <div class="product-image" @click="goToProduct">
      <img
        :src="product.mainImage || '/placeholder-product.svg'"
        :alt="product.name"
        @error="handleImageError"
      />
      <div class="product-overlay">
        <el-button type="primary" circle @click.stop="quickAddToCart">
          <el-icon><ShoppingCart /></el-icon>
        </el-button>
      </div>
    </div>
    
    <div class="product-info">
      <h3 class="product-name" @click="goToProduct">{{ product.name }}</h3>
      <p class="product-subtitle">{{ truncatedSubtitle }}</p>
      
      <div class="product-meta">
        <div class="product-price">
          <span class="current-price">€{{ Number(product.price).toFixed(2) }}</span>
          <span v-if="product.originalPrice && product.originalPrice > product.price" class="original-price">
            €{{ Number(product.originalPrice).toFixed(2) }}
          </span>
        </div>
        
        <div class="product-stock">
          <el-tag v-if="product.stock > 0" type="success" size="small">
            In Stock ({{ product.stock }})
          </el-tag>
          <el-tag v-else type="danger" size="small">
            Out of Stock
          </el-tag>
        </div>
      </div>
      
      <div class="product-actions">
        <el-button 
          type="primary" 
          :disabled="product.stock === 0 || loading"
          :loading="loading"
          @click="addToCart"
          block
        >
          <el-icon><ShoppingCart /></el-icon>
          Add to Cart
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useAuthStore } from '@/stores/auth'
import { ShoppingCart } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const cartStore = useCartStore()
const authStore = useAuthStore()
const loading = ref(false)

const truncatedSubtitle = computed(() => {
  if (!props.product.subtitle) return ''
  return props.product.subtitle.length > 100
    ? props.product.subtitle.substring(0, 100) + '...'
    : props.product.subtitle
})

const goToProduct = () => {
  router.push(`/products/${props.product.id}`)
}

const addToCart = async () => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('Please login to add items to cart')
    router.push('/login')
    return
  }
  
  if (props.product.stock === 0) {
    ElMessage.error('Product is out of stock')
    return
  }
  
  loading.value = true
  try {
    await cartStore.addToCart(props.product, 1)
  } catch (error) {
    console.error('Error adding to cart:', error)
  } finally {
    loading.value = false
  }
}

const quickAddToCart = async () => {
  await addToCart()
}

const handleImageError = (event) => {
  // Prevent infinite loop by checking if we already tried the placeholder
  if (event.target.src.includes('placeholder-product.svg')) {
    // Remove the error handler to prevent further attempts
    event.target.onerror = null
    // Set a data URL for a simple gray placeholder
    event.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjYwIiBoZWlnaHQ9IjYwIiBmaWxsPSIjRjVGNUY1Ii8+CjxwYXRoIGQ9Ik0yMCAyMEg0MFY0MEgyMFYyMFoiIGZpbGw9IiNEREREREQiLz4KPC9zdmc+'
  } else {
    event.target.src = '/placeholder-product.svg'
  }
}
</script>

<style scoped>
.product-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.product-image {
  position: relative;
  height: 200px;
  overflow: hidden;
  cursor: pointer;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-image:hover img {
  transform: scale(1.05);
}

.product-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.product-image:hover .product-overlay {
  opacity: 1;
}

.product-info {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 10px;
  color: #333;
  cursor: pointer;
  transition: color 0.3s;
  line-height: 1.4;
}

.product-name:hover {
  color: #409eff;
}

.product-subtitle {
  color: #666;
  font-size: 0.9rem;
  line-height: 1.5;
  margin-bottom: 15px;
  flex: 1;
}

.product-meta {
  margin-bottom: 15px;
}

.product-price {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.current-price {
  font-size: 1.2rem;
  font-weight: bold;
  color: #409eff;
}

.original-price {
  font-size: 0.9rem;
  color: #999;
  text-decoration: line-through;
}

.product-stock {
  margin-bottom: 10px;
}

.product-actions {
  margin-top: auto;
}

/* Mobile Styles */
@media (max-width: 768px) {
  .product-image {
    height: 180px;
  }
  
  .product-info {
    padding: 15px;
  }
  
  .product-name {
    font-size: 1rem;
  }
  
  .current-price {
    font-size: 1.1rem;
  }
}
</style>
