package com.intelijake.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelijake.mall.pojo.Product;
import com.intelijake.mall.product.mapper.ProductMapper;
import com.intelijake.mall.product.pojo.query.ProductQuery;
import com.intelijake.mall.product.pojo.vo.ProductVO;
import com.intelijake.mall.product.service.IProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Jake
 * @since 2025-06-11
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisTemplate<String, Object> template;

    @Override
    public IPage<ProductVO> list(ProductQuery productQuery) {
        // Provide default values if pagination parameters are null
        Integer page = productQuery.getPage() != null ? productQuery.getPage() : 1;
        Integer limit = productQuery.getLimit() != null ? productQuery.getLimit() : 10;

        IPage<ProductVO> pageObj = new Page<>(page, limit);

        productMapper.list(pageObj, productQuery);
        return pageObj;
    }

    // @Cacheable(value = "productCache", key = "#id", sync = true) // Temporarily disabled due to Redis SSL issues
    @Override
    public Product selectById(Integer id) {
        return productMapper.selectById(id);
    }

    // @CacheEvict(value = "productCache", key = "#product.id") // Temporarily disabled due to Redis SSL issues
    @Override
    public void update(Product product) {

        productMapper.updateById(product);
    }

    @Override
    public List<Product> selectByCategoryId(Integer id) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_id", id);
        queryWrapper.eq("is_deleted", 0); // Only show non-deleted products
        queryWrapper.eq("status", 1); // Only show active products
        queryWrapper.orderByDesc("create_time"); // Order by newest first

        return productMapper.selectList(queryWrapper);
    }

    /*
     * @Override
     * public Product selectById(Integer id) {
     * 
     * Product product = (Product) template.opsForValue().get("product:" + id);
     * 
     * if (ObjectUtils.isEmpty(product)){
     * 
     * redissonClient.getLock("product_lock_"+id).lock();
     * 
     * try {
     * product = (Product) template.opsForValue().get("product:" + id);
     * 
     * if (ObjectUtils.isEmpty(product)){
     * product = productMapper.selectById(id);
     * //if existed in db
     * if (!ObjectUtils.isEmpty(product)){
     * 
     * template.opsForValue().set("product",product);
     * }
     * // null product, cache null value with an expire date
     * else {
     * template.opsForValue().set("product",new Product(),1, TimeUnit.MINUTES);
     * }
     * }
     * }
     * finally {
     * redissonClient.getLock("product_lock_"+id).unlock();
     * }
     * }
     * return product;
     * }
     */

    /*
     * @Override
     * public Product selectById(Integer id) {
     * 
     * Product product = (Product) template.opsForValue().get("product:" + id);
     * 
     * if (ObjectUtils.isEmpty(product)){
     * 
     * synchronized (this){
     * product = (Product) template.opsForValue().get("product:" + id);
     * 
     * if (ObjectUtils.isEmpty(product)){
     * product = productMapper.selectById(id);
     * //if existed in db
     * if (!ObjectUtils.isEmpty(product)){
     * 
     * template.opsForValue().set("product",product);
     * }
     * // null product, cache null value with an expire date
     * else {
     * template.opsForValue().set("product",new Product(),1, TimeUnit.MINUTES);
     * }
     * }
     * }
     * }
     * return product;
     * }
     */
}
