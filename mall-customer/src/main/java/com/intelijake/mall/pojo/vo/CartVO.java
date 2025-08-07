package com.intelijake.mall.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Shopping Cart Value Object
 * Used for returning cart data with product information
 * 
 * @author Jake
 * @since 2025-07-22
 */
@Data
public class CartVO {

    /**
     * Shopping cart item ID
     */
    private Integer id;

    /**
     * Customer ID
     */
    private Integer customerId;

    /**
     * Product ID
     */
    private Integer productId;

    /**
     * Product name
     */
    private String productName;

    /**
     * Product price
     */
    private BigDecimal productPrice;

    /**
     * Product main image URL
     */
    private String productMainImage;

    /**
     * Quantity in cart
     */
    private Integer quantity;

    /**
     * Is checked for checkout (1: checked, 0: unchecked)
     */
    private Integer checked;
}
