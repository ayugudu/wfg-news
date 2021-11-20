package com.wfg.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wfg.pojo.AppUser;
import com.wfg.pojo.bo.UpdateUserInfoBO;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-16 12:28
 */
public interface UserService  {
    /**
     * 判断用户是否存在，返回user信息
     * @param mobile
     * @return
     */
    public AppUser  queryMobileIsExist(String mobile);

    /**
     * 创建新用户到数据库
     */
     public AppUser createUser(String mobile);

    /**
     * 根据用户主键id 查询用户信息
     */
    public AppUser getUser(String userId);

    /**
     * 用户修改信息，完善资料，并且激活
     */
    public void updateUserInfo(UpdateUserInfoBO updateUserInfoBO);



}
