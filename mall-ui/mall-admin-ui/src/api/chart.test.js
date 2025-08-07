import { describe, it, expect, vi, beforeEach } from 'vitest'
import chartApi from './chart.js'
import service from '@/utils/request.js'

// Mock the axios service
vi.mock('@/utils/request.js', () => {
  return {
    default: {
      get: vi.fn().mockImplementation(() => Promise.resolve({ code: 0, data: {} }))
    }
  }
})

describe('Chart API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should call selectClassCount with correct endpoint', async () => {
    await chartApi.selectClassCount()
    expect(service.get).toHaveBeenCalledWith('/statistics/category-count')
  })

  it('should call getOrderStats with correct endpoint', async () => {
    await chartApi.getOrderStats()
    expect(service.get).toHaveBeenCalledWith('/statistics/dashboard')
  })

  it('should call getRevenueTrends with correct endpoint and default period', async () => {
    await chartApi.getRevenueTrends()
    expect(service.get).toHaveBeenCalledWith('/statistics/revenue', { params: { period: '7d' } })
  })

  it('should call getRevenueTrends with custom period', async () => {
    await chartApi.getRevenueTrends('90d')
    expect(service.get).toHaveBeenCalledWith('/statistics/revenue', { params: { period: '90d' } })
  })

  it('should call getOrderStatusDistribution with correct endpoint', async () => {
    await chartApi.getOrderStatusDistribution()
    expect(service.get).toHaveBeenCalledWith('/statistics/order-status')
  })

  it('should call getPaymentMethodStats with correct endpoint', async () => {
    await chartApi.getPaymentMethodStats()
    expect(service.get).toHaveBeenCalledWith('/statistics/payment-methods')
  })

  it('should call getTopProducts with correct endpoint and default limit', async () => {
    await chartApi.getTopProducts()
    expect(service.get).toHaveBeenCalledWith('/statistics/top-products', { params: { limit: 10 } })
  })

  it('should call getTopProducts with custom limit', async () => {
    await chartApi.getTopProducts(5)
    expect(service.get).toHaveBeenCalledWith('/statistics/top-products', { params: { limit: 5 } })
  })

  it('should call getCustomerTrends with correct endpoint and default period', async () => {
    await chartApi.getCustomerTrends()
    expect(service.get).toHaveBeenCalledWith('/statistics/customer-trends', { params: { period: '30d' } })
  })

  it('should call getSalesByCategory with correct endpoint', async () => {
    await chartApi.getSalesByCategory()
    expect(service.get).toHaveBeenCalledWith('/statistics/sales-by-category')
  })

  it('should call getMonthlySales with correct endpoint and year', async () => {
    const year = 2023
    await chartApi.getMonthlySales(year)
    expect(service.get).toHaveBeenCalledWith('/statistics/monthly-sales', { params: { year } })
  })

  it('should call getDashboardStats with correct endpoint', async () => {
    await chartApi.getDashboardStats()
    expect(service.get).toHaveBeenCalledWith('/statistics/dashboard')
  })

  it('should handle errors in API calls', async () => {
    const errorMessage = 'Network Error'
    service.get.mockRejectedValueOnce(new Error(errorMessage))
    
    const consoleSpy = vi.spyOn(console, 'error')
    
    await expect(chartApi.getDashboardStats()).rejects.toThrow(errorMessage)
    expect(consoleSpy).toHaveBeenCalled()
    expect(consoleSpy.mock.calls[0][0]).toContain('Failed to fetch dashboard stats')
  })
})