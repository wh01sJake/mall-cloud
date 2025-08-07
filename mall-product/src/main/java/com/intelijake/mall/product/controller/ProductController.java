package com.intelijake.mall.product.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.intelijake.mall.common.constant.RedisConstants;
import com.intelijake.mall.common.util.JwtUtil;
import com.intelijake.mall.common.util.Result;
import com.intelijake.mall.pojo.Product;
import com.intelijake.mall.product.pojo.query.ProductQuery;
import com.intelijake.mall.product.pojo.vo.ProductVO;
import com.intelijake.mall.product.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.redis.core.RedisTemplate; // Temporarily disabled due to Redis SSL issues
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Product Management Controller
 * Enhanced with portal functionality for both admin and customer operations
 * </p>
 *
 * @author Jake
 * @since 2025-06-11
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private IProductService productService;

    // @Autowired
    // private RedisTemplate redisTemplate; // Temporarily disabled due to Redis SSL issues

    // ==================== Customer-Facing Endpoints (Portal Functionality) ====================

    /**
     * Get products by category ID (Customer endpoint)
     */
    @GetMapping("/selectByCategoryId")
    public Result selectByCategoryId(@RequestParam Integer id) {
        List<Product> list = productService.selectByCategoryId(id);
        return Result.ok(list);
    }



    /**
     * Get product list with optional category filter and limit (Customer endpoint)
     */
    @GetMapping("/customer/list")
    public Result getCustomerProductList(@RequestParam(required = false) Integer categoryId,
                                        @RequestParam(required = false) Integer limit) {
        List<Product> products;

        if (categoryId != null) {
            // Get products by category
            products = productService.selectByCategoryId(categoryId);
        } else {
            // Get all active products
            QueryWrapper<Product> wrapper = new QueryWrapper<>();
            wrapper.eq("is_deleted", 0);
            wrapper.eq("status", 1);
            wrapper.orderByDesc("create_time");
            products = productService.list(wrapper);
        }

        // Apply limit if specified
        if (limit != null && limit > 0 && products.size() > limit) {
            products = products.subList(0, limit);
        }

        // Return in the format expected by the frontend (with records property)
        Map<String, Object> result = new HashMap<>();
        result.put("records", products);
        result.put("total", products.size());

        return Result.ok(result);
    }

    // ==================== Admin Management Endpoints ====================




    @GetMapping("/list")
    public Result list(ProductQuery productQuery) {

        IPage<ProductVO> page = productService.list(productQuery);
        return Result.ok(page);
    }

    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        productService.removeById(id);
        return Result.ok("删除成功");
    }

    @DeleteMapping("/deleteAll/{ids}")
    public Result deleteAll(@PathVariable Integer[] ids) {
        productService.removeBatchByIds(Arrays.asList(ids));
        return Result.ok("删除成功");
    }


    @PostMapping("/add")
    public Result add(@RequestBody Product product) {
        productService.save(product);
        // redisTemplate.opsForSet().add(RedisConstants.UPLOAD_IMAGE_TO_DB,product.getMainImage()); // Temporarily disabled due to Redis SSL issues

        return Result.ok("添加成功");
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Product product = productService.selectById(id);
        return Result.ok(product);
    }

    @PutMapping("/update")
    public Result update(@RequestBody Product product) {
//        productService.updateById(product); // has to override this to enable cacheable
        productService.update(product);
        // redisTemplate.opsForSet().add(RedisConstants.UPLOAD_IMAGE_TO_DB,product.getMainImage()); // Temporarily disabled due to Redis SSL issues
        return Result.ok("更新成功");
    }



    @GetMapping("/productInfo")
    public Result productInfo(@RequestHeader(name = "Authorization") String token) {
        Map<String, Object> map = JwtUtil.parseToken(token);
        int id = (int) map.get("id");
        Product product = productService.getById(id);
        return Result.ok(product);
    }
}

