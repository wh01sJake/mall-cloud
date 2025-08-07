package com.intelijake.mall.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
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
public class ProductCategory implements Serializable {


    /**
     * Category ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * Parent category ID. 0 indicates a root category.
     */
    @TableField("parent_id")
    private Integer parentId;

    /**
     * Category name
     */
    private String name;

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
