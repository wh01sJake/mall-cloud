package com.intelijake.mall.order.dto;


/**
 * Order Creation Data Transfer Object
 * Used for creating new orders with cart items
 * 
 * @author Jake
 * @since 2025-07-27
 */
public class OrderCreateDTO {

    /**
     * Comma-separated cart item IDs
     */
    private String cartIds;

    /**
     * Shipping address ID
     */
    private Integer shippingId;

    /**
     * Order remark/note
     */
    private String remark;

    /**
     * Payment type: 1-Stripe, 2-PayPal, 3-Credit Card, 4-Cash on Delivery
     */
    private Integer paymentType;

    /**
     * Total amount (including shipping, discounts, etc.)
     */
    private java.math.BigDecimal totalAmount;

    // Getters and Setters
    public String getCartIds() {
        return cartIds;
    }

    public void setCartIds(String cartIds) {
        this.cartIds = cartIds;
    }

    public Integer getShippingId() {
        return shippingId;
    }

    public void setShippingId(Integer shippingId) {
        this.shippingId = shippingId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public java.math.BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(java.math.BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
