package com.intelijake.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.intelijake.mall.pojo.Product;
import com.intelijake.mall.product.pojo.query.ProductQuery;
import com.intelijake.mall.product.pojo.vo.ProductVO;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Jake
 * @since 2025-06-11
 */
public interface ProductMapper extends BaseMapper<Product> {

    IPage<ProductVO> list(IPage<ProductVO> page, ProductQuery productQuery);
}
