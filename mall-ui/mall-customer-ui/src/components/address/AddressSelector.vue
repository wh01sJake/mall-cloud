<template>
  <div class="address-selector">
    <div class="selector-header">
      <h3>Select Shipping Address</h3>
      <el-button 
        type="primary" 
        size="small"
        @click="showAddForm = true"
      >
        Add New Address
      </el-button>
    </div>

    <!-- Loading State -->
    <div v-if="addressStore.loading" class="loading-container">
      <el-skeleton :rows="2" animated />
    </div>

    <!-- Address List -->
    <div v-else-if="addressStore.addresses.length > 0" class="address-list">
      <AddressCard
        v-for="address in addressStore.addresses"
        :key="address.id"
        :address="address"
        :selectable="true"
        :is-selected="selectedAddressId === address.id"
        :hide-delete="true"
        @select="handleSelectAddress"
        @edit="handleEditAddress"
        @refresh="refreshAddresses"
      />
    </div>

    <!-- Empty State -->
    <div v-else class="empty-state">
      <el-empty 
        :image-size="100"
        description="No addresses found"
      >
        <el-button 
          type="primary"
          @click="showAddForm = true"
        >
          Add Your First Address
        </el-button>
      </el-empty>
    </div>

    <!-- Add/Edit Address Dialog -->
    <el-dialog
      v-model="showAddForm"
      :title="editingAddress ? 'Edit Address' : 'Add New Address'"
      width="90%"
      :max-width="600"
      :close-on-click-modal="false"
    >
      <AddressForm
        :address="editingAddress"
        :loading="formLoading"
        @submit="handleFormSubmit"
        @cancel="handleFormCancel"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useAddressStore } from '@/stores/address'
import AddressCard from './AddressCard.vue'
import AddressForm from './AddressForm.vue'

const props = defineProps({
  selectedAddressId: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['address-selected'])

const addressStore = useAddressStore()

// Reactive data
const showAddForm = ref(false)
const editingAddress = ref(null)
const formLoading = ref(false)

// Computed properties
const selectedAddress = computed(() => {
  return addressStore.getAddressById(props.selectedAddressId)
})

// Methods
const refreshAddresses = async () => {
  await addressStore.fetchAddresses()
}

const handleSelectAddress = (address) => {
  emit('address-selected', address)
}

const handleEditAddress = (address) => {
  editingAddress.value = address
  showAddForm.value = true
}

const handleFormSubmit = async (formData) => {
  try {
    formLoading.value = true
    
    if (editingAddress.value) {
      // Update existing address
      await addressStore.updateAddress(editingAddress.value.id, formData)
    } else {
      // Create new address
      await addressStore.createAddress(formData)
    }
    
    // Close dialog and refresh
    handleFormCancel()
    await refreshAddresses()
    
    // Auto-select the new/updated address if it's set as default
    if (formData.defaultStatus) {
      const defaultAddress = addressStore.getDefaultAddress
      if (defaultAddress) {
        emit('address-selected', defaultAddress)
      }
    }
  } catch (error) {
    console.error('Failed to save address:', error)
  } finally {
    formLoading.value = false
  }
}

const handleFormCancel = () => {
  showAddForm.value = false
  editingAddress.value = null
}

// Lifecycle
onMounted(async () => {
  await refreshAddresses()
  
  // Auto-select default address if no address is selected
  if (!props.selectedAddressId) {
    const defaultAddress = addressStore.getDefaultAddress
    if (defaultAddress) {
      emit('address-selected', defaultAddress)
    }
  }
})
</script>

<style scoped>
.address-selector {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

.selector-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e4e7ed;
}

.selector-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.loading-container {
  padding: 20px 0;
}

.address-list {
  max-height: 400px;
  overflow-y: auto;
}

.empty-state {
  padding: 40px 20px;
  text-align: center;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .address-selector {
    padding: 16px;
  }
  
  .selector-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .selector-header h3 {
    font-size: 16px;
    text-align: center;
  }
  
  .address-list {
    max-height: 300px;
  }
  
  .empty-state {
    padding: 30px 16px;
  }
}
</style>
