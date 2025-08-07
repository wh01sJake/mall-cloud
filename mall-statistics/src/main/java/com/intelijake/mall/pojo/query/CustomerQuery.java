package com.intelijake.mall.pojo.query;

import lombok.Data;

@Data
public class CustomerQuery {
    private Integer page;
    private Integer limit;
    private String username;
    private String email;
    private String phone;
}