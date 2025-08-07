<template>
  <div class="orders-page">
    <div class="container">
      <div class="page-header">
        <h1>My Orders</h1>
        <p class="page-subtitle">Track and manage your order history</p>
      </div>

      <!-- Order Status Filter -->
      <OrderStatusFilter
        :current-status="currentStatus"
        :order-stats="orderStore.orderStats"
        @status-change="handleStatusChange"
      />

      <!-- Loading State -->
      <div v-if="orderStore.loading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>

      <!-- Orders List -->
      <div v-else-if="orderStore.orders.length > 0" class="orders-list">
        <OrderCard
          v-for="order in orderStore.orders"
          :key="order.orderNo"
          :order="order"
          @refresh="refreshOrders"
        />

        <!-- Pagination -->
        <div v-if="orderStore.pagination.total > orderStore.pagination.size" class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50]"
            :total="orderStore.pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="empty-state">
        <el-empty
          :image-size="120"
          description="No orders found"
        >
          <template #description>
            <p>{{ getEmptyMessage() }}</p>
          </template>
          <el-button
            type="primary"
            @click="goToProducts"
          >
            Start Shopping
          </el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/order'
import OrderCard from '@/components/order/OrderCard.vue'
import OrderStatusFilter from '@/components/order/OrderStatusFilter.vue'

const router = useRouter()
const orderStore = useOrderStore()

// Reactive data
const currentStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

// Computed properties
const getEmptyMessage = () => {
  if (currentStatus.value) {
    return `No orders with status "${getStatusText(currentStatus.value)}" found.`
  }
  return 'You haven\'t placed any orders yet. Start shopping to see your orders here!'
}

// Get status text for display
const getStatusText = (status) => {
  const statusMap = {
    'PENDING_PAYMENT': 'Pending Payment',
    'PAID': 'Paid',
    'SHIPPED': 'Shipped',
    'COMPLETED': 'Completed',
    'CANCELLED': 'Cancelled'
  }
  return statusMap[status] || status
}

// Methods
const fetchOrders = async () => {
  await orderStore.fetchOrders({
    page: currentPage.value,
    size: pageSize.value,
    status: currentStatus.value || undefined
  })
}

const refreshOrders = async () => {
  await fetchOrders()
  await orderStore.fetchOrderStats()
}

const handleStatusChange = (status) => {
  currentStatus.value = status
  currentPage.value = 1 // Reset to first page
  fetchOrders()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchOrders()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1 // Reset to first page
  fetchOrders()
}

const goToProducts = () => {
  router.push({ name: 'products' })
}

// Lifecycle
onMounted(async () => {
  await Promise.all([
    orderStore.fetchOrderStats(),
    fetchOrders()
  ])
})
</script>

<style scoped>
.orders-page {
  padding: 20px 0 40px;
  min-height: 60vh;
  background: #f5f7fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 16px;
  color: #909399;
  margin: 0;
}

.loading-container {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.orders-list {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.empty-state {
  background: white;
  border-radius: 8px;
  padding: 40px 20px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* Mobile responsive */
@media (max-width: 768px) {
  .orders-page {
    padding: 16px 0 32px;
  }

  .container {
    padding: 0 16px;
  }

  .page-header {
    margin-bottom: 20px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .page-subtitle {
    font-size: 14px;
  }

  .pagination-container {
    padding: 16px;
  }

  .empty-state {
    padding: 30px 16px;
  }
}
</style>
