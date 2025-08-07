package com.intelijake.mall.product.service.impl;

import com.intelijake.mall.common.util.AWSUtil;
import com.intelijake.mall.product.service.ImageProcessingService;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Image Processing Service Implementation
 * Handles image resizing, optimization, and multi-variant uploads
 * 
 * @author Jake
 * @since 2025-08-03
 */
@Service
public class ImageProcessingServiceImpl implements ImageProcessingService {

    @Autowired(required = false)
    private AWSUtil awsUtil;

    // Image size configurations
    private static final int THUMBNAIL_SIZE = 150;
    private static final int MEDIUM_SIZE = 400;
    private static final int LARGE_SIZE = 800;
    private static final float QUALITY = 0.8f;

    @Override
    public Map<String, Object> processAndUploadImage(MultipartFile file, String folder) throws Exception {
        // Validate the image file
        validateImageFile(file);

        if (awsUtil == null) {
            throw new RuntimeException("AWS S3 service is not configured");
        }

        // Generate unique base filename
        String originalFilename = file.getOriginalFilename();
        String originalExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            originalExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String baseFilename = UUID.randomUUID().toString();

        // Determine optimal output format and extension for variants
        String outputFormat = getOptimalOutputFormat(file.getContentType());
        String variantExtension = "." + outputFormat;

        // Generate image variants
        Map<String, byte[]> variants = generateImageVariants(file, outputFormat);

        // Upload all variants to S3
        Map<String, Object> result = new HashMap<>();
        Map<String, String> urls = new HashMap<>();

        // Upload original (preserve original format)
        String originalKey = folder + "/original/" + baseFilename + originalExtension;
        String originalUrl = awsUtil.uploadFile(originalKey, file.getInputStream(), file.getSize());
        urls.put("original", originalUrl);

        // Upload variants (use optimized format)
        for (Map.Entry<String, byte[]> entry : variants.entrySet()) {
            String variantName = entry.getKey();
            byte[] imageData = entry.getValue();

            String variantKey = folder + "/" + variantName + "/" + baseFilename + variantExtension;
            String variantUrl = awsUtil.uploadFile(variantKey, 
                new ByteArrayInputStream(imageData), imageData.length);
            urls.put(variantName, variantUrl);
        }

        // Build response
        result.put("urls", urls);
        result.put("baseFilename", baseFilename);
        result.put("originalName", originalFilename);
        result.put("originalSize", file.getSize());
        result.put("contentType", file.getContentType());
        result.put("variants", variants.keySet());

        return result;
    }

    @Override
    public Map<String, byte[]> generateImageVariants(MultipartFile originalFile) throws Exception {
        String outputFormat = getOptimalOutputFormat(originalFile.getContentType());
        return generateImageVariants(originalFile, outputFormat);
    }

    private Map<String, byte[]> generateImageVariants(MultipartFile originalFile, String outputFormat) throws Exception {
        Map<String, byte[]> variants = new HashMap<>();

        try {
            // Generate thumbnail (150x150)
            ByteArrayOutputStream thumbnailOutput = new ByteArrayOutputStream();
            Thumbnails.of(originalFile.getInputStream())
                    .size(THUMBNAIL_SIZE, THUMBNAIL_SIZE)
                    .outputQuality(QUALITY)
                    .outputFormat(outputFormat)
                    .toOutputStream(thumbnailOutput);
            variants.put("thumbnail", thumbnailOutput.toByteArray());

            // Generate medium (400x400)
            ByteArrayOutputStream mediumOutput = new ByteArrayOutputStream();
            Thumbnails.of(originalFile.getInputStream())
                    .size(MEDIUM_SIZE, MEDIUM_SIZE)
                    .outputQuality(QUALITY)
                    .outputFormat(outputFormat)
                    .toOutputStream(mediumOutput);
            variants.put("medium", mediumOutput.toByteArray());

            // Generate large (800x800)
            ByteArrayOutputStream largeOutput = new ByteArrayOutputStream();
            Thumbnails.of(originalFile.getInputStream())
                    .size(LARGE_SIZE, LARGE_SIZE)
                    .outputQuality(QUALITY)
                    .outputFormat(outputFormat)
                    .toOutputStream(largeOutput);
            variants.put("large", largeOutput.toByteArray());

            return variants;

        } catch (IOException e) {
            throw new Exception("Failed to process image variants: " + e.getMessage(), e);
        }
    }

    @Override
    public void validateImageFile(MultipartFile file) throws IllegalArgumentException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        // Validate supported image formats
        // Note: WebP support temporarily disabled due to ImageIO limitations
        if (!contentType.equals("image/jpeg") &&
            !contentType.equals("image/jpg") &&
            !contentType.equals("image/png") &&
            !contentType.equals("image/gif")) {
            throw new IllegalArgumentException("Supported formats: JPEG, PNG, GIF (WebP support coming soon)");
        }

        // Validate file size (max 10MB for processing)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size must be less than 10MB");
        }

        // Validate filename
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid filename");
        }
    }

    /**
     * Get image variant URL by base filename and variant type
     * 
     * @param baseFilename The base filename
     * @param variant The variant type (thumbnail, medium, large, original)
     * @param folder The S3 folder
     * @param extension The file extension
     * @return The full S3 URL for the variant
     */
    public String getVariantUrl(String baseFilename, String variant, String folder, String extension) {
        if (awsUtil == null) {
            return null;
        }
        
        String bucketName = "jake-mall-bucket"; // This should be configurable
        String region = "eu-west-1"; // This should be configurable
        String key = folder + "/" + variant + "/" + baseFilename + extension;
        
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
    }

    /**
     * Determine the optimal output format based on input content type
     * Note: Thumbnailator supports JPG, PNG, GIF, BMP for output (not WebP)
     * WebP input is accepted but converted to JPG for processing
     */
    private String getOptimalOutputFormat(String contentType) {
        if (contentType == null) {
            return "jpg"; // Default fallback
        }

        switch (contentType.toLowerCase()) {
            case "image/png":
                return "png"; // Preserve PNG for transparency
            case "image/gif":
                return "gif"; // Preserve GIF for animations
            case "image/webp":
                return "jpg"; // Convert WebP to JPG (Thumbnailator doesn't support WebP output)
            case "image/jpeg":
            case "image/jpg":
            default:
                return "jpg"; // Convert everything else to JPG for compatibility
        }
    }
}
