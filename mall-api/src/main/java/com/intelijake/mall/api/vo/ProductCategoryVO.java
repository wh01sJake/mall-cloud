package com.intelijake.mall.api.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Product Category View Object
 * Hierarchical category structure with parent-child relationships
 * 
 * @author Jake
 * @since 2025-07-22
 */
@Data
public class ProductCategoryVO {
    
    /**
     * Category ID
     */
    private Integer id;
    
    /**
     * Category name
     */
    private String name;
    
    /**
     * Parent category ID (0 for top-level categories)
     */
    private Integer parentId;
    
    /**
     * Category description
     */
    private String description;
    
    /**
     * Category image URL
     */
    private String image;
    
    /**
     * Display order
     */
    private Integer sort;
    
    /**
     * Category status: 1-Active, 0-Inactive
     */
    private Integer status;
    
    /**
     * Child categories (for hierarchical display)
     */
    private List<ProductCategoryVO> childList;
    
    /**
     * Creation time
     */
    private Date createTime;
    
    /**
     * Last update time
     */
    private Date updateTime;
}
