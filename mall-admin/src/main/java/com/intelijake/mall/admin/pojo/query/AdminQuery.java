package com.intelijake.mall.admin.pojo.query;

import lombok.Data;

@Data
public class AdminQuery {
    private Integer page;
    private Integer limit;
    private String username;
    private String email;
}
