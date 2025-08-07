import { apiClient } from './api'

export const productService = {
  /**
   * Get products with optional category filter and limit
   * @param {number} categoryId - Optional category ID
   * @param {number} limit - Optional limit
   * @returns {Promise}
   */
  async getProducts(categoryId = null, limit = null) {
    const params = {}
    if (categoryId) params.categoryId = categoryId
    if (limit) params.limit = limit
    
    return apiClient.get('/product/customer/list', { params })
  },

  /**
   * Get product by ID
   * @param {number} id - Product ID
   * @returns {Promise}
   */
  async getProductById(id) {
    return apiClient.get(`/product/selectById/${id}`)
  },

  /**
   * Get products by category ID
   * @param {number} categoryId - Category ID
   * @returns {Promise}
   */
  async getProductsByCategory(categoryId) {
    return apiClient.get('/product/selectByCategoryId', {
      params: { id: categoryId }
    })
  },

  /**
   * Get all product categories
   * @returns {Promise}
   */
  async getCategories() {
    return apiClient.get('/product/category/listAll')
  },

  /**
   * Search products with advanced filtering
   * @param {Object} searchParams - Search parameters
   * @param {string} searchParams.query - Search query
   * @param {number} searchParams.categoryId - Optional category filter
   * @param {string} searchParams.sortBy - Sort field (price, name, createTime)
   * @param {string} searchParams.sortOrder - Sort order (asc, desc)
   * @param {number} searchParams.minPrice - Minimum price filter
   * @param {number} searchParams.maxPrice - Maximum price filter
   * @param {number} searchParams.page - Page number
   * @param {number} searchParams.size - Page size
   * @returns {Promise}
   */
  async searchProducts(searchParams = {}) {
    const {
      query,
      categoryId,
      sortBy = 'createTime',
      sortOrder = 'desc',
      minPrice,
      maxPrice,
      page = 1,
      size = 20
    } = searchParams

    // For now, we'll get all products and filter client-side
    // In a real implementation, this would be a backend search endpoint
    const params = {}
    if (categoryId) params.categoryId = categoryId

    const response = await apiClient.get('/product/customer/list', { params })

    if (response.data && response.data.records) {
      let filteredProducts = response.data.records

      // Text search filter
      if (query && query.trim()) {
        const searchTerm = query.toLowerCase().trim()
        filteredProducts = filteredProducts.filter(product =>
          product.name.toLowerCase().includes(searchTerm) ||
          (product.subtitle && product.subtitle.toLowerCase().includes(searchTerm)) ||
          (product.detail && product.detail.toLowerCase().includes(searchTerm)) ||
          (product.keywords && product.keywords.toLowerCase().includes(searchTerm))
        )
      }

      // Price range filter
      if (minPrice !== undefined && minPrice !== null) {
        filteredProducts = filteredProducts.filter(product => product.price >= minPrice)
      }
      if (maxPrice !== undefined && maxPrice !== null) {
        filteredProducts = filteredProducts.filter(product => product.price <= maxPrice)
      }

      // Sorting
      filteredProducts.sort((a, b) => {
        let aValue = a[sortBy]
        let bValue = b[sortBy]

        // Handle different data types
        if (sortBy === 'price') {
          aValue = parseFloat(aValue) || 0
          bValue = parseFloat(bValue) || 0
        } else if (sortBy === 'createTime') {
          aValue = new Date(aValue).getTime()
          bValue = new Date(bValue).getTime()
        } else {
          aValue = String(aValue).toLowerCase()
          bValue = String(bValue).toLowerCase()
        }

        if (sortOrder === 'asc') {
          return aValue > bValue ? 1 : -1
        } else {
          return aValue < bValue ? 1 : -1
        }
      })

      // Pagination
      const startIndex = (page - 1) * size
      const endIndex = startIndex + size
      const paginatedProducts = filteredProducts.slice(startIndex, endIndex)

      return {
        ...response,
        data: {
          records: paginatedProducts,
          total: filteredProducts.length,
          current: page,
          size: size,
          pages: Math.ceil(filteredProducts.length / size)
        }
      }
    }

    return response
  },

  /**
   * Get search suggestions
   * @param {string} query - Search query
   * @param {number} limit - Maximum number of suggestions
   * @returns {Promise}
   */
  async getSearchSuggestions(query, limit = 10) {
    if (!query || query.trim().length < 2) {
      return { data: [] }
    }

    try {
      // Get all products for suggestions
      const response = await apiClient.get('/product/customer/list')

      if (response.data && response.data.records) {
        const searchTerm = query.toLowerCase().trim()
        const suggestions = []
        const seen = new Set()

        // Extract suggestions from product names
        response.data.records.forEach(product => {
          const name = product.name.toLowerCase()
          if (name.includes(searchTerm)) {
            // Add the full product name
            if (!seen.has(product.name) && suggestions.length < limit) {
              suggestions.push({
                text: product.name,
                type: 'product',
                id: product.id
              })
              seen.add(product.name)
            }

            // Add individual words from the product name
            const words = product.name.split(' ')
            words.forEach(word => {
              const cleanWord = word.toLowerCase().trim()
              if (cleanWord.includes(searchTerm) && !seen.has(cleanWord) && suggestions.length < limit) {
                suggestions.push({
                  text: cleanWord,
                  type: 'keyword',
                  id: null
                })
                seen.add(cleanWord)
              }
            })
          }
        })

        return { data: suggestions.slice(0, limit) }
      }
    } catch (error) {
      console.error('Error getting search suggestions:', error)
    }

    return { data: [] }
  },

  /**
   * Get popular search terms
   * @returns {Promise}
   */
  async getPopularSearches() {
    // Mock popular searches - in a real app this would come from analytics
    return {
      data: [
        'E-cigarette',
        'Vape juice',
        'Pod system',
        'Coils',
        'Battery',
        'Starter kit',
        'Nicotine salt',
        'Mod'
      ]
    }
  },
}
