<template>
  <div class="address-card" :class="{ selected: isSelected, default: address.defaultStatus === 1 }">
    <div class="address-header">
      <div class="receiver-info">
        <span class="receiver-name">{{ address.receiverName }}</span>
        <span class="receiver-phone">{{ address.receiverPhone }}</span>
      </div>
      <div class="address-badges">
        <el-tag v-if="address.defaultStatus === 1" type="primary" size="small">
          Default
        </el-tag>
        <el-tag v-if="isSelected" type="success" size="small">
          Selected
        </el-tag>
      </div>
    </div>

    <div class="address-content">
      <p class="address-text">{{ formatAddress(address) }}</p>
      <p v-if="address.postCode" class="postal-code">Postal Code: {{ address.postCode }}</p>
    </div>

    <div class="address-actions">
      <div class="action-buttons">
        <el-button 
          v-if="selectable && !isSelected"
          type="primary"
          size="small"
          @click="handleSelect"
        >
          Select
        </el-button>
        <el-button 
          v-if="!address.defaultStatus && !hideSetDefault"
          size="small"
          @click="handleSetDefault"
          :loading="loading"
        >
          Set as Default
        </el-button>
        <el-button 
          size="small"
          @click="handleEdit"
        >
          Edit
        </el-button>
        <el-button 
          v-if="!hideDelete && address.defaultStatus !== 1"
          type="danger"
          size="small"
          @click="handleDelete"
          :loading="loading"
        >
          Delete
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAddressStore } from '@/stores/address'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps({
  address: {
    type: Object,
    required: true
  },
  selectable: {
    type: Boolean,
    default: false
  },
  isSelected: {
    type: Boolean,
    default: false
  },
  hideSetDefault: {
    type: Boolean,
    default: false
  },
  hideDelete: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['select', 'edit', 'refresh'])

const addressStore = useAddressStore()
const loading = ref(false)

// Format address for display
const formatAddress = (address) => {
  return addressStore.formatAddress(address)
}

// Handle address selection
const handleSelect = () => {
  emit('select', props.address)
}

// Handle edit
const handleEdit = () => {
  emit('edit', props.address)
}

// Handle set as default
const handleSetDefault = async () => {
  try {
    loading.value = true
    await addressStore.setDefaultAddress(props.address.id)
    emit('refresh')
  } catch (error) {
    console.error('Failed to set default address:', error)
  } finally {
    loading.value = false
  }
}

// Handle delete
const handleDelete = async () => {
  try {
    await ElMessageBox.confirm(
      'Are you sure you want to delete this address?',
      'Confirm Deletion',
      {
        confirmButtonText: 'Yes, Delete',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    )
    
    loading.value = true
    await addressStore.deleteAddress(props.address.id)
    emit('refresh')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete address:', error)
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.address-card {
  background: white;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.address-card:hover {
  border-color: #c0c4cc;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.address-card.selected {
  border-color: #409eff;
  background: #f0f9ff;
}

.address-card.default {
  border-color: #67c23a;
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.receiver-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.receiver-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.receiver-phone {
  font-size: 14px;
  color: #909399;
}

.address-badges {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.address-content {
  margin-bottom: 16px;
}

.address-text {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  margin: 0 0 8px 0;
}

.postal-code {
  font-size: 12px;
  color: #909399;
  margin: 0;
}

.address-actions {
  border-top: 1px solid #e4e7ed;
  padding-top: 12px;
}

.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .address-card {
    padding: 12px;
    margin-bottom: 12px;
  }
  
  .address-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .receiver-info {
    width: 100%;
  }
  
  .address-badges {
    align-self: flex-start;
  }
  
  .action-buttons {
    justify-content: center;
  }
  
  .action-buttons .el-button {
    flex: 1;
    min-width: 0;
  }
}

/* Small mobile */
@media (max-width: 480px) {
  .action-buttons {
    flex-direction: column;
  }
  
  .action-buttons .el-button {
    width: 100%;
  }
}
</style>
