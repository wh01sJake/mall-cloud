package com.intelijake.mall.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.intelijake.mall.customer.mapper.ShoppingCartMapper;
import com.intelijake.mall.customer.pojo.vo.CartVO;
import com.intelijake.mall.customer.service.IShoppingCartService;
import com.intelijake.mall.pojo.ShoppingCart;
import com.intelijake.mall.pojo.query.CartQuery;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 * Shopping Cart Service Implementation
 * </p>
 *
 * @author Jake
 * @since 2025-07-21
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements IShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Override
    public IPage<CartVO> list(CartQuery cartQuery) {
        // Default pagination if not provided
        int page = cartQuery.getPage() != null ? cartQuery.getPage() : 1;
        int limit = cartQuery.getLimit() != null ? cartQuery.getLimit() : 10;
        
        // Get cart items with product details using simple method
        List<CartVO> cartItems = shoppingCartMapper.getCartItemsWithProductDetailsSimple(
            cartQuery.getCustomerId(), 
            cartQuery.getIsChecked()
        );
        
        // Create manual pagination
        Page<CartVO> pageInfo = new Page<>(page, limit);
        pageInfo.setTotal(cartItems.size());
        
        // Calculate pagination
        int start = (page - 1) * limit;
        int end = Math.min(start + limit, cartItems.size());
        
        if (start < cartItems.size()) {
            pageInfo.setRecords(cartItems.subList(start, end));
        } else {
            pageInfo.setRecords(new ArrayList<>());
        }
        
        return pageInfo;
    }

    @Override
    public boolean addToCart(ShoppingCart shoppingCart) {
        // Check if item already exists in cart
        ShoppingCart existingItem = getCartItem(shoppingCart.getUserId(), shoppingCart.getProductId());
        
        if (existingItem != null) {
            // Update quantity if item already exists
            existingItem.setQuantity(existingItem.getQuantity() + shoppingCart.getQuantity());
            existingItem.setIsChecked(shoppingCart.getIsChecked());
            return updateById(existingItem);
        } else {
            // Add new item to cart
            return save(shoppingCart);
        }
    }

    @Override
    public boolean clearCart(Integer userId) {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("is_deleted", 0);
        return remove(queryWrapper);
    }

    @Override
    public Integer getCartCount(Integer userId) {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("status", 1);
        queryWrapper.eq("is_deleted", 0);
        return Math.toIntExact(count(queryWrapper));
    }

    @Override
    public boolean updateCartItemQuantity(Integer userId, Integer productId, Integer quantity) {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("product_id", productId);
        queryWrapper.eq("is_deleted", 0);
        
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setQuantity(quantity);
        
        return update(shoppingCart, queryWrapper);
    }

    @Override
    public boolean removeFromCart(Integer userId, Integer productId) {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("product_id", productId);
        queryWrapper.eq("is_deleted", 0);
        return remove(queryWrapper);
    }

    @Override
    public ShoppingCart getCartItem(Integer userId, Integer productId) {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("product_id", productId);
        queryWrapper.eq("status", 1);
        queryWrapper.eq("is_deleted", 0);
        return getOne(queryWrapper);
    }

    @Override
    public List<CartVO> getCartItemsByIds(List<Integer> cartIds) {
        if (cartIds == null || cartIds.isEmpty()) {
            return new ArrayList<>();
        }

        // Use the existing mapper method to get cart items with product details
        return shoppingCartMapper.getCartItemsByIds(cartIds);
    }
}