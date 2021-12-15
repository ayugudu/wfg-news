package com.wfg.article.html;

import com.wfg.api.config.RabbitMQConfig;
import com.wfg.article.html.controller.ArticleHtmlComponent;
import com.wfg.result.GraceJSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;

/**
 * @program: imooc-news-dev
 * @description: 消费者实现
 * @author: wfg
 * @create: 2021-11-08 19:51
 */
@Component
public class RabbitMQConsumer  {
    @Autowired
    ArticleHtmlComponent articleHtmlComponent;
  @RabbitListener(queues = {RabbitMQConfig.QUEUE_DOWNLOAD_HTML})
  public void watchQueue(String payload, Message message){
    String routingKey = message.getMessageProperties().getReceivedRoutingKey();
    if(routingKey.equalsIgnoreCase("article.download.do")){
        //执行文件下载 ： articleId+","+articleMongoId
        String  articleId = payload.split(",")[0];
        String  articleMongoId= payload.split(",")[1];
        try {
            articleHtmlComponent.download(articleId,articleMongoId);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    if(routingKey.equalsIgnoreCase("article.delete.do")){
        String articleId= payload;
        articleHtmlComponent.delete(articleId);
    }


    System.out.println(payload);
 }




}
