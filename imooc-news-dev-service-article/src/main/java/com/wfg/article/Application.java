package com.wfg.article;

import com.rule.MyRule;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-08 20:00
 */
@SpringBootApplication//(exclude = MongoAutoConfiguration.class)
@ComponentScan(basePackages = {"com.wfg","com.wfg.utils","org.n3r.idworker"})
@MapperScan("com.wfg.article.mapper")
@EnableEurekaClient
//@RibbonClient(name = "service-user",configuration = MyRule.class)
@EnableFeignClients({"com.wfg"})
//开启客户端降级
@EnableHystrix
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
