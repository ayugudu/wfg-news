package com.wfg.api.interceptors;

import com.wfg.enums.UserStatus;
import com.wfg.exection.GraceException;
import com.wfg.pojo.AppUser;
import com.wfg.result.ResponseStatusEnum;
import com.wfg.utils.JsonUtils;
import com.wfg.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户激活状态检查拦截器
 * 发文章，修改文章，删除文章
 * 发表评论，查看评论等
 *
 */
public class UserActiveInterceptor extends BaseInterceptor implements HandlerInterceptor {

    @Autowired
    public RedisOperator redis;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       String userId= request.getHeader("headerUserId");
        String userJson = redis.get(REDIS_USER_INFO+":"+userId);
        AppUser user = null;
        if(StringUtils.isNotBlank(userJson)){
            user = JsonUtils.jsonToPojo(userJson,AppUser.class);
        }else{
            GraceException.display(ResponseStatusEnum.UN_LOGIN);
            return  false;
        }
        if(user.getActiveStatus()==null||user.getActiveStatus()!= UserStatus.ACTIVE.type){
            GraceException.display(ResponseStatusEnum.USER_INACTIVE_ERROR);
            return  false;
        }
        return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
