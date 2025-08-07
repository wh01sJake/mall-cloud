package com.intelijake.mall.product;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.intelijake.mall.api.query.ProductCategoryQuery;
import com.intelijake.mall.api.query.ProductQuery;
import com.intelijake.mall.api.vo.ProductCategoryVO;
import com.intelijake.mall.api.vo.ProductVO;
import com.intelijake.mall.common.util.Result;
import com.intelijake.mall.pojo.Product;
import com.intelijake.mall.pojo.ProductCategory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Product Service Fallback Implementation
 * Provides circuit breaker fallback responses when product service is unavailable
 * 
 * @author Jake
 * @since 2025-07-22
 */
@Component
public class ProductClientFallback implements ProductClient {

    // ==================== Product Operations ====================
    
    @Override
    public Result<IPage<ProductVO>> getProductList(ProductQuery productQuery) {
        return Result.error("Product service is temporarily unavailable. Please try again later.");
    }
    
    @Override
    public Result<Product> getProductById(Integer id) {
        return Result.error("Unable to retrieve product details. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<List<Product>> getProductsByCategoryId(Integer categoryId) {
        return Result.error("Unable to retrieve products by category. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<Map<String, Object>> getCustomerProductList(Integer categoryId, Integer limit) {
        return Result.error("Product catalog is temporarily unavailable. Please try again later.");
    }
    
    @Override
    public Result<String> addProduct(Product product) {
        return Result.error("Unable to add product. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> updateProduct(Product product) {
        return Result.error("Unable to update product. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> deleteProduct(Integer id) {
        return Result.error("Unable to delete product. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> deleteProducts(Integer[] ids) {
        return Result.error("Unable to delete products. Service is temporarily unavailable.");
    }

    // ==================== Product Category Operations ====================
    
    @Override
    public Result<List<ProductCategoryVO>> getAllCategories() {
        return Result.error("Product categories are temporarily unavailable. Please try again later.");
    }
    
    @Override
    public Result<IPage<ProductCategory>> getCategoryList(ProductCategoryQuery productCategoryQuery) {
        return Result.error("Category service is temporarily unavailable. Please try again later.");
    }
    
    @Override
    public Result<ProductCategory> getCategoryById(Integer id) {
        return Result.error("Unable to retrieve category details. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<List<ProductCategory>> getTopCategories() {
        return Result.error("Unable to retrieve top categories. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<List<ProductCategory>> getSecondCategoriesByParentId(Integer parentId) {
        return Result.error("Unable to retrieve subcategories. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> addCategory(ProductCategory category) {
        return Result.error("Unable to add category. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> updateCategory(ProductCategory category) {
        return Result.error("Unable to update category. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> deleteCategory(Integer id) {
        return Result.error("Unable to delete category. Service is temporarily unavailable.");
    }
    
    @Override
    public Result<String> deleteCategories(Integer[] ids) {
        return Result.error("Unable to delete categories. Service is temporarily unavailable.");
    }
}
