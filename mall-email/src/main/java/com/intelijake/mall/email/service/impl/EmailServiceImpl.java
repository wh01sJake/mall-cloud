package com.intelijake.mall.email.service.impl;

import com.intelijake.mall.email.dto.OrderConfirmationEmailRequest;
import com.intelijake.mall.email.dto.WelcomeEmailRequest;
import com.intelijake.mall.email.dto.EmailResponse;
import com.intelijake.mall.email.service.EmailService;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EmailServiceImpl.class);

    private final SendGrid sendGrid;
    private final TemplateEngine templateEngine;
    
    @Value("${sendgrid.from-email}")
    private String fromEmail;
    
    @Value("${sendgrid.from-name}")
    private String fromName;
    
    @Value("${email.admin-email}")
    private String adminEmail;

    public EmailServiceImpl(@Value("${sendgrid.api-key}") String apiKey, TemplateEngine templateEngine) {
        this.sendGrid = new SendGrid(apiKey);
        this.templateEngine = templateEngine;
    }

    @Override
    public EmailResponse sendOrderConfirmationEmail(OrderConfirmationEmailRequest request) {
        try {
            log.info("📧 Sending order confirmation email to: {}", request.getCustomerEmail());
            
            // Create email context
            Context context = new Context();
            context.setVariable("customerName", request.getCustomerName());
            context.setVariable("orderNo", request.getOrderNo());
            context.setVariable("orderDate", request.getOrderDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            context.setVariable("paymentAmount", request.getPaymentAmount());
            context.setVariable("paymentType", getPaymentTypeString(request.getPaymentType()));
            context.setVariable("orderItems", request.getOrderItems());
            context.setVariable("shippingAddress", request.getShippingAddress());
            context.setVariable("remark", request.getRemark());
            
            // Process template
            String htmlContent = templateEngine.process("order-confirmation", context);
            
            // Create and send email
            Email from = new Email(fromEmail, fromName);
            Email to = new Email(request.getCustomerEmail(), request.getCustomerName());
            String subject = "Order Confirmation - VapeMall Order #" + request.getOrderNo();
            Content content = new Content("text/html", htmlContent);
            
            Mail mail = new Mail(from, subject, to, content);
            
            Request sendGridRequest = new Request();
            sendGridRequest.setMethod(Method.POST);
            sendGridRequest.setEndpoint("mail/send");
            sendGridRequest.setBody(mail.build());
            
            Response response = sendGrid.api(sendGridRequest);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                String emailId = UUID.randomUUID().toString();
                log.info("✅ Order confirmation email sent successfully to: {}", request.getCustomerEmail());
                return EmailResponse.success("Order confirmation email sent successfully", emailId);
            } else {
                log.error("❌ Failed to send order confirmation email. Status: {}, Body: {}", 
                         response.getStatusCode(), response.getBody());
                return EmailResponse.failure("Failed to send email: " + response.getBody());
            }
            
        } catch (IOException e) {
            log.error("❌ Error sending order confirmation email", e);
            return EmailResponse.failure("Error sending email: " + e.getMessage());
        }
    }

    @Override
    public EmailResponse sendWelcomeEmail(WelcomeEmailRequest request) {
        try {
            log.info("📧 Sending welcome email to: {}", request.getCustomerEmail());
            
            // Create email context
            Context context = new Context();
            context.setVariable("customerName", request.getCustomerName());
            context.setVariable("username", request.getUsername());
            
            // Process template
            String htmlContent = templateEngine.process("welcome", context);
            
            // Create and send email
            Email from = new Email(fromEmail, fromName);
            Email to = new Email(request.getCustomerEmail(), request.getCustomerName());
            String subject = "Welcome to VapeMall - Your Account is Ready!";
            Content content = new Content("text/html", htmlContent);
            
            Mail mail = new Mail(from, subject, to, content);
            
            Request sendGridRequest = new Request();
            sendGridRequest.setMethod(Method.POST);
            sendGridRequest.setEndpoint("mail/send");
            sendGridRequest.setBody(mail.build());
            
            Response response = sendGrid.api(sendGridRequest);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                String emailId = UUID.randomUUID().toString();
                log.info("✅ Welcome email sent successfully to: {}", request.getCustomerEmail());
                return EmailResponse.success("Welcome email sent successfully", emailId);
            } else {
                log.error("❌ Failed to send welcome email. Status: {}, Body: {}", 
                         response.getStatusCode(), response.getBody());
                return EmailResponse.failure("Failed to send email: " + response.getBody());
            }
            
        } catch (IOException e) {
            log.error("❌ Error sending welcome email", e);
            return EmailResponse.failure("Error sending email: " + e.getMessage());
        }
    }

    @Override
    public EmailResponse sendAdminNotificationEmail(OrderConfirmationEmailRequest request) {
        try {
            log.info("📧 Sending admin notification email for order: {}", request.getOrderNo());
            
            // Create email context
            Context context = new Context();
            context.setVariable("customerName", request.getCustomerName());
            context.setVariable("customerEmail", request.getCustomerEmail());
            context.setVariable("orderNo", request.getOrderNo());
            context.setVariable("orderDate", request.getOrderDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            context.setVariable("paymentAmount", request.getPaymentAmount());
            context.setVariable("paymentType", getPaymentTypeString(request.getPaymentType()));
            context.setVariable("orderItems", request.getOrderItems());
            context.setVariable("shippingAddress", request.getShippingAddress());
            
            // Process template
            String htmlContent = templateEngine.process("admin-notification", context);
            
            // Create and send email
            Email from = new Email(fromEmail, fromName);
            Email to = new Email(adminEmail, "VapeMall Admin");
            String subject = "🛒 New Order Received - #" + request.getOrderNo();
            Content content = new Content("text/html", htmlContent);
            
            Mail mail = new Mail(from, subject, to, content);
            
            Request sendGridRequest = new Request();
            sendGridRequest.setMethod(Method.POST);
            sendGridRequest.setEndpoint("mail/send");
            sendGridRequest.setBody(mail.build());
            
            Response response = sendGrid.api(sendGridRequest);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                String emailId = UUID.randomUUID().toString();
                log.info("✅ Admin notification email sent successfully");
                return EmailResponse.success("Admin notification email sent successfully", emailId);
            } else {
                log.error("❌ Failed to send admin notification email. Status: {}, Body: {}", 
                         response.getStatusCode(), response.getBody());
                return EmailResponse.failure("Failed to send email: " + response.getBody());
            }
            
        } catch (IOException e) {
            log.error("❌ Error sending admin notification email", e);
            return EmailResponse.failure("Error sending email: " + e.getMessage());
        }
    }
    
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
