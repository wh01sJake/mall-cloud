package com.intelijake.mall.customer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.intelijake.mall.common.context.UserContext;
import com.intelijake.mall.common.util.Result;
import com.intelijake.mall.customer.pojo.vo.CartVO;
import com.intelijake.mall.customer.service.IShoppingCartService;
import com.intelijake.mall.pojo.ShoppingCart;
import com.intelijake.mall.pojo.query.CartQuery;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 * Shopping Cart Management Controller
 * </p>
 *
 * @author Jake
 * @since 2025-07-21
 */
@RestController
@RequestMapping("/customer/cart")
public class ShoppingCartController {

    @Autowired
    private IShoppingCartService shoppingCartService;

    /**
     * Get shopping cart items for current customer
     */
    @GetMapping("/list")
    public Result<IPage<CartVO>> list(CartQuery cartQuery) {
        // Get current user from context
        Integer currentUserId = UserContext.requireCurrentUserId();
        cartQuery.setCustomerId(currentUserId);

        IPage<CartVO> page = shoppingCartService.list(cartQuery);
        return Result.ok(page);
    }

    /**
     * Add item to shopping cart
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody ShoppingCart shoppingCart) {
        // Get current user from context and set it on the cart item
        Integer currentUserId = UserContext.requireCurrentUserId();
        shoppingCart.setUserId(currentUserId);

        // Set default values if not provided
        if (shoppingCart.getIsChecked() == null) {
            shoppingCart.setIsChecked(1); // Default to checked
        }
        if (shoppingCart.getStatus() == null) {
            shoppingCart.setStatus(1); // Default to active
        }

        shoppingCartService.addToCart(shoppingCart);
        return Result.ok("Added to cart successfully");
    }

    /**
     * Update cart item quantity
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody ShoppingCart shoppingCart) {
        shoppingCartService.updateById(shoppingCart);
        return Result.ok("Cart updated successfully");
    }

    /**
     * Remove item from cart
     */
    @DeleteMapping("/remove/{id}")
    public Result<String> removeById(@PathVariable Integer id) {
        shoppingCartService.removeById(id);
        return Result.ok("Item removed from cart successfully");
    }

    /**
     * Remove multiple items from cart
     */
    @DeleteMapping("/removeAll/{ids}")
    public Result<String> removeAll(@PathVariable Integer[] ids) {
        shoppingCartService.removeBatchByIds(Arrays.asList(ids));
        return Result.ok("Items removed from cart successfully");
    }

    /**
     * Get cart item by ID
     */
    @GetMapping("/selectById/{id}")
    public Result<ShoppingCart> selectById(@PathVariable Integer id) {
        ShoppingCart shoppingCart = shoppingCartService.getById(id);
        return Result.ok(shoppingCart);
    }

    /**
     * Update item checked status
     */
    @PutMapping("/updateChecked/{id}/{isChecked}")
    public Result<String> updateChecked(@PathVariable Integer id, @PathVariable Integer isChecked) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(id);
        shoppingCart.setIsChecked(isChecked);
        shoppingCartService.updateById(shoppingCart);
        
        String statusText = isChecked == 1 ? "checked" : "unchecked";
        return Result.ok("Item " + statusText + " successfully");
    }

    /**
     * Clear cart for current customer
     */
    @DeleteMapping("/clear")
    public Result<String> clearCart() {
        Integer currentUserId = UserContext.requireCurrentUserId();
        shoppingCartService.clearCart(currentUserId);
        return Result.ok("Cart cleared successfully");
    }

    /**
     * Get cart items count for current customer
     */
    @GetMapping("/count")
    public Result<Integer> getCartCount() {
        Integer currentUserId = UserContext.requireCurrentUserId();
        Integer count = shoppingCartService.getCartCount(currentUserId);
        return Result.ok(count);
    }

    /**
     * Get cart items by IDs with product details for order calculation
     * This endpoint is used by the order service to calculate order totals
     */
    @GetMapping("/items/{ids}")
    public Result<List<CartVO>> getCartItemsByIds(@PathVariable String ids) {
        try {
            // Parse comma-separated IDs
            List<Integer> cartIds = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            // Get cart items with product details
            List<CartVO> cartItems = shoppingCartService.getCartItemsByIds(cartIds);
            return Result.ok(cartItems);
        } catch (Exception e) {
            return Result.error("Failed to get cart items: " + e.getMessage());
        }
    }
}