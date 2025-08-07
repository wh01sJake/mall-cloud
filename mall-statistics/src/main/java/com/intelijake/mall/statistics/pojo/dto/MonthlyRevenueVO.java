package com.intelijake.mall.statistics.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Internal DTO for monthly revenue mapping from database
 * Used for monthly sales comparison statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRevenueVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Year of the revenue data
     */
    @NotNull
    private Integer year;
    
    /**
     * Month number (1-12)
     */
    @NotNull
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;
    
    /**
     * Revenue amount for the specific month
     */
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal revenue;
}