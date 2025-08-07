package com.intelijake.mall.customer;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.intelijake.mall.api.query.CartQuery;
import com.intelijake.mall.common.util.Result;
import com.intelijake.mall.pojo.Customer;
import com.intelijake.mall.pojo.ShippingAddress;
import com.intelijake.mall.pojo.ShoppingCart;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Customer Service Fallback Implementation
 * Provides circuit breaker fallback responses when customer service is unavailable
 * 
 * @author Jake
 * @since 2025-07-22
 */
@Component
public class CustomerClientFallback implements CustomerClient {

    // ==================== Existing Customer Operations ====================
    
    @Override
    public Result<ShippingAddress> getAddressById(Integer id) {
        return Result.error("Customer service is temporarily unavailable. Please try again later.");
    }

    // ==================== Shopping Cart Operations ====================
    
    @Override
    public Result<IPage<ShoppingCart>> getCartItems(CartQuery cartQuery) {
        return Result.error("Shopping cart service is temporarily unavailable. Please try again later.");
    }
    
    @Override
    public Result<String> addToCart(ShoppingCart shoppingCart) {
        return Result.error("Unable to add item to cart. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> updateCartItem(ShoppingCart shoppingCart) {
        return Result.error("Unable to update cart item. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> removeCartItem(Integer id) {
        return Result.error("Unable to remove cart item. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> removeCartItems(Integer[] ids) {
        return Result.error("Unable to remove cart items. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<ShoppingCart> getCartItemById(Integer id) {
        return Result.error("Unable to retrieve cart item. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> updateCartItemChecked(Integer id, Integer isChecked) {
        return Result.error("Unable to update cart item status. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> clearCart(Integer userId) {
        return Result.error("Unable to clear cart. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<Integer> getCartCount(Integer userId) {
        return Result.error("Unable to get cart count. Service is temporarily unavailable.");
    }

    @Override
    public Result getCartItemsByIds(String ids) {
        return Result.error("Unable to get cart items. Service is temporarily unavailable.");
    }

    // ==================== Shipping Address Operations ====================
    
    @Override
    public Result<List<ShippingAddress>> getCustomerAddresses(Integer customerId) {
        return Result.error("Unable to retrieve addresses. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> addShippingAddress(ShippingAddress address) {
        return Result.error("Unable to add address. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> updateShippingAddress(ShippingAddress address) {
        return Result.error("Unable to update address. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> deleteShippingAddress(Integer id) {
        return Result.error("Unable to delete address. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<ShippingAddress> getShippingAddressById(Integer id) {
        return Result.error("Unable to retrieve address. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> setDefaultAddress(Integer id) {
        return Result.error("Unable to set default address. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<ShippingAddress> getDefaultAddress() {
        return Result.error("Unable to retrieve default address. Service is temporarily unavailable.");
    }

    // ==================== Customer Management Operations ====================
    
    @Override
    public Result<Customer> getCustomerById(Integer id) {
        return Result.error("Unable to retrieve customer information. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> updateCustomerStatus(Integer id, Integer status) {
        return Result.error("Unable to update customer status. Service is temporarily unavailable.");
    }
}
