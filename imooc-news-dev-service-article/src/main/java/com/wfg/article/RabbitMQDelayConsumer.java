package com.wfg.article;

import com.wfg.api.config.RabbitMQConfig;

import com.wfg.api.config.RabbitMQDelayConfig;
import com.wfg.article.service.ArticleService;
import com.wfg.pojo.Article;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.util.Date;

/**
 * @program: imooc-news-dev
 * @description: 消费者实现
 * @author: wfg
 * @create: 2021-11-08 19:51
 */
@Component
public class RabbitMQDelayConsumer {

  @Autowired
  private ArticleService articleService;

  @RabbitListener(queues = {RabbitMQDelayConfig.QUEUE_DELAY})
  public void watchQueue(String payload, Message message){
    String routingKey = message.getMessageProperties().getReceivedRoutingKey();

    System.out.println(routingKey);
    System.out.println("消费者接受的延迟消息："+new Date());

    // 消费者接受定时发布的延迟消息，修改当前的文章状态为 即时发布
    String articleId= payload;
    articleService.updateArticleToPublish(articleId);

  }

}
