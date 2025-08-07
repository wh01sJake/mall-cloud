package com.intelijake.mall.api.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Product View Object
 * Enhanced product information with category details
 * 
 * @author Jake
 * @since 2025-07-22
 */
@Data
public class ProductVO {
    
    /**
     * Product ID
     */
    private Integer id;
    
    /**
     * Product name
     */
    private String name;
    
    /**
     * Product description
     */
    private String description;
    
    /**
     * Product image URL
     */
    private String image;
    
    /**
     * Product price in EUR
     */
    private BigDecimal price;
    
    /**
     * Stock quantity
     */
    private Integer stock;
    
    /**
     * Product status: 1-On Sale, 0-Delisted
     */
    private Integer status;
    
    /**
     * Category ID
     */
    private Integer categoryId;
    
    /**
     * Category name (joined from category table)
     */
    private String categoryName;
    
    /**
     * Creation time
     */
    private Date createTime;
    
    /**
     * Last update time
     */
    private Date updateTime;
}
