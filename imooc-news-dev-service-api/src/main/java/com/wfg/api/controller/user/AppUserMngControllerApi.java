package com.wfg.api.controller.user;

import com.wfg.pojo.bo.UpdateUserInfoBO;
import com.wfg.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Date;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-12 20:24
 */

@Api(value = "用户管理相关的controller",tags = {"用户管理相关的controller"})
@RequestMapping("/appUser")
public interface AppUserMngControllerApi {

    @ApiOperation(value="查询网站用户基本信息", notes = "查询网站用户基本信息",httpMethod = "POST")
    @PostMapping("/queryAll")
    public GraceJSONResult queryAll(@RequestParam String nickname,
                                    @RequestParam Integer status,
                                    @RequestParam Date  startDate,
                                    @RequestParam Date   endDate,
                                    @RequestParam Integer page,
                                    @RequestParam Integer pageSize);


    @ApiOperation(value="查询用户详情", notes = "查询用户详情",httpMethod = "POST")
    @PostMapping("/userDetail")
    public GraceJSONResult userDetail(@RequestParam String userId);

    @ApiOperation(value="冻结用户或者解冻用户", notes = "冻结用户或者解冻用户",httpMethod = "POST")
    @PostMapping("/freezeUserOrNot")
    public GraceJSONResult freezeUserOrNot(@RequestParam String userId,@RequestParam  Integer doStatus);

}
