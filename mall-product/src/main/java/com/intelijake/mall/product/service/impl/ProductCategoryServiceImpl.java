package com.intelijake.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelijake.mall.pojo.ProductCategory;
import com.intelijake.mall.product.mapper.ProductCategoryMapper;
import com.intelijake.mall.product.pojo.query.ProductCategoryQuery;
import com.intelijake.mall.product.pojo.vo.ProductCategoryVO;
import com.intelijake.mall.product.service.IProductCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jake
 * @since 2025-06-11
 */
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements IProductCategoryService {

    private final ProductCategoryMapper productCategoryMapper;

    public ProductCategoryServiceImpl(ProductCategoryMapper productCategoryMapper) {
        this.productCategoryMapper = productCategoryMapper;
    }

    @Override
    public IPage<ProductCategory> list(ProductCategoryQuery productCategoryQuery) {
        // Provide default values if pagination parameters are null
        Integer page = productCategoryQuery.getPage() != null ? productCategoryQuery.getPage() : 1;
        Integer limit = productCategoryQuery.getLimit() != null ? productCategoryQuery.getLimit() : 10;

        IPage<ProductCategory> pageObj = new Page<>(page, limit);
        QueryWrapper<ProductCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(!ObjectUtils.isEmpty(productCategoryQuery.getName()), "name", productCategoryQuery.getName());
        productCategoryMapper.selectPage(pageObj, queryWrapper);
        return pageObj;
    }

    @Override
    public List<ProductCategoryVO> listAll() {
        return productCategoryMapper.listAll();
    }
}
