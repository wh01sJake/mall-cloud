package com.intelijake.mall.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelijake.mall.customer.pojo.vo.CartVO;
import com.intelijake.mall.pojo.ShoppingCart;
import com.intelijake.mall.pojo.query.CartQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Shopping Cart Mapper Interface
 * </p>
 *
 * @author Jake
 * @since 2025-07-21
 */
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

    /**
     * Get shopping cart items with product details
     * @param page pagination info
     * @param customerId customer ID
     * @param isChecked checked status filter
     * @return cart items with product details
     */
    IPage<CartVO> getCartItemsWithProductDetails(Page<CartVO> page, @Param("customerId") Integer customerId, @Param("isChecked") Integer isChecked);
    
    /**
     * Get shopping cart items with product details (non-paginated for testing)
     * @param customerId customer ID
     * @param isChecked checked status filter
     * @return cart items with product details
     */
    List<CartVO> getCartItemsWithProductDetailsSimple(@Param("customerId") Integer customerId, @Param("isChecked") Integer isChecked);

    /**
     * Get cart items by IDs with product details
     * @param cartIds list of cart item IDs
     * @return cart items with product details
     */
    List<CartVO> getCartItemsByIds(@Param("cartIds") List<Integer> cartIds);

}