package com.intelijake.mall.statistics.pojo.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Internal DTO for payment method count mapping from database
 * Used for payment method distribution statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodCountVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Payment method type code (numeric value from database)
     */
    private Integer paymentType;
    
    /**
     * Human-readable payment method name
     */
    private String paymentTypeName;
    
    /**
     * Count of orders using this payment method
     */
    @NotNull
    private Long count;
}