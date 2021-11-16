package com.wfg.user.service;

import com.wfg.pojo.AppUser;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-16 12:28
 */
public interface UserService {
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
}
