import { defineStore } from 'pinia'
import { productService } from '@/services/product'

export const useProductStore = defineStore('product', {
  state: () => ({
    products: [],
    categories: [],
    currentProduct: null,
    loading: false,
    searchResults: [],
    searchLoading: false,
    searchSuggestions: [],
    popularSearches: [],
    searchHistory: [],
    searchPagination: {
      page: 1,
      size: 20,
      total: 0,
      pages: 0
    },
    searchFilters: {
      query: '',
      categoryId: null,
      sortBy: 'createTime',
      sortOrder: 'desc',
      minPrice: null,
      maxPrice: null
    }
  }),

  getters: {
    getProductById: (state) => (id) => {
      return state.products.find(product => product.id === id)
    },
    
    getProductsByCategory: (state) => (categoryId) => {
      return state.products.filter(product => product.categoryId === categoryId)
    },
    
    featuredProducts: (state) => {
      // Return first 8 products as featured
      return state.products.slice(0, 8)
    },
  },

  actions: {
    /**
     * Fetch products with optional filters
     * @param {number} categoryId 
     * @param {number} limit 
     */
    async fetchProducts(categoryId = null, limit = null) {
      this.loading = true
      try {
        const response = await productService.getProducts(categoryId, limit)
        
        if (response && response.code === 0 && response.data) {
          // Handle the backend response format which has records property
          const products = response.data.records || response.data
          this.products = products
        } else {
          this.products = []
        }
      } catch (error) {
        console.warn('Product service unavailable - using mock products:', error.message)
        // Provide mock products when service is unavailable
        this.products = [
          {
            id: 1,
            name: 'Premium Vape Kit',
            subtitle: 'High-quality starter vape kit with everything you need',
            detail: '<div><h3>Premium Features</h3><p>This kit includes everything needed for a premium vaping experience.</p></div>',
            price: 49.99,
            stock: 15,
            categoryId: 1,
            mainImage: '/placeholder-product.svg'
          },
          {
            id: 2,
            name: 'Strawberry E-Liquid',
            subtitle: 'Delicious strawberry flavored e-liquid',
            detail: '<div><h3>Flavor Profile</h3><p>Sweet and refreshing strawberry flavor with smooth finish.</p></div>',
            price: 12.99,
            stock: 25,
            categoryId: 2,
            mainImage: '/placeholder-product.svg'
          },
          {
            id: 3,
            name: 'Replacement Coils',
            subtitle: 'High-quality replacement coils pack of 5',
            detail: '<div><h3>Specifications</h3><p>Compatible with most standard vape devices. Long-lasting performance.</p></div>',
            price: 19.99,
            stock: 30,
            categoryId: 3,
            mainImage: '/placeholder-product.svg'
          },
          {
            id: 4,
            name: 'Beginner Starter Kit',
            subtitle: 'Perfect kit for those new to vaping',
            detail: '<div><h3>Beginner Friendly</h3><p>Easy to use kit with step-by-step instructions included.</p></div>',
            price: 29.99,
            stock: 20,
            categoryId: 4,
            mainImage: '/placeholder-product.svg'
          }
        ].slice(0, limit || 8)
      } finally {
        this.loading = false
      }
    },

    /**
     * Fetch product detail by ID
     * @param {number} id 
     */
    async fetchProductDetail(id) {
      this.loading = true
      try {
        const response = await productService.getProductById(id)
        if (response && response.code === 0 && response.data) {
          this.currentProduct = response.data
        } else {
          this.currentProduct = null
        }
      } catch (error) {
        console.error('Error fetching product detail:', error)
        this.currentProduct = null
      } finally {
        this.loading = false
      }
    },

    /**
     * Fetch products by category
     * @param {number} categoryId 
     */
    async fetchProductsByCategory(categoryId) {
      this.loading = true
      try {
        const response = await productService.getProductsByCategory(categoryId)
        if (response && response.code === 0 && response.data) {
          this.products = Array.isArray(response.data) ? response.data : []
        } else {
          this.products = []
        }
      } catch (error) {
        console.error('Error fetching products by category:', error)
        this.products = []
      } finally {
        this.loading = false
      }
    },

    /**
     * Fetch all categories
     */
    async fetchCategories() {
      try {
        const response = await productService.getCategories()
        
        if (response && response.code === 0 && response.data) {
          const categories = Array.isArray(response.data) ? response.data : []
          this.categories = categories
        } else {
          this.categories = []
        }
      } catch (error) {
        console.warn('Product service unavailable - using mock categories:', error.message)
        // Provide mock categories when service is unavailable
        this.categories = [
          { id: 1, name: 'E-Cigarettes', description: 'Premium electronic cigarettes' },
          { id: 2, name: 'E-Liquids', description: 'Variety of flavored e-liquids' },
          { id: 3, name: 'Accessories', description: 'Vaping accessories and parts' },
          { id: 4, name: 'Starter Kits', description: 'Complete starter kits for beginners' }
        ]
      }
    },

    /**
     * Search products with advanced filtering
     * @param {Object} searchParams - Search parameters
     */
    async searchProducts(searchParams = {}) {
      this.searchLoading = true
      try {
        // Update search filters
        Object.assign(this.searchFilters, searchParams)

        const response = await productService.searchProducts(this.searchFilters)
        if (response.data) {
          if (response.data.records) {
            // Paginated response
            this.searchResults = response.data.records
            this.searchPagination = {
              page: response.data.current || 1,
              size: response.data.size || 20,
              total: response.data.total || 0,
              pages: response.data.pages || 0
            }
          } else {
            // Simple array response
            this.searchResults = response.data
            this.searchPagination.total = response.data.length
          }

          // Add to search history if there's a query
          if (searchParams.query && searchParams.query.trim()) {
            this.addToSearchHistory(searchParams.query.trim())
          }
        }
      } catch (error) {
        console.error('Error searching products:', error)
        this.searchResults = []
      } finally {
        this.searchLoading = false
      }
    },

    /**
     * Get search suggestions
     * @param {string} query - Search query
     */
    async getSearchSuggestions(query) {
      if (!query || query.trim().length < 2) {
        this.searchSuggestions = []
        return
      }

      try {
        const response = await productService.getSearchSuggestions(query)
        if (response.data) {
          this.searchSuggestions = response.data
        }
      } catch (error) {
        console.error('Error getting search suggestions:', error)
        this.searchSuggestions = []
      }
    },

    /**
     * Get popular searches
     */
    async getPopularSearches() {
      try {
        const response = await productService.getPopularSearches()
        if (response.data) {
          this.popularSearches = response.data
        }
      } catch (error) {
        console.error('Error getting popular searches:', error)
      }
    },

    /**
     * Add search term to history
     * @param {string} query - Search query
     */
    addToSearchHistory(query) {
      const trimmedQuery = query.trim()
      if (!trimmedQuery) return

      // Remove if already exists
      this.searchHistory = this.searchHistory.filter(item => item !== trimmedQuery)

      // Add to beginning
      this.searchHistory.unshift(trimmedQuery)

      // Keep only last 10 searches
      this.searchHistory = this.searchHistory.slice(0, 10)

      // Save to localStorage
      try {
        localStorage.setItem('searchHistory', JSON.stringify(this.searchHistory))
      } catch (error) {
        console.error('Error saving search history:', error)
      }
    },

    /**
     * Load search history from localStorage
     */
    loadSearchHistory() {
      try {
        const saved = localStorage.getItem('searchHistory')
        if (saved) {
          this.searchHistory = JSON.parse(saved)
        }
      } catch (error) {
        console.error('Error loading search history:', error)
        this.searchHistory = []
      }
    },

    /**
     * Clear search history
     */
    clearSearchHistory() {
      this.searchHistory = []
      try {
        localStorage.removeItem('searchHistory')
      } catch (error) {
        console.error('Error clearing search history:', error)
      }
    },

    /**
     * Clear search results and filters
     */
    clearSearchResults() {
      this.searchResults = []
      this.searchSuggestions = []
      this.searchFilters = {
        query: '',
        categoryId: null,
        sortBy: 'createTime',
        sortOrder: 'desc',
        minPrice: null,
        maxPrice: null
      }
      this.searchPagination = {
        page: 1,
        size: 20,
        total: 0,
        pages: 0
      }
    },

    /**
     * Clear current product
     */
    clearCurrentProduct() {
      this.currentProduct = null
    },
  },
})
