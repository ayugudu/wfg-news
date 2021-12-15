package com.rule;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: imooc-news-dev
 * @description: 负载均衡配置
 * @author: wfg
 * @create: 2021-12-07 18:58
 */
@Configuration
public class MyRule {
  //设置不同的负载均衡算法
    @Bean
    public IRule  iRule(){
        return  new RandomRule();
    }
}
