package com.intelijake.mall.product.controller;

import com.intelijake.mall.common.util.AWSUtil;
import com.intelijake.mall.common.util.Result;
import com.intelijake.mall.product.service.ImageProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * File Upload Controller
 * Handles image uploads for products and users using AWS S3
 * 
 * @author Jake
 * @since 2025-07-27
 */
@RestController
@RequestMapping("/upload")
@CrossOrigin
public class UploadController {

    @Autowired(required = false)
    private AWSUtil awsUtil;

    @Autowired
    private ImageProcessingService imageProcessingService;

    /**
     * Upload product image to AWS S3
     * 
     * @param file The image file to upload
     * @return Result containing the uploaded file URL
     */
    @PostMapping("/product")
    public Result uploadProductImage(@RequestParam("file") MultipartFile file) {
        return uploadImage(file, "products");
    }

    /**
     * Upload user avatar to AWS S3
     *
     * @param file The image file to upload
     * @return Result containing the uploaded file URL
     */
    @PostMapping("/user")
    public Result uploadUserAvatar(@RequestParam("file") MultipartFile file) {
        return uploadImage(file, "users");
    }

    /**
     * Generic upload endpoint for backward compatibility
     * Defaults to general uploads folder
     *
     * @param file The image file to upload
     * @return Result containing the uploaded file URL
     */
    @PostMapping("")
    public Result uploadGeneric(@RequestParam("file") MultipartFile file) {
        return uploadImage(file, "general");
    }

    /**
     * Upload product image with automatic resizing and multiple variants
     * Generates thumbnail (150x150), medium (400x400), large (800x800), and keeps original
     *
     * @param file The image file to upload
     * @return Result containing URLs for all image variants
     */
    @PostMapping("/product/enhanced")
    public Result uploadProductImageEnhanced(@RequestParam("file") MultipartFile file) {
        return uploadImageWithProcessing(file, "products");
    }

    /**
     * Upload user avatar with automatic resizing and multiple variants
     *
     * @param file The image file to upload
     * @return Result containing URLs for all image variants
     */
    @PostMapping("/user/enhanced")
    public Result uploadUserAvatarEnhanced(@RequestParam("file") MultipartFile file) {
        return uploadImageWithProcessing(file, "users");
    }

    /**
     * Generic image upload method
     * 
     * @param file The image file to upload
     * @param folder The S3 folder to upload to
     * @return Result containing the uploaded file URL
     */
    private Result uploadImage(MultipartFile file, String folder) {
        try {
            // Validate file
            if (file == null || file.isEmpty()) {
                return Result.error("Please select a file to upload");
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("Only image files are allowed");
            }

            // Validate file size (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return Result.error("File size must be less than 5MB");
            }

            // Check if AWS S3 is configured
            if (awsUtil == null) {
                return Result.error("File upload service is not configured");
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = folder + "/" + UUID.randomUUID().toString() + fileExtension;

            // Upload to S3
            String fileUrl = awsUtil.uploadFile(uniqueFilename, file.getInputStream(), file.getSize());

            // Return success response
            Map<String, Object> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", uniqueFilename);
            result.put("originalName", originalFilename);
            result.put("size", file.getSize());
            result.put("contentType", contentType);

            return Result.ok(result);

        } catch (IOException e) {
            return Result.error("Failed to read file: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("Upload failed: " + e.getMessage());
        }
    }

    /**
     * Enhanced image upload with automatic processing and multiple variants
     *
     * @param file The image file to upload
     * @param folder The S3 folder to upload to
     * @return Result containing URLs for all image variants
     */
    private Result uploadImageWithProcessing(MultipartFile file, String folder) {
        try {
            // Use the image processing service to handle validation, processing, and upload
            Map<String, Object> result = imageProcessingService.processAndUploadImage(file, folder);

            return Result.ok(result);

        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("Image processing failed: " + e.getMessage());
        }
    }

    /**
     * Delete uploaded file from S3
     * 
     * @param filename The filename to delete
     * @return Result indicating success or failure
     */
    @DeleteMapping("/delete")
    public Result deleteFile(@RequestParam("filename") String filename) {
        try {
            if (awsUtil == null) {
                return Result.error("File upload service is not configured");
            }

            awsUtil.deleteFile(filename);
            return Result.ok("File deleted successfully");

        } catch (Exception e) {
            return Result.error("Failed to delete file: " + e.getMessage());
        }
    }

    /**
     * Get upload configuration and limits
     * 
     * @return Result containing upload configuration
     */
    @GetMapping("/config")
    public Result getUploadConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("maxFileSize", "5MB");
        config.put("allowedTypes", new String[]{"image/jpeg", "image/png", "image/gif"});
        config.put("s3Configured", awsUtil != null);
        
        return Result.ok(config);
    }
}
