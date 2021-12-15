package com.wfg.api.config;

import com.wfg.api.interceptors.*;
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

    @Bean
    public ArticleReadInterceptor articleReadInterceptor(){
        return  new ArticleReadInterceptor();
    }
    @Bean
    public AdminUserInterceptor adminUserInterceptor(){
        return  new AdminUserInterceptor();
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
               .addPathPatterns("/user/getAccountInfo")
               .addPathPatterns("/fs/uploadFace")
               .addPathPatterns("/fs/uploadSomeFaces")
               .addPathPatterns("/fans/follow")
               .addPathPatterns("/fans/unfollow")

       ;

       //添加用户激活状态拦截器
        registry.addInterceptor(userActiveInterceptor())
                .addPathPatterns("/user/getAccountInfo")
                .addPathPatterns("/fans/follow")
                .addPathPatterns("/fans/unfollow");


        // 验证管理员token登录拦截器
        registry.addInterceptor(adminUserInterceptor())
                .addPathPatterns("/adminMng/adminIsExist")
                .addPathPatterns("/adminMng/getAdminList")
                .addPathPatterns("/adminMng/addNewAdmin")
                .addPathPatterns("/fs/uploadToGridFs")
                .addPathPatterns("/fs/readInGridFs")


                .addPathPatterns("/friendLinkMng/saveOrUpdateFriendLink")
                .addPathPatterns("/friendLinkMng/getFriendLinkList")
                .addPathPatterns("/friendLinkMng/delete")
         ;

        registry.addInterceptor(articleReadInterceptor())
                .addPathPatterns("/portal/article/readArticle");
    }


}
