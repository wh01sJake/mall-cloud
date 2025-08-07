package com.intelijake.mall.statistics.controller;

import com.intelijake.mall.common.util.Result;
import com.intelijake.mall.statistics.pojo.vo.ChartDataVO;
import com.intelijake.mall.statistics.pojo.vo.DashboardStatsVO;
import com.intelijake.mall.statistics.pojo.vo.MonthlyDataVO;
import com.intelijake.mall.statistics.pojo.vo.TopProductVO;
import com.intelijake.mall.statistics.pojo.vo.TrendDataVO;
import com.intelijake.mall.statistics.service.IStatisticsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for statistics and analytics endpoints
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    
    @Autowired
    private IStatisticsService statisticsService;
    
    @GetMapping("/dashboard")
    public Result<DashboardStatsVO> getDashboardStats() {
        return Result.ok(statisticsService.getDashboardStats());
    }
    
    @GetMapping("/order-status")
    public Result<List<ChartDataVO>> getOrderStatusDistribution() {
        return Result.ok(statisticsService.getOrderStatusDistribution());
    }
    
    @GetMapping("/revenue")
    public Result<TrendDataVO> getRevenueTrends(
            @RequestParam(defaultValue = "30d") String period) {
        return Result.ok(statisticsService.getRevenueTrends(period));
    }
    
    @GetMapping("/payment-methods")
    public Result<List<ChartDataVO>> getPaymentMethodStats() {
        return Result.ok(statisticsService.getPaymentMethodStats());
    }
    
    @GetMapping("/category-count")
    public Result<List<ChartDataVO>> getCategoryCount() {
        return Result.ok(statisticsService.getCategoryCount());
    }
    
    @GetMapping("/top-products")
    public Result<List<TopProductVO>> getTopProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.ok(statisticsService.getTopProducts(limit));
    }
    
    @GetMapping("/sales-by-category")
    public Result<List<ChartDataVO>> getSalesByCategory() {
        return Result.ok(statisticsService.getSalesByCategory());
    }
    
    @GetMapping("/monthly-sales")
    public Result<List<MonthlyDataVO>> getMonthlySales(
            @RequestParam(required = false) Integer year) {
        return Result.ok(statisticsService.getMonthlySales(year));
    }
    
    @GetMapping("/customer-trends")
    public Result<TrendDataVO> getCustomerTrends(
            @RequestParam(defaultValue = "30d") String period) {
        return Result.ok(statisticsService.getCustomerTrends(period));
    }
}