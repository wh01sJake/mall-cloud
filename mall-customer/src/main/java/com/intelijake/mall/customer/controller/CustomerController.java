package com.intelijake.mall.customer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.intelijake.mall.common.util.JwtUtil;
import com.intelijake.mall.common.util.PasswordUtil;
import com.intelijake.mall.common.util.Result;
import com.intelijake.mall.customer.service.ICustomerService;
import com.intelijake.mall.customer.service.EmailNotificationService;
import com.intelijake.mall.customer.service.IShippingAddressService;
import com.intelijake.mall.pojo.Customer;
import com.intelijake.mall.pojo.ShippingAddress;
import com.intelijake.mall.pojo.query.CustomerQuery;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Customer Management Controller
 * Enhanced with portal authentication functionality
 * </p>
 *
 * @author Jake
 * @since 2025-06-11
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private IShippingAddressService shippingAddressService;

    @Autowired
    private EmailNotificationService emailNotificationService;

    // ==================== Authentication Endpoints (Portal Functionality) ====================

    /**
     * Customer login - JWT version
     */
    @PostMapping("/login")
    public Result login(@RequestBody Customer loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        Customer customer = customerService.login(username, password);

        if (customer == null) {
            return Result.error("Invalid username or password");
        }

        // Generate JWT token for the customer using Lombok getters
        String token = JwtUtil.generateToken(customer.getId(), customer.getUsername(), customer.getEmail(), customer.getStatus());
        String refreshToken = JwtUtil.generateRefreshToken(customer.getId());

        // Create response with token and user information
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("refreshToken", refreshToken);
        result.put("user", Map.of(
            "id", customer.getId(),
            "username", customer.getUsername(),
            "email", customer.getEmail() != null ? customer.getEmail() : "",
            "status", customer.getStatus()
        ));
        result.put("message", "Login successful");

        return Result.ok(result);
    }

    /**
     * Get current user information from JWT token
     */
    @GetMapping("/session-info")
    public Result sessionInfo(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error("No valid authorization token provided");
        }

        try {
            String token = authHeader.substring(7);
            Map<String, Object> userInfo = JwtUtil.extractUserInfo(token);

            Map<String, Object> result = new HashMap<>();
            result.put("userId", userInfo.get("userId"));
            result.put("username", userInfo.get("username"));
            result.put("email", userInfo.get("email"));
            result.put("status", userInfo.get("status"));

            return Result.ok(result);
        } catch (Exception e) {
            return Result.error("Invalid or expired token");
        }
    }

    /**
     * Customer logout - JWT version
     * Note: With JWT, logout is primarily handled on the client side by removing the token
     * In a production system, you might want to implement token blacklisting
     */
    @PostMapping("/logout")
    public Result logout(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        // For JWT-based authentication, logout is mainly client-side
        // The client should remove the token from storage
        // In a production system, you might want to add the token to a blacklist

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Optional: Log the logout event or add token to blacklist
            // For now, we'll just return success
        }

        return Result.ok("Logout successful");
    }

    /**
     * Refresh JWT token
     */
    @PostMapping("/refresh-token")
    public Result refreshToken(@RequestParam String refreshToken) {
        try {
            // Validate the refresh token
            Map<String, Object> userInfo = JwtUtil.extractUserInfo(refreshToken);

            // Check if it's actually a refresh token (you might want to add a claim for this)
            if (JwtUtil.isTokenExpired(refreshToken)) {
                return Result.error("Refresh token expired. Please login again.");
            }

            // Generate new access token
            Integer userId = (Integer) userInfo.get("userId");
            String username = (String) userInfo.get("username");
            String email = (String) userInfo.get("email");
            Integer status = (Integer) userInfo.get("status");

            String newToken = JwtUtil.generateToken(userId, username, email, status);
            String newRefreshToken = JwtUtil.generateRefreshToken(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("token", newToken);
            result.put("refreshToken", newRefreshToken);

            return Result.ok(result);
        } catch (Exception e) {
            return Result.error("Invalid refresh token. Please login again.");
        }
    }

    /**
     * Customer registration
     */
    @PostMapping("/register")
    public Result register(@RequestParam String inviteCode,
                          @RequestParam String username,
                          @RequestParam String email,
                          @RequestParam(required = false) String phone,
                          @RequestParam String password,
                          HttpSession httpSession) {

        // Hardcoded invite codes for registration
        final String[] VALID_INVITE_CODES = {
            "VAPE2024",
            "WELCOME123",
            "NEWUSER2024",
            "VAPEHUB2024",
            "INVITE123"
        };

        // Validate invite code
        boolean validInviteCode = false;
        for (String validCode : VALID_INVITE_CODES) {
            if (validCode.equals(inviteCode)) {
                validInviteCode = true;
                break;
            }
        }

        if (!validInviteCode) {
            return Result.error("Invalid invite code. Please contact support for a valid invite code.");
        }

        // Validate required fields
        if (username == null || username.trim().isEmpty()) {
            return Result.error("Username is required");
        }

        if (email == null || email.trim().isEmpty()) {
            return Result.error("Email is required");
        }

        if (password == null || password.trim().isEmpty()) {
            return Result.error("Password is required");
        }

        if (password.length() < 6) {
            return Result.error("Password must be at least 6 characters long");
        }

        // Check if username already exists
        Customer existingCustomer = customerService.findByUsername(username);
        if (existingCustomer != null) {
            return Result.error("Username already exists. Please choose a different username.");
        }

        // Check if email already exists
        Customer existingEmailCustomer = customerService.findByEmail(email);
        if (existingEmailCustomer != null) {
            return Result.error("Email already exists. Please use a different email address.");
        }

        // Create new customer
        Customer newCustomer = new Customer();
        newCustomer.setUsername(username.trim());
        newCustomer.setEmail(email.trim());
        newCustomer.setPhone(phone != null ? phone.trim() : null);
        newCustomer.setPassword(PasswordUtil.hashPassword(password)); // Hash password with BCrypt
        newCustomer.setStatus(1); // Active status

        try {
            customerService.save(newCustomer);

            // Send welcome email (asynchronously to avoid blocking registration)
            try {
                emailNotificationService.sendWelcomeEmail(
                    newCustomer.getEmail(),
                    newCustomer.getUsername(), // Using username as name for now
                    newCustomer.getUsername()
                );
            } catch (Exception emailError) {
                // Log error but don't fail the registration
                System.err.println("Failed to send welcome email to " + newCustomer.getEmail() + ": " + emailError.getMessage());
            }

            return Result.ok("Registration successful! Please check your email for a welcome message and login with your credentials.");
        } catch (Exception e) {
            return Result.error("Registration failed. Please try again.");
        }
    }

    // ==================== Admin Management Endpoints ====================

    /**
     * Get paginated customer list (Admin functionality)
     */
    @GetMapping("/list")
    public Result list(CustomerQuery customerQuery) {
        IPage<Customer> page = customerService.list(customerQuery);
        return Result.ok(page);
    }

//    @MyLog(module = "customer module")
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        customerService.removeById(id);
        return Result.ok("Deleted successfully");
    }

//    @MyLog(module = "customer module")
    @DeleteMapping("/deleteAll/{ids}")
    public Result deleteAll(@PathVariable Integer[] ids) {
        customerService.removeBatchByIds(Arrays.asList(ids));
        return Result.ok("Deleted successfully");
    }

    @PostMapping("/add")
    public Result add(@RequestBody Customer customer) {
        // Set default status if not provided
        if (customer.getStatus() == null) {
            customer.setStatus(1); // Default to active
        }
        customerService.save(customer);
        return Result.ok("Added successfully");
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Customer customer = customerService.getById(id);
        return Result.ok(customer);
    }

    @PutMapping("/update")
    public Result update(@RequestBody Customer customer) {
        customerService.updateById(customer);
        return Result.ok("Updated successfully");
    }

//    @MyLog(module = "customer module")
    @PutMapping("/updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Integer id, @PathVariable Integer status) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setStatus(status);
        customerService.updateById(customer);
        
        String statusText = status == 1 ? "activated" : "blocked";
        return Result.ok("Customer " + statusText + " successfully");
    }


    @GetMapping("/getAddressById/{id}")
    public Result<ShippingAddress> getAddressById(@PathVariable Integer id){

        ShippingAddress address = shippingAddressService.getById(id);

        if (address != null){
            return Result.ok(address);
        }
        else {
            return Result.error("No shipping address found for this order");
        }
    }



}

