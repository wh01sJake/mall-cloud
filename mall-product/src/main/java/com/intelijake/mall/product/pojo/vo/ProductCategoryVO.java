package com.intelijake.mall.product.pojo.vo;


import java.util.List;

/**
 * Product Category Value Object
 * Used for hierarchical category display with parent-child relationships
 * 
 * @author Jake
 * @since 2025-07-22
 */
public class ProductCategoryVO {
    
    /**
     * Category ID
     */
    private Integer id;
    
    /**
     * Parent category ID (0 for top-level categories)
     */
    private Integer parentId;
    
    /**
     * Category name
     */
    private String name;
    
    /**
     * Child categories list (for hierarchical display)
     */
    private List<ProductCategoryVO> childList;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductCategoryVO> getChildList() {
        return childList;
    }

    public void setChildList(List<ProductCategoryVO> childList) {
        this.childList = childList;
    }
}
