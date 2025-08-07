package com.intelijake.mall.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.intelijake.mall.common.util.Result;
import com.intelijake.mall.pojo.CustomerOrder;
import com.intelijake.mall.pojo.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Order Service Fallback Implementation
 * Provides circuit breaker fallback responses when order service is unavailable
 * 
 * @author Jake
 * @since 2025-07-22
 */
@Component
public class OrderClientFallback implements OrderClient {

    // ==================== Customer Order Operations ====================
    
    @Override
    public Result<String> submitOrder(CustomerOrder order) {
        return Result.error("Unable to submit order. Order service is temporarily unavailable.");
    }
    
    @Override
    public Result<List<CustomerOrder>> getCustomerOrders() {
        return Result.error("Unable to retrieve your orders. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<CustomerOrder> getCustomerOrderDetail(Long orderNo) {
        return Result.error("Unable to retrieve order details. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> cancelCustomerOrder(Long orderNo) {
        return Result.error("Unable to cancel order. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<Map<String, Object>> getCustomerOrderAddress(Long orderNo) {
        return Result.error("Unable to retrieve order address. Service is temporarily unavailable.");
    }

    // ==================== Admin Order Management ====================
    
    @Override
    public Result<IPage<CustomerOrder>> getOrderList(Integer page, Integer limit, String orderNo, Integer status, String customerName) {
        return Result.error("Order management service is temporarily unavailable. Please try again later.");
    }
    
    @Override
    public Result<CustomerOrder> getOrderDetail(Long orderNo) {
        return Result.error("Unable to retrieve order details. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> updateOrderStatus(Map<String, Object> params) {
        return Result.error("Unable to update order status. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> deleteOrder(Long orderNo) {
        return Result.error("Unable to delete order. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> deleteOrders(Long[] orderNos) {
        return Result.error("Unable to delete orders. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<Map<String, Object>> getOrderStatistics() {
        return Result.error("Unable to retrieve order statistics. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<List<CustomerOrder>> getOrdersByStatus(Integer status) {
        return Result.error("Unable to retrieve orders by status. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<Map<String, Object>> getOrderCountByDateRange(String startDate, String endDate) {
        return Result.error("Unable to retrieve order count. Service is temporarily unavailable.");
    }

    // ==================== Order Item Operations ====================
    
    @Override
    public Result<List<OrderItem>> getCustomerOrderItems(Long orderNo) {
        return Result.error("Unable to retrieve order items. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<IPage<OrderItem>> getCustomerAllOrderItems(Integer page, Integer limit) {
        return Result.error("Unable to retrieve your order items. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<OrderItem> getCustomerOrderItemDetail(Integer id) {
        return Result.error("Unable to retrieve order item details. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<IPage<OrderItem>> getOrderItemList(Integer page, Integer limit, Long orderNo, Integer productId, Integer userId) {
        return Result.error("Order item management service is temporarily unavailable. Please try again later.");
    }
    
    @Override
    public Result<OrderItem> getOrderItemDetail(Integer id) {
        return Result.error("Unable to retrieve order item details. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<List<OrderItem>> getOrderItemsByOrderNo(Long orderNo) {
        return Result.error("Unable to retrieve order items. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<Map<String, Object>> getOrderItemsStatistics() {
        return Result.error("Unable to retrieve order item statistics. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<IPage<OrderItem>> getOrderItemsByProduct(Integer productId, Integer page, Integer limit) {
        return Result.error("Unable to retrieve order items by product. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<IPage<OrderItem>> getOrderItemsByCustomer(Integer userId, Integer page, Integer limit) {
        return Result.error("Unable to retrieve order items by customer. Service is temporarily unavailable.");
    }
}
