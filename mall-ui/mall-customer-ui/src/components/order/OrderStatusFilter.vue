<template>
  <div class="order-status-filter">
    <div class="filter-tabs">
      <div 
        v-for="status in statusOptions"
        :key="status.value"
        class="filter-tab"
        :class="{ active: currentStatus === status.value }"
        @click="selectStatus(status.value)"
      >
        <span class="tab-text">{{ status.label }}</span>
        <span 
          v-if="status.count !== undefined"
          class="tab-count"
        >
          {{ status.count }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  currentStatus: {
    type: String,
    default: ''
  },
  orderStats: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['status-change'])

// Status options with counts
const statusOptions = computed(() => [
  {
    value: '',
    label: 'All Orders',
    count: props.orderStats.totalOrders || 0
  },
  {
    value: 'PENDING_PAYMENT',
    label: 'Pending Payment',
    count: props.orderStats.pendingPayment || 0
  },
  {
    value: 'PAID',
    label: 'Paid',
    count: props.orderStats.pendingShipment || 0
  },
  {
    value: 'SHIPPED',
    label: 'Shipped',
    count: props.orderStats.pendingReceipt || 0
  },
  {
    value: 'COMPLETED',
    label: 'Completed',
    count: props.orderStats.completed || 0
  }
])

// Select status
const selectStatus = (status) => {
  emit('status-change', status)
}
</script>

<style scoped>
.order-status-filter {
  background: white;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.filter-tabs {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.filter-tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border: 1px solid #e4e7ed;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  min-width: fit-content;
}

.filter-tab:hover {
  border-color: #409eff;
  color: #409eff;
}

.filter-tab.active {
  background: #409eff;
  border-color: #409eff;
  color: white;
}

.tab-text {
  font-size: 14px;
  font-weight: 500;
}

.tab-count {
  background: rgba(255, 255, 255, 0.2);
  color: inherit;
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
  min-width: 18px;
  text-align: center;
}

.filter-tab:not(.active) .tab-count {
  background: #f0f0f0;
  color: #666;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .order-status-filter {
    padding: 12px;
    margin-bottom: 16px;
  }
  
  .filter-tabs {
    gap: 6px;
  }
  
  .filter-tab {
    padding: 6px 12px;
    font-size: 13px;
  }
  
  .tab-text {
    font-size: 13px;
  }
  
  .tab-count {
    font-size: 11px;
    padding: 1px 4px;
  }
}
</style>
