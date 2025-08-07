package com.intelijake.mall.email.service;

import com.intelijake.mall.email.dto.OrderConfirmationEmailRequest;
import com.intelijake.mall.email.dto.WelcomeEmailRequest;
import com.intelijake.mall.email.dto.EmailResponse;

public interface EmailService {
    
    /**
     * Send order confirmation email to customer
     */
    EmailResponse sendOrderConfirmationEmail(OrderConfirmationEmailRequest request);
    
    /**
     * Send welcome email to new customer
     */
    EmailResponse sendWelcomeEmail(WelcomeEmailRequest request);
    
    /**
     * Send admin notification email for new order
     */
    EmailResponse sendAdminNotificationEmail(OrderConfirmationEmailRequest request);
}
