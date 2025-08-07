package com.intelijake.mall.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelijake.mall.admin.pojo.query.AdminQuery;
import com.intelijake.mall.pojo.Admin;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Jake
 * @since 2025-06-11
 */
public interface IAdminService extends IService<Admin> {

    IPage<Admin> list(AdminQuery adminQuery);
}
