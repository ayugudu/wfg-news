package com.wfg.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-08 20:00
 */
@SpringBootApplication
@MapperScan("com.wfg.user.mapper")
@ComponentScan("com.wfg")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
