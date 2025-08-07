package com.intelijake.mall.customer.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.intelijake.mall.common.context.UserContext;
import com.intelijake.mall.common.util.Result;
import com.intelijake.mall.customer.service.IShippingAddressService;
import com.intelijake.mall.pojo.Customer;
import com.intelijake.mall.pojo.ShippingAddress;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * Shipping Address Management Controller
 * </p>
 *
 * @author Jake
 * @since 2025-07-22
 */
@RestController
@RequestMapping("/customer/address")
public class ShippingAddressController {

    @Autowired
    private IShippingAddressService shippingAddressService;

    /**
     * Get all shipping addresses for the current customer
     */
    @GetMapping("/list")
    public Result<List<ShippingAddress>> list() {
        Integer currentUserId = UserContext.requireCurrentUserId();

        QueryWrapper<ShippingAddress> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", currentUserId);
        wrapper.eq("is_deleted", 0); // Only show non-deleted addresses
        wrapper.orderByDesc("is_default").orderByDesc("create_time");

        List<ShippingAddress> list = shippingAddressService.list(wrapper);
        return Result.ok(list);
    }

    /**
     * Get shipping addresses for a specific customer (admin endpoint)
     */
    @GetMapping("/list/{customerId}")
    public Result<List<ShippingAddress>> listByCustomerId(@PathVariable Integer customerId) {
        QueryWrapper<ShippingAddress> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", customerId);
        wrapper.eq("is_deleted", 0);
        wrapper.orderByDesc("is_default").orderByDesc("create_time");

        List<ShippingAddress> list = shippingAddressService.list(wrapper);
        return Result.ok(list);
    }

    /**
     * Add new shipping address
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody ShippingAddress address) {
        Integer currentUserId = UserContext.requireCurrentUserId();
        address.setUserId(currentUserId);

        // If this is set as default, unset other default addresses
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            QueryWrapper<ShippingAddress> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", currentUserId);
            wrapper.eq("is_default", 1);

            List<ShippingAddress> defaultAddresses = shippingAddressService.list(wrapper);
            for (ShippingAddress defaultAddr : defaultAddresses) {
                defaultAddr.setIsDefault(0);
                shippingAddressService.updateById(defaultAddr);
            }
        }

        boolean success = shippingAddressService.save(address);

        if (success) {
            return Result.ok("Address added successfully");
        } else {
            return Result.error("Failed to add address");
        }
    }

    /**
     * Update shipping address
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody ShippingAddress address) {
        Integer currentUserId = UserContext.requireCurrentUserId();

        // Verify the address belongs to the current user
        ShippingAddress existingAddress = shippingAddressService.getById(address.getId());
        if (existingAddress == null || !existingAddress.getUserId().equals(currentUserId)) {
            return Result.error("Address not found");
        }

        address.setUserId(currentUserId);

        // If this is set as default, unset other default addresses
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            QueryWrapper<ShippingAddress> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", currentUserId);
            wrapper.eq("is_default", 1);
            wrapper.ne("id", address.getId());

            List<ShippingAddress> defaultAddresses = shippingAddressService.list(wrapper);
            for (ShippingAddress defaultAddr : defaultAddresses) {
                defaultAddr.setIsDefault(0);
                shippingAddressService.updateById(defaultAddr);
            }
        }

        boolean success = shippingAddressService.updateById(address);

        if (success) {
            return Result.ok("Address updated successfully");
        } else {
            return Result.error("Failed to update address");
        }
    }

    /**
     * Delete shipping address
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        Integer currentUserId = UserContext.requireCurrentUserId();

        // Verify the address belongs to the current user
        ShippingAddress address = shippingAddressService.getById(id);
        if (address == null || !address.getUserId().equals(currentUserId)) {
            return Result.error("Address not found");
        }

        boolean success = shippingAddressService.removeById(id);

        if (success) {
            return Result.ok("Address deleted successfully");
        } else {
            return Result.error("Failed to delete address");
        }
    }

    /**
     * Get shipping address by ID
     */
    @GetMapping("/selectById/{id}")
    public Result<ShippingAddress> selectById(@PathVariable Integer id) {
        Integer currentUserId = UserContext.requireCurrentUserId();

        ShippingAddress address = shippingAddressService.getById(id);

        // Verify the address belongs to the current user
        if (address == null || !address.getUserId().equals(currentUserId)) {
            return Result.error("Address not found");
        }

        return Result.ok(address);
    }

    /**
     * Set address as default
     */
    @PutMapping("/setDefault/{id}")
    public Result<String> setDefault(@PathVariable Integer id) {
        Integer currentUserId = UserContext.requireCurrentUserId();

        // Verify the address belongs to the current user
        ShippingAddress address = shippingAddressService.getById(id);
        if (address == null || !address.getUserId().equals(currentUserId)) {
            return Result.error("Address not found");
        }

        // Unset other default addresses
        QueryWrapper<ShippingAddress> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", currentUserId);
        wrapper.eq("is_default", 1);

        List<ShippingAddress> defaultAddresses = shippingAddressService.list(wrapper);
        for (ShippingAddress defaultAddr : defaultAddresses) {
            defaultAddr.setIsDefault(0);
            shippingAddressService.updateById(defaultAddr);
        }

        // Set this address as default
        address.setIsDefault(1);
        boolean success = shippingAddressService.updateById(address);

        if (success) {
            return Result.ok("Default address set successfully");
        } else {
            return Result.error("Failed to set default address");
        }
    }

    /**
     * Get default shipping address for customer
     */
    @GetMapping("/default")
    public Result<ShippingAddress> getDefaultAddress() {
        Integer currentUserId = UserContext.requireCurrentUserId();

        QueryWrapper<ShippingAddress> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", currentUserId);
        wrapper.eq("is_default", 1);
        wrapper.eq("is_deleted", 0);

        ShippingAddress defaultAddress = shippingAddressService.getOne(wrapper);

        if (defaultAddress != null) {
            return Result.ok(defaultAddress);
        } else {
            return Result.error("No default address found");
        }
    }
}
