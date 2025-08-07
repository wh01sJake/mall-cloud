<template>
  <div class="search-box" :class="{ expanded: showSuggestions }">
    <el-input
      ref="searchInput"
      v-model="searchQuery"
      placeholder="Search products..."
      class="search-input"
      @input="handleInput"
      @focus="handleFocus"
      @blur="handleBlur"
      @keyup.enter="handleSearch"
      @keydown.down="handleArrowDown"
      @keydown.up="handleArrowUp"
      @keydown.escape="hideSuggestions"
    >
      <template #append>
        <el-button @click="handleSearch" :loading="productStore.searchLoading">
          <el-icon><Search /></el-icon>
        </el-button>
      </template>
    </el-input>

    <!-- Search Suggestions Dropdown -->
    <div 
      v-if="showSuggestions" 
      class="suggestions-dropdown"
      @mousedown.prevent
    >
      <!-- Search Suggestions -->
      <div v-if="productStore.searchSuggestions.length > 0" class="suggestion-section">
        <div class="section-title">Suggestions</div>
        <div
          v-for="(suggestion, index) in productStore.searchSuggestions"
          :key="`suggestion-${index}`"
          class="suggestion-item"
          :class="{ active: selectedIndex === index }"
          @click="selectSuggestion(suggestion.text)"
          @mouseenter="selectedIndex = index"
        >
          <el-icon class="suggestion-icon">
            <Search v-if="suggestion.type === 'keyword'" />
            <Box v-else />
          </el-icon>
          <span class="suggestion-text">{{ suggestion.text }}</span>
          <span v-if="suggestion.type === 'product'" class="suggestion-type">Product</span>
        </div>
      </div>

      <!-- Search History -->
      <div v-if="productStore.searchHistory.length > 0 && !searchQuery.trim()" class="suggestion-section">
        <div class="section-title">
          Recent Searches
          <el-button 
            type="text" 
            size="small"
            @click="clearHistory"
            class="clear-button"
          >
            Clear
          </el-button>
        </div>
        <div
          v-for="(term, index) in productStore.searchHistory.slice(0, 5)"
          :key="`history-${index}`"
          class="suggestion-item"
          :class="{ active: selectedIndex === index + productStore.searchSuggestions.length }"
          @click="selectSuggestion(term)"
          @mouseenter="selectedIndex = index + productStore.searchSuggestions.length"
        >
          <el-icon class="suggestion-icon"><Clock /></el-icon>
          <span class="suggestion-text">{{ term }}</span>
        </div>
      </div>

      <!-- Popular Searches -->
      <div v-if="productStore.popularSearches.length > 0 && !searchQuery.trim()" class="suggestion-section">
        <div class="section-title">Popular Searches</div>
        <div class="popular-tags">
          <el-tag
            v-for="term in productStore.popularSearches.slice(0, 6)"
            :key="term"
            size="small"
            @click="selectSuggestion(term)"
            class="popular-tag"
          >
            {{ term }}
          </el-tag>
        </div>
      </div>

      <!-- No Results -->
      <div v-if="searchQuery.trim() && productStore.searchSuggestions.length === 0" class="no-results">
        <div class="no-results-text">No suggestions found</div>
        <div class="no-results-action">
          <el-button size="small" @click="handleSearch">
            Search for "{{ searchQuery }}"
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useProductStore } from '@/stores/product'
import { Search, Box, Clock } from '@element-plus/icons-vue'

const props = defineProps({
  initialQuery: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: 'Search products...'
  }
})

const emit = defineEmits(['search'])

const router = useRouter()
const productStore = useProductStore()

// Reactive data
const searchQuery = ref(props.initialQuery)
const showSuggestions = ref(false)
const selectedIndex = ref(-1)
const searchInput = ref()
const debounceTimer = ref(null)

// Methods
const handleInput = () => {
  // Clear previous timer
  if (debounceTimer.value) {
    clearTimeout(debounceTimer.value)
  }

  // Debounce suggestions request
  debounceTimer.value = setTimeout(() => {
    if (searchQuery.value.trim()) {
      productStore.getSearchSuggestions(searchQuery.value)
    }
    showSuggestions.value = true
  }, 300)
}

const handleFocus = () => {
  showSuggestions.value = true
  selectedIndex.value = -1
}

const handleBlur = () => {
  // Delay hiding to allow click events on suggestions
  setTimeout(() => {
    showSuggestions.value = false
    selectedIndex.value = -1
  }, 200)
}

const handleSearch = () => {
  const query = searchQuery.value.trim()
  if (query) {
    hideSuggestions()
    emit('search', query)
    router.push({ name: 'products', query: { search: query } })
  }
}

const selectSuggestion = (text) => {
  searchQuery.value = text
  handleSearch()
}

const handleArrowDown = () => {
  const totalItems = productStore.searchSuggestions.length + 
                    (searchQuery.value.trim() ? 0 : productStore.searchHistory.length)
  
  if (selectedIndex.value < totalItems - 1) {
    selectedIndex.value++
  }
}

const handleArrowUp = () => {
  if (selectedIndex.value > 0) {
    selectedIndex.value--
  } else {
    selectedIndex.value = -1
  }
}

const hideSuggestions = () => {
  showSuggestions.value = false
  selectedIndex.value = -1
}

const clearHistory = () => {
  productStore.clearSearchHistory()
}

// Focus search input
const focus = () => {
  nextTick(() => {
    searchInput.value?.focus()
  })
}

// Lifecycle
onMounted(() => {
  productStore.loadSearchHistory()
  productStore.getPopularSearches()
})

// Expose methods
defineExpose({
  focus
})
</script>

<style scoped>
.search-box {
  position: relative;
  width: 100%;
  max-width: 400px;
}

.search-input {
  width: 100%;
}

.suggestions-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e4e7ed;
  border-top: none;
  border-radius: 0 0 4px 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  max-height: 400px;
  overflow-y: auto;
  z-index: 1000;
}

.suggestion-section {
  padding: 8px 0;
}

.suggestion-section:not(:last-child) {
  border-bottom: 1px solid #f0f0f0;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  font-size: 12px;
  font-weight: 600;
  color: #909399;
  text-transform: uppercase;
}

.clear-button {
  padding: 0;
  font-size: 12px;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.suggestion-item:hover,
.suggestion-item.active {
  background: #f5f7fa;
}

.suggestion-icon {
  color: #909399;
  font-size: 14px;
}

.suggestion-text {
  flex: 1;
  font-size: 14px;
  color: #303133;
}

.suggestion-type {
  font-size: 12px;
  color: #909399;
}

.popular-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 8px 16px;
}

.popular-tag {
  cursor: pointer;
  transition: all 0.2s ease;
}

.popular-tag:hover {
  background: #409eff;
  color: white;
}

.no-results {
  padding: 16px;
  text-align: center;
}

.no-results-text {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.no-results-action {
  margin-top: 8px;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .search-box {
    max-width: 100%;
  }
  
  .suggestions-dropdown {
    max-height: 300px;
  }
  
  .suggestion-item {
    padding: 12px 16px;
  }
  
  .popular-tags {
    gap: 6px;
  }
}
</style>
