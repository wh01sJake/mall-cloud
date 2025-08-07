package com.intelijake.mall.email.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data

public class OrderConfirmationEmailRequest {
    

    private String customerEmail;
    
    private String customerName;
    
    private String orderNo;
    
    private LocalDateTime orderDate;

    private BigDecimal paymentAmount;
    
    private Integer paymentType;
    
    private String remark;
    
    private List<OrderItem> orderItems;
    
    private ShippingAddress shippingAddress;
    
    // Constructors
    public OrderConfirmationEmailRequest() {}

    public OrderConfirmationEmailRequest(String customerEmail, String customerName, String orderNo,
                                       LocalDateTime orderDate, BigDecimal paymentAmount, Integer paymentType,
                                       String remark, List<OrderItem> orderItems, ShippingAddress shippingAddress) {
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.orderNo = orderNo;
        this.orderDate = orderDate;
        this.paymentAmount = paymentAmount;
        this.paymentType = paymentType;
        this.remark = remark;
        this.orderItems = orderItems;
        this.shippingAddress = shippingAddress;
    }

    // Getters and Setters
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public BigDecimal getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(BigDecimal paymentAmount) { this.paymentAmount = paymentAmount; }

    public Integer getPaymentType() { return paymentType; }
    public void setPaymentType(Integer paymentType) { this.paymentType = paymentType; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }

    public ShippingAddress getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(ShippingAddress shippingAddress) { this.shippingAddress = shippingAddress; }

    public static class OrderItem {
        private String productName;
        private Integer quantity;
        private BigDecimal price;
        private String imageUrl;

        public OrderItem() {}

        public OrderItem(String productName, Integer quantity, BigDecimal price, String imageUrl) {
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
            this.imageUrl = imageUrl;
        }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }

    public static class ShippingAddress {
        private String fullName;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String county;
        private String postcode;
        private String country;
        private String phone;

        public ShippingAddress() {}

        public ShippingAddress(String fullName, String addressLine1, String addressLine2, String city,
                             String county, String postcode, String country, String phone) {
            this.fullName = fullName;
            this.addressLine1 = addressLine1;
            this.addressLine2 = addressLine2;
            this.city = city;
            this.county = county;
            this.postcode = postcode;
            this.country = country;
            this.phone = phone;
        }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getAddressLine1() { return addressLine1; }
        public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

        public String getAddressLine2() { return addressLine2; }
        public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getCounty() { return county; }
        public void setCounty(String county) { this.county = county; }

        public String getPostcode() { return postcode; }
        public void setPostcode(String postcode) { this.postcode = postcode; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
}
