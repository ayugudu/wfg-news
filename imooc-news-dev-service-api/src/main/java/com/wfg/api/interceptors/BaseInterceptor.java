package com.wfg.api.interceptors;

import com.wfg.exection.GraceException;
import com.wfg.result.ResponseStatusEnum;
import com.wfg.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.Response;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-19 18:24
 */
public class BaseInterceptor {

    @Autowired
    protected RedisOperator redis;
   public static final String REDIS_USER_TOKEN="redis_user_token";
    public static final String  REDIS_USER_INFO="redis_user_info";

  public boolean verifyUserIdToken(String id,
                                   String token,
                                   String redisKeyPrefix){

      if(StringUtils.isNotBlank(id)&&StringUtils.isNotBlank(token)){
          String redisToken =redis.get(redisKeyPrefix+":"+id);
          if(StringUtils.isBlank(redisToken)){
             GraceException.display(ResponseStatusEnum.UN_LOGIN);
             return  false;
          }else{
              if(!redisToken.equalsIgnoreCase(token)){
                  GraceException.display(ResponseStatusEnum.TICKET_INVALID);
                  return  false;
              }
          }
      }else{
          GraceException.display(ResponseStatusEnum.UN_LOGIN);
          return  false;
      }
        return  true;
  }


}
