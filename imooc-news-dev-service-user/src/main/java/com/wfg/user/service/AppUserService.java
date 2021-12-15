package com.wfg.user.service;

import com.wfg.pojo.AppUser;
import com.wfg.pojo.bo.UpdateUserInfoBO;
import com.wfg.result.PagedGridResult;

import java.util.Date;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-16 12:28
 */
public interface AppUserService {

    /**
     * 查询管理员列表
     * @param nickname
     * @param status
     * @param startDate
     * @param endDate
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryAllUserList(String nickname,
                                        Integer status,
                                        Date startDate,
                                        Date endDate,
                                        Integer page,
                                        Integer pageSize);


    public void freezeUserOrNot(String userId,Integer doStatus);
}
