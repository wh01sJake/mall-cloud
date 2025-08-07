package com.intelijake.mall.api.query;

import lombok.Data;

/**
 * Product Query Parameters
 * Shared query object for API communication
 * 
 * @author Jake
 * @since 2025-07-22
 */
@Data
public class ProductQuery {
    
    /**
     * Page number for pagination
     */
    private Integer page;
    
    /**
     * Page size limit for pagination
     */
    private Integer limit;
    
    /**
     * Product name for search
     */
    private String name;
    
    /**
     * Category ID to filter products
     */
    private Integer categoryId;
}
