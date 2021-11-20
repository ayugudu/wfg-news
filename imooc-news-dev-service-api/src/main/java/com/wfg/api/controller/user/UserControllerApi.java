package com.wfg.api.controller.user;

import com.wfg.pojo.bo.RegisterLoginBO;
import com.wfg.pojo.bo.UpdateUserInfoBO;
import com.wfg.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.context.properties.bind.BindResult;
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

@Api(value = "用户信息相关的controller",tags = {"用户信息相关的controller"})
@RequestMapping("/user")
public interface UserControllerApi {

    @ApiOperation(value="获得用户基本信息", notes = "获得用户基本信息",httpMethod = "POST")
    @PostMapping("/getUserInfo")
    public GraceJSONResult getUserInfo(@RequestParam String userId);


    @ApiOperation(value="获得用户账号信息", notes = "获得用户账号信息",httpMethod = "POST")
    @PostMapping("/getAccountInfo")
    public GraceJSONResult getAccountInfo(@RequestParam String userId);


    @ApiOperation(value="完善用户账号信息", notes = "完善用户账号信息",httpMethod = "POST")
    @PostMapping("/updateUserInfo")
    public GraceJSONResult updateUserInfo(@RequestBody @Valid UpdateUserInfoBO updateUserInfoBO,
                                          BindingResult result);


}
