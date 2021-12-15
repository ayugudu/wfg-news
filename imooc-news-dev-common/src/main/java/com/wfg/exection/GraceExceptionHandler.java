package com.wfg.exection;

import com.wfg.result.GraceJSONResult;
import com.wfg.result.ResponseStatusEnum;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @ExceptionHandler(MaxUploadSizeExceededException.class)
   public GraceJSONResult returnMaxUploadSizeException(MaxUploadSizeExceededException e){
    return GraceJSONResult.exception(ResponseStatusEnum.FILE_MAX_SIZE_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public GraceJSONResult returnMethodException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        Map<String,String> map = getErrors(bindingResult);
        return  GraceJSONResult.errorMap(map);

    }

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
