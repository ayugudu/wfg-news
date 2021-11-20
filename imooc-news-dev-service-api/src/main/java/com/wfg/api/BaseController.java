package com.wfg.api;

import com.wfg.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-13 15:21
 */
public class BaseController {

    @Autowired
    protected RedisOperator redis;

    public static final String MOBILE_SMSCODE="mobile_smscode";
    public static final String REDIS_USER_TOKEN="redis_user_token";


    public static final Integer COOKIE_MONTH=30*24*60*60;
    public static final Integer COOKIE_DELETE=0;
    public static final String  REDIS_USER_INFO="redis_user_info";

    @Value("${website.domain-name}")
    public String DOMAIN_NAME;
    /**
     * 获取BO中的错误信息
     * @param result
     */
    public Map<String,String> getErrors(BindingResult result){

        Map<String,String> map = new HashMap<>();

        List<FieldError> errorList = result.getFieldErrors();

        for(FieldError error : errorList){
            // 验证错误时 所对应的某个属性
            String field = error.getField();
            // 验证的错误消息
            String msg = error.getDefaultMessage();

            map.put(field,msg);

        }

        return  map;
    }

    /**
     * 解决分布式存储session 问题 ：redis+cookie
     */
     public void setCookie(HttpServletRequest request,
                           HttpServletResponse response,
                           String  cookieName,
                           String  cookieValue,
                           Integer maxAge
                           ){
         try {
             cookieValue = URLEncoder.encode(cookieValue,"utf-8");
             setCookieValue(request, response, cookieName, cookieValue, maxAge);
         } catch (UnsupportedEncodingException e) {
             e.printStackTrace();
         }

     }

    public void setCookieValue(HttpServletRequest request,
                          HttpServletResponse response,
                          String  cookieName,
                          String  cookieValue,
                          Integer maxAge
    ){
        //填充cookie
        Cookie cookie = new Cookie(cookieName,cookieValue);
        cookie.setMaxAge(maxAge);
        // 跨域共享设置 相同域名
        cookie.setDomain(DOMAIN_NAME);
        cookie.setPath("/");
        //设置到响应体中
        response.addCookie(cookie);
           }

}
