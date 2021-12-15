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
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-17 21:45
 */
public class UserTokenInterceptor  extends BaseInterceptor implements HandlerInterceptor {

    @Autowired
    public RedisOperator redis;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId= request.getHeader("headerUserId");
        String userToken= request.getHeader("headerUserToken");
        //判断是否放行
        boolean run = verifyUserIdToken(userId,userToken,REDIS_USER_TOKEN);

        return run;
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
