package com.intelijake.mall.pojo.query;

import lombok.Data;

/**
 * Shopping Cart Query Parameters
 * 
 * @author Jake
 * @since 2025-07-21
 */
@Data
public class CartQuery {
    
    /**
     * Page number for pagination
     */
    private Integer page;
    
    /**
     * Page size limit for pagination
     */
    private Integer limit;
    
    /**
     * Customer ID to filter cart items
     */
    private Integer customerId;
    
    /**
     * Filter by checked status (1: checked, 0: unchecked)
     */
    private Integer isChecked;
    
    /**
     * Product ID to filter specific product in cart
     */
    private Integer productId;
}