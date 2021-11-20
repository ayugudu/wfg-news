package com.wfg.api.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @program: imooc-news-dev
 * @description: 方法拦截配置日志通知
 * @author: wfg
 * @create: 2021-11-19 19:19
 */
@Component
@Aspect
@Slf4j
public class ServiceLogAspect {

   final static Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);
    @Pointcut("execution(* com.wfg.*.service.impl..*.*(..))")
    public void servicePointcut(){}


    @Around("servicePointcut()")
    public Object recordTimeOfService(ProceedingJoinPoint joinPoint) throws Throwable {
       logger.info("=====开始执行{}.{}=======",joinPoint.getTarget().getClass(),joinPoint.getSignature().getName());
       long  start = System.currentTimeMillis();
       Object result = joinPoint.proceed();
       long  end = System.currentTimeMillis();
       long takeTime = end -start;
       if(takeTime>3000){
            logger.error("当前执行耗时:{}",takeTime);
       }
       else if(takeTime>2000){
           logger.warn("当前执行耗时:{}",takeTime);
       }
       else{
           logger.info("当前执行耗时:{}",takeTime);
       }
       return  result;
    }


}
