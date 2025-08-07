package com.intelijake.mall.statistics.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Internal DTO for category sales mapping from database
 * Used for sales by category statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorySalesVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Category ID
     */
    @NotNull
    private Integer categoryId;
    
    /**
     * Category name
     */
    @NotBlank
    private String categoryName;
    
    /**
     * Total sales revenue for this category
     */
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal totalSales;
}