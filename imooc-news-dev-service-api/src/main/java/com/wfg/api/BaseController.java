package com.wfg.api;

import com.wfg.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-13 15:21
 */
public class BaseController {

    @Autowired
    protected RedisOperator redis;

    public static final String MOBILE_SMSCODE="mobile_smscode";

    /**
     * 获取BO中的错误信息
     * @param result
     */
    public Map<String,String> getErrors(BindingResult result){

        Map<String,String> map = new HashMap<>();

        List<FieldError> errorList = result.getFieldErrors();

        for(FieldError error : errorList){
            // 验证错误时 所对应的某个属性
            String field = error.getField();
            // 验证的错误消息
            String msg = error.getDefaultMessage();

            map.put(field,msg);

        }

        return  map;
    }

}
