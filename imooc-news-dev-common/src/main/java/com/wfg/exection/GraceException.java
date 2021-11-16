package com.wfg.exection;

import com.wfg.result.ResponseStatusEnum;

/**
 * @program: imooc-news-dev
 * @description: 异常的统一处理
 * @author: wfg
 * @create: 2021-11-13 17:51
 */
public class GraceException {

    public static void display(ResponseStatusEnum responseStatusEnum){
        throw new MyCustomException(responseStatusEnum);
    }
}
