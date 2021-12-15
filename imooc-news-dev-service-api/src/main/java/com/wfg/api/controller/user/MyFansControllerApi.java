package com.wfg.api.controller.user;

import com.wfg.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-08 20:14
 */
@Api(value="粉丝管理",tags = "具有粉丝管理功能的controller")
@RequestMapping("fans")
public interface MyFansControllerApi {

    @PostMapping("/isMeFollowThisWriter")
    @ApiOperation(value ="查询当前用户是否关注作家",notes = "查询当前用户是否关注作家",httpMethod = "POST")
    public GraceJSONResult isMeFollowThisWriter(@RequestParam String writerId,@RequestParam String fanId);



    @PostMapping("/follow")
    @ApiOperation(value ="用户关注作家成为粉丝",notes = "用户关注作家成为粉丝",httpMethod = "POST")
    public GraceJSONResult follow(@RequestParam String writerId,@RequestParam String fanId);

    @PostMapping("/unfollow")
    @ApiOperation(value ="取消关注，作家损失粉丝",notes = "取消关注，作家损失粉丝",httpMethod = "POST")
    public GraceJSONResult unfollow(@RequestParam String writerId,@RequestParam String fanId);

    @PostMapping("/queryAll")
    @ApiOperation(value ="查询我的所有粉丝列表",notes = "查询我的所有粉丝列表",httpMethod = "POST")
    public GraceJSONResult queryAll( @RequestParam String writerId,
                                     @ApiParam(name="page",value="查询下一页的第几页")
                                     @RequestParam Integer page,
                                     @ApiParam(name="pageSize",value="分页查询每一页显示的条数")
                                         @RequestParam Integer pageSize);




    @PostMapping("/queryRatio")
    @ApiOperation(value ="查询男女粉丝数量",notes = "查询男女粉丝数量",httpMethod = "POST")
    public GraceJSONResult queryRatio(@RequestParam String writerId);


    @PostMapping("/queryRatioByRegion")
    @ApiOperation(value ="根据地域查询粉丝数量",notes = "根据地域查询粉丝数量",httpMethod = "POST")
    public GraceJSONResult queryRatioByRegion(@RequestParam String writerId);



}
