package com.wfg.zuul.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import com.wfg.result.GraceJSONResult;
import com.wfg.result.ResponseStatusEnum;
import com.wfg.utils.IPUtil;
import com.wfg.utils.JsonUtils;
import com.wfg.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServletRequest;

/**
 * @program: imooc-news-dev
 * @description: 构建zuul的自定义过滤器
 * @author: wfg
 * @create: 2021-12-13 09:04
 */
@Component
public class BlackIpFilter extends ZuulFilter {

    @Value("${blackIp.continueCounts}")
    private Integer continueCounts;
    @Value("${blackIp.timeInterval}")
    private Integer timeInterval;
    @Value("${blackIp.limitTimes}")
    private Integer limitTimes;


    RedisOperator redis;
    /**
     * 定义过滤器的类型
     *   pre:    在请求被路由之前执行
     *   route:  在路由请求的时候执行
     *   post:   请求路由以后执行
     *   error： 处理请求时发生错误时候执行
     * @return
     */

    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 过滤器执行的顺序，配置多个有顺序的过滤
     * 执行顺序从小到大
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 是否开启过滤器
     *     true:开启
     *     false:禁用
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 过滤器的业务实现
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        System.out.println("执行【ip黑命大】");
        //获得上下文
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        //获得ip
        String ip= IPUtil.getRequestIp(request);
        /**
         *  需求
         *  判断ip在10秒内的请求次数是否超过10次
         *  如果超过，则限制这个ip访问15秒，15秒以后在放行
         */
        final String ipRedisKey ="zuul-ip:"+ip;
        final String ipRedisLimitKey ="zuul-ip-limit:"+ip;
       // 获得当前ip这个ip的剩余时间
        long limit=redis.ttl(ipRedisLimitKey);
        //如果当前限制ip的key还存在剩余时间，说你这个ip不能访问

        if(limit>0){
         stopRequest(context);
         return  null;
        }

        //在redis中累加ip的请求访问次数
        long requestCounts = redis.increment(ipRedisKey,1);
        //从0开始计算请求次数 初期为1，则设置过期时间
        if(requestCounts==1){
            redis.expire(ipRedisKey,timeInterval);
        }
        //一旦请求次数超过了连续访问的次数，则需要限制这个ip的访问
        if(requestCounts>continueCounts){
            //限制ip的访问时间
            redis.set(ipRedisLimitKey,ipRedisLimitKey,limitTimes);
            stopRequest(context);
        }
        return null;
    }

    private void stopRequest(RequestContext context){
         // 停止zuul继续向下路由，禁止请求通信
        context.setSendZuulResponse(false);
        context.setResponseStatusCode(200);
        String result= JsonUtils.objectToJson(
                GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR_ZUUL)
        );
        context.setResponseBody(result);
        context.getResponse().setCharacterEncoding("utf-8");
        context.getResponse().setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

}
