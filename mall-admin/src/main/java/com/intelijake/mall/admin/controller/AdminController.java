package com.intelijake.mall.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.intelijake.mall.admin.pojo.dto.AdminPasswordDTO;
import com.intelijake.mall.admin.pojo.query.AdminQuery;
import com.intelijake.mall.admin.service.IAdminService;
import com.intelijake.mall.common.constant.RedisConstants;
import com.intelijake.mall.common.context.UserContext;
import com.intelijake.mall.common.util.JwtUtil;
import com.intelijake.mall.common.util.PasswordUtil;
import com.intelijake.mall.common.util.Result;
import com.intelijake.mall.pojo.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  Frontend Controller
 * </p>
 *
 * @author Jake
 * @since 2025-06-11
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private IAdminService adminService;

    @Autowired
    private RedisTemplate redisTemplate;




    @GetMapping("/list")
    public Result list(AdminQuery adminQuery) {

        IPage<Admin> page = adminService.list(adminQuery);
        return Result.ok(page);
    }

//    @MyLog(module = "admin module")
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        adminService.removeById(id);
        return Result.ok("Deleted successfully");
    }

//    @MyLog(module = "admin module")
    @DeleteMapping("/deleteAll/{ids}")
    public Result deleteAll(@PathVariable Integer[] ids) {
        adminService.removeBatchByIds(Arrays.asList(ids));
        return Result.ok("Deleted successfully");
    }


    @PostMapping("/add")
    public Result add(@RequestBody Admin admin) {
        // Set default status if not provided
        if (admin.getStatus() == null) {
            admin.setStatus(1); // Default to active
        }

        // Hash the password before saving
        if (admin.getPassword() != null && !admin.getPassword().trim().isEmpty()) {
            admin.setPassword(PasswordUtil.hashPassword(admin.getPassword()));
            System.out.println("🔐 Admin password hashed for: " + admin.getUsername());
        }

        adminService.save(admin);

        // Try to add to Redis, but don't fail if Redis is unavailable
        try {
            if (admin.getAvatar() != null) {
                redisTemplate.opsForSet().add(RedisConstants.UPLOAD_IMAGE_TO_DB, admin.getAvatar());
            }
        } catch (Exception e) {
            // Log the error but don't fail the operation
            System.err.println("Redis operation failed: " + e.getMessage());
        }

        return Result.ok("Added successfully");
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Admin admin = adminService.getById(id);
        return Result.ok(admin);
    }

    @PutMapping("/update")
    public Result update(@RequestBody Admin admin) {
        adminService.updateById(admin);

        // Try to add to Redis, but don't fail if Redis is unavailable
        try {
            if (admin.getAvatar() != null) {
                redisTemplate.opsForSet().add(RedisConstants.UPLOAD_IMAGE_TO_DB, admin.getAvatar());
            }
        } catch (Exception e) {
            // Log the error but don't fail the operation
            System.err.println("Redis operation failed: " + e.getMessage());
        }

        return Result.ok("Updated successfully");
    }

    @PostMapping("/login")
    public Result login(@RequestBody Admin admin) {
        // First, find the admin by username
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", admin.getUsername());
        Admin loginAdmin = adminService.getOne(queryWrapper);

        if (loginAdmin == null) {
            System.out.println("❌ Admin not found: " + admin.getUsername());
            return Result.error("Invalid username or password");
        }

        // Verify password using BCrypt
        if (!PasswordUtil.verifyPassword(admin.getPassword(), loginAdmin.getPassword())) {
            System.out.println("❌ Invalid password for admin: " + admin.getUsername());
            return Result.error("Invalid username or password");
        }

        System.out.println("✅ Admin login successful: " + admin.getUsername());

        // Generate JWT token using new system
        String token = JwtUtil.generateToken(loginAdmin.getId(), loginAdmin.getUsername(), null, 1);
        String refreshToken = JwtUtil.generateRefreshToken(loginAdmin.getId());

        // Create response with token and admin information
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("refreshToken", refreshToken);
        result.put("admin", Map.of(
            "id", loginAdmin.getId(),
            "username", loginAdmin.getUsername(),
            "email", loginAdmin.getEmail() != null ? loginAdmin.getEmail() : ""
        ));
        result.put("message", "Login successful");

        return Result.ok(result);
    }

    @GetMapping("/adminInfo")
    public Result adminInfo() {
        try {
            // Get current user from JWT context
            Integer currentUserId = UserContext.requireCurrentUserId();
            Admin admin = adminService.getById(currentUserId);
            return Result.ok(admin);
        } catch (RuntimeException e) {
            return Result.error("Please login first");
        }
    }

    @PutMapping("/resetPassword")
    public Result resetPassword(@RequestBody AdminPasswordDTO adminPasswordDTO,
                                @RequestHeader(name = "Authorization") String token) {

        Map<String, Object> map = JwtUtil.parseToken(token);

        int id = (int) map.get("id");
        Admin admin = adminService.getById(id);
        if (!admin.getPassword().equalsIgnoreCase(adminPasswordDTO.getOldPassword())) {
            return Result.error("Incorrect old password");
        }

        Admin resetPasswordAdmin = new Admin();
        resetPasswordAdmin.setId(id);
        resetPasswordAdmin.setPassword(adminPasswordDTO.getNewPassword());
        adminService.updateById(resetPasswordAdmin);
        return Result.ok("Password changed successfully");
    }

//    @MyLog(module = "admin modulle")
    @PutMapping("/updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Integer id, @PathVariable Integer status) {
        Admin admin = new Admin();
        admin.setId(id);
        admin.setStatus(status);
        adminService.updateById(admin);
        
        String statusText = status == 1 ? "activated" : "blocked";
        return Result.ok("Admin " + statusText + " successfully");
    }




}

