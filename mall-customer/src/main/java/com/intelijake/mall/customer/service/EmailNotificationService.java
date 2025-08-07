package com.intelijake.mall.customer.service;

/**
 * Email Notification Service Interface
 * Handles sending email notifications for customer events
 * 
 * @author Jake
 * @since 2025-08-03
 */
public interface EmailNotificationService {
    
    /**
     * Send welcome email to new customer
     * 
     * @param customerEmail Customer's email address
     * @param customerName Customer's name
     * @param username Customer's username
     */
    void sendWelcomeEmail(String customerEmail, String customerName, String username);
}
