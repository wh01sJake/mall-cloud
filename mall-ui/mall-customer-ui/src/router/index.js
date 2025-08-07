import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// Import views
import Home from '@/views/Home.vue'
import ProductList from '@/views/ProductList.vue'
import ProductDetail from '@/views/ProductDetail.vue'
import Cart from '@/views/Cart.vue'
import Checkout from '@/views/Checkout.vue'
import Orders from '@/views/Orders.vue'
import OrderDetail from '@/views/OrderDetail.vue'
import Profile from '@/views/Profile.vue'
import Login from '@/views/auth/Login.vue'
import Register from '@/views/auth/Register.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  // Scroll to top on route change
  scrollBehavior(to, from, savedPosition) {
    // If there's a saved position (back/forward navigation), use it
    if (savedPosition) {
      return savedPosition
    }
    // For hash links, scroll to the element
    if (to.hash) {
      return {
        el: to.hash,
        behavior: 'smooth'
      }
    }
    // Default: scroll to top
    return { top: 0, behavior: 'smooth' }
  },
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home,
      meta: { requiresAuth: false }
    },
    {
      path: '/products',
      name: 'products',
      component: ProductList,
      meta: { requiresAuth: false }
    },
    {
      path: '/products/:id',
      name: 'product-detail',
      component: ProductDetail,
      meta: { requiresAuth: false }
    },
    {
      path: '/category/:categoryId',
      name: 'category',
      component: ProductList,
      meta: { requiresAuth: false }
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
      meta: { requiresAuth: false, hideForAuth: true }
    },
    {
      path: '/register',
      name: 'register',
      component: Register,
      meta: { requiresAuth: false, hideForAuth: true }
    },
    {
      path: '/cart',
      name: 'cart',
      component: Cart,
      meta: { requiresAuth: true }
    },
    {
      path: '/checkout',
      name: 'checkout',
      component: Checkout,
      meta: { requiresAuth: true }
    },
    {
      path: '/orders',
      name: 'orders',
      component: Orders,
      meta: { requiresAuth: true }
    },
    {
      path: '/orders/:orderNo',
      name: 'order-detail',
      component: OrderDetail,
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: Profile,
      meta: { requiresAuth: true }
    }
  ]
})

// Navigation guards
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()

  // Only initialize auth for routes that might need it
  if (to.meta.requiresAuth || to.meta.hideForAuth) {
    // Initialize auth state from stored tokens if not already done
    if (!authStore.isLoggedIn) {
      authStore.initializeAuth()
    }
  }

  // Check if route requires authentication
  if (to.meta.requiresAuth) {
    // Only check session if we don't already know we're authenticated
    if (!authStore.isAuthenticated) {
      // Try to check session/validate token
      const isValid = await authStore.checkSession()

      if (!isValid) {
        next({ name: 'login', query: { redirect: to.fullPath } })
        return
      }
    }
  }

  // Hide login/register pages for authenticated users
  if (to.meta.hideForAuth && authStore.isAuthenticated) {
    next({ name: 'home' })
    return
  }

  next()
})

export default router
