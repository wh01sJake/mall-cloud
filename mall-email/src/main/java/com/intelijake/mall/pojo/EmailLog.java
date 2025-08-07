package com.intelijake.mall.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * Email Log Entity
 * </p>
 *
 * @author Jake
 * @since 2025-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("email_log")
public class EmailLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Email Log ID - Primary Key (Auto Increment)
     */
    @TableId(value = "id", type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * Email Type (order-confirmation, welcome, admin-notification)
     */
    @TableField("email_type")
    private String emailType;

    /**
     * Recipient Email Address
     */
    @TableField("recipient_email")
    private String recipientEmail;

    /**
     * Recipient Name
     */
    @TableField("recipient_name")
    private String recipientName;

    /**
     * Email Subject
     */
    @TableField("subject")
    private String subject;

    /**
     * Email Content (HTML)
     */
    @TableField("content")
    private String content;

    /**
     * Send Status (0: Failed, 1: Success, 2: Pending)
     */
    @TableField("status")
    private Integer status;

    /**
     * External Email ID (from SendGrid)
     */
    @TableField("external_email_id")
    private String externalEmailId;

    /**
     * Error Message (if failed)
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * Related Order Number (if applicable)
     */
    @TableField("order_no")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderNo;

    /**
     * Retry Count
     */
    @TableField("retry_count")
    private Integer retryCount;

    /**
     * Soft Delete Flag (0: Not Deleted, 1: Deleted)
     */
    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;

    /**
     * Creation Time
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * Update Time
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
