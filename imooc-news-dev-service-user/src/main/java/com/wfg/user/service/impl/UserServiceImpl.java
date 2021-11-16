package com.wfg.user.service.impl;

import com.wfg.pojo.AppUser;
import com.wfg.user.mapper.AppUserMapper;
import com.wfg.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-16 12:29
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    public AppUserMapper appUserMapper;

    @Override
    public AppUser queryMobileIsExist(String mobile) {
        return null;
    }

    /**
     * 新增增加 事务
     * @param mobile
     * @return
     */
    @Transactional
    @Override
    public AppUser createUser(String mobile) {
        return null;
    }
}
