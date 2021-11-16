package com.wfg.api.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-08 20:14
 */
@Api(value="controller 标题",tags = "具有xxx功能的controller")
public interface HelloControllerApi {
    /**
     * api 的作用： 在这里对所有api接口统一进行管理和调度
     * 对controller进行抽象（领导），其他服务层都是实现（员工）
     * 老板只看一下每个人（服务）的进度，做什么事，老板只对接部门经理
     * @return
     */

    /**
     * 运作 ： 现在所有接口都在此暴露，实现都是在各自的微服务中
     * 本项目只写接口，不写实现，实现在各自的微服务工程中
     * controller 也会分散在各个微服务中一旦就很难统一管理和查看
     *
     * 微服务之间的调用都是基于接口的
     * 如果不这么做，微服务之间的调用需要相互依赖，耦合度也就高，接口目的为了能够解耦
     *
     * @return
     */
    @GetMapping("/hello")
    @ApiOperation(value ="hello 方法的接口",notes = "hello方法接口",httpMethod = "GET")
    Object hello();






}
