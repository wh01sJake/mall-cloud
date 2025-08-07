package com.intelijake.mall.statistics.pojo.vo;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Chart data response DTO
 * Used for pie charts, bar charts, etc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Name/label for the chart segment
     */
    @NotNull
    private String name;
    
    /**
     * Value for the chart segment
     */
    @NotNull
    private Long value;
}