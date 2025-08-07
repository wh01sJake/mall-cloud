package com.intelijake.mall.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.intelijake.mall.api.query.OrderItemQuery;
import com.intelijake.mall.api.query.OrderQuery;
import com.intelijake.mall.common.util.Result;
import com.intelijake.mall.pojo.CustomerOrder;
import com.intelijake.mall.pojo.OrderItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Order Service Feign Client
 * Provides order management and order item operations
 * 
 * @author Jake
 * @since 2025-07-22
 */
@FeignClient(value = "order-service", fallback = OrderClientFallback.class)
public interface OrderClient {

    // ==================== Customer Order Operations ====================
    
    /**
     * Submit new order (Customer endpoint)
     */
    @PostMapping("/order/add")
    Result<String> submitOrder(@RequestBody CustomerOrder order);
    
    /**
     * Get customer's order list (Customer endpoint)
     */
    @GetMapping("/order/customer/list")
    Result<List<CustomerOrder>> getCustomerOrders();
    
    /**
     * Get customer order details (Customer endpoint)
     */
    @GetMapping("/order/customer/detail/{orderNo}")
    Result<CustomerOrder> getCustomerOrderDetail(@PathVariable("orderNo") Long orderNo);
    
    /**
     * Cancel customer order (Customer endpoint)
     */
    @PutMapping("/order/customer/cancel/{orderNo}")
    Result<String> cancelCustomerOrder(@PathVariable("orderNo") Long orderNo);
    
    /**
     * Get customer order shipping address (Customer endpoint)
     */
    @GetMapping("/order/customer/address/{orderNo}")
    Result<Map<String, Object>> getCustomerOrderAddress(@PathVariable("orderNo") Long orderNo);

    // ==================== Admin Order Management ====================
    
    /**
     * Get paginated order list for admin
     */
    @GetMapping("/order/list")
    Result<IPage<CustomerOrder>> getOrderList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                             @RequestParam(value = "orderNo", required = false) String orderNo,
                                             @RequestParam(value = "status", required = false) Integer status,
                                             @RequestParam(value = "customerName", required = false) String customerName);
    
    /**
     * Get order details by order number (Admin endpoint)
     */
    @GetMapping("/order/detail/{orderNo}")
    Result<CustomerOrder> getOrderDetail(@PathVariable("orderNo") Long orderNo);
    
    /**
     * Update order status (Admin endpoint)
     */
    @PutMapping("/order/updateStatus")
    Result<String> updateOrderStatus(@RequestBody Map<String, Object> params);
    
    /**
     * Delete order by order number (Admin endpoint)
     */
    @DeleteMapping("/order/delete/{orderNo}")
    Result<String> deleteOrder(@PathVariable("orderNo") Long orderNo);
    
    /**
     * Delete multiple orders (Admin endpoint)
     */
    @DeleteMapping("/order/deleteAll")
    Result<String> deleteOrders(@RequestBody Long[] orderNos);
    
    /**
     * Get order statistics for dashboard (Admin endpoint)
     */
    @GetMapping("/order/statistics")
    Result<Map<String, Object>> getOrderStatistics();
    
    /**
     * Get orders by status (Admin endpoint)
     */
    @GetMapping("/order/status/{status}")
    Result<List<CustomerOrder>> getOrdersByStatus(@PathVariable("status") Integer status);
    
    /**
     * Get order count by date range (Admin endpoint)
     */
    @GetMapping("/order/count")
    Result<Map<String, Object>> getOrderCountByDateRange(@RequestParam("startDate") String startDate,
                                                         @RequestParam("endDate") String endDate);

    // ==================== Order Item Operations ====================
    
    /**
     * Get customer's order items for a specific order (Customer endpoint)
     */
    @GetMapping("/order/item/customer/order/{orderNo}")
    Result<List<OrderItem>> getCustomerOrderItems(@PathVariable("orderNo") Long orderNo);

    /**
     * Get customer's all order items (Customer endpoint)
     */
    @GetMapping("/order/item/customer/list")
    Result<IPage<OrderItem>> getCustomerAllOrderItems(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                      @RequestParam(value = "limit", defaultValue = "10") Integer limit);

    /**
     * Get customer order item details (Customer endpoint)
     */
    @GetMapping("/order/item/customer/detail/{id}")
    Result<OrderItem> getCustomerOrderItemDetail(@PathVariable("id") Integer id);

    /**
     * Get paginated order item list for admin
     */
    @GetMapping("/order/item/list")
    Result<IPage<OrderItem>> getOrderItemList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                             @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                             @RequestParam(value = "orderNo", required = false) Long orderNo,
                                             @RequestParam(value = "productId", required = false) Integer productId,
                                             @RequestParam(value = "userId", required = false) Integer userId);

    /**
     * Get order item details by ID (Admin endpoint)
     */
    @GetMapping("/order/item/detail/{id}")
    Result<OrderItem> getOrderItemDetail(@PathVariable("id") Integer id);

    /**
     * Get all order items for a specific order (Admin endpoint)
     */
    @GetMapping("/order/item/order/{orderNo}")
    Result<List<OrderItem>> getOrderItemsByOrderNo(@PathVariable("orderNo") Long orderNo);

    /**
     * Get order items statistics (Admin endpoint)
     */
    @GetMapping("/order/item/statistics")
    Result<Map<String, Object>> getOrderItemsStatistics();

    /**
     * Get order items by product (Admin endpoint)
     */
    @GetMapping("/order/item/product/{productId}")
    Result<IPage<OrderItem>> getOrderItemsByProduct(@PathVariable("productId") Integer productId,
                                                    @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                    @RequestParam(value = "limit", defaultValue = "10") Integer limit);

    /**
     * Get order items by customer (Admin endpoint)
     */
    @GetMapping("/order/item/customer/{userId}")
    Result<IPage<OrderItem>> getOrderItemsByCustomer(@PathVariable("userId") Integer userId,
                                                     @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                     @RequestParam(value = "limit", defaultValue = "10") Integer limit);
}
