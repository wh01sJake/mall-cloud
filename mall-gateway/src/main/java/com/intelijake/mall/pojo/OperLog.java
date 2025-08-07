package com.intelijake.mall.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * Operation Log Record
 * </p>
 *
 * @author Jake
 * @since 2025-06-16
 */
@Getter
@Setter
@TableName("oper_log")
public class OperLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * Module Title
     */
    private String module;

    /**
     * Log Type
     */
    private String logType;

    /**
     * Operator ID
     */
    private Integer adminId;

    /**
     * Operator Name
     */
    private String adminName;

    /**
     * Request Method (GET/POST)
     */
    private String requestMethod;

    /**
     * Request URI
     */
    private String requestUri;

    /**
     * Request Parameters
     */
    private String requestParams;

    /**
     * Response Parameters
     */
    private String responseParams;

    /**
     * Host Address
     */
    private String requestIp;

    /**
     * Request Server Address
     */
    private String serverAddress;

    /**
     * Is Exception (1 for yes, 0 for no)
     */
    private Integer exception;

    /**
     * Exception Message
     */
    private String exceptionMsg;

    /**
     * Start Time
     */
    private Date startTime;

    /**
     * End Time
     */
    private Date endTime;

    /**
     * Execution Time
     */
    private Integer executeTime;

    /**
     * User Agent
     */
    private String userAgent;

    /**
     * Operating System
     */
    private String deviceName;

    /**
     * Browser Name
     */
    private String browserName;

    /**
     * Logical Delete (1 for deleted, 0 for not deleted)
     */
    private Byte deleted;

    /**
     * Creation Time
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * Update Time
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
