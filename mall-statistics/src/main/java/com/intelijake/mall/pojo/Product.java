package com.intelijake.mall.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author Jake
 * @since 2025-06-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Product implements Serializable {


    /**
     * Product ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * Category ID, foreign key to product_category table
     */
    @TableField("category_id")
    private Integer categoryId;

    /**
     * Product name
     */
    private String name;

    /**
     * Product subtitle
     */
    private String subtitle;

    /**
     * Main product image, relative URL
     */
    @TableField("main_image")
    private String mainImage;

    /**
     * Image URLs, JSON format, for additional images
     */
    @TableField("sub_images")
    private String subImages;

    /**
     * Product details
     */
    private String detail;

    /**
     * Price in EUR, with two decimal places
     */
    private BigDecimal price;

    /**
     * Stock quantity
     */
    private Integer stock;

    /**
     * Product status: 1-On Sale, 0-Delisted
     */
    private Integer status;

    /**
     * Logical delete (1: deleted, 0: not deleted)
     */
    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;

    /**
     * Creation time
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * Last update time
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
