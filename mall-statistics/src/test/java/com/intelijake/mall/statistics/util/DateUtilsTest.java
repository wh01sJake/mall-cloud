package com.intelijake.mall.statistics.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    @DisplayName("Test period parsing with days")
    void testParsePeriodWithDays() {
        // Test with 7 days
        Map<String, Date> result = DateUtils.parsePeriod("7d");
        
        LocalDate expectedStartDate = LocalDate.now().minusDays(7);
        LocalDate actualStartDate = result.get("startDate").toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        
        assertEquals(expectedStartDate, actualStartDate, "Start date should be 7 days ago");
        
        // End date should be today (end of day)
        LocalDate expectedEndDate = LocalDate.now();
        LocalDate actualEndDate = result.get("endDate").toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        
        assertEquals(expectedEndDate, actualEndDate, "End date should be today");
    }
    
    @Test
    @DisplayName("Test period parsing with months")
    void testParsePeriodWithMonths() {
        // Test with 1 month (30 days)
        Map<String, Date> result = DateUtils.parsePeriod("1m");
        
        LocalDate expectedStartDate = LocalDate.now().minusDays(30);
        LocalDate actualStartDate = result.get("startDate").toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        
        assertEquals(expectedStartDate, actualStartDate, "Start date should be 30 days ago");
    }
    
    @Test
    @DisplayName("Test period parsing with years")
    void testParsePeriodWithYears() {
        // Test with 1 year (365 days)
        Map<String, Date> result = DateUtils.parsePeriod("1y");
        
        LocalDate expectedStartDate = LocalDate.now().minusDays(365);
        LocalDate actualStartDate = result.get("startDate").toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        
        assertEquals(expectedStartDate, actualStartDate, "Start date should be 365 days ago");
    }
    
    @Test
    @DisplayName("Test period parsing with default value")
    void testParsePeriodWithDefault() {
        // Test with null (should default to 30d)
        Map<String, Date> result = DateUtils.parsePeriod(null);
        
        LocalDate expectedStartDate = LocalDate.now().minusDays(30);
        LocalDate actualStartDate = result.get("startDate").toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        
        assertEquals(expectedStartDate, actualStartDate, "Start date should be 30 days ago for default");
    }
    
    @Test
    @DisplayName("Test period parsing with invalid format")
    void testParsePeriodWithInvalidFormat() {
        // Test with invalid format
        assertThrows(IllegalArgumentException.class, () -> {
            DateUtils.parsePeriod("invalid");
        }, "Should throw IllegalArgumentException for invalid format");
    }
    
    @Test
    @DisplayName("Test year date range")
    void testGetYearDateRange() {
        // Test with year 2023
        Map<String, Date> result = DateUtils.getYearDateRange(2023);
        
        LocalDate expectedStartDate = LocalDate.of(2023, 1, 1);
        LocalDate actualStartDate = result.get("startDate").toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        
        assertEquals(expectedStartDate, actualStartDate, "Start date should be January 1, 2023");
        
        LocalDate expectedEndDate = LocalDate.of(2023, 12, 31);
        LocalDate actualEndDate = result.get("endDate").toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        
        assertEquals(expectedEndDate, actualEndDate, "End date should be December 31, 2023");
    }
    
    @Test
    @DisplayName("Test date formatting")
    void testFormatDate() {
        // Create a date for January 15, 2023
        LocalDate localDate = LocalDate.of(2023, 1, 15);
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        String formattedDate = DateUtils.formatDate(date);
        
        assertEquals("2023-01-15", formattedDate, "Date should be formatted as yyyy-MM-dd");
    }
}