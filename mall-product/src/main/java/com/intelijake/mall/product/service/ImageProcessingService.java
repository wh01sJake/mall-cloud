package com.intelijake.mall.product.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Image Processing Service Interface
 * Handles image resizing, optimization, and multi-variant uploads
 * 
 * @author Jake
 * @since 2025-08-03
 */
public interface ImageProcessingService {
    
    /**
     * Process and upload image with multiple variants
     * 
     * @param file The original image file
     * @param folder The S3 folder to upload to
     * @return Map containing URLs for all image variants
     * @throws Exception if processing or upload fails
     */
    Map<String, Object> processAndUploadImage(MultipartFile file, String folder) throws Exception;
    
    /**
     * Generate image variants (thumbnail, medium, large)
     * 
     * @param originalFile The original image file
     * @return Map containing processed image variants as byte arrays
     * @throws Exception if image processing fails
     */
    Map<String, byte[]> generateImageVariants(MultipartFile originalFile) throws Exception;
    
    /**
     * Validate image file
     * 
     * @param file The image file to validate
     * @throws IllegalArgumentException if validation fails
     */
    void validateImageFile(MultipartFile file) throws IllegalArgumentException;
}
