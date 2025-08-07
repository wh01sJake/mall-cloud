package com.intelijake.mall.email.dto;

public class EmailResponse {
    private boolean success;
    private String message;
    private String emailId;

    public EmailResponse() {}

    public EmailResponse(boolean success, String message, String emailId) {
        this.success = success;
        this.message = message;
        this.emailId = emailId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public static EmailResponse success(String message, String emailId) {
        return new EmailResponse(true, message, emailId);
    }

    public static EmailResponse failure(String message) {
        return new EmailResponse(false, message, null);
    }
}
