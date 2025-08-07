package com.intelijake.mall.statistics.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Dashboard statistics response DTO
 * Contains summary statistics for the admin dashboard
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Total number of orders in the system
     */
    @NotNull
    private Long totalOrders;
    
    /**
     * Total revenue from all completed orders
     */
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal totalRevenue;
    
    /**
     * Number of pending orders
     */
    @NotNull
    private Long pendingOrders;
    
    /**
     * Number of completed orders
     */
    @NotNull
    private Long completedOrders;
}