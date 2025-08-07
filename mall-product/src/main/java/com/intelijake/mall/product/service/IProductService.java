package com.intelijake.mall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelijake.mall.pojo.Product;
import com.intelijake.mall.product.pojo.query.ProductQuery;
import com.intelijake.mall.product.pojo.vo.ProductVO;

import java.util.List;

/**
 * <p>
 * Product Service Interface
 * Enhanced with portal functionality for both admin and customer operations
 * </p>
 *
 * @author Jake
 * @since 2025-06-11
 */
public interface IProductService extends IService<Product> {

    /**
     * Get paginated product list with search criteria (Admin functionality)
     * @param productQuery Query parameters for filtering and pagination
     * @return Paginated product list with category information
     */
    IPage<ProductVO> list(ProductQuery productQuery);

    /**
     * Get product by ID with caching (Admin functionality)
     * @param id Product ID
     * @return Product details
     */
    Product selectById(Integer id);

    /**
     * Update product with cache eviction (Admin functionality)
     * @param product Product to update
     */
    void update(Product product);

    /**
     * Get products by category ID (Portal functionality)
     * @param id Category ID
     * @return List of products in the specified category
     */
    List<Product> selectByCategoryId(Integer id);
}
