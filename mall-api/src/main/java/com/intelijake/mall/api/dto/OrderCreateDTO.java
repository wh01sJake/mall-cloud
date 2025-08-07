package com.intelijake.mall.api.dto;

import lombok.Data;

/**
 * Order Creation Data Transfer Object
 * Used for creating new orders with cart items
 * 
 * @author Jake
 * @since 2025-07-27
 */
@Data
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
}
