import service from '@/utils/request.js'

// Chart and statistics API endpoints
const chartApi = {
    // Get category count for pie chart
    selectClassCount() {
        return service.get('/statistics/category-count')
            .catch(error => {
                console.error('Failed to fetch category count:', error);
                throw error;
            });
    },
    
    // Get order statistics for charts
    getOrderStats() {
        return service.get('/statistics/dashboard')
            .catch(error => {
                console.error('Failed to fetch order stats:', error);
                throw error;
            });
    },
    
    // Get revenue trends
    getRevenueTrends(period = '7d') {
        return service.get('/statistics/revenue', {
            params: { period: period }
        }).catch(error => {
            console.error(`Failed to fetch revenue trends for period ${period}:`, error);
            throw error;
        });
    },
    
    // Get order status distribution
    getOrderStatusDistribution() {
        return service.get('/statistics/order-status')
            .catch(error => {
                console.error('Failed to fetch order status distribution:', error);
                throw error;
            });
    },
    
    // Get payment method distribution
    getPaymentMethodStats() {
        return service.get('/statistics/payment-methods')
            .catch(error => {
                console.error('Failed to fetch payment method stats:', error);
                throw error;
            });
    },
    
    // Get top selling products
    getTopProducts(limit = 10) {
        return service.get('/statistics/top-products', {
            params: { limit: limit }
        }).catch(error => {
            console.error(`Failed to fetch top ${limit} products:`, error);
            throw error;
        });
    },
    
    // Get customer registration trends
    getCustomerTrends(period = '30d') {
        return service.get('/statistics/customer-trends', {
            params: { period: period }
        }).catch(error => {
            console.error(`Failed to fetch customer trends for period ${period}:`, error);
            throw error;
        });
    },
    
    // Get sales by category
    getSalesByCategory() {
        return service.get('/statistics/sales-by-category')
            .catch(error => {
                console.error('Failed to fetch sales by category:', error);
                throw error;
            });
    },
    
    // Get monthly sales comparison
    getMonthlySales(year) {
        return service.get('/statistics/monthly-sales', {
            params: { year: year }
        }).catch(error => {
            console.error(`Failed to fetch monthly sales for year ${year}:`, error);
            throw error;
        });
    },
    
    // Get dashboard summary stats
    getDashboardStats() {
        return service.get('/statistics/dashboard')
            .catch(error => {
                console.error('Failed to fetch dashboard stats:', error);
                throw error;
            });
    }
}

export default chartApi
