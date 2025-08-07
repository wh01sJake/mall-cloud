<template>
  <div class="search-filters">
    <div class="filters-header">
      <h3>Filters</h3>
      <el-button 
        v-if="hasActiveFilters"
        type="text" 
        size="small"
        @click="clearFilters"
      >
        Clear All
      </el-button>
    </div>

    <!-- Category Filter -->
    <div class="filter-section">
      <h4>Category</h4>
      <el-select
        v-model="filters.categoryId"
        placeholder="All Categories"
        clearable
        @change="handleFilterChange"
      >
        <el-option
          v-for="category in productStore.categories"
          :key="category.id"
          :label="category.name"
          :value="category.id"
        />
      </el-select>
    </div>

    <!-- Price Range Filter -->
    <div class="filter-section">
      <h4>Price Range</h4>
      <div class="price-inputs">
        <el-input
          v-model.number="filters.minPrice"
          placeholder="Min"
          type="number"
          :min="0"
          @change="handleFilterChange"
        >
          <template #prepend>¥</template>
        </el-input>
        <span class="price-separator">-</span>
        <el-input
          v-model.number="filters.maxPrice"
          placeholder="Max"
          type="number"
          :min="0"
          @change="handleFilterChange"
        >
          <template #prepend>¥</template>
        </el-input>
      </div>
      
      <!-- Quick Price Ranges -->
      <div class="quick-prices">
        <el-tag
          v-for="range in priceRanges"
          :key="range.label"
          size="small"
          :type="isPriceRangeActive(range) ? 'primary' : ''"
          @click="selectPriceRange(range)"
          class="price-tag"
        >
          {{ range.label }}
        </el-tag>
      </div>
    </div>

    <!-- Sort Options -->
    <div class="filter-section">
      <h4>Sort By</h4>
      <el-select
        v-model="sortOption"
        @change="handleSortChange"
      >
        <el-option
          v-for="option in sortOptions"
          :key="option.value"
          :label="option.label"
          :value="option.value"
        />
      </el-select>
    </div>

    <!-- Active Filters Display -->
    <div v-if="hasActiveFilters" class="active-filters">
      <h4>Active Filters</h4>
      <div class="filter-tags">
        <el-tag
          v-if="filters.categoryId"
          closable
          @close="removeFilter('categoryId')"
        >
          Category: {{ getCategoryName(filters.categoryId) }}
        </el-tag>
        <el-tag
          v-if="filters.minPrice !== null"
          closable
          @close="removeFilter('minPrice')"
        >
          Min: ¥{{ filters.minPrice }}
        </el-tag>
        <el-tag
          v-if="filters.maxPrice !== null"
          closable
          @close="removeFilter('maxPrice')"
        >
          Max: ¥{{ filters.maxPrice }}
        </el-tag>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useProductStore } from '@/stores/product'

const emit = defineEmits(['filter-change'])

const productStore = useProductStore()

// Reactive data
const filters = ref({
  categoryId: null,
  minPrice: null,
  maxPrice: null
})

const sortOption = ref('createTime-desc')

// Computed properties
const hasActiveFilters = computed(() => {
  return filters.value.categoryId !== null ||
         filters.value.minPrice !== null ||
         filters.value.maxPrice !== null
})

// Sort options
const sortOptions = [
  { label: 'Newest First', value: 'createTime-desc' },
  { label: 'Oldest First', value: 'createTime-asc' },
  { label: 'Price: Low to High', value: 'price-asc' },
  { label: 'Price: High to Low', value: 'price-desc' },
  { label: 'Name: A to Z', value: 'name-asc' },
  { label: 'Name: Z to A', value: 'name-desc' }
]

// Quick price ranges
const priceRanges = [
  { label: 'Under ¥50', min: 0, max: 50 },
  { label: '¥50 - ¥100', min: 50, max: 100 },
  { label: '¥100 - ¥200', min: 100, max: 200 },
  { label: '¥200 - ¥500', min: 200, max: 500 },
  { label: 'Over ¥500', min: 500, max: null }
]

// Methods
const handleFilterChange = () => {
  emitFilters()
}

const handleSortChange = () => {
  emitFilters()
}

const emitFilters = () => {
  const [sortBy, sortOrder] = sortOption.value.split('-')
  
  const filterData = {
    ...filters.value,
    sortBy,
    sortOrder
  }
  
  emit('filter-change', filterData)
}

const clearFilters = () => {
  filters.value = {
    categoryId: null,
    minPrice: null,
    maxPrice: null
  }
  sortOption.value = 'createTime-desc'
  emitFilters()
}

const removeFilter = (filterKey) => {
  filters.value[filterKey] = null
  emitFilters()
}

const selectPriceRange = (range) => {
  filters.value.minPrice = range.min
  filters.value.maxPrice = range.max
  emitFilters()
}

const isPriceRangeActive = (range) => {
  return filters.value.minPrice === range.min && filters.value.maxPrice === range.max
}

const getCategoryName = (categoryId) => {
  const category = productStore.categories.find(cat => cat.id === categoryId)
  return category ? category.name : 'Unknown'
}

// Initialize filters from store
watch(() => productStore.searchFilters, (newFilters) => {
  filters.value = {
    categoryId: newFilters.categoryId,
    minPrice: newFilters.minPrice,
    maxPrice: newFilters.maxPrice
  }
  sortOption.value = `${newFilters.sortBy}-${newFilters.sortOrder}`
}, { immediate: true })
</script>

<style scoped>
.search-filters {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.filters-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e4e7ed;
}

.filters-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.filter-section {
  margin-bottom: 24px;
}

.filter-section:last-child {
  margin-bottom: 0;
}

.filter-section h4 {
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  margin: 0 0 12px 0;
}

.filter-section .el-select {
  width: 100%;
}

.price-inputs {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.price-inputs .el-input {
  flex: 1;
}

.price-separator {
  color: #909399;
  font-weight: 500;
}

.quick-prices {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.price-tag {
  cursor: pointer;
  transition: all 0.2s ease;
}

.price-tag:hover {
  opacity: 0.8;
}

.active-filters {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
}

.active-filters h4 {
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  margin: 0 0 12px 0;
}

.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .search-filters {
    padding: 16px;
  }
  
  .filters-header {
    margin-bottom: 16px;
  }
  
  .filter-section {
    margin-bottom: 20px;
  }
  
  .price-inputs {
    flex-direction: column;
    align-items: stretch;
  }
  
  .price-separator {
    text-align: center;
    padding: 4px 0;
  }
  
  .quick-prices {
    gap: 6px;
  }
}
</style>
