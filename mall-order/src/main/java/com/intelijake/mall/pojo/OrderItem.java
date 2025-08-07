package com.intelijake.mall.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
public class OrderItem implements Serializable {


    /**
     * Order item sub-table id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private Integer userId;

    @TableField("order_no")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderNo;

    /**
     * Product ID
     */
    @TableField("product_id")
    private Integer productId;

    /**
     * Product name
     */
    @TableField("product_name")
    private String productName;

    /**
     * Product image URL
     */
    @TableField("product_image")
    private String productImage;

    /**
     * Unit price at order creation, in EUR, with two decimal places
     */
    @TableField("current_unit_price")
    private BigDecimal currentUnitPrice;

    /**
     * Product quantity
     */
    private Integer quantity;

    /**
     * Product total price, in EUR, with two decimal places
     */
    @TableField("total_price")
    private BigDecimal totalPrice;

    /**
     * Status (1: Active, 0: Disabled)
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
