package com.wfg.article.controller;


import com.wfg.api.config.RabbitMQConfig;
import com.wfg.api.config.RabbitMQDelayConfig;
import com.wfg.result.GraceJSONResult;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-08 19:51
 */
@RestController
@RequestMapping("/producer")
public class ProducerController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /***
     * rabbitMq 的路由规则
     *
     *       *.* -> * 代表一个占位符
     *              # 代表任意多个占位符
     * @return
     */
    @GetMapping("/hello")
   public Object hello(){
    /*   rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ARTICLE,
               "article.hello",
               "生产值");*/


        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ARTICLE,
                "article.publish.download.do",
                "1001");

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ARTICLE,
                "article.publish.success.do",
                "1002");

       return  GraceJSONResult.ok();
    }
 @GetMapping("/delay")
    public Object delay(){

        MessagePostProcessor messagePostProcessor = new MessagePostProcessor(){
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //设置消息的持久
                message.getMessageProperties()
                        .setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                //设置消息延迟的时间，单位ms毫秒
                message.getMessageProperties()
                        .setDelay(5000);
                return  message;
            }
        };

        rabbitTemplate.convertAndSend(
                RabbitMQDelayConfig.EXCHANGE_DELAY,
                "publish.delay.display","延迟消息",messagePostProcessor

        );
        System.out.println("生产者时间："+new Date());


        return "ok";
 }



}
