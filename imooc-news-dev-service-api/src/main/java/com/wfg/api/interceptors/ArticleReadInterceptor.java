package com.wfg.api.interceptors;

import com.wfg.utils.IPUtil;
import com.wfg.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-23 17:40
 */
public class ArticleReadInterceptor extends BaseInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从参数中获取getParameter
       String articleId= request.getParameter("articleId");

        String ip = IPUtil.getRequestIp(request);
       boolean isExit= redis.keyIsExist(REDIS_ALREADY_READ+":"+articleId+":"+ip);
       if(isExit){
           return  false;
       }
        return  true;
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
