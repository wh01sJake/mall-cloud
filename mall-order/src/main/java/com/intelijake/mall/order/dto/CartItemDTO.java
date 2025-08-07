package com.intelijake.mall.order.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * Cart Item Data Transfer Object
 * Used for transferring cart item data with product information between services
 *
 * @author Jake
 * @since 2025-07-27
 */
@Data
public class CartItemDTO {

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

    /**
     * Calculate subtotal for this cart item
     */
    public BigDecimal getSubtotal() {
        if (productPrice != null && quantity != null) {
            return productPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
}
