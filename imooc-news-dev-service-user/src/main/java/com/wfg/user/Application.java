package com.wfg.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-08 20:00
 */
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@MapperScan("com.wfg.user.mapper")
@ComponentScan(basePackages = {"com.wfg","com.wfg.utils","org.n3r.idworker"})
@EnableEurekaClient    //开启eureka client 注册到server中
@EnableCircuitBreaker //开启hystrix的熔断机制
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
