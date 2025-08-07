package com.intelijake.mall.statistics.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Internal DTO for daily revenue mapping from database
 * Used for revenue trend calculations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyRevenueVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Date for the revenue data
     */
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;
    
    /**
     * Revenue amount for the specific date
     */
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal revenue;
}