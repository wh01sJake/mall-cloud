package com.intelijake.mall.statistics.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Monthly data response DTO
 * Used for monthly sales comparison charts
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyDataVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Month name (January, February, etc.)
     */
    @NotNull
    private String month;
    
    /**
     * Revenue amount for the month
     */
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal revenue;
}