package com.wfg.admin.service;

import com.wfg.pojo.AdminUser;
import com.wfg.pojo.bo.NewAdminBO;
import com.wfg.result.PagedGridResult;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-22 17:17
 */
public interface AdminUserService {

    /**
     * 获取管理员信息
     * @param userName
     * @return
     */
    public AdminUser queryAdminByUsername(String userName);

    /**
     * 新增管理员
     */
    public void createAdminUser(NewAdminBO newAdminBO);

    /**
     * 分页查询admin列表
     */
    public PagedGridResult queryAdminList(Integer page, Integer pageSize);
}
