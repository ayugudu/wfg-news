package com.wfg.api.controller.user;

import com.wfg.pojo.bo.RegisterLoginBO;
import com.wfg.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-12 20:24
 */

@Api(value = "用户注册登录",tags = {"用户登录注册的controller"})
@RequestMapping("/passport")
public interface PassportControllerApi {



    @ApiOperation(value="获得短信验证码", notes = "获得短信验证码",httpMethod = "GET")
    @GetMapping("/getSMSCode")
    public GraceJSONResult getSMSCode(@RequestParam String mobile, HttpServletRequest request);



    @ApiOperation(value ="一键注册接口",notes = "一键注册接口",httpMethod = "POST")
    @PostMapping("doLogin")
    public GraceJSONResult doLogin(@RequestBody @Valid RegisterLoginBO registerLoginBO,
                                   BindingResult result,
                                   HttpServletRequest request,
                                   HttpServletResponse response);

    @ApiOperation(value ="用户退出登录",notes = "用户退出登录",httpMethod = "POST")
    @PostMapping("logout")
    public GraceJSONResult logout(
            @RequestParam String userId,
            HttpServletRequest request,
            HttpServletResponse response
    );
}
