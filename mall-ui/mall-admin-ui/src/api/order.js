import service from '@/utils/request.js'

// Order management API endpoints for admin
const orderApi = {
    // Get paginated order list with search filters
    list(orderQuery) {
        return service.get('/order/list', {params: orderQuery})
    },
    
    // Get order details by order number
    getByOrderNo(orderNo) {
        return service.get(`/order/detail/${orderNo}`)
    },
    
    // Update order status
    updateStatus(orderNo, status) {
        return service.put('/order/updateStatus', {
            orderNo: orderNo,
            status: status
        })
    },
    
    // Get order items for specific order
    getOrderItems(orderNo) {
        return service.get(`/order/items/${orderNo}`)
    },
    
    // Get order shipping address
    getOrderAddress(orderNo) {
        return service.get(`/order/address/${orderNo}`)
    },
    
    // Get order statistics for dashboard
    getStatistics() {
        return service.get('/order/statistics')
    },
    
    // Get orders by status
    getByStatus(status) {
        return service.get(`/order/status/${status}`)
    },
    
    // Search orders by customer info
    searchByCustomer(customerQuery) {
        return service.get('/order/search', {params: customerQuery})
    },
    
    // Export orders to CSV/Excel
    exportOrders(exportQuery) {
        return service.get('/order/export', {
            params: exportQuery,
            responseType: 'blob'
        })
    },
    
    // Get order count by date range
    getOrderCountByDateRange(startDate, endDate) {
        return service.get('/order/count', {
            params: {
                startDate: startDate,
                endDate: endDate
            }
        })
    },
    
    // Get revenue statistics
    getRevenueStats(period) {
        return service.get('/order/revenue', {
            params: { period: period }
        })
    }
}

export default orderApi
