package com.wfg.zuul.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;

/**
 * @program: imooc-news-dev
 * @description: 构建zuul的自定义过滤器
 * @author: wfg
 * @create: 2021-12-13 09:04
 */
@Component
public class MyFilter extends ZuulFilter {
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
        System.out.println("display pre zuul");
        return null;
    }
}
