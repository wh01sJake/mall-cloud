package com.intelijake.mall.customer;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.intelijake.mall.api.query.CartQuery;
import com.intelijake.mall.common.util.Result;
import com.intelijake.mall.pojo.Customer;
import com.intelijake.mall.pojo.ShippingAddress;
import com.intelijake.mall.pojo.ShoppingCart;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Customer Service Feign Client
 * Enhanced with shopping cart and shipping address operations
 *
 * @author Jake
 * @since 2025-07-22
 */
@FeignClient(value = "customer-service", fallback = CustomerClientFallback.class)
public interface CustomerClient {

    // ==================== Existing Customer Operations ====================

    /**
     * Get shipping address by ID
     */
    @GetMapping("/customer/getAddressById/{id}")
    Result<ShippingAddress> getAddressById(@PathVariable("id") Integer id);

    // ==================== Shopping Cart Operations ====================

    /**
     * Get shopping cart items for a customer
     */
    @GetMapping("/customer/cart/list")
    Result<IPage<ShoppingCart>> getCartItems(CartQuery cartQuery);

    /**
     * Add item to shopping cart
     */
    @PostMapping("/customer/cart/add")
    Result<String> addToCart(@RequestBody ShoppingCart shoppingCart);

    /**
     * Update cart item
     */
    @PutMapping("/customer/cart/update")
    Result<String> updateCartItem(@RequestBody ShoppingCart shoppingCart);

    /**
     * Remove item from cart by ID
     */
    @DeleteMapping("/customer/cart/remove/{id}")
    Result<String> removeCartItem(@PathVariable("id") Integer id);

    /**
     * Remove multiple items from cart
     */
    @DeleteMapping("/customer/cart/removeAll/{ids}")
    Result<String> removeCartItems(@PathVariable("ids") Integer[] ids);

    /**
     * Get cart item by ID
     */
    @GetMapping("/customer/cart/selectById/{id}")
    Result<ShoppingCart> getCartItemById(@PathVariable("id") Integer id);

    /**
     * Update item checked status
     */
    @PutMapping("/customer/cart/updateChecked/{id}/{isChecked}")
    Result<String> updateCartItemChecked(@PathVariable("id") Integer id, @PathVariable("isChecked") Integer isChecked);

    /**
     * Clear cart for a customer
     */
    @DeleteMapping("/customer/cart/clear/{userId}")
    Result<String> clearCart(@PathVariable("userId") Integer userId);

    /**
     * Get cart items count for a customer
     */
    @GetMapping("/customer/cart/count/{userId}")
    Result<Integer> getCartCount(@PathVariable("userId") Integer userId);

    /**
     * Get cart items by IDs with product details for order calculation
     */
    @GetMapping("/customer/cart/items/{ids}")
    Result getCartItemsByIds(@PathVariable("ids") String ids);

    // ==================== Shipping Address Operations ====================

    /**
     * Get all shipping addresses for a customer
     */
    @GetMapping("/customer/address/list/{customerId}")
    Result<List<ShippingAddress>> getCustomerAddresses(@PathVariable("customerId") Integer customerId);

    /**
     * Add new shipping address
     */
    @PostMapping("/customer/address/add")
    Result<String> addShippingAddress(@RequestBody ShippingAddress address);

    /**
     * Update shipping address
     */
    @PutMapping("/customer/address/update")
    Result<String> updateShippingAddress(@RequestBody ShippingAddress address);

    /**
     * Delete shipping address
     */
    @DeleteMapping("/customer/address/delete/{id}")
    Result<String> deleteShippingAddress(@PathVariable("id") Integer id);

    /**
     * Get shipping address by ID
     */
    @GetMapping("/customer/address/selectById/{id}")
    Result<ShippingAddress> getShippingAddressById(@PathVariable("id") Integer id);

    /**
     * Set default shipping address
     */
    @PutMapping("/customer/address/setDefault/{id}")
    Result<String> setDefaultAddress(@PathVariable("id") Integer id);

    /**
     * Get default shipping address for customer
     */
    @GetMapping("/customer/address/default")
    Result<ShippingAddress> getDefaultAddress();

    // ==================== Customer Management Operations ====================

    /**
     * Get customer by ID
     */
    @GetMapping("/customer/selectById/{id}")
    Result<Customer> getCustomerById(@PathVariable("id") Integer id);

    /**
     * Update customer status
     */
    @PutMapping("/customer/updateStatus/{id}/{status}")
    Result<String> updateCustomerStatus(@PathVariable("id") Integer id, @PathVariable("status") Integer status);
}
