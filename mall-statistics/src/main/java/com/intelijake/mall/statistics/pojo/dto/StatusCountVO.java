package com.intelijake.mall.statistics.pojo.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Internal DTO for order status count mapping from database
 * Used for aggregating order counts by status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusCountVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Order status code (numeric value from database)
     */
    @NotNull
    private Integer status;
    
    /**
     * Human-readable status name
     */
    private String statusName;
    
    /**
     * Count of orders with this status
     */
    @NotNull
    private Long count;
}