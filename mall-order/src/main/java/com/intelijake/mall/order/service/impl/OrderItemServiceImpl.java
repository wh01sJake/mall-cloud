package com.intelijake.mall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelijake.mall.order.mapper.OrderItemMapper;
import com.intelijake.mall.order.service.IOrderItemService;
import com.intelijake.mall.pojo.OrderItem;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jake
 * @since 2025-06-11
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService {

}
