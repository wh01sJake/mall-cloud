<template>
  <div class="home">
    <!-- Hero Section -->
    <section class="hero">
      <div class="container">
        <div class="hero-content">
          <h1>Welcome to VapeMall</h1>
          <p>Discover premium vaping products with quality you can trust</p>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="$router.push('/products')">
              Shop Now
            </el-button>
            <el-button size="large" @click="loadData">
              Browse Categories
            </el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- Categories Section -->
    <section class="categories">
      <div class="container">
        <h2>Shop by Category</h2>
        <div class="category-grid" v-loading="loading">
          <!-- Parent Categories -->
          <div v-for="category in categories" :key="category.id" class="category-card parent-category"
            @click="$router.push(`/category/${category.id}`)">
            <div class="category-icon">
              <el-icon size="48">
                <Box />
              </el-icon>
            </div>
            <h3>{{ category.name }}</h3>
            <p>{{ category.description || 'Explore our collection' }}</p>
            
            <!-- Subcategories -->
            <div v-if="category.childList && category.childList.length > 0" class="subcategories">
              <div v-for="subCategory in category.childList" :key="subCategory.id" 
                   class="subcategory-item"
                   @click.stop="$router.push(`/category/${subCategory.id}`)">
                <span>{{ subCategory.name }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Featured Products -->
    <section class="featured-products">
      <div class="container">
        <h2>Featured Products</h2>
        
        
        <div class="products-grid" v-loading="loading">
          <ProductCard v-for="product in products" :key="product.id" :product="product" />
        </div>
        
        <!-- Show message if no products -->
        <div v-if="!loading && products.length === 0" class="no-products">
          <p>No products available at the moment.</p>
        </div>
        
        <div class="section-actions">
          <el-button type="primary" @click="$router.push('/products')">
            View All Products
          </el-button>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features">
      <div class="container">
        <h2>Why Choose VapeMall?</h2>
        <div class="features-grid">
          <div class="feature-card">
            <el-icon size="48" color="#409eff">
              <Check />
            </el-icon>
            <h3>Quality Assured</h3>
            <p>All products are tested and verified for quality and safety</p>
          </div>
          <div class="feature-card">
            <el-icon size="48" color="#409eff">
              <Van />
            </el-icon>
            <h3>Fast Shipping</h3>
            <p>Quick and reliable delivery to your doorstep</p>
          </div>
          <div class="feature-card">
            <el-icon size="48" color="#409eff">
              <Headset />
            </el-icon>
            <h3>Expert Support</h3>
            <p>Knowledgeable customer service team ready to help</p>
          </div>
          <div class="feature-card">
            <el-icon size="48" color="#409eff">
              <Money />
            </el-icon>
            <h3>Best Prices</h3>
            <p>Competitive pricing with regular deals and discounts</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useProductStore } from '@/stores/product'
import ProductCard from '@/components/product/ProductCard.vue'
import {
  Box,
  Check,
  Van,
  Headset,
  Money
} from '@element-plus/icons-vue'

const productStore = useProductStore()
const loading = ref(false)
const categories = ref([])
const products = ref([])

const loadData = async () => {
  loading.value = true
  try {
    // Load categories and featured products
    await productStore.fetchCategories()
    await productStore.fetchProducts(null, 8) // Get 8 featured products

    categories.value = productStore.categories
    products.value = productStore.products
  } catch (error) {
    console.error('Error loading data:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 100px 0;
  text-align: center;
}

.hero-content h1 {
  font-size: 3rem;
  margin-bottom: 20px;
  font-weight: bold;
}

.hero-content p {
  font-size: 1.2rem;
  margin-bottom: 40px;
  opacity: 0.9;
}

.hero-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
}

.categories {
  padding: 80px 0;
  background: #f8f9fa;
}

.categories h2,
.featured-products h2,
.features h2 {
  text-align: center;
  margin-bottom: 50px;
  font-size: 2.5rem;
  color: #333;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.category-card {
  background: white;
  padding: 30px 20px;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.category-icon {
  color: #409eff;
  margin-bottom: 20px;
}

.category-card h3 {
  margin-bottom: 15px;
  color: #333;
  font-size: 1.3rem;
}

.category-card p {
  color: #666;
  margin-bottom: 20px;
}

.subcategories {
  margin-top: auto;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.subcategory-item {
  display: inline-block;
  background: #f8f9fa;
  color: #666;
  padding: 6px 12px;
  margin: 4px;
  border-radius: 20px;
  font-size: 0.85rem;
  transition: all 0.3s;
  cursor: pointer;
}

.subcategory-item:hover {
  background: #409eff;
  color: white;
  transform: scale(1.05);
}

.featured-products {
  padding: 80px 0;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
  margin-bottom: 50px;
}

.section-actions {
  text-align: center;
}

.features {
  padding: 80px 0;
  background: #f8f9fa;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 40px;
}

.feature-card {
  text-align: center;
  padding: 30px 20px;
}

.feature-card h3 {
  margin: 20px 0 15px;
  color: #333;
}

.feature-card p {
  color: #666;
  line-height: 1.6;
}

/* Mobile Styles */
@media (max-width: 768px) {
  .hero-content h1 {
    font-size: 2rem;
  }

  .hero-actions {
    flex-direction: column;
    align-items: center;
  }

  .categories h2,
  .featured-products h2,
  .features h2 {
    font-size: 2rem;
  }

  .category-grid,
  .features-grid {
    grid-template-columns: 1fr;
  }

  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
}
</style>
