// Create and export router
// Step 1: Import createRouter
import {createRouter, createWebHistory} from 'vue-router'
// Import components that may be rendered
import Layout from '@/views/Layout.vue'
import Login from '@/views/Login.vue'
import Admin from '@/views/Admin.vue'
import Customer from '@/views/Customer.vue'
import Product from "@/views/Product.vue";
import UserInfo from '@/views/UserInfo.vue'
import Category from "@/views/Category.vue";
import Order from "@/views/Order.vue";
import OrderChart from "@/views/OrderChart.vue";
import Chart from "@/views/chart.vue";


//Create router
const router = createRouter({
    history: createWebHistory(),
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
        {path: '/login', component: Login},
        {
            path: '/', component: Layout, children: [
                {path: '/admin', component: Admin},
                {path: '/customer', component: Customer},
                {path: '/product', component: Product},
                {path: '/category', component: Category},
                {path: '/order', component: Order},
                {path: '/order-chart', component: OrderChart},
                {path: '/chart', component: Chart}
            ]
        }
    ]
})

// Export router
export default router