package com.intelijake.mall.statistics.mapper;

import com.intelijake.mall.statistics.pojo.dto.CategoryCountVO;
import com.intelijake.mall.statistics.pojo.dto.CategorySalesVO;
import com.intelijake.mall.statistics.pojo.dto.DailyRegistrationVO;
import com.intelijake.mall.statistics.pojo.dto.DailyRevenueVO;
import com.intelijake.mall.statistics.pojo.dto.MonthlyRevenueVO;
import com.intelijake.mall.statistics.pojo.dto.PaymentMethodCountVO;
import com.intelijake.mall.statistics.pojo.dto.StatusCountVO;
import com.intelijake.mall.statistics.pojo.vo.TopProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Statistics Mapper interface for database queries related to analytics and reporting
 */
@Mapper
public interface StatisticsMapper {
    
    /**
     * Get total count of all orders
     * @return Total order count
     */
    Long getTotalOrderCount();
    
    /**
     * Get total revenue from all completed orders
     * @return Total revenue amount
     */
    BigDecimal getTotalRevenue();
    
    /**
     * Get count of orders with pending status
     * @return Pending order count
     */
    Long getPendingOrderCount();
    
    /**
     * Get count of orders with completed status
     * @return Completed order count
     */
    Long getCompletedOrderCount();
    
    /**
     * Get order count distribution by status
     * @return List of status counts
     */
    List<StatusCountVO> getOrderStatusDistribution();
    
    /**
     * Get daily revenue for a date range
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return List of daily revenue data
     */
    List<DailyRevenueVO> getRevenueByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    /**
     * Get order count by payment method
     * @return List of payment method counts
     */
    List<PaymentMethodCountVO> getPaymentMethodStats();
    
    /**
     * Get product count by category
     * @return List of category product counts
     */
    List<CategoryCountVO> getCategoryProductCount();
    
    /**
     * Get top selling products by quantity sold
     * @param limit Maximum number of products to return
     * @return List of top selling products
     */
    List<TopProductVO> getTopSellingProducts(@Param("limit") Integer limit);
    
    /**
     * Get sales revenue by product category
     * @return List of category sales data
     */
    List<CategorySalesVO> getSalesByCategory();
    
    /**
     * Get monthly revenue for a specific year
     * @param year Year to get monthly revenue for
     * @return List of monthly revenue data
     */
    List<MonthlyRevenueVO> getMonthlyRevenue(@Param("year") Integer year);
    
    /**
     * Get daily customer registrations for a date range
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return List of daily registration counts
     */
    List<DailyRegistrationVO> getCustomerRegistrationsByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}