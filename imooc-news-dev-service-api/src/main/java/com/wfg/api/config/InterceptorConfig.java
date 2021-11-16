package com.wfg.api.config;

import com.wfg.api.interceptors.PassportInterceptor;
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
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加拦截器
       registry.addInterceptor(passportInterceptor())
        //添加拦截路径
               .addPathPatterns("/passport/getSMSCode");
    }
}
