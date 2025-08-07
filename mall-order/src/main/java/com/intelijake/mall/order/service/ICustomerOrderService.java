package com.intelijake.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.intelijake.mall.order.vo.OrderDetailVO;
import com.intelijake.mall.pojo.CustomerOrder;

/**
 * <p>
 * Customer Order Service Interface
 * Enhanced with portal functionality for order creation and management
 * </p>
 *
 * @author Jake
 * @since 2025-06-11
 */
public interface ICustomerOrderService extends IService<CustomerOrder> {

    /**
     * Create new order from shopping cart
     * This method handles the complete order creation workflow:
     * - Generates order number
     * - Creates order items from checked cart items
     * - Calculates total payment amount
     * - Clears shopping cart
     * - Sends delayed message for order processing
     *
     * @param order Customer order to create
     */
    void add(CustomerOrder order);

    /**
     * Create new order with cart items
     * This method calculates the total price from cart items and creates the order
     *
     * @param order Customer order to create
     * @param cartIds Comma-separated cart item IDs
     */
    void addWithCartItems(CustomerOrder order, String cartIds);

    /**
     * Cancel an existing order
     * Updates order status to cancelled
     *
     * @param orderNo Order number to cancel
     */
    void cancel(Long orderNo);

    /**
     * Get complete order details including items and shipping information
     *
     * @param orderNo Order number
     * @return OrderDetailVO with complete order information
     */
    OrderDetailVO getOrderDetailVO(Long orderNo);
}
