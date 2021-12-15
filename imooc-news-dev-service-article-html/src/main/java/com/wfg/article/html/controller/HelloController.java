package com.wfg.article.html.controller;

import com.wfg.api.controller.user.HelloControllerApi;
import com.wfg.result.GraceJSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-08 19:51
 */
@RestController
public class HelloController implements HelloControllerApi {

   final static Logger logger = LoggerFactory.getLogger(HelloController.class);


   public Object hello(){
       return GraceJSONResult.ok("hello");
    }




}
