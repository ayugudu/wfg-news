package com.wfg.exection;

import com.wfg.result.GraceJSONResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @program: imooc-news-dev
 * @description: 统一异常拦截处理
 *
 * 针对异常的类型进行捕获，然后返回json信息到前端
 *
 */

@RestControllerAdvice
public class GraceExceptionHandler {

    @ExceptionHandler(MyCustomException.class)

    public GraceJSONResult  returnMyException(MyCustomException e){

      e.printStackTrace();

      return  GraceJSONResult.exception(e.getResponseStatusEnum());
    }
}
