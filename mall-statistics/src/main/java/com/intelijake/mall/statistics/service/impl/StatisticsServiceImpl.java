package com.intelijake.mall.statistics.service.impl;

import com.intelijake.mall.statistics.mapper.StatisticsMapper;
import com.intelijake.mall.statistics.pojo.dto.DailyRegistrationVO;
import com.intelijake.mall.statistics.pojo.dto.DailyRevenueVO;
import com.intelijake.mall.statistics.pojo.dto.MonthlyRevenueVO;
import com.intelijake.mall.statistics.pojo.vo.ChartDataVO;
import com.intelijake.mall.statistics.pojo.vo.DashboardStatsVO;
import com.intelijake.mall.statistics.pojo.vo.MonthlyDataVO;
import com.intelijake.mall.statistics.pojo.vo.TopProductVO;
import com.intelijake.mall.statistics.pojo.vo.TrendDataVO;
import com.intelijake.mall.statistics.service.IStatisticsService;
import com.intelijake.mall.statistics.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the statistics service for analytics and reporting
 */
@Service
public class StatisticsServiceImpl implements IStatisticsService {

    @Autowired
    private StatisticsMapper statisticsMapper;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String[] MONTH_NAMES = {
        "January", "February", "March", "April", "May", "June", 
        "July", "August", "September", "October", "November", "December"
    };

    @Override
    // @Cacheable(value = "dashboard_stats") // Temporarily disabled due to Redis SSL issues
    public DashboardStatsVO getDashboardStats() {
        DashboardStatsVO stats = new DashboardStatsVO();
        // Implement with actual database queries
        stats.setTotalOrders(statisticsMapper.getTotalOrderCount());
        stats.setTotalRevenue(statisticsMapper.getTotalRevenue());
        stats.setPendingOrders(statisticsMapper.getPendingOrderCount());
        stats.setCompletedOrders(statisticsMapper.getCompletedOrderCount());
        return stats;
    }

    @Override
    // @Cacheable(value = "order_status_dist") // Temporarily disabled due to Redis SSL issues
    public List<ChartDataVO> getOrderStatusDistribution() {
        return statisticsMapper.getOrderStatusDistribution().stream()
                .map(status -> {
                    ChartDataVO chartData = new ChartDataVO();
                    chartData.setName(status.getStatusName());
                    chartData.setValue(status.getCount());
                    return chartData;
                })
                .collect(Collectors.toList());
    }

    @Override
    // @Cacheable(value = "revenue_trends", key = "#period") // Temporarily disabled due to Redis SSL issues
    public TrendDataVO getRevenueTrends(String period) {
        // Parse the period to get start and end dates
        Map<String, Date> dateRange = DateUtils.parsePeriod(period);
        Date startDate = dateRange.get("startDate");
        Date endDate = dateRange.get("endDate");
        
        // Get revenue data from database
        List<DailyRevenueVO> revenueData = statisticsMapper.getRevenueByDateRange(startDate, endDate);
        
        // Create a map of date to revenue for quick lookup
        Map<String, BigDecimal> revenueByDate = new HashMap<>();
        for (DailyRevenueVO data : revenueData) {
            String dateStr = DateUtils.formatDate(data.getDate());
            revenueByDate.put(dateStr, data.getRevenue());
        }
        
        // Generate all dates in the range
        List<String> allDates = new ArrayList<>();
        List<BigDecimal> allValues = new ArrayList<>();
        
        LocalDate currentDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        while (!currentDate.isAfter(endLocalDate)) {
            String dateStr = currentDate.format(DATE_FORMATTER);
            allDates.add(dateStr);
            
            // Use the revenue from the map or zero if not present
            BigDecimal revenue = revenueByDate.getOrDefault(dateStr, BigDecimal.ZERO);
            allValues.add(revenue);
            
            currentDate = currentDate.plusDays(1);
        }
        
        return new TrendDataVO(allDates, allValues);
    }

    @Override
    // @Cacheable(value = "payment_methods") // Temporarily disabled due to Redis SSL issues
    public List<ChartDataVO> getPaymentMethodStats() {
        return statisticsMapper.getPaymentMethodStats().stream()
                .map(payment -> {
                    ChartDataVO chartData = new ChartDataVO();
                    chartData.setName(payment.getPaymentTypeName());
                    chartData.setValue(payment.getCount());
                    return chartData;
                })
                .collect(Collectors.toList());
    }

    @Override
    // @Cacheable(value = "category_count") // Temporarily disabled due to Redis SSL issues
    public List<ChartDataVO> getCategoryCount() {
        return statisticsMapper.getCategoryProductCount().stream()
                .map(category -> {
                    ChartDataVO chartData = new ChartDataVO();
                    chartData.setName(category.getCategoryName());
                    chartData.setValue(category.getProductCount());
                    return chartData;
                })
                .collect(Collectors.toList());
    }

    @Override
    // @Cacheable(value = "top_products", key = "#limit") // Temporarily disabled due to Redis SSL issues
    public List<TopProductVO> getTopProducts(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // Default to 10 if not specified or invalid
        }
        return statisticsMapper.getTopSellingProducts(limit);
    }

    @Override
    // @Cacheable(value = "sales_by_category") // Temporarily disabled due to Redis SSL issues
    public List<ChartDataVO> getSalesByCategory() {
        return statisticsMapper.getSalesByCategory().stream()
                .map(category -> {
                    ChartDataVO chartData = new ChartDataVO();
                    chartData.setName(category.getCategoryName());
                    chartData.setValue(category.getTotalSales().longValue());
                    return chartData;
                })
                .collect(Collectors.toList());
    }

    @Override
    // @Cacheable(value = "monthly_sales", key = "#year") // Temporarily disabled due to Redis SSL issues
    public List<MonthlyDataVO> getMonthlySales(Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear(); // Default to current year if not specified
        }
        
        // Get monthly revenue data from database
        List<MonthlyRevenueVO> monthlyData = statisticsMapper.getMonthlyRevenue(year);
        
        // Create a map of month to revenue for quick lookup
        Map<Integer, BigDecimal> revenueByMonth = new HashMap<>();
        for (MonthlyRevenueVO data : monthlyData) {
            revenueByMonth.put(data.getMonth(), data.getRevenue());
        }
        
        // Generate data for all months
        List<MonthlyDataVO> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            MonthlyDataVO monthData = new MonthlyDataVO();
            monthData.setMonth(MONTH_NAMES[month - 1]);
            monthData.setRevenue(revenueByMonth.getOrDefault(month, BigDecimal.ZERO));
            result.add(monthData);
        }
        
        return result;
    }

    @Override
    // @Cacheable(value = "customer_trends", key = "#period") // Temporarily disabled due to Redis SSL issues
    public TrendDataVO getCustomerTrends(String period) {
        // Parse the period to get start and end dates
        Map<String, Date> dateRange = DateUtils.parsePeriod(period);
        Date startDate = dateRange.get("startDate");
        Date endDate = dateRange.get("endDate");
        
        // Get registration data from database
        List<DailyRegistrationVO> registrationData = statisticsMapper.getCustomerRegistrationsByDateRange(startDate, endDate);
        
        // Create a map of date to registration count for quick lookup
        Map<String, Long> registrationsByDate = new HashMap<>();
        for (DailyRegistrationVO data : registrationData) {
            String dateStr = DateUtils.formatDate(data.getDate());
            registrationsByDate.put(dateStr, data.getRegistrationCount());
        }
        
        // Generate all dates in the range
        List<String> allDates = new ArrayList<>();
        List<BigDecimal> allValues = new ArrayList<>();
        
        LocalDate currentDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        while (!currentDate.isAfter(endLocalDate)) {
            String dateStr = currentDate.format(DATE_FORMATTER);
            allDates.add(dateStr);
            
            // Use the registration count from the map or zero if not present
            Long count = registrationsByDate.getOrDefault(dateStr, 0L);
            allValues.add(new BigDecimal(count));
            
            currentDate = currentDate.plusDays(1);
        }
        
        return new TrendDataVO(allDates, allValues);
    }
}