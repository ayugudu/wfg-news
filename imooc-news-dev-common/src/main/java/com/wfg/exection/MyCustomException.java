package com.wfg.exection;

import com.wfg.result.ResponseStatusEnum;
import lombok.Data;

/**
 * @program: imooc-news-dev
 * @description: 自定义异常
 * 目的：统一处理异常信息
 *       便于解耦，
 * @author: wfg
 * @create: 2021-11-13 17:52
 */
@Data
public class MyCustomException extends RuntimeException {

    private ResponseStatusEnum responseStatusEnum;

    public MyCustomException(ResponseStatusEnum responseStatusEnum){
        super("异常状态码为:"+responseStatusEnum.status()+";具体异常信息为："+responseStatusEnum.msg());
    }
}
