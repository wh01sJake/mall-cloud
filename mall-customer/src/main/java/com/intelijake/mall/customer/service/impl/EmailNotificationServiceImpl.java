package com.intelijake.mall.customer.service.impl;

import com.intelijake.mall.customer.service.EmailNotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    public void sendWelcomeEmail(String customerEmail, String customerName, String username) {
        try {
            System.out.println("📧 Sending welcome email to new customer: " + customerEmail);

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
            // Log error but don't fail the registration
            System.err.println("❌ Error sending welcome email to " + customerEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
