package com.intelijake.mall.statistics.pojo.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Internal DTO for category product count mapping from database
 * Used for category distribution statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCountVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Category ID
     */
    @NotNull(message = "Category ID cannot be null")
    private Integer categoryId;
    
    /**
     * Category name
     */
    @NotBlank(message = "Category name cannot be blank")
    private String categoryName;
    
    /**
     * Count of products in this category
     */
    @NotNull(message = "Product count cannot be null")
    private Long productCount;
}