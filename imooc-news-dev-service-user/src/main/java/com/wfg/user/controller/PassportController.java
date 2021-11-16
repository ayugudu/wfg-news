package com.wfg.user.controller;

import com.wfg.api.BaseController;
import com.wfg.api.controller.user.PassportControllerApi;
import com.wfg.pojo.bo.RegisterLoginBO;
import com.wfg.result.GraceJSONResult;
import com.wfg.result.ResponseStatusEnum;
import com.wfg.utils.IPUtil;
import com.wfg.utils.SMSUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @Override
    public GraceJSONResult getSMSCode(String mobile, HttpServletRequest request) {
        // 获取用户ip
        String ip = IPUtil.getRequestIp(request);
        //根据用户ip进行限制，用户在60秒内只能获得一次验证码
        redis.setnx60s(MOBILE_SMSCODE+":"+ip,ip);
        //生成6位验证吗
        String random = (int)((Math.random()*9+1)*100000)+"";
        //发送验证码
        smsUtils.sendSMS(mobile,random);
        // 发送成功后，将验证码存储redis,用于后期验证
        redis.set(MOBILE_SMSCODE+":"+mobile,random,5*60);
        return GraceJSONResult.ok();
    }




    @Override
    public GraceJSONResult doLogin(@RequestBody @Valid  RegisterLoginBO registerLoginBO,
                                   BindingResult result) {

       // 判断BindingResult 中是否保存了错误的验证信息,如果有则返回
        if(result.hasErrors()){
          Map<String,String> map = getErrors(result);
          return  GraceJSONResult.errorMap(map);
        }

        String mobile = registerLoginBO.getMobile();

        String smsCode = registerLoginBO.getSmsCode();
        // 校验验证码 是否匹配
        String redisSMSCode = redis.get(MOBILE_SMSCODE+":"+mobile);
        if(StringUtils.isBlank(redisSMSCode)||redisSMSCode!=smsCode){
           return  GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }
        return GraceJSONResult.ok();
    }



}
