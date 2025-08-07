package com.intelijake.mall.api.query;

import lombok.Data;

/**
 * Order Item Query Parameters
 * Shared query object for API communication
 * 
 * @author Jake
 * @since 2025-07-22
 */
@Data
public class OrderItemQuery {
    
    /**
     * Page number for pagination
     */
    private Integer page;
    
    /**
     * Page size limit for pagination
     */
    private Integer limit;
    
    /**
     * Order number filter
     */
    private Long orderNo;
    
    /**
     * Product ID filter
     */
    private Integer productId;
    
    /**
     * Customer ID filter
     */
    private Integer userId;
}
