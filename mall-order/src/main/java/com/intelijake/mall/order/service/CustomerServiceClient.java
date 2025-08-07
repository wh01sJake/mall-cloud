package com.intelijake.mall.order.service;

import com.intelijake.mall.common.util.Result;
import com.intelijake.mall.order.dto.CartItemDTO;
import com.intelijake.mall.pojo.ShippingAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Customer Service Client
 * Handles communication with the customer service to fetch customer data
 * 
 * @author Jake
 * @since 2025-08-03
 */
@Service
public class CustomerServiceClient {

    private final RestTemplate restTemplate;

    @Value("${customer.service.url:http://localhost:8082}")
    private String customerServiceUrl;

    public CustomerServiceClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Get customer information by user ID
     * 
     * @param userId The user ID
     * @return Customer information including email and name
     */
    public CustomerInfo getCustomerById(Integer userId) {
        try {
            System.out.println("🔍 Fetching customer data for user ID: " + userId);

            String url = customerServiceUrl + "/customer/selectById/" + userId;
            
            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make the request
            ResponseEntity<Result> response = restTemplate.exchange(url, HttpMethod.GET, entity, Result.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Result result = response.getBody();
                
                if (result.getCode() == 0 && result.getData() != null) {
                    // Parse customer data from response
                    @SuppressWarnings("unchecked")
                    Map<String, Object> customerData = (Map<String, Object>) result.getData();
                    
                    String email = (String) customerData.get("email");
                    String name = (String) customerData.get("username"); // Use username as name since name field might be null
                    String username = (String) customerData.get("username");
                    
                    if (email != null && username != null) {
                        System.out.println("✅ Customer data retrieved: " + username + " (" + email + ")");
                        return new CustomerInfo(email, username, username);
                    }
                }
            }

            System.err.println("❌ Failed to get customer data from service. Response: " + response.getBody());
            
        } catch (Exception e) {
            System.err.println("❌ Error calling customer service for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }

        // Return fallback customer info
        System.out.println("⚠️ Using fallback customer data for user ID: " + userId);
        return new CustomerInfo(
            "customer" + userId + "@example.com",
            "Customer #" + userId,
            "customer" + userId
        );
    }

    /**
     * Get shipping address by ID
     *
     * @param addressId The shipping address ID
     * @return Shipping address information
     */
    public ShippingAddress getShippingAddressById(Integer addressId) {
        try {
            System.out.println("🏠 Fetching shipping address for ID: " + addressId);

            String url = customerServiceUrl + "/customer/getAddressById/" + addressId;

            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make the request
            ResponseEntity<Result> response = restTemplate.exchange(url, HttpMethod.GET, entity, Result.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Result result = response.getBody();

                if (result.getCode() == 0 && result.getData() != null) {
                    // Parse shipping address data from response
                    @SuppressWarnings("unchecked")
                    Map<String, Object> addressData = (Map<String, Object>) result.getData();

                    ShippingAddress address = new ShippingAddress();
                    address.setId((Integer) addressData.get("id"));
                    address.setReceiverName((String) addressData.get("receiverName"));
                    address.setReceiverPhone((String) addressData.get("receiverPhone"));
                    address.setReceiverMobile((String) addressData.get("receiverMobile"));
                    address.setAddressLine1((String) addressData.get("addressLine1"));
                    address.setAddressLine2((String) addressData.get("addressLine2"));
                    address.setTownCity((String) addressData.get("townCity"));
                    address.setEircode((String) addressData.get("eircode"));

                    System.out.println("✅ Shipping address retrieved: " + address.getReceiverName());
                    return address;
                }
            }

            System.err.println("❌ Failed to get shipping address from service. Response: " + response.getBody());

        } catch (Exception e) {
            System.err.println("❌ Error calling customer service for address " + addressId + ": " + e.getMessage());
            e.printStackTrace();
        }

        // Return fallback address
        System.out.println("⚠️ Using fallback shipping address for ID: " + addressId);
        ShippingAddress fallbackAddress = new ShippingAddress();
        fallbackAddress.setId(addressId);
        fallbackAddress.setReceiverName("Sample Customer");
        fallbackAddress.setReceiverPhone("123-456-7890");
        fallbackAddress.setReceiverMobile("123-456-7890");
        fallbackAddress.setAddressLine1("123 Sample Street");
        fallbackAddress.setAddressLine2("Sample Building");
        fallbackAddress.setTownCity("Dublin");
        fallbackAddress.setEircode("D01 A1B2");

        return fallbackAddress;
    }

    /**
     * Get cart items by IDs with product details
     *
     * @param cartIds Comma-separated cart item IDs
     * @return List of cart items with product details
     */
    public java.util.List<CartItemDTO> getCartItemsByIds(String cartIds) {
        try {
            System.out.println("🛒 Fetching cart items for IDs: " + cartIds);

            // Use the correct endpoint that exists in customer service
            String url = customerServiceUrl + "/customer/cart/items/" + cartIds;

            // Create headers for GET request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make GET request to the correct endpoint
            ResponseEntity<Result> response = restTemplate.exchange(url, HttpMethod.GET, entity, Result.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Result result = response.getBody();

                if (result.getCode() == 0 && result.getData() != null) {
                    // Parse cart items from response
                    @SuppressWarnings("unchecked")
                    java.util.List<java.util.Map<String, Object>> cartItemsData =
                        (java.util.List<java.util.Map<String, Object>>) result.getData();

                    java.util.List<CartItemDTO> cartItems = new java.util.ArrayList<>();
                    for (java.util.Map<String, Object> itemData : cartItemsData) {
                        CartItemDTO cartItem = new CartItemDTO();
                        cartItem.setId((Integer) itemData.get("id"));
                        cartItem.setProductId((Integer) itemData.get("productId"));
                        cartItem.setProductName((String) itemData.get("productName"));
                        cartItem.setProductMainImage((String) itemData.get("productMainImage"));
                        cartItem.setQuantity((Integer) itemData.get("quantity"));

                        // Handle price conversion
                        Object priceObj = itemData.get("productPrice");
                        if (priceObj instanceof Number) {
                            cartItem.setProductPrice(new java.math.BigDecimal(priceObj.toString()));
                        }

                        cartItems.add(cartItem);
                    }

                    System.out.println("✅ Cart items retrieved: " + cartItems.size() + " items");
                    return cartItems;
                }
            }

            System.err.println("❌ Failed to get cart items from service. Response: " + response.getBody());

        } catch (Exception e) {
            System.err.println("❌ Error calling customer service for cart items " + cartIds + ": " + e.getMessage());
            e.printStackTrace();
        }

        // Return empty list if service call fails
        System.out.println("⚠️ Returning empty cart items list");
        return new java.util.ArrayList<>();
    }

    /**
     * Customer information data class
     */
    public static class CustomerInfo {
        private final String email;
        private final String name;
        private final String username;

        public CustomerInfo(String email, String name, String username) {
            this.email = email;
            this.name = name;
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public String getName() {
            return name;
        }

        public String getUsername() {
            return username;
        }
    }
}
