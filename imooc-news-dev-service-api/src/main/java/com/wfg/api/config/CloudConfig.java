package com.wfg.api.config;


import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


/**
 * @program: imooc-news-dev
 * @description: 不同服务之间的调用
 * @author: wfg
 * @create: 2021-11-13 15:54
 */
@Configuration
public class CloudConfig {
    public CloudConfig(){}


    /**
     * 基于 okhttp3 的配置来实例RestTemplate
     * @return
     */
    @Bean
    // 负载均衡 轮询效果
    @LoadBalanced
    public RestTemplate restTemplate(){
       return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }


}
