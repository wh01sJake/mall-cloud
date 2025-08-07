package com.intelijake.mall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelijake.mall.pojo.ProductCategory;
import com.intelijake.mall.product.pojo.query.ProductCategoryQuery;
import com.intelijake.mall.product.pojo.vo.ProductCategoryVO;

import java.util.List;

/**
 * <p>
 * Product Category Service Interface
 * Enhanced with portal functionality for both admin and customer operations
 * </p>
 *
 * @author Jake
 * @since 2025-06-11
 */
public interface IProductCategoryService extends IService<ProductCategory> {

    /**
     * Get paginated product category list with search criteria (Admin functionality)
     * @param productCategoryQuery Query parameters for filtering and pagination
     * @return Paginated product category list
     */
    IPage<ProductCategory> list(ProductCategoryQuery productCategoryQuery);

    /**
     * Get all product categories in hierarchical structure (Portal functionality)
     * @return List of product categories with parent-child relationships
     */
    List<ProductCategoryVO> listAll();
}
