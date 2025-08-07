package com.intelijake.mall.statistics.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Internal DTO for daily customer registration mapping from database
 * Used for customer registration trend calculations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyRegistrationVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Date for the registration data
     */
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;
    
    /**
     * Number of customer registrations for the specific date
     */
    @NotNull
    private Long registrationCount;
}