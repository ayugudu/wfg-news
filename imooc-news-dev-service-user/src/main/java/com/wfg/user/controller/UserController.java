package com.wfg.user.controller;

import com.wfg.api.BaseController;
import com.wfg.api.controller.user.UserControllerApi;
import com.wfg.pojo.AppUser;
import com.wfg.pojo.bo.UpdateUserInfoBO;

import com.wfg.pojo.vo.AppUserVO;
import com.wfg.result.GraceJSONResult;
import com.wfg.result.ResponseStatusEnum;
import com.wfg.user.service.UserService;
import com.wfg.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-16 21:05
 */
@RestController
public class UserController extends BaseController implements UserControllerApi {


    @Autowired
    UserService userService;




    @Override
    public GraceJSONResult getUserInfo(String userId) {
        // 判断参数不能为空
        if(StringUtils.isBlank(userId)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }
        // 根据userId 查询用户信息
        com.wfg.pojo.AppUser appUser = getUser(userId);
        AppUserVO appUserVO =new AppUserVO();
        BeanUtils.copyProperties(appUser,appUserVO);
        return GraceJSONResult.ok(appUserVO);
    }

    @Override
    public GraceJSONResult getAccountInfo(String userId) {
        // 判断参数不能为空
        if(StringUtils.isBlank(userId)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }
        // 根据userId 查询用户信息
        com.wfg.pojo.AppUser appUser = getUser(userId);

        // 返回用户信息
        AppUser accountInfoVO = new AppUser();
        //将数据库信息copy成视图信息
        BeanUtils.copyProperties(appUser,accountInfoVO);

        return GraceJSONResult.ok(accountInfoVO) ;
    }

    @Override
    public GraceJSONResult updateUserInfo(UpdateUserInfoBO updateUserInfoBO, BindingResult bindResult) {
       //校验bo
        if(bindResult.hasErrors()){
            Map<String,String> map = getErrors(bindResult);
            return  GraceJSONResult.errorMap(map);
        }
        //执行更新操作
        userService.updateUserInfo(updateUserInfoBO);


        return  GraceJSONResult.ok();
    }




    // TODO 本方法后续公用，并且扩展
    private com.wfg.pojo.AppUser getUser(String userId){
        AppUser user =null;
        // 查询判断redis中是否包含用户信息，如果包含，则查询后直接返回，就不去查询数据库了
        String userJson = redis.get(REDIS_USER_INFO+":"+userId);
        if(StringUtils.isNotBlank(userJson)){
            user =JsonUtils.jsonToPojo(userJson,AppUser.class);
        }
        else{
            user =userService.getUser(userId);

            // 由于用户信息不怎么动，因此我们可以不直接去查询数据库，可以依靠redis，把查询后的数据存入到Redis中
        redis.set(REDIS_USER_INFO+":"+userId, JsonUtils.objectToJson(user));
        }
        return  user;

    }



}

