<template>
  <div class="product-detail">
    <div class="container">
      <div v-if="productStore.currentProduct" class="product-content">
        <div class="product-images">
          <img
            :src="productStore.currentProduct.mainImage || '/placeholder-product.svg'"
            :alt="productStore.currentProduct.name"
            class="main-image"
          />
        </div>
        
        <div class="product-info">
          <h1>{{ productStore.currentProduct.name }}</h1>
          <p class="subtitle">{{ productStore.currentProduct.subtitle }}</p>

          <div class="price">
            <span class="current-price">€{{ Number(productStore.currentProduct.price).toFixed(2) }}</span>
          </div>
          
          <div class="stock-info">
            <el-tag v-if="productStore.currentProduct.stock > 0" type="success">
              In Stock ({{ productStore.currentProduct.stock }})
            </el-tag>
            <el-tag v-else type="danger">Out of Stock</el-tag>
          </div>
          
          <div class="actions">
            <el-button
              type="primary"
              size="large"
              :disabled="productStore.currentProduct.stock === 0"
              @click="addToCart"
            >
              Add to Cart
            </el-button>
          </div>
        </div>
      </div>

      <!-- Rich Product Details Section -->
      <div v-if="productStore.currentProduct && productStore.currentProduct.detail" class="product-details-section">
        <div class="container">
          <h2>Product Details</h2>
          <div class="rich-content" v-html="productStore.currentProduct.detail"></div>
        </div>
      </div>

      <!-- Debug: Show if detail exists but is empty -->
      <div v-else-if="productStore.currentProduct && !productStore.currentProduct.detail" class="no-details">
        <div class="container">
          <p style="text-align: center; color: #999; padding: 20px;">
            No detailed description available for this product.
          </p>
        </div>
      </div>
      
      <div v-else-if="!productStore.loading" class="not-found">
        <el-empty description="Product not found">
          <el-button type="primary" @click="$router.push('/products')">
            Browse Products
          </el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProductStore } from '@/stores/product'
import { useCartStore } from '@/stores/cart'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const productStore = useProductStore()
const cartStore = useCartStore()
const authStore = useAuthStore()

const productId = parseInt(route.params.id)

const addToCart = async () => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('Please login to add items to cart')
    router.push('/login')
    return
  }

  await cartStore.addToCart(productStore.currentProduct, 1)

  // Scroll to top after adding to cart to show success message
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  if (productId) {
    productStore.fetchProductDetail(productId)
  }
})
</script>

<style scoped>
.product-detail {
  padding: 40px 0;
}

.product-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: start;
}

.main-image {
  width: 100%;
  border-radius: 12px;
}

.product-info h1 {
  font-size: 2rem;
  margin-bottom: 20px;
  color: #333;
}

.subtitle {
  color: #666;
  line-height: 1.6;
  margin-bottom: 30px;
  font-size: 1.1rem;
}

.current-price {
  font-size: 2rem;
  font-weight: bold;
  color: #409eff;
}

.stock-info {
  margin: 20px 0;
}

.actions {
  margin-top: 30px;
}

/* Rich Product Details Section */
.product-details-section {
  background: #f8f9fa;
  padding: 60px 0;
  margin-top: 40px;
}

.product-details-section h2 {
  font-size: 2rem;
  margin-bottom: 30px;
  color: #333;
  text-align: center;
}

.rich-content {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  line-height: 1.6;
}

/* Style the rich HTML content */
.rich-content h2,
.rich-content h3,
.rich-content h4 {
  margin-top: 30px;
  margin-bottom: 15px;
}

.rich-content h2 {
  font-size: 1.8rem;
  color: #2c3e50;
}

.rich-content h3 {
  font-size: 1.4rem;
  color: #34495e;
}

.rich-content h4 {
  font-size: 1.2rem;
  color: #34495e;
}

.rich-content ul,
.rich-content ol {
  margin: 15px 0;
  padding-left: 20px;
}

.rich-content li {
  margin-bottom: 8px;
}

.rich-content p {
  margin-bottom: 15px;
}

.rich-content img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 20px 0;
}

.rich-content table {
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0;
}

.rich-content th,
.rich-content td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.rich-content th {
  background-color: #f8f9fa;
  font-weight: 600;
}

/* Enhanced WangEditor content styling */
.rich-content h1 {
  font-size: 2.2rem;
  color: #2c3e50;
  margin-top: 30px;
  margin-bottom: 15px;
  font-weight: 600;
}

.rich-content h5,
.rich-content h6 {
  font-size: 1rem;
  color: #34495e;
  margin-top: 20px;
  margin-bottom: 10px;
  font-weight: 600;
}

.rich-content p {
  color: #555;
  line-height: 1.7;
}

.rich-content strong,
.rich-content b {
  font-weight: 600;
  color: #2c3e50;
}

.rich-content em,
.rich-content i {
  font-style: italic;
  color: #34495e;
}

.rich-content u {
  text-decoration: underline;
  text-decoration-color: #667eea;
}

.rich-content s,
.rich-content del {
  text-decoration: line-through;
  color: #999;
}

.rich-content a {
  color: #667eea;
  text-decoration: none;
  border-bottom: 1px solid transparent;
  transition: border-color 0.3s ease;
}

.rich-content a:hover {
  border-bottom-color: #667eea;
}

.rich-content blockquote {
  border-left: 4px solid #667eea;
  padding: 15px 20px;
  margin: 20px 0;
  font-style: italic;
  color: #666;
  background: #f8f9fa;
  border-radius: 0 8px 8px 0;
}

.rich-content code {
  background: #f4f4f4;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  color: #e74c3c;
  font-size: 0.9em;
}

.rich-content pre {
  background: #f4f4f4;
  padding: 15px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 15px 0;
  border: 1px solid #e1e1e1;
}

.rich-content pre code {
  background: none;
  padding: 0;
  color: #333;
}

.rich-content hr {
  border: none;
  height: 2px;
  background: linear-gradient(to right, transparent, #e1e1e1, transparent);
  margin: 30px 0;
}

.rich-content li {
  color: #555;
  line-height: 1.6;
}

.rich-content img {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

@media (max-width: 768px) {
  .product-content {
    grid-template-columns: 1fr;
    gap: 30px;
  }

  .product-details-section {
    padding: 40px 0;
  }

  .rich-content {
    padding: 20px;
  }

  .product-details-section h2 {
    font-size: 1.5rem;
  }
}
</style>
