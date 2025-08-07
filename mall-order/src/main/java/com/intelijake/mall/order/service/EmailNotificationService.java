package com.intelijake.mall.order.service;

import com.intelijake.mall.pojo.CustomerOrder;

/**
 * Email Notification Service Interface
 * Handles sending email notifications for order events
 * 
 * @author Jake
 * @since 2025-08-03
 */
public interface EmailNotificationService {
    
    /**
     * Send order confirmation email to customer
     * 
     * @param order The customer order
     * @param customerEmail Customer's email address
     * @param customerName Customer's name
     */
    void sendOrderConfirmationEmail(CustomerOrder order, String customerEmail, String customerName);
    
    /**
     * Send admin notification email for new order
     * 
     * @param order The customer order
     * @param customerEmail Customer's email address
     * @param customerName Customer's name
     */
    void sendAdminNotificationEmail(CustomerOrder order, String customerEmail, String customerName);
    
    /**
     * Send welcome email to new customer
     * 
     * @param customerEmail Customer's email address
     * @param customerName Customer's name
     * @param username Customer's username (optional)
     */
    void sendWelcomeEmail(String customerEmail, String customerName, String username);
}
