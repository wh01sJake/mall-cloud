package com.intelijake.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * ClassName: StatisticsApplication
 * Description: Statistics service application main class
 * <p>
 * Datetime: 21/07/2025 12:00
 * Author: Kiro
 * Version: 1.0
 */

@SpringBootApplication
@MapperScan("com.intelijake.mall.statistics.mapper")
// @EnableCaching // Temporarily disabled due to Redis SSL connection issues
public class StatisticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(StatisticsApplication.class, args);
    }
}