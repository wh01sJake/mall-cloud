package com.intelijake.mall.product.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.intelijake.mall.common.util.JwtUtil;
import com.intelijake.mall.common.util.Result;
import com.intelijake.mall.pojo.ProductCategory;
import com.intelijake.mall.product.pojo.query.ProductCategoryQuery;
import com.intelijake.mall.product.pojo.vo.ProductCategoryVO;
import com.intelijake.mall.product.service.IProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Product Category Management Controller
 * Enhanced with portal functionality for both admin and customer operations
 * </p>
 *
 * @author Jake
 * @since 2025-06-11
 */
@RestController
@RequestMapping("/product/category")
public class ProductCategoryController {

    @Autowired
    private IProductCategoryService ProductCategoryService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    // ==================== Customer-Facing Endpoints (Portal Functionality) ====================

    /**
     * Get all product categories in hierarchical structure (Customer endpoint)
     */
    @GetMapping("/listAll")
    public Result<List<ProductCategoryVO>> listAll() {
        List<ProductCategoryVO> list = ProductCategoryService.listAll();
        return Result.ok(list);
    }

    // ==================== Admin Management Endpoints ====================




    @GetMapping("/list")
    public Result list(ProductCategoryQuery ProductCategoryQuery) {

        // Temporarily disable Redis caching due to SSL connection issues
        // TODO: Fix Redis SSL configuration and re-enable caching
        /*
        // use redis to cache the category list
        //1. query if list existed in the redis
        IPage<ProductCategory> page = (IPage<ProductCategory>) redisTemplate.opsForValue().get("page");

        // 2. if null query db
        if (ObjectUtils.isEmpty(page)){
            page = ProductCategoryService.list(ProductCategoryQuery);

            // update to redis
            redisTemplate.opsForValue().set("page",page);
        }
        */

        // Direct database query without Redis caching
        IPage<ProductCategory> page = ProductCategoryService.list(ProductCategoryQuery);

        return Result.ok(page);
    }

    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        ProductCategoryService.removeById(id);
        return Result.ok("删除成功");
    }

    @DeleteMapping("/deleteAll/{ids}")
    public Result deleteAll(@PathVariable Integer[] ids) {
        ProductCategoryService.removeBatchByIds(Arrays.asList(ids));
        return Result.ok("删除成功");
    }


    @PostMapping("/add")
    public Result add(@RequestBody ProductCategory ProductCategory) {
        ProductCategoryService.save(ProductCategory);

        return Result.ok("添加成功");
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        ProductCategory ProductCategory = ProductCategoryService.getById(id);
        return Result.ok(ProductCategory);
    }

    @PutMapping("/update")
    public Result update(@RequestBody ProductCategory ProductCategory) {
        ProductCategoryService.updateById(ProductCategory);
        return Result.ok("更新成功");
    }



    @GetMapping("/ProductCategoryInfo")
    public Result ProductCategoryInfo(@RequestHeader(name = "Authorization") String token) {
        Map<String, Object> map = JwtUtil.parseToken(token);
        int id = (int) map.get("id");
        ProductCategory ProductCategory = ProductCategoryService.getById(id);
        return Result.ok(ProductCategory);
    }

    @GetMapping("/selectTopCategoryList")
    public Result<List<ProductCategory>> selectTopCategoryList(){

        QueryWrapper queryWrapper = new QueryWrapper();

        queryWrapper.eq("parent_id",0);

        List<ProductCategory> list = ProductCategoryService.list(queryWrapper);

        return Result.ok(list);
    }

    @GetMapping("/selectSecondCategoryListByParentId/{id}")
    public Result<List<ProductCategory>> selectSecondCategoryListByParentId(@PathVariable Integer id){

        QueryWrapper queryWrapper = new QueryWrapper();

        queryWrapper.eq("parent_id",id);

        List<ProductCategory> list = ProductCategoryService.list(queryWrapper);

        return Result.ok(list);
    }


}

