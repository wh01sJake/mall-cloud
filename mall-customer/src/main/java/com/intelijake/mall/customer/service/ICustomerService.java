package com.intelijake.mall.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelijake.mall.pojo.Customer;
import com.intelijake.mall.pojo.query.CustomerQuery;


/**
 * <p>
 * Customer Service Interface
 * Enhanced with portal functionality for authentication and customer management
 * </p>
 *
 * @author Jake
 * @since 2025-06-11
 */
public interface ICustomerService extends IService<Customer> {

    /**
     * Get paginated customer list with search criteria (Admin functionality)
     * @param customerQuery Query parameters for filtering and pagination
     * @return Paginated customer list
     */
    IPage<Customer> list(CustomerQuery customerQuery);

    /**
     * Customer login authentication
     * @param username Customer username
     * @param password Customer password
     * @return Customer object if login successful, null otherwise
     */
    Customer login(String username, String password);

    /**
     * Find customer by username
     * @param username Customer username
     * @return Customer object if found, null otherwise
     */
    Customer findByUsername(String username);

    /**
     * Find customer by email
     * @param email Customer email
     * @return Customer object if found, null otherwise
     */
    Customer findByEmail(String email);
}
