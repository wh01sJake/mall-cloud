package com.intelijake.mall.api.query;

import lombok.Data;

/**
 * Product Category Query Parameters
 * Shared query object for API communication
 * 
 * @author Jake
 * @since 2025-07-22
 */
@Data
public class ProductCategoryQuery {
    
    /**
     * Page number for pagination
     */
    private Integer page;
    
    /**
     * Page size limit for pagination
     */
    private Integer limit;
    
    /**
     * Category name for search
     */
    private String name;
}
