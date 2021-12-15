package com.wfg.api.controller.admin;

import com.wfg.pojo.bo.SaveFriendLinkBO;
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
 * @create: 2021-11-27 16:51
 */
@Api(value = "首页友情链接维护",tags = {"首页友情链接维护"})
@RequestMapping("friendLinkMng")
public interface FriendLinkControllerApi {

    @ApiOperation(value="新增或者修改友情链接",notes = "新增或者修改友情链接",httpMethod = "POST")
    @PostMapping("/saveOrUpdateFriendLink")
    public GraceJSONResult saveOrUpdateFriend(@RequestBody @Valid SaveFriendLinkBO saveFriendLinkBO,
                                              BindingResult result);



    @ApiOperation(value="插询友情链接链表",notes = "插询友情链接列表",httpMethod = "POST")
    @PostMapping("/getFriendLinkList")
    public GraceJSONResult getFriendLinkList();

    @ApiOperation(value="删除友情链接",notes = "删除友情链接列表",httpMethod = "POST")
    @PostMapping("/delete")
    public GraceJSONResult delete(@RequestParam String linkId);

    @ApiOperation(value="门户端查询友情链接",notes = "门户端友情链接列表",httpMethod = "GET")
    @GetMapping("/portal/list")
    public GraceJSONResult queryPortalAllFriendLinkList();

}
