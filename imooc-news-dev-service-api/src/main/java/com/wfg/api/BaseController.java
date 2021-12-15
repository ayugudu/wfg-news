package com.wfg.api;

import com.wfg.pojo.vo.AppUserVO;
import com.wfg.result.GraceJSONResult;
import com.wfg.utils.JsonUtils;
import com.wfg.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-13 15:21
 */
public class BaseController {

    @Autowired
    protected RedisOperator redis;

    @Autowired
    protected RestTemplate restTemplate;

    public static final String MOBILE_SMSCODE="mobile_smscode";
    public static final String REDIS_USER_TOKEN="redis_user_token";
    public static final String  REDIS_USER_INFO="redis_user_info";
    public static final String REDIS_ADMIN_TOKEN="redis_admin_token";

    public static final String REDIS_WRITER_FANS_COUNTS = "redis_writer_fans_counts";
    public static final String REDIS_MY_FOLLOW_COUNTS = "redis_my_follow_counts";
    public static final String REDIS_ARTICLE_READ_COUNTS = "redis_article_read_counts";
    public static final String REDIS_ALREADY_READ = "redis_already_read";

    public static final String REDIS_ARTICLE_COMMENT_COUNTS = "redis_article_comment_counts";


    public static  final String REDIS_ALL_CATEGORY="redis_all_category";


    public static final Integer COOKIE_MONTH=30*24*60*60;
    public static final Integer COOKIE_DELETE=0;


    public static final Integer COMMON_START_PAGE=1;
    public static final Integer COMMON_PAGE_SIZE=10;

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

    /**
     * 删除cookie
      */
    public void delCookie(HttpServletRequest request,HttpServletResponse response,String cookieName){
        String deleteValue = null;
        try {
            deleteValue = URLEncoder.encode("","utf-8");
            setCookieValue(request,response,cookieName,deleteValue,COOKIE_DELETE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    public Integer getCountsFromRedis(String key){
        String countsStr = redis.get(key);
        if(StringUtils.isBlank(countsStr)){
            countsStr="0";
        }
        return  Integer.valueOf(countsStr);
    }
    //发起远程调用 获取Appuser
    public List<AppUserVO> getBasicUserList(Set idSet){
        String  userServerUrlExecute="http://user.imoocnews.com:8003/user/queryByIds?userIds="+ JsonUtils.objectToJson(idSet);
        ResponseEntity<GraceJSONResult> responseEntity
                =  restTemplate.getForEntity(userServerUrlExecute,GraceJSONResult.class);
        GraceJSONResult bodyResult = responseEntity.getBody();
        List<AppUserVO> userVOList =null;
        if(bodyResult.getStatus()==200){
            String userJson= JsonUtils.objectToJson(bodyResult.getData());
            userVOList=JsonUtils.jsonToList(userJson,AppUserVO.class);
        }
        return  userVOList;
    }
}
