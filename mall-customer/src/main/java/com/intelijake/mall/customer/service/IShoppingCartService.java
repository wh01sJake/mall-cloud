package com.intelijake.mall.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelijake.mall.customer.pojo.vo.CartVO;
import com.intelijake.mall.pojo.ShoppingCart;
import com.intelijake.mall.pojo.query.CartQuery;

import java.util.List;

/**
 * <p>
 * Shopping Cart Service Interface
 * </p>
 *
 * @author Jake
 * @since 2025-07-21
 */
public interface IShoppingCartService extends IService<ShoppingCart> {

    /**
     * Get paginated shopping cart items with product details based on query
     * criteria
     * 
     * @param cartQuery Query parameters including customerId, isChecked, pagination
     * @return Paginated shopping cart items with product details
     */
    IPage<CartVO> list(CartQuery cartQuery);

    /**
     * Add item to shopping cart or update quantity if item already exists
     * 
     * @param shoppingCart Shopping cart item to add
     * @return Success/failure result
     */
    boolean addToCart(ShoppingCart shoppingCart);

    /**
     * Clear all cart items for a specific customer
     * 
     * @param userId Customer ID
     * @return Success/failure result
     */
    boolean clearCart(Integer userId);

    /**
     * Get total count of items in cart for a customer
     * 
     * @param userId Customer ID
     * @return Total count of cart items
     */
    Integer getCartCount(Integer userId);

    /**
     * Update cart item quantity
     * 
     * @param userId    Customer ID
     * @param productId Product ID
     * @param quantity  New quantity
     * @return Success/failure result
     */
    boolean updateCartItemQuantity(Integer userId, Integer productId, Integer quantity);

    /**
     * Remove specific product from cart
     * 
     * @param userId    Customer ID
     * @param productId Product ID
     * @return Success/failure result
     */
    boolean removeFromCart(Integer userId, Integer productId);

    /**
     * Check if product exists in customer's cart
     *
     * @param userId    Customer ID
     * @param productId Product ID
     * @return Shopping cart item if exists, null otherwise
     */
    ShoppingCart getCartItem(Integer userId, Integer productId);

    /**
     * Get cart items by IDs with product details
     *
     * @param cartIds List of cart item IDs
     * @return List of cart items with product details
     */
    List<CartVO> getCartItemsByIds(List<Integer> cartIds);
}