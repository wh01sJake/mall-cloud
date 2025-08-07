package com.intelijake.mall.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelijake.mall.common.context.UserContext;
import com.intelijake.mall.common.util.Result;
import com.intelijake.mall.order.service.IOrderItemService;
import com.intelijake.mall.pojo.Customer;
import com.intelijake.mall.pojo.OrderItem;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Order Item Management Controller
 * Enhanced with portal functionality for both admin and customer operations
 * </p>
 *
 * @author Jake
 * @since 2025-07-22
 */
@RestController
@RequestMapping("/order/item")
public class OrderItemController {

    @Autowired
    private IOrderItemService orderItemService;

    // ==================== Customer-Facing Endpoints (Portal Functionality) ====================

    /**
     * Get customer's order items for a specific order (Customer endpoint)
     */
    @GetMapping("/customer/order/{orderNo}")
    public Result getCustomerOrderItems(@PathVariable Long orderNo) {
        Integer currentUserId = UserContext.requireCurrentUserId();

        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        wrapper.eq("user_id", currentUserId);
        wrapper.orderByDesc("create_time");

        List<OrderItem> orderItems = orderItemService.list(wrapper);

        return Result.ok(orderItems);
    }

    /**
     * Get customer's all order items (Customer endpoint)
     */
    @GetMapping("/customer/list")
    public Result getCustomerAllOrderItems(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer limit) {
        Integer currentUserId = UserContext.requireCurrentUserId();

        Page<OrderItem> pageObj = new Page<>(page, limit);
        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", currentUserId);
        wrapper.orderByDesc("create_time");

        IPage<OrderItem> result = orderItemService.page(pageObj, wrapper);

        return Result.ok(result);
    }

    /**
     * Get customer order item details (Customer endpoint)
     */
    @GetMapping("/customer/detail/{id}")
    public Result getCustomerOrderItemDetail(@PathVariable Integer id) {
        Integer currentUserId = UserContext.requireCurrentUserId();

        OrderItem orderItem = orderItemService.getById(id);

        if (orderItem == null) {
            return Result.error("Order item not found");
        }

        // Verify ownership
        if (!orderItem.getUserId().equals(currentUserId)) {
            return Result.error("Access denied");
        }

        return Result.ok(orderItem);
    }

    // ==================== Admin Management Endpoints ====================

    /**
     * Get paginated order items list for admin
     */
    @GetMapping("/list")
    public Result getOrderItemsList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Long orderNo,
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false) Integer userId) {

        Page<OrderItem> pageObj = new Page<>(page, limit);
        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();

        // Add search conditions
        if (orderNo != null) {
            wrapper.eq("order_no", orderNo);
        }
        if (productId != null) {
            wrapper.eq("product_id", productId);
        }
        if (userId != null) {
            wrapper.eq("user_id", userId);
        }

        wrapper.orderByDesc("create_time");
        IPage<OrderItem> result = orderItemService.page(pageObj, wrapper);

        return Result.ok(result);
    }

    /**
     * Get order item details by ID (Admin endpoint)
     */
    @GetMapping("/detail/{id}")
    public Result getOrderItemDetail(@PathVariable Integer id) {
        OrderItem orderItem = orderItemService.getById(id);
        
        if (orderItem == null) {
            return Result.error("Order item not found");
        }

        return Result.ok(orderItem);
    }

    /**
     * Get all order items for a specific order (Admin endpoint)
     */
    @GetMapping("/order/{orderNo}")
    public Result getOrderItemsByOrderNo(@PathVariable Long orderNo) {
        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        wrapper.orderByDesc("create_time");

        List<OrderItem> orderItems = orderItemService.list(wrapper);

        return Result.ok(orderItems);
    }

    /**
     * Update order item (Admin endpoint)
     */
    @PutMapping("/update")
    public Result updateOrderItem(@RequestBody OrderItem orderItem) {
        if (orderItem.getId() == null) {
            return Result.error("Order item ID is required");
        }

        OrderItem existingItem = orderItemService.getById(orderItem.getId());
        if (existingItem == null) {
            return Result.error("Order item not found");
        }

        boolean updated = orderItemService.updateById(orderItem);

        if (updated) {
            return Result.ok("Order item updated successfully");
        } else {
            return Result.error("Failed to update order item");
        }
    }

    /**
     * Delete order item (Admin endpoint)
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteOrderItem(@PathVariable Integer id) {
        OrderItem orderItem = orderItemService.getById(id);
        
        if (orderItem == null) {
            return Result.error("Order item not found");
        }

        boolean deleted = orderItemService.removeById(id);

        if (deleted) {
            return Result.ok("Order item deleted successfully");
        } else {
            return Result.error("Failed to delete order item");
        }
    }

    /**
     * Get order items statistics (Admin endpoint)
     */
    @GetMapping("/statistics")
    public Result getOrderItemsStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Total order items
        long totalItems = orderItemService.count();
        stats.put("totalItems", totalItems);

        // Total revenue from order items
        // This would require a custom query to sum total_price
        // For now, we'll set a placeholder
        stats.put("totalRevenue", 0.0);

        // Average order value
        // This would require a custom query
        stats.put("averageOrderValue", 0.0);

        return Result.ok(stats);
    }

    /**
     * Get order items by product (Admin endpoint)
     */
    @GetMapping("/product/{productId}")
    public Result getOrderItemsByProduct(@PathVariable Integer productId,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer limit) {
        Page<OrderItem> pageObj = new Page<>(page, limit);
        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();
        wrapper.eq("product_id", productId);
        wrapper.orderByDesc("create_time");

        IPage<OrderItem> result = orderItemService.page(pageObj, wrapper);

        return Result.ok(result);
    }

    /**
     * Get order items by customer (Admin endpoint)
     */
    @GetMapping("/customer/{userId}")
    public Result getOrderItemsByCustomer(@PathVariable Integer userId,
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer limit) {
        Page<OrderItem> pageObj = new Page<>(page, limit);
        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");

        IPage<OrderItem> result = orderItemService.page(pageObj, wrapper);

        return Result.ok(result);
    }
}
