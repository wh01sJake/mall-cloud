package com.intelijake.mall.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author Jake
 * @since 2025-06-23
 */
@Getter
@Setter
@TableName("shopping_cart")
public class ShoppingCart implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    /**
     * Product ID
     */
    private Integer productId;

    /**
     * Quantity
     */
    private Integer quantity;

    /**
     * Is selected in cart (1: checked, 0: unchecked)
     */
    private Integer isChecked;

    /**
     * Status (1: Active, 0: Disabled)
     */
    private Integer status;

    /**
     * Logical delete (1: deleted, 0: not deleted)
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * Creation time
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * Last update time
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
