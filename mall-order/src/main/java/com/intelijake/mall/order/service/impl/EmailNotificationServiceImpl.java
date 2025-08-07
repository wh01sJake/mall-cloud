package com.intelijake.mall.order.service.impl;

import com.intelijake.mall.order.service.EmailNotificationService;
import com.intelijake.mall.pojo.CustomerOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Email Notification Service Implementation
 * Handles sending email notifications via the email service
 * 
 * @author Jake
 * @since 2025-08-03
 */
@Service
public class EmailNotificationServiceImpl implements EmailNotificationService {

    private final RestTemplate restTemplate;

    @Value("${email.service.url:http://localhost:8084}")
    private String emailServiceUrl;

    public EmailNotificationServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void sendOrderConfirmationEmail(CustomerOrder order, String customerEmail, String customerName) {
        try {
            System.out.println("📧 Sending order confirmation email for order: " + order.getOrderNo());

            // Build email request
            Map<String, Object> emailRequest = new HashMap<>();
            emailRequest.put("customerEmail", customerEmail);
            emailRequest.put("customerName", customerName);
            emailRequest.put("orderNo", order.getOrderNo());
            emailRequest.put("orderDate", LocalDateTime.now());
            emailRequest.put("paymentAmount", order.getPaymentAmount() != null ? order.getPaymentAmount() : BigDecimal.ZERO);
            emailRequest.put("paymentType", order.getPaymentType());
            emailRequest.put("remark", null); // No remark field in CustomerOrder entity

            // Add shipping address if available
            if (order.getShippingId() != null) {
                Map<String, Object> shippingAddress = new HashMap<>();
                shippingAddress.put("fullName", customerName);
                shippingAddress.put("addressLine1", "Address details will be fetched from shipping service");
                shippingAddress.put("city", "Dublin");
                shippingAddress.put("county", "Dublin");
                shippingAddress.put("postcode", "D01 A1B2");
                shippingAddress.put("country", "Ireland");
                emailRequest.put("shippingAddress", shippingAddress);
            }

            // Add order items (placeholder for now)
            // TODO: Fetch actual order items from order items table
            // For now, we'll send a basic order confirmation without detailed items

            // Send HTTP request to email service
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(emailRequest, headers);

            String url = emailServiceUrl + "/email/order-confirmation";
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("✅ Order confirmation email sent successfully for order: " + order.getOrderNo());
            } else {
                System.err.println("❌ Failed to send order confirmation email. Status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            // Log error but don't fail the order creation
            System.err.println("❌ Error sending order confirmation email for order " + order.getOrderNo() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void sendAdminNotificationEmail(CustomerOrder order, String customerEmail, String customerName) {
        try {
            System.out.println("📧 Sending admin notification email for order: " + order.getOrderNo());

            // Build email request (same structure as order confirmation)
            Map<String, Object> emailRequest = new HashMap<>();
            emailRequest.put("customerEmail", customerEmail);
            emailRequest.put("customerName", customerName);
            emailRequest.put("orderNo", order.getOrderNo());
            emailRequest.put("orderDate", LocalDateTime.now());
            emailRequest.put("paymentAmount", order.getPaymentAmount() != null ? order.getPaymentAmount() : BigDecimal.ZERO);
            emailRequest.put("paymentType", order.getPaymentType());
            emailRequest.put("remark", null); // No remark field in CustomerOrder entity

            // Add shipping address if available
            if (order.getShippingId() != null) {
                Map<String, Object> shippingAddress = new HashMap<>();
                shippingAddress.put("fullName", customerName);
                shippingAddress.put("addressLine1", "Address details will be fetched from shipping service");
                shippingAddress.put("city", "Dublin");
                shippingAddress.put("county", "Dublin");
                shippingAddress.put("postcode", "D01 A1B2");
                shippingAddress.put("country", "Ireland");
                emailRequest.put("shippingAddress", shippingAddress);
            }

            // Send HTTP request to email service
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(emailRequest, headers);

            String url = emailServiceUrl + "/email/admin-notification";
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("✅ Admin notification email sent successfully for order: " + order.getOrderNo());
            } else {
                System.err.println("❌ Failed to send admin notification email. Status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            // Log error but don't fail the order creation
            System.err.println("❌ Error sending admin notification email for order " + order.getOrderNo() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void sendWelcomeEmail(String customerEmail, String customerName, String username) {
        try {
            System.out.println("📧 Sending welcome email to: " + customerEmail);

            // Build email request
            Map<String, Object> emailRequest = new HashMap<>();
            emailRequest.put("customerEmail", customerEmail);
            emailRequest.put("customerName", customerName);
            emailRequest.put("username", username);

            // Send HTTP request to email service
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(emailRequest, headers);

            String url = emailServiceUrl + "/email/welcome";
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("✅ Welcome email sent successfully to: " + customerEmail);
            } else {
                System.err.println("❌ Failed to send welcome email. Status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            // Log error but don't fail the operation
            System.err.println("❌ Error sending welcome email to " + customerEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get payment type string representation
     */
    private String getPaymentTypeString(Integer paymentType) {
        if (paymentType == null) return "Not specified";
        return switch (paymentType) {
            case 1 -> "Stripe";
            case 2 -> "PayPal";
            case 3 -> "Credit Card";
            case 4 -> "Cash on Delivery";
            default -> "Unknown";
        };
    }
}
