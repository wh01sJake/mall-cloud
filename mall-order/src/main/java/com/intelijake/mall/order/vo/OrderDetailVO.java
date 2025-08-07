package com.intelijake.mall.order.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.intelijake.mall.pojo.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Order Detail View Object
 * Contains complete order information including items and shipping details
 * 
 * @author Jake
 * @since 2025-08-05
 */
@Data
public class OrderDetailVO {

    /**
     * Order Number - Primary Key
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderNo;

    /**
     * User ID
     */
    private Integer userId;

    /**
     * Shipping Address ID
     */
    private Integer shippingId;

    /**
     * Actual payment amount in EUR, with two decimal places
     */
    private BigDecimal paymentAmount;

    /**
     * Payment type: 1-Stripe, 2-PayPal, 3-Credit Card, 4-Cash on Delivery
     */
    private Integer paymentType;

    /**
     * Postage fee in EUR
     */
    private Integer postageFee;

    /**
     * Payment time
     */
    private Date paymentTime;

    /**
     * Shipping time
     */
    private Date shippingTime;

    /**
     * Order completion time
     */
    private Date endTime;

    /**
     * Order close time
     */
    private Date closeTime;

    /**
     * Order status: 1-Pending Payment, 2-Paid, 3-Shipped, 4-Completed, 5-Cancelled
     */
    private Integer status;

    /**
     * Order status description
     */
    private String statusDesc;

    /**
     * Creation time
     */
    private Date createTime;

    /**
     * Last update time
     */
    private Date updateTime;

    /**
     * Order items (products in this order)
     */
    private List<OrderItem> orderItems;

    /**
     * Shipping address information
     */
    private ShippingAddressVO shippingAddress;

    /**
     * Customer information
     */
    private CustomerInfoVO customerInfo;

    /**
     * Get status description based on status code
     */
    public String getStatusDesc() {
        if (status == null) return "Unknown";
        
        switch (status) {
            case 1: return "Pending Payment";
            case 2: return "Paid";
            case 3: return "Shipped";
            case 4: return "Completed";
            case 5: return "Cancelled";
            default: return "Unknown";
        }
    }

    /**
     * Calculate total quantity of items in order
     */
    public Integer getTotalQuantity() {
        if (orderItems == null || orderItems.isEmpty()) {
            return 0;
        }
        return orderItems.stream()
                .mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0)
                .sum();
    }

    /**
     * Calculate total items count (number of different products)
     */
    public Integer getTotalItemsCount() {
        return orderItems != null ? orderItems.size() : 0;
    }

    /**
     * Shipping Address VO
     */
    @Data
    public static class ShippingAddressVO {
        private Integer id;
        private String receiverName;
        private String receiverPhone;
        private String receiverMobile;
        private String receiverProvince;
        private String receiverCity;
        private String receiverDistrict;
        private String receiverAddress;
        private String receiverZip;
    }

    /**
     * Customer Info VO
     */
    @Data
    public static class CustomerInfoVO {
        private Integer id;
        private String username;
        private String email;
        private String phone;
    }
}
