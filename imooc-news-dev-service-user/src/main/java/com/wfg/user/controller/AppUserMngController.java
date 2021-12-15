package com.wfg.user.controller;

import com.wfg.api.BaseController;
import com.wfg.api.controller.user.AppUserMngControllerApi;
import com.wfg.api.controller.user.HelloControllerApi;
import com.wfg.enums.UserStatus;
import com.wfg.result.GraceJSONResult;
import com.wfg.result.PagedGridResult;
import com.wfg.result.ResponseStatusEnum;
import com.wfg.user.service.AppUserService;
import com.wfg.user.service.UserService;
import com.wfg.utils.RedisOperator;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-08 19:51
 */
@RestController
public class AppUserMngController extends BaseController implements  AppUserMngControllerApi {
  @Autowired
    AppUserService appUserService;
  @Autowired
  private UserService userService;

    @Override
    public GraceJSONResult queryAll(String nickname,
                                    Integer status,
                                    Date startDate,
                                    Date endDate,
                                    Integer page,
                                    Integer pageSize) {
       if(page ==null){
           page=COMMON_START_PAGE;
       }
       if(pageSize==null){
           pageSize=COMMON_PAGE_SIZE;
       }
      PagedGridResult pagedGridResult= appUserService.queryAllUserList(nickname, status, startDate, endDate, page, pageSize);


        return GraceJSONResult.ok(pagedGridResult);
    }

    @Override
    public GraceJSONResult userDetail(String userId) {
        return GraceJSONResult.ok(userService.getUser(userId));
    }

    @Override
    public GraceJSONResult freezeUserOrNot(String userId, Integer doStatus) {

        if(!UserStatus.isUserStatusValid(doStatus)){
          return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_STATUS_ERROR);
        }
        appUserService.freezeUserOrNot(userId,doStatus);
        //刷新用户状态（会话）两种方式:
        // 1. 删除用户会话，从而保障用户需要重新登录以后来刷新他会话（推荐）
        // 2.查询最新用户信息，重新放入到redis中做更新
        redis.del(REDIS_USER_INFO+":"+userId);

        return GraceJSONResult.ok();
    }
}
