package com.wfg.api.interceptors;

import com.wfg.exection.GraceException;
import com.wfg.result.ResponseStatusEnum;
import com.wfg.utils.IPUtil;
import com.wfg.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: imooc-news-dev
 * @description: 拦截器校验用户ip进行60秒限制
 * @author: wfg
 * @create: 2021-11-13 17:08
 */

public class PassportInterceptor implements HandlerInterceptor {
   @Autowired
   public RedisOperator redis;

   public static final String MOBILE_SMSCODE="mobile_smscode";

    /**
     *  拦截请求，访问controller之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取用户ip
        String ip = IPUtil.getRequestIp(request);

        boolean keyIsExit= redis.keyIsExist(MOBILE_SMSCODE+":"+ip);

        if(keyIsExit){
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
            return  false;
        }
        return true;
    }

    /**
     * 在请求访问到controller之后 渲染视图之前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * 在请求访问到controller之后 渲染视图之后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
