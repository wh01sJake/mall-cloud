<template>
  <header class="app-header">
    <div class="container">
      <div class="header-content">
        <!-- Logo -->
        <div class="logo">
          <router-link to="/" class="logo-link">
            <h1>VapeMall</h1>
          </router-link>
        </div>

        <!-- Navigation -->
        <nav class="nav-menu" :class="{ 'nav-open': mobileMenuOpen }">
          <router-link to="/" class="nav-link" @click="closeMobileMenu">Home</router-link>
          <router-link to="/products" class="nav-link" @click="closeMobileMenu">Products</router-link>
          <el-dropdown trigger="hover" class="category-dropdown">
            <span class="nav-link">
              Categories <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu class="category-dropdown-menu">
                <template v-for="category in categories" :key="category.id">
                  <!-- Parent Category -->
                  <el-dropdown-item class="parent-category-item">
                    <router-link :to="`/category/${category.id}`" @click="closeMobileMenu" class="parent-category-link">
                      <strong>{{ category.name }}</strong>
                    </router-link>
                  </el-dropdown-item>
                  
                  <!-- Subcategories -->
                  <template v-if="category.childList && category.childList.length > 0">
                    <el-dropdown-item 
                      v-for="subCategory in category.childList" 
                      :key="subCategory.id"
                      class="sub-category-item"
                    >
                      <router-link :to="`/category/${subCategory.id}`" @click="closeMobileMenu" class="sub-category-link">
                        {{ subCategory.name }}
                      </router-link>
                    </el-dropdown-item>
                  </template>
                  
                  <!-- Divider after each parent category group -->
                  <el-dropdown-item divided v-if="category !== categories[categories.length - 1]" class="category-divider">
                  </el-dropdown-item>
                </template>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </nav>

        <!-- Search Bar -->
        <div class="search-bar">
          <el-input
            v-model="searchQuery"
            placeholder="Search products..."
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button @click="handleSearch">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>

        <!-- User Actions -->
        <div class="user-actions">
          <!-- Cart -->
          <router-link to="/cart" class="cart-link">
            <el-badge :value="cartCount" :hidden="cartCount === 0">
              <el-icon size="24"><ShoppingCart /></el-icon>
            </el-badge>
          </router-link>

          <!-- User Menu -->
          <div v-if="authStore.isLoggedIn" class="user-menu">
            <el-dropdown trigger="hover">
              <span class="user-info">
                <el-icon><User /></el-icon>
                {{ authStore.user?.username }}
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>
                    <router-link to="/profile">Profile</router-link>
                  </el-dropdown-item>
                  <el-dropdown-item>
                    <router-link to="/orders">My Orders</router-link>
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    Logout
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>

          <!-- Login/Register -->
          <div v-else class="auth-links">
            <router-link to="/login" class="auth-link">Login</router-link>
            <router-link to="/register" class="auth-link register">Register</router-link>
          </div>

          <!-- Mobile Menu Toggle -->
          <button class="mobile-menu-toggle" @click="toggleMobileMenu">
            <el-icon><Menu /></el-icon>
          </button>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { useProductStore } from '@/stores/product'
import {
  ArrowDown,
  Search,
  ShoppingCart,
  User,
  Menu
} from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()
const productStore = useProductStore()

const searchQuery = ref('')
const mobileMenuOpen = ref(false)

const cartCount = computed(() => cartStore.totalCount)
const categories = computed(() => productStore.categories)

onMounted(async () => {
  try {
    // Load categories for navigation
    await productStore.fetchCategories()

    // Load cart if user is logged in
    if (authStore.isLoggedIn) {
      await cartStore.fetchCart()
    }
  } catch (error) {
    console.log('Header initialization error:', error)
  }
})

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({ name: 'products', query: { search: searchQuery.value.trim() } })
  }
}

const handleLogout = async () => {
  await authStore.logout()
  cartStore.clearCart()
  router.push('/')
}

const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

const closeMobileMenu = () => {
  mobileMenuOpen.value = false
}
</script>

<style scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  height: 80px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 80px;
  gap: 20px;
}

.logo h1 {
  color: #409eff;
  font-size: 24px;
  font-weight: bold;
}

.logo-link {
  text-decoration: none;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 30px;
}

.nav-link {
  text-decoration: none;
  color: #333;
  font-weight: 500;
  padding: 8px 0;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
  cursor: pointer;
}

.nav-link:hover,
.nav-link.router-link-active {
  color: #409eff;
  border-bottom-color: #409eff;
}

.search-bar {
  flex: 1;
  max-width: 400px;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.cart-link {
  color: #333;
  transition: color 0.3s;
}

.cart-link:hover {
  color: #409eff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #333;
}

.auth-links {
  display: flex;
  gap: 15px;
}

.auth-link {
  text-decoration: none;
  color: #333;
  padding: 8px 16px;
  border-radius: 4px;
  transition: all 0.3s;
}

.auth-link:hover {
  background: #f5f5f5;
}

.auth-link.register {
  background: #409eff;
  color: white;
}

.auth-link.register:hover {
  background: #66b1ff;
}

.mobile-menu-toggle {
  display: none;
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
}

/* Category Dropdown Styles */
.category-dropdown-menu {
  max-height: 400px;
  overflow-y: auto;
}

.parent-category-item {
  background: #f8f9fa;
  margin: 2px 0;
}

.parent-category-link {
  color: #409eff !important;
  text-decoration: none;
  font-weight: 600;
}

.parent-category-link:hover {
  color: #66b1ff !important;
}

.sub-category-item {
  padding-left: 20px;
  background: white;
}

.sub-category-link {
  color: #666 !important;
  text-decoration: none;
  font-size: 0.9rem;
  padding-left: 10px;
  border-left: 2px solid #e0e0e0;
  transition: all 0.3s;
}

.sub-category-link:hover {
  color: #409eff !important;
  border-left-color: #409eff;
}

.category-divider {
  height: 1px;
  background: #e0e0e0;
  margin: 5px 0;
  padding: 0;
}

/* Mobile Styles */
@media (max-width: 768px) {
  .nav-menu {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: white;
    flex-direction: column;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transform: translateY(-100%);
    opacity: 0;
    visibility: hidden;
    transition: all 0.3s;
  }

  .nav-menu.nav-open {
    transform: translateY(0);
    opacity: 1;
    visibility: visible;
  }

  .search-bar {
    display: none;
  }

  .mobile-menu-toggle {
    display: block;
  }

  .user-actions {
    gap: 15px;
  }
}
</style>
