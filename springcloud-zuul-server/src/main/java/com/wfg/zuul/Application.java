package com.wfg.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.context.annotation.ComponentScan;


/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-08 20:00
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,MongoAutoConfiguration.class})
@ComponentScan(basePackages = {"com.wfg","org.n3r.idworker"})
//@EnableZuulServer
@EnableZuulProxy // enablezuulProxy 是 enablezuulServer的一个增强升级版，当zuul和eureka,ribbon等组件共同使用，则使用增强版即可
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
