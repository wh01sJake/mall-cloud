<template>
  <el-form
    ref="formRef"
    :model="formData"
    :rules="formRules"
    label-width="120px"
    class="address-form"
  >
    <el-form-item label="Receiver Name" prop="receiverName">
      <el-input
        v-model="formData.receiverName"
        placeholder="Enter receiver name"
        maxlength="50"
        show-word-limit
      />
    </el-form-item>

    <el-form-item label="Phone Number" prop="receiverPhone">
      <el-input
        v-model="formData.receiverPhone"
        placeholder="Enter 9-digit phone number (e.g., 123456789)"
        maxlength="9"
      />
    </el-form-item>

    <el-form-item label="County" prop="province">
      <el-select
        v-model="formData.province"
        placeholder="Select county"
        filterable
        @change="handleProvinceChange"
      >
        <el-option
          v-for="county in counties"
          :key="county.value"
          :label="county.label"
          :value="county.value"
        />
      </el-select>
    </el-form-item>

    <el-form-item label="City/Town" prop="city">
      <el-select
        v-model="formData.city"
        placeholder="Select city or town"
        filterable
        :disabled="!formData.province"
        @change="handleCityChange"
      >
        <el-option
          v-for="city in cities"
          :key="city.value"
          :label="city.label"
          :value="city.value"
        />
      </el-select>
    </el-form-item>

    <el-form-item label="Area" prop="region">
      <el-input
        v-model="formData.region"
        placeholder="Enter area/suburb (optional)"
        maxlength="100"
      />
    </el-form-item>

    <el-form-item label="Detailed Address" prop="detailAddress">
      <el-input
        v-model="formData.detailAddress"
        type="textarea"
        :rows="3"
        placeholder="Enter detailed address (street, building, room number, etc.)"
        maxlength="200"
        show-word-limit
      />
    </el-form-item>

    <el-form-item label="Eircode" prop="postCode">
      <el-input
        v-model="formData.postCode"
        placeholder="Enter Eircode (e.g., D02 XY45)"
        maxlength="8"
      />
    </el-form-item>

    <el-form-item>
      <el-checkbox v-model="formData.defaultStatus">
        Set as default address
      </el-checkbox>
    </el-form-item>

    <el-form-item>
      <div class="form-actions">
        <el-button @click="handleCancel">Cancel</el-button>
        <el-button 
          type="primary" 
          @click="handleSubmit"
          :loading="loading"
        >
          {{ isEdit ? 'Update' : 'Add' }} Address
        </el-button>
      </div>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useAddressStore } from '@/stores/address'

const props = defineProps({
  address: {
    type: Object,
    default: null
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['submit', 'cancel'])

const addressStore = useAddressStore()
const formRef = ref()

// Check if this is edit mode
const isEdit = computed(() => !!props.address)

// Form data
const formData = reactive({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  region: '',
  detailAddress: '',
  postCode: '',
  defaultStatus: false
})

// Form validation rules
const formRules = {
  receiverName: [
    { required: true, message: 'Please enter receiver name', trigger: 'blur' },
    { min: 2, max: 50, message: 'Name should be 2-50 characters', trigger: 'blur' }
  ],
  receiverPhone: [
    { required: true, message: 'Please enter phone number', trigger: 'blur' }
  ],
  province: [
    { required: true, message: 'Please select county', trigger: 'change' }
  ],
  city: [
    { required: true, message: 'Please select city or town', trigger: 'change' }
  ],
  detailAddress: [
    { required: true, message: 'Please enter detailed address', trigger: 'blur' },
    { min: 5, max: 200, message: 'Address should be 5-200 characters', trigger: 'blur' }
  ],
  postCode: [
    { pattern: /^[A-Z]\d{2}\s?[A-Z0-9]{4}$/i, message: 'Please enter a valid Eircode (e.g., D02 XY45)', trigger: 'blur' }
  ]
}

// Irish counties and cities data
const counties = ref([
  { value: 'Dublin', label: 'Dublin' },
  { value: 'Cork', label: 'Cork' },
  { value: 'Galway', label: 'Galway' },
  { value: 'Limerick', label: 'Limerick' },
  { value: 'Waterford', label: 'Waterford' },
  { value: 'Kerry', label: 'Kerry' },
  { value: 'Clare', label: 'Clare' },
  { value: 'Tipperary', label: 'Tipperary' },
  { value: 'Kilkenny', label: 'Kilkenny' },
  { value: 'Wexford', label: 'Wexford' },
  { value: 'Wicklow', label: 'Wicklow' },
  { value: 'Kildare', label: 'Kildare' },
  { value: 'Meath', label: 'Meath' },
  { value: 'Louth', label: 'Louth' },
  { value: 'Carlow', label: 'Carlow' },
  { value: 'Laois', label: 'Laois' },
  { value: 'Offaly', label: 'Offaly' },
  { value: 'Westmeath', label: 'Westmeath' },
  { value: 'Longford', label: 'Longford' },
  { value: 'Roscommon', label: 'Roscommon' },
  { value: 'Sligo', label: 'Sligo' },
  { value: 'Leitrim', label: 'Leitrim' },
  { value: 'Donegal', label: 'Donegal' },
  { value: 'Cavan', label: 'Cavan' },
  { value: 'Monaghan', label: 'Monaghan' },
  { value: 'Mayo', label: 'Mayo' }
])

const cities = ref([])

// Irish cities and towns by county
const cityData = {
  'Dublin': [
    { value: 'Dublin City', label: 'Dublin City' },
    { value: 'Dún Laoghaire', label: 'Dún Laoghaire' },
    { value: 'Blackrock', label: 'Blackrock' },
    { value: 'Swords', label: 'Swords' },
    { value: 'Blanchardstown', label: 'Blanchardstown' },
    { value: 'Tallaght', label: 'Tallaght' }
  ],
  'Cork': [
    { value: 'Cork City', label: 'Cork City' },
    { value: 'Cobh', label: 'Cobh' },
    { value: 'Mallow', label: 'Mallow' },
    { value: 'Youghal', label: 'Youghal' },
    { value: 'Fermoy', label: 'Fermoy' }
  ],
  'Galway': [
    { value: 'Galway City', label: 'Galway City' },
    { value: 'Tuam', label: 'Tuam' },
    { value: 'Ballinasloe', label: 'Ballinasloe' },
    { value: 'Loughrea', label: 'Loughrea' }
  ],
  'Limerick': [
    { value: 'Limerick City', label: 'Limerick City' },
    { value: 'Newcastle West', label: 'Newcastle West' },
    { value: 'Kilmallock', label: 'Kilmallock' }
  ],
  'Waterford': [
    { value: 'Waterford City', label: 'Waterford City' },
    { value: 'Dungarvan', label: 'Dungarvan' },
    { value: 'Tramore', label: 'Tramore' }
  ],
  'Kerry': [
    { value: 'Killarney', label: 'Killarney' },
    { value: 'Tralee', label: 'Tralee' },
    { value: 'Dingle', label: 'Dingle' },
    { value: 'Kenmare', label: 'Kenmare' }
  ]
}

// Handle county change
const handleProvinceChange = (county) => {
  formData.city = ''
  cities.value = cityData[county] || []
}

// Handle city change
const handleCityChange = (city) => {
  // No additional logic needed for Ireland
}

// Handle form submission
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    emit('submit', { ...formData })
  } catch (error) {
    console.error('Form validation failed:', error)
  }
}

// Handle cancel
const handleCancel = () => {
  emit('cancel')
}

// Initialize form data when address prop changes
watch(() => props.address, (newAddress) => {
  if (newAddress) {
    Object.assign(formData, {
      receiverName: newAddress.receiverName || '',
      receiverPhone: newAddress.receiverPhone || '',
      province: newAddress.province || '',
      city: newAddress.city || '',
      region: newAddress.region || '',
      detailAddress: newAddress.detailAddress || '',
      postCode: newAddress.postCode || '',
      defaultStatus: newAddress.defaultStatus === 1
    })
    
    // Load cities based on existing county data
    if (newAddress.province) {
      cities.value = cityData[newAddress.province] || []
    }
  }
}, { immediate: true })

// Reset form
const resetForm = () => {
  Object.assign(formData, {
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    region: '',
    detailAddress: '',
    postCode: '',
    defaultStatus: false
  })
  cities.value = []
  formRef.value?.clearValidate()
}

// Expose reset method
defineExpose({
  resetForm
})
</script>

<style scoped>
.address-form {
  max-width: 600px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .address-form {
    max-width: 100%;
  }
  
  .address-form :deep(.el-form-item__label) {
    width: 100px !important;
  }
  
  .form-actions {
    justify-content: center;
  }
  
  .form-actions .el-button {
    flex: 1;
  }
}
</style>
