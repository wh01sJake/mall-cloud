package com.intelijake.mall.statistics.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Trend data response DTO
 * Used for line charts showing trends over time
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendDataVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * List of date labels for the trend chart
     */
    @NotNull(message = "Dates list cannot be null")
    private List<String> dates;
    
    /**
     * List of values corresponding to each date
     * Can represent revenue amounts, counts, etc.
     */
    @NotNull(message = "Values list cannot be null")
    private List<BigDecimal> values;
}