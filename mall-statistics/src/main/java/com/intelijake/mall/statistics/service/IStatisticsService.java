package com.intelijake.mall.statistics.service;

import com.intelijake.mall.statistics.pojo.vo.ChartDataVO;
import com.intelijake.mall.statistics.pojo.vo.DashboardStatsVO;
import com.intelijake.mall.statistics.pojo.vo.MonthlyDataVO;
import com.intelijake.mall.statistics.pojo.vo.TopProductVO;
import com.intelijake.mall.statistics.pojo.vo.TrendDataVO;

import java.util.List;

/**
 * Statistics service interface for analytics and reporting
 */
public interface IStatisticsService {
    
    /**
     * Get dashboard summary statistics
     * @return Dashboard statistics data
     */
    DashboardStatsVO getDashboardStats();
    
    /**
     * Get order status distribution for pie chart
     * @return List of chart data with status name and count
     */
    List<ChartDataVO> getOrderStatusDistribution();
    
    /**
     * Get revenue trends for a specified period
     * @param period Period string (e.g., "7d", "30d", "90d", "1y")
     * @return Trend data with dates and revenue values
     */
    TrendDataVO getRevenueTrends(String period);
    
    /**
     * Get payment method distribution for pie chart
     * @return List of chart data with payment method name and count
     */
    List<ChartDataVO> getPaymentMethodStats();
    
    /**
     * Get product count by category for pie chart
     * @return List of chart data with category name and product count
     */
    List<ChartDataVO> getCategoryCount();
    
    /**
     * Get top selling products by quantity sold
     * @param limit Maximum number of products to return
     * @return List of top selling products with details
     */
    List<TopProductVO> getTopProducts(Integer limit);
    
    /**
     * Get sales revenue by product category for bar chart
     * @return List of chart data with category name and sales amount
     */
    List<ChartDataVO> getSalesByCategory();
    
    /**
     * Get monthly sales comparison for a specific year
     * @param year Year to get monthly sales for
     * @return List of monthly data with month name and revenue
     */
    List<MonthlyDataVO> getMonthlySales(Integer year);
    
    /**
     * Get customer registration trends for a specified period
     * @param period Period string (e.g., "7d", "30d", "90d", "1y")
     * @return Trend data with dates and registration counts
     */
    TrendDataVO getCustomerTrends(String period);
}