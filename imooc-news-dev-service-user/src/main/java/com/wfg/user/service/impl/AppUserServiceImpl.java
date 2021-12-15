package com.wfg.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.wfg.enums.UserStatus;

import com.wfg.pojo.AppUser;

import com.wfg.result.PagedGridResult;

import com.wfg.user.mapper.AppUserMapper;
import com.wfg.user.service.AppUserService;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-16 12:29
 */
@Service
public class AppUserServiceImpl implements AppUserService {
       @Autowired
       public AppUserMapper appUserMapper;

    @Override
    public PagedGridResult queryAllUserList(String nickname,
                                            Integer status,
                                            Date startDate,
                                            Date endDate, Integer page, Integer pageSize) {
       QueryWrapper<AppUser> wrapper = new QueryWrapper<>();
       wrapper.orderByDesc("created_time");
       if(StringUtils.isNotBlank(nickname)){
           wrapper.like("nickname",nickname);
       }
       if(UserStatus.isUserStatusValid(status)){
            wrapper.eq("active_status",status);
       }
       if(startDate!=null){
           wrapper.ge("created_time",startDate);
       }
       if(endDate!=null){
           wrapper.le("created_time",endDate);
       }
        Page<AppUser>  pageMp = new Page<>(page,pageSize);
        Page<AppUser> appUserPage = appUserMapper.selectPage(pageMp, wrapper);

        return new PagedGridResult(appUserPage);
    }


    @Transactional
    @Override
    public void freezeUserOrNot(String userId, Integer doStatus) {
        AppUser user = new AppUser();
        user.setId(userId);
        user.setActiveStatus(doStatus);
        appUserMapper.updateById(user);
    }
}
