package com.intelijake.mall.email.controller;

import com.intelijake.mall.email.dto.OrderConfirmationEmailRequest;
import com.intelijake.mall.email.dto.WelcomeEmailRequest;
import com.intelijake.mall.email.dto.EmailResponse;
import com.intelijake.mall.email.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/email")
@Validated
public class EmailController {

    private static final Logger log = LoggerFactory.getLogger(EmailController.class);
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Send order confirmation email
     */
    @PostMapping("/order-confirmation")
    public ResponseEntity<EmailResponse> sendOrderConfirmationEmail(
            @Valid @RequestBody OrderConfirmationEmailRequest request) {
        
        log.info("📧 Received request to send order confirmation email for order: {}", request.getOrderNo());
        
        EmailResponse response = emailService.sendOrderConfirmationEmail(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Send welcome email to new customer
     */
    @PostMapping("/welcome")
    public ResponseEntity<EmailResponse> sendWelcomeEmail(
            @Valid @RequestBody WelcomeEmailRequest request) {
        
        log.info("📧 Received request to send welcome email to: {}", request.getCustomerEmail());
        
        EmailResponse response = emailService.sendWelcomeEmail(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Send admin notification email for new order
     */
    @PostMapping("/admin-notification")
    public ResponseEntity<EmailResponse> sendAdminNotificationEmail(
            @Valid @RequestBody OrderConfirmationEmailRequest request) {
        
        log.info("📧 Received request to send admin notification for order: {}", request.getOrderNo());
        
        EmailResponse response = emailService.sendAdminNotificationEmail(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Email Service is running");
    }
}
