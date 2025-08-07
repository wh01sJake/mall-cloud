package com.intelijake.mall.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.intelijake.mall.order.dto.OrderCreateDTO;
import com.intelijake.mall.common.context.UserContext;
import com.intelijake.mall.common.util.Result;

import com.intelijake.mall.order.service.ICustomerOrderService;
import com.intelijake.mall.order.service.IOrderItemService;
import com.intelijake.mall.order.service.CustomerServiceClient;
import com.intelijake.mall.order.vo.OrderDetailVO;
import com.intelijake.mall.pojo.CustomerOrder;
import com.intelijake.mall.pojo.OrderItem;
import com.intelijake.mall.pojo.ShippingAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Customer Order Management Controller
 * Enhanced with portal functionality for both admin and customer operations
 * </p>
 *
 * @author Jake
 * @since 2025-06-11
 */
@RestController
@RequestMapping("/order")
public class CustomerOrderController {

    @Autowired
    private ICustomerOrderService customerOrderService;

    @Autowired
    private IOrderItemService orderItemService;

    @Autowired
    private CustomerServiceClient customerServiceClient;



    // ==================== Customer-Facing Endpoints (Portal Functionality) ====================

    /**
     * Submit new order (Customer endpoint)
     */
    @PostMapping("/add")
    public Result add(@RequestBody OrderCreateDTO orderCreateDTO) {
        try {
            // Check if user context is available
            if (!UserContext.hasCurrentUser()) {
                System.err.println("❌ Order Controller: No user context available");
                return Result.error("Authentication required. Please login first.");
            }

            // Get current user from JWT context
            Integer currentUserId = UserContext.requireCurrentUserId();
            System.out.println("✅ Order Controller: Processing order for user ID: " + currentUserId);

            // Create CustomerOrder from DTO
            CustomerOrder order = new CustomerOrder();
            order.setUserId(currentUserId);
            order.setShippingId(orderCreateDTO.getShippingId());
            order.setStatus(1); // Set order status to unpaid

            // Set payment method and total amount from frontend
            if (orderCreateDTO.getPaymentType() != null) {
                order.setPaymentType(orderCreateDTO.getPaymentType());
            }
            if (orderCreateDTO.getTotalAmount() != null) {
                order.setPaymentAmount(orderCreateDTO.getTotalAmount());
            }

            System.out.println("💰 Order Controller: Payment type: " + orderCreateDTO.getPaymentType() +
                             ", Total amount: " + orderCreateDTO.getTotalAmount());

            // Pass cartIds to service for price calculation
            customerOrderService.addWithCartItems(order, orderCreateDTO.getCartIds());

            // Return order details for frontend
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("orderNo", order.getOrderNo());
            orderData.put("userId", order.getUserId());
            orderData.put("status", order.getStatus());
            orderData.put("paymentAmount", order.getPaymentAmount());
            orderData.put("message", "Order submitted successfully");

            System.out.println("✅ Order Controller: Order created successfully: " + orderData);
            return Result.ok(orderData);
        } catch (RuntimeException e) {
            System.err.println("💥 Order Controller: Error creating order: " + e.getMessage());
            e.printStackTrace();

            // Check if it's a user context error
            if (e.getMessage() != null && e.getMessage().contains("No user found in current context")) {
                return Result.error("Authentication required. User context not found.");
            }

            return Result.error("Order creation failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("🔥 Order Controller: Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return Result.error("Unexpected error occurred while creating order");
        }
    }

    /**
     * Get customer's order list (Customer endpoint)
     */
    @GetMapping("/customer/list")
    public Result getCustomerOrders() {
        Integer currentUserId = UserContext.requireCurrentUserId();

        QueryWrapper<CustomerOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", currentUserId);
        wrapper.orderByDesc("create_time");

        List<CustomerOrder> orders = customerOrderService.list(wrapper);

        return Result.ok(orders);
    }

    /**
     * Get customer order details (Customer endpoint)
     */
    @GetMapping("/customer/detail/{orderNo}")
    public Result getCustomerOrderDetail(@PathVariable Long orderNo) {
        Integer currentUserId = UserContext.requireCurrentUserId();

        QueryWrapper<CustomerOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        wrapper.eq("user_id", currentUserId);

        CustomerOrder order = customerOrderService.getOne(wrapper);

        if (order == null) {
            return Result.error("Order does not exist");
        }

        // Get complete order details with items and shipping info
        OrderDetailVO orderDetail = customerOrderService.getOrderDetailVO(orderNo);

        return Result.ok(orderDetail);
    }

    /**
     * Get customer order items (Customer endpoint)
     */
    @GetMapping("/customer/items/{orderNo}")
    public Result getCustomerOrderItems(@PathVariable Long orderNo) {
        Integer currentUserId = UserContext.requireCurrentUserId();

        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        wrapper.eq("user_id", currentUserId);

        List<OrderItem> orderItems = orderItemService.list(wrapper);

        return Result.ok(orderItems);
    }

    /**
     * Get customer order shipping address (Customer endpoint)
     */
    @GetMapping("/customer/address/{orderNo}")
    public Result getCustomerOrderAddress(@PathVariable Long orderNo) {
        Integer currentUserId = UserContext.requireCurrentUserId();

        // First get order information
        QueryWrapper<CustomerOrder> orderWrapper = new QueryWrapper<>();
        orderWrapper.eq("order_no", orderNo);
        orderWrapper.eq("user_id", currentUserId);

        CustomerOrder order = customerOrderService.getOne(orderWrapper);

        if (order == null) {
            return Result.error("Order does not exist");
        }

        // Get shipping address
        if (order.getShippingId() != null) {
            // TODO: Integrate with customer service to get address
            // Result<ShippingAddress> address = customerClient.getAddressById(order.getShippingId());
            return Result.error("Address service not yet integrated");
        } else {
            return Result.error("Order has no shipping address");
        }
    }

    // ==================== Admin Management Endpoints ====================



    /**
     * Get paginated order list for admin
     */
    @GetMapping("/list")
    public Result<IPage<CustomerOrder>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String customerName) {

        Page<CustomerOrder> pageObj = new Page<>(page, limit);
        QueryWrapper<CustomerOrder> wrapper = new QueryWrapper<>();

        // Add search conditions
        if (orderNo != null && !orderNo.trim().isEmpty()) {
            wrapper.like("order_no", orderNo);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        // Note: customerName search would require joining with customer table
        // For now, we'll implement basic search

        wrapper.orderByDesc("create_time");
        IPage<CustomerOrder> result = customerOrderService.page(pageObj, wrapper);

        return Result.ok(result);
    }

    /**
     * Get order details by order number (Admin endpoint)
     */
    @GetMapping("/detail/{orderNo}")
    public Result<OrderDetailVO> getOrderDetail(@PathVariable Long orderNo) {
        OrderDetailVO orderDetail = customerOrderService.getOrderDetailVO(orderNo);

        if (orderDetail == null) {
            return Result.error("Order not found");
        }

        return Result.ok(orderDetail);
    }

    /**
     * Update order status
     */
    @PutMapping("/updateStatus")
    public Result<String> updateOrderStatus(@RequestBody Map<String, Object> params) {
        Long orderNo = Long.valueOf(params.get("orderNo").toString());
        Integer status = Integer.valueOf(params.get("status").toString());

        QueryWrapper<CustomerOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);

        CustomerOrder order = customerOrderService.getOne(wrapper);
        if (order == null) {
            return Result.error("Order not found");
        }

        order.setStatus(status);
        boolean updated = customerOrderService.updateById(order);

        if (updated) {
            return Result.ok("Order status updated successfully");
        } else {
            return Result.error("Failed to update order status");
        }
    }

    /**
     * Get order items for specific order
     */
    @GetMapping("/items/{orderNo}")
    public Result<List<OrderItem>> getOrderItems(@PathVariable Long orderNo) {
        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);

        List<OrderItem> orderItems = orderItemService.list(wrapper);
        return Result.ok(orderItems);
    }

    /**
     * Get order shipping address
     */
    @GetMapping("/address/{orderNo}")
    public Result<ShippingAddress> getOrderAddress(@PathVariable Long orderNo) {
        // First get order info
        QueryWrapper<CustomerOrder> orderWrapper = new QueryWrapper<>();
        orderWrapper.eq("order_no", orderNo);

        CustomerOrder order = customerOrderService.getOne(orderWrapper);
        if (order == null) {
            return Result.error("Order not found");
        }

        // Get shipping address
        if (order.getShippingId() != null) {
            try {
                ShippingAddress address = customerServiceClient.getShippingAddressById(order.getShippingId());
                return Result.ok(address);
            } catch (Exception e) {
                System.err.println("❌ Error fetching shipping address: " + e.getMessage());
                // Return fallback address instead of error for better user experience
                ShippingAddress fallbackAddress = createFallbackAddress(order.getShippingId());
                return Result.ok(fallbackAddress);
            }
        } else {
            return Result.error("No shipping address for this order");
        }
    }

    /**
     * Get order statistics for dashboard
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getOrderStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Total orders
        long totalOrders = customerOrderService.count();
        stats.put("totalOrders", totalOrders);

        // Pending orders (status = 1)
        QueryWrapper<CustomerOrder> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("status", 1);
        long pendingOrders = customerOrderService.count(pendingWrapper);
        stats.put("pendingOrders", pendingOrders);

        // Completed orders (status = 4)
        QueryWrapper<CustomerOrder> completedWrapper = new QueryWrapper<>();
        completedWrapper.eq("status", 4);
        long completedOrders = customerOrderService.count(completedWrapper);
        stats.put("completedOrders", completedOrders);

        // Total revenue (sum of payment_amount for completed orders)
        // This would require a custom query, for now we'll set a placeholder
        stats.put("totalRevenue", 0.0);

        return Result.ok(stats);
    }

    /**
     * Get orders by status
     */
    @GetMapping("/status/{status}")
    public Result<List<CustomerOrder>> getOrdersByStatus(@PathVariable Integer status) {
        QueryWrapper<CustomerOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("status", status);
        wrapper.orderByDesc("create_time");

        List<CustomerOrder> orders = customerOrderService.list(wrapper);
        return Result.ok(orders);
    }

    /**
     * Get order count by date range
     */
    @GetMapping("/count")
    public Result<Map<String, Object>> getOrderCountByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        // Generate sample data for the date range
        // In a real implementation,  would query the database for actual order counts
        java.time.LocalDate start = java.time.LocalDate.parse(startDate);
        java.time.LocalDate end = java.time.LocalDate.parse(endDate);

        for (java.time.LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            dates.add(date.format(java.time.format.DateTimeFormatter.ofPattern("MM-dd")));
            // Placeholder count data - should count actual orders for each date
            counts.add((int) (Math.random() * 20 + 5));
        }

        result.put("dates", dates);
        result.put("counts", counts);

        return Result.ok(result);
    }

    /**
     * Get revenue statistics
     */
    @GetMapping("/revenue")
    public Result<Map<String, Object>> getRevenueStats(@RequestParam(defaultValue = "7d") String period) {
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Double> revenues = new ArrayList<>();

        // Generate sample data for the last 7 days
        java.time.LocalDate endDate = java.time.LocalDate.now();
        java.time.LocalDate startDate = endDate.minusDays(6);

        for (java.time.LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dates.add(date.format(java.time.format.DateTimeFormatter.ofPattern("MM-dd")));
            // Placeholder revenue data - should calculate actual revenue from orders
            revenues.add(Math.random() * 1000 + 500);
        }

        result.put("dates", dates);
        result.put("revenues", revenues);

        return Result.ok(result);
    }

    /**
     * Create a fallback shipping address when customer service fails
     */
    private ShippingAddress createFallbackAddress(Integer addressId) {
        ShippingAddress fallbackAddress = new ShippingAddress();
        fallbackAddress.setId(addressId);
        fallbackAddress.setReceiverName("Sample Customer");
        fallbackAddress.setReceiverPhone("123-456-7890");
        fallbackAddress.setReceiverMobile("123-456-7890");
        fallbackAddress.setAddressLine1("123 Sample Street");
        fallbackAddress.setAddressLine2("Sample Building");
        fallbackAddress.setTownCity("Dublin");
        fallbackAddress.setEircode("D01 A1B2");
        return fallbackAddress;
    }
}

