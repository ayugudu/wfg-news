package com.wfg.api.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


/**
 * @program: imooc-news-dev
 * @description: 跨域配置
 * @author: wfg
 * @create: 2021-11-13 15:54
 */
@Configuration
public class CorsConfig {
    public CorsConfig(){}


    @Bean
    public CorsFilter corsFilter(){
        // 1 添加cors配置信息
        CorsConfiguration config = new CorsConfiguration();
        //开放所有的站点向后端发送请求
        config.addAllowedOrigin("*");
        // 设置是否发送cookie信息
        config.setAllowCredentials(true);
        //设置允许请求的方式
        config.addAllowedMethod("*");
        //设置允许的header
        config.addAllowedHeader("*");
        // 2. 为url添加映射路径
        UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
        corsSource.registerCorsConfiguration("/**", config);
        // 3. 返回重新定义好的corsSource
        return new CorsFilter(corsSource);



    }


}
