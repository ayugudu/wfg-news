package com.wfg.api.config;

import com.wfg.api.interceptors.PassportInterceptor;
import com.wfg.api.interceptors.UserActiveInterceptor;
import com.wfg.api.interceptors.UserTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @program: imooc-news-dev
 * @description: 注册拦截器到容器里面
 * @author: wfg
 * @create: 2021-11-13 17:20
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public PassportInterceptor passportInterceptor(){
        return  new PassportInterceptor();
    }
    @Bean
    public UserTokenInterceptor userTokenInterceptor(){
        return  new UserTokenInterceptor();
    }

    @Bean
    public UserActiveInterceptor userActiveInterceptor(){
        return  new UserActiveInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加验证码60秒拦截器
       registry.addInterceptor(passportInterceptor())
        //添加拦截路径
               .addPathPatterns("/passport/getSMSCode");
       //添加用户token登录拦截器
       registry.addInterceptor(userTokenInterceptor())
               .addPathPatterns("/user/updateUserInfo")
               .addPathPatterns("/user/getAccountInfo");

  /*     //添加用户激活状态拦截器
        registry.addInterceptor(userActiveInterceptor())
                .addPathPatterns("/user/updateUserInfo");*/
    }
}
