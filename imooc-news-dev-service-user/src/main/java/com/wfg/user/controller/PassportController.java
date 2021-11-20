package com.wfg.user.controller;

import com.wfg.api.BaseController;
import com.wfg.api.controller.user.PassportControllerApi;
import com.wfg.enums.UserStatus;
import com.wfg.pojo.AppUser;
import com.wfg.pojo.bo.RegisterLoginBO;
import com.wfg.result.GraceJSONResult;
import com.wfg.result.ResponseStatusEnum;
import com.wfg.user.service.UserService;
import com.wfg.utils.IPUtil;
import com.wfg.utils.SMSUtils;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-08 19:51
 */
@RestController

public class PassportController extends BaseController implements PassportControllerApi {

   final static Logger logger = LoggerFactory.getLogger(PassportController.class);

   @Autowired
   private SMSUtils smsUtils;
    @Autowired
    private UserService userService;

    @Override
    public GraceJSONResult getSMSCode(String mobile, HttpServletRequest request) {
        // 获取用户ip
        String ip = IPUtil.getRequestIp(request);
        //根据用户ip进行限制，用户在60秒内只能获得一次验证码
        redis.setnx60s(MOBILE_SMSCODE+":"+ip,ip);
        //生成6位验证吗
        String random = (int)((Math.random()*9+1)*100000)+"";
        //发送验证码
        //smsUtils.sendSMS(mobile,random);
        // 发送成功后，将验证码存储redis,用于后期验证
        redis.set(MOBILE_SMSCODE+":"+mobile,random,5*60);
        return GraceJSONResult.ok(random);
    }




    @Override
    public GraceJSONResult doLogin(@RequestBody @Valid  RegisterLoginBO registerLoginBO,
                                   BindingResult result,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

       // 判断BindingResult 中是否保存了错误的验证信息,如果有则返回
        if(result.hasErrors()){
          Map<String,String> map = getErrors(result);
          return  GraceJSONResult.errorMap(map);
        }

        String mobile = registerLoginBO.getMobile();

        String smsCode = registerLoginBO.getSmsCode();
        // 1 校验验证码 是否匹配
        String redisSMSCode = redis.get(MOBILE_SMSCODE+":"+mobile);
        if(StringUtils.isBlank(redisSMSCode)||!redisSMSCode.equals(smsCode)){
           return  GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        // 2 判断用户是否注册
         AppUser user= userService.queryMobileIsExist(mobile);
         if(user!=null && user.getActiveStatus()== UserStatus.FROZEN.type){
              // 如果用户不为空，并且状态为冻结，则抛出异常
             return  GraceJSONResult.errorCustom(ResponseStatusEnum.USER_FROZEN);
         }else if(user==null){
            // 如果用户没有注册过，则为null 需要注册信息入库
             user = userService.createUser(mobile);
         }

         // 3 保存用户分布式会话相关操作
        int userActiveStatus=  user.getActiveStatus();
         if(userActiveStatus!= UserStatus.FROZEN.type){
             // 保存token到redis中
             String uToKen = UUID.randomUUID().toString();
             redis.set(REDIS_USER_TOKEN+":"+user.getId(),uToKen);

             // 保存用户id和token 到cookie中
             setCookie(request,response,"utoken",uToKen,COOKIE_MONTH);
             setCookie(request,response,"uid",user.getId(),COOKIE_MONTH);

         }
         // 4 用户登录或注册以后，删除redis中验证码
        redis.del(MOBILE_SMSCODE+":"+mobile);


        return GraceJSONResult.ok(userActiveStatus);
    }

    @Override
    public GraceJSONResult logout(String userId, HttpServletRequest request, HttpServletResponse response) {
        redis.del(REDIS_USER_TOKEN+":"+userId);
        //清除cookie ，只需要设置过期时间为0
        setCookie(request,response,"utoken","",COOKIE_DELETE );
        setCookie(request,response,"uid","",COOKIE_DELETE);
        return GraceJSONResult.ok();
    }
}
