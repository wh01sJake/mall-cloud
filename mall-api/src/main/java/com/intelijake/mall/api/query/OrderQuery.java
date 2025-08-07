package com.intelijake.mall.api.query;

import lombok.Data;

/**
 * Order Query Parameters
 * Shared query object for API communication
 * 
 * @author Jake
 * @since 2025-07-22
 */
@Data
public class OrderQuery {
    
    /**
     * Page number for pagination
     */
    private Integer page;
    
    /**
     * Page size limit for pagination
     */
    private Integer limit;
    
    /**
     * Order number for search
     */
    private String orderNo;
    
    /**
     * Order status filter
     */
    private Integer status;
    
    /**
     * Customer name for search
     */
    private String customerName;
    
    /**
     * Customer ID filter
     */
    private Integer customerId;
    
    /**
     * Start date for date range filter
     */
    private String startDate;
    
    /**
     * End date for date range filter
     */
    private String endDate;
}
