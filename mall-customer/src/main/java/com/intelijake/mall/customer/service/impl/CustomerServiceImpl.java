package com.intelijake.mall.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.intelijake.mall.common.util.PasswordUtil;
import com.intelijake.mall.customer.mapper.CustomerMapper;
import com.intelijake.mall.customer.service.ICustomerService;
import com.intelijake.mall.pojo.Customer;
import com.intelijake.mall.pojo.query.CustomerQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jake
 * @since 2025-06-11
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements ICustomerService {

    @Autowired
    CustomerMapper customerMapper;

    @Override
    public IPage<Customer> list(CustomerQuery customerQuery) {
        IPage<Customer> page = new Page<>(customerQuery.getPage(), customerQuery.getLimit());
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(!ObjectUtils.isEmpty(customerQuery.getUsername()), "username", customerQuery.getUsername());
        queryWrapper.like(!ObjectUtils.isEmpty(customerQuery.getEmail()), "email", customerQuery.getEmail());
        queryWrapper.like(!ObjectUtils.isEmpty(customerQuery.getPhone()), "phone", customerQuery.getPhone());
        customerMapper.selectPage(page, queryWrapper);
        return page;
    }

    @Override
    public Customer login(String username, String password) {
        // First, find the customer by username
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("status", 1); // Only allow active customers to login
        queryWrapper.eq("is_deleted", 0); // Only allow non-deleted customers

        Customer customer = customerMapper.selectOne(queryWrapper);

        if (customer == null) {
            System.out.println("❌ Customer not found: " + username);
            return null;
        }

        // Verify password using BCrypt
        if (PasswordUtil.verifyPassword(password, customer.getPassword())) {
            System.out.println("✅ Customer login successful: " + username);
            return customer;
        } else {
            System.out.println("❌ Invalid password for customer: " + username);
            return null;
        }
    }

    @Override
    public Customer findByUsername(String username) {
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("is_deleted", 0); // Only find non-deleted customers

        return customerMapper.selectOne(queryWrapper);
    }

    @Override
    public Customer findByEmail(String email) {
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        queryWrapper.eq("is_deleted", 0); // Only find non-deleted customers

        return customerMapper.selectOne(queryWrapper);
    }
}
