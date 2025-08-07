package com.intelijake.mall.product;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.intelijake.mall.api.query.ProductCategoryQuery;
import com.intelijake.mall.api.query.ProductQuery;
import com.intelijake.mall.common.util.Result;
import com.intelijake.mall.pojo.Product;
import com.intelijake.mall.pojo.ProductCategory;
import com.intelijake.mall.api.vo.ProductCategoryVO;
import com.intelijake.mall.api.vo.ProductVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Product Service Feign Client
 * Provides product browsing, search, and category operations
 * 
 * @author Jake
 * @since 2025-07-22
 */
@FeignClient(value = "product-service", fallback = ProductClientFallback.class)
public interface ProductClient {

    // ==================== Product Operations ====================
    
    /**
     * Get paginated product list with search criteria (Admin functionality)
     */
    @GetMapping("/product/list")
    Result<IPage<ProductVO>> getProductList(ProductQuery productQuery);
    
    /**
     * Get product by ID
     */
    @GetMapping("/product/selectById/{id}")
    Result<Product> getProductById(@PathVariable("id") Integer id);
    
    /**
     * Get products by category ID (Customer endpoint)
     */
    @GetMapping("/product/selectByCategoryId")
    Result<List<Product>> getProductsByCategoryId(@RequestParam("id") Integer categoryId);
    
    /**
     * Get customer product list with optional category filter and limit
     */
    @GetMapping("/product/customer/list")
    Result<Map<String, Object>> getCustomerProductList(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                                                       @RequestParam(value = "limit", required = false) Integer limit);
    
    /**
     * Add new product (Admin functionality)
     */
    @PostMapping("/product/add")
    Result<String> addProduct(@RequestBody Product product);
    
    /**
     * Update product (Admin functionality)
     */
    @PutMapping("/product/update")
    Result<String> updateProduct(@RequestBody Product product);
    
    /**
     * Delete product by ID (Admin functionality)
     */
    @DeleteMapping("/product/deleteById/{id}")
    Result<String> deleteProduct(@PathVariable("id") Integer id);
    
    /**
     * Delete multiple products (Admin functionality)
     */
    @DeleteMapping("/product/deleteAll/{ids}")
    Result<String> deleteProducts(@PathVariable("ids") Integer[] ids);

    // ==================== Product Category Operations ====================
    
    /**
     * Get all product categories in hierarchical structure (Customer endpoint)
     */
    @GetMapping("/product/category/listAll")
    Result<List<ProductCategoryVO>> getAllCategories();
    
    /**
     * Get paginated product category list with search criteria (Admin functionality)
     */
    @GetMapping("/product/category/list")
    Result<IPage<ProductCategory>> getCategoryList(ProductCategoryQuery productCategoryQuery);
    
    /**
     * Get category by ID
     */
    @GetMapping("/product/category/selectById/{id}")
    Result<ProductCategory> getCategoryById(@PathVariable("id") Integer id);
    
    /**
     * Get top-level categories (parent_id = 0)
     */
    @GetMapping("/product/category/selectTopCategoryList")
    Result<List<ProductCategory>> getTopCategories();
    
    /**
     * Get second-level categories by parent ID
     */
    @GetMapping("/product/category/selectSecondCategoryListByParentId/{id}")
    Result<List<ProductCategory>> getSecondCategoriesByParentId(@PathVariable("id") Integer parentId);
    
    /**
     * Add new category (Admin functionality)
     */
    @PostMapping("/product/category/add")
    Result<String> addCategory(@RequestBody ProductCategory category);
    
    /**
     * Update category (Admin functionality)
     */
    @PutMapping("/product/category/update")
    Result<String> updateCategory(@RequestBody ProductCategory category);
    
    /**
     * Delete category by ID (Admin functionality)
     */
    @DeleteMapping("/product/category/deleteById/{id}")
    Result<String> deleteCategory(@PathVariable("id") Integer id);
    
    /**
     * Delete multiple categories (Admin functionality)
     */
    @DeleteMapping("/product/category/deleteAll/{ids}")
    Result<String> deleteCategories(@PathVariable("ids") Integer[] ids);
}
