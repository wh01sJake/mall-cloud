package com.intelijake.mall.statistics.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for date operations and period parsing
 */
public class DateUtils {

    private static final Pattern PERIOD_PATTERN = Pattern.compile("^(\\d+)([dmy])$");
    private static final Map<String, Integer> UNIT_MAP = new HashMap<>();

    static {
        UNIT_MAP.put("d", 1); // days
        UNIT_MAP.put("m", 30); // months (approximate)
        UNIT_MAP.put("y", 365); // years (approximate)
    }

    /**
     * Parse a period string (e.g., "7d", "30d", "90d", "1y") and return start and end dates
     *
     * @param period Period string in format [number][unit] where unit is d (days), m (months), or y (years)
     * @return Map containing "startDate" and "endDate" keys with corresponding Date objects
     * @throws IllegalArgumentException if period format is invalid
     */
    public static Map<String, Date> parsePeriod(String period) {
        if (period == null || period.isEmpty()) {
            period = "30d"; // Default to 30 days if not specified
        }

        Matcher matcher = PERIOD_PATTERN.matcher(period);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid period format. Expected format: [number][unit] (e.g., 7d, 30d, 1y)");
        }

        int amount = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(2);
        
        if (!UNIT_MAP.containsKey(unit)) {
            throw new IllegalArgumentException("Invalid period unit. Expected: d (days), m (months), or y (years)");
        }
        
        int days = amount * UNIT_MAP.get(unit);
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        
        Map<String, Date> result = new HashMap<>();
        result.put("startDate", Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        result.put("endDate", Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusSeconds(1).toInstant()));
        
        return result;
    }
    
    /**
     * Get the start and end dates for a specific year
     *
     * @param year The year to get dates for
     * @return Map containing "startDate" and "endDate" keys with corresponding Date objects
     */
    public static Map<String, Date> getYearDateRange(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        
        Map<String, Date> result = new HashMap<>();
        result.put("startDate", Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        result.put("endDate", Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusSeconds(1).toInstant()));
        
        return result;
    }
    
    /**
     * Format a Date object to a string in yyyy-MM-dd format
     *
     * @param date The date to format
     * @return Formatted date string
     */
    public static String formatDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .toString();
    }
}