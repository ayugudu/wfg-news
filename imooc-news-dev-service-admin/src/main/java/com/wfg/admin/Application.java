package com.wfg.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-08 20:00
 */
@SpringBootApplication//(exclude = MongoAutoConfiguration.class)
@ComponentScan(basePackages = {"com.wfg","com.wfg.utils","org.n3r.idworker"})
@MapperScan("com.wfg.admin.mapper")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
