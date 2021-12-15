package com.wfg.article.task;

import com.wfg.article.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

/**
 * @program: imooc-news-dev
 * @description: 定时任务(全表扫描  一般建议使用 Mq使用异步任务)
 * @author: wfg
 * @create: 2021-11-28 19:40
 */
/*@Configuration      //标记配置类，加入到springboot
@EnableScheduling   // 开启定时任务*/
public class TaskPublishArticles {
/*
    @Autowired
    private ArticleService articleService;
   //定时任务表达式
    @Scheduled(cron = "0/3 * * * * ?")
    private  void publishArticles(){
        System.out.println("执行定时任务"+ LocalDateTime.now());
         // 4 调用文章service，把当前时间应该发布的定时文章，状态改为即时
        articleService.updateAppointToPublish();
    }*/
}
