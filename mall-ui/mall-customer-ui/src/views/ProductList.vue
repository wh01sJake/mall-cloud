<template>
  <div class="product-list">
    <div class="container">
      <!-- Search Header -->
      <div v-if="isSearchMode" class="search-header">
        <SearchBox
          :initial-query="searchQuery"
          @search="handleSearch"
        />
        <div class="search-info">
          <span class="results-count">
            {{ productStore.searchPagination.total }} results found
            <span v-if="searchQuery">for "{{ searchQuery }}"</span>
          </span>
        </div>
      </div>

      <!-- Page Header -->
      <div v-else class="page-header">
        <h1>{{ pageTitle }}</h1>
        <p v-if="categoryId">Browse our {{ categoryName }} collection</p>
        <p v-else>Discover our complete product range</p>
      </div>

      <!-- Content Layout -->
      <div class="content-layout">
        <!-- Filters Sidebar -->
        <div v-if="isSearchMode" class="filters-sidebar">
          <SearchFilters @filter-change="handleFilterChange" />
        </div>

        <!-- Products Content -->
        <div class="products-content" :class="{ 'with-sidebar': isSearchMode }">
          <!-- Loading State -->
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="3" animated />
          </div>

          <!-- Products Grid -->
          <div v-else-if="products.length > 0">
            <!-- Results Summary -->
            <div v-if="isSearchMode" class="results-summary">
              <div class="summary-info">
                <span>Showing {{ products.length }} of {{ productStore.searchPagination.total }} results</span>
              </div>
            </div>

            <div class="products-grid">
              <ProductCard
                v-for="product in products"
                :key="product.id"
                :product="product"
              />
            </div>

            <!-- Pagination -->
            <div v-if="showPagination" class="pagination-container">
              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[12, 24, 48]"
                :total="productStore.searchPagination.total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handlePageChange"
              />
            </div>
          </div>

          <!-- Empty State -->
          <div v-else class="no-products">
            <el-empty
              :image-size="120"
              :description="emptyStateMessage"
            >
              <div class="empty-actions">
                <el-button v-if="isSearchMode" @click="clearSearch">
                  Clear Search
                </el-button>
                <el-button type="primary" @click="$router.push('/')">
                  Back to Home
                </el-button>
              </div>
            </el-empty>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProductStore } from '@/stores/product'
import ProductCard from '@/components/product/ProductCard.vue'
import SearchBox from '@/components/search/SearchBox.vue'
import SearchFilters from '@/components/search/SearchFilters.vue'

const route = useRoute()
const router = useRouter()
const productStore = useProductStore()

// Reactive data
const currentPage = ref(1)
const pageSize = ref(24)

// Computed properties
const categoryId = computed(() => route.params.categoryId ? parseInt(route.params.categoryId) : null)
const searchQuery = computed(() => route.query.search || '')
const isSearchMode = computed(() => !!searchQuery.value)

const products = computed(() => {
  if (isSearchMode.value) {
    return productStore.searchResults
  }
  return productStore.products
})

const loading = computed(() => {
  return isSearchMode.value ? productStore.searchLoading : productStore.loading
})

const categoryName = computed(() => {
  if (categoryId.value) {
    // First check parent categories
    let category = productStore.categories.find(cat => cat.id === categoryId.value)
    if (category) {
      return category.name
    }
    
    // Then check subcategories
    for (const parentCategory of productStore.categories) {
      if (parentCategory.childList) {
        const subCategory = parentCategory.childList.find(child => child.id === categoryId.value)
        if (subCategory) {
          return subCategory.name
        }
      }
    }
    
    return 'Category'
  }
  return ''
})

const pageTitle = computed(() => {
  if (searchQuery.value) {
    return 'Search Results'
  } else if (categoryId.value) {
    return categoryName.value
  }
  return 'All Products'
})

const emptyStateMessage = computed(() => {
  if (isSearchMode.value) {
    return `No products found for "${searchQuery.value}"`
  } else if (categoryId.value) {
    return `No products found in ${categoryName.value}`
  }
  return 'No products available'
})

const showPagination = computed(() => {
  return isSearchMode.value && productStore.searchPagination.total > pageSize.value
})

// Methods
const loadProducts = async () => {
  if (isSearchMode.value) {
    await productStore.searchProducts({
      query: searchQuery.value,
      categoryId: categoryId.value,
      page: currentPage.value,
      size: pageSize.value
    })
  } else if (categoryId.value) {
    await productStore.fetchProductsByCategory(categoryId.value)
  } else {
    await productStore.fetchProducts()
  }
}

const handleSearch = (query) => {
  router.push({ name: 'products', query: { search: query } })
}

const handleFilterChange = (filters) => {
  currentPage.value = 1 // Reset to first page
  productStore.searchProducts({
    query: searchQuery.value,
    ...filters,
    page: currentPage.value,
    size: pageSize.value
  })
}

const handlePageChange = (page) => {
  currentPage.value = page
  productStore.searchProducts({
    query: searchQuery.value,
    ...productStore.searchFilters,
    page: currentPage.value,
    size: pageSize.value
  })
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1 // Reset to first page
  productStore.searchProducts({
    query: searchQuery.value,
    ...productStore.searchFilters,
    page: currentPage.value,
    size: pageSize.value
  })
}

const clearSearch = () => {
  productStore.clearSearchResults()
  router.push({ name: 'products' })
}

// Lifecycle
onMounted(async () => {
  await productStore.fetchCategories()
  await loadProducts()
})

// Watch for route changes
watch([categoryId, searchQuery], () => {
  currentPage.value = 1 // Reset page when route changes
  loadProducts()
})
</script>

<style scoped>
.product-list {
  padding: 20px 0 40px;
  min-height: 60vh;
  background: #f5f7fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.search-header {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-info {
  margin-top: 12px;
  text-align: center;
}

.results-count {
  font-size: 14px;
  color: #909399;
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

.page-header p {
  font-size: 16px;
  color: #909399;
  margin: 0;
}

.content-layout {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.filters-sidebar {
  width: 280px;
  flex-shrink: 0;
}

.products-content {
  flex: 1;
  min-width: 0;
}

.products-content.with-sidebar {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.loading-container {
  padding: 20px;
}

.results-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e4e7ed;
}

.summary-info {
  font-size: 14px;
  color: #606266;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.no-products {
  text-align: center;
  padding: 60px 20px;
}

.empty-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 16px;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .product-list {
    padding: 16px 0 32px;
  }

  .container {
    padding: 0 16px;
  }

  .search-header {
    padding: 16px;
    margin-bottom: 16px;
  }

  .page-header {
    margin-bottom: 20px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .page-header p {
    font-size: 14px;
  }

  .content-layout {
    flex-direction: column;
    gap: 16px;
  }

  .filters-sidebar {
    width: 100%;
  }

  .products-content.with-sidebar {
    padding: 16px;
  }

  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 16px;
    margin-bottom: 20px;
  }

  .results-summary {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }

  .empty-actions {
    flex-direction: column;
  }

  .empty-actions .el-button {
    width: 100%;
  }
}

/* Small mobile */
@media (max-width: 480px) {
  .products-grid {
    grid-template-columns: 1fr;
  }
}
</style>
