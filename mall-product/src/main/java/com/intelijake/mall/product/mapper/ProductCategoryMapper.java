package com.intelijake.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intelijake.mall.pojo.ProductCategory;
import com.intelijake.mall.product.pojo.vo.ProductCategoryVO;

import java.util.List;

/**
 * <p>
 * Product Category Mapper Interface
 * Enhanced with portal functionality for hierarchical category queries
 * </p>
 *
 * @author Jake
 * @since 2025-06-11
 */
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {

    /**
     * Get all product categories in hierarchical structure
     * @return List of product categories with parent-child relationships
     */
    List<ProductCategoryVO> listAll();
}
