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
@TableName("shipping_address")
public class ShippingAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * User ID
     */
    private Integer userId;

    /**
     * County ID, foreign key to irish_counties table
     */
    private Integer countyId;

    /**
     * Recipient name
     */
    private String receiverName;

    /**
     * Recipient landline phone
     */
    private String receiverPhone;

    /**
     * Recipient mobile phone
     */
    private String receiverMobile;

    @TableField("address_line_1")
    private String addressLine1;

    @TableField("address_line_2")
    private String addressLine2;

    private String townCity;

    private String eircode;

    /**
     * Is this the default address? (1: Yes, 0: No)
     */
    private Integer isDefault;

    /**
     * Status (1: Active, 0: Inactive)
     */
    private Integer status;

    /**
     * Logical delete (1: deleted, 0: not deleted)
     */
    @TableLogic
    private Byte isDeleted;

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
