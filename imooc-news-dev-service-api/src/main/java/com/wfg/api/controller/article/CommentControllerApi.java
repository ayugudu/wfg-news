package com.wfg.api.controller.article;

import com.wfg.pojo.bo.CommentReplyBO;
import com.wfg.pojo.bo.NewArticleBO;
import com.wfg.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-08 20:14
 */
@Api(value="评论相关业务的controller",tags = "评论相关业务的controller")
@RequestMapping("comment")
public interface CommentControllerApi {

     @PostMapping("createComment")
     @ApiOperation(value="用户评论",notes = "用户评论")
     public GraceJSONResult saveOrUpdateCategory(@RequestBody @Valid CommentReplyBO commentReplyBO,
                                            BindingResult result);



    @GetMapping("counts")
    @ApiOperation(value="用户评论数查询",notes = "用户评论数查询")
    public GraceJSONResult commentCounts(@RequestParam String articleId);

    @GetMapping("list")
    @ApiOperation(value="查询文章的所有评论列表",notes = "查询文章的所有评论列表")
    public GraceJSONResult list(@RequestParam String articleId,@RequestParam Integer page,@RequestParam Integer pageSize);

    @PostMapping("mng")
    @ApiOperation(value ="查询我的评论管理列表",notes="查询我的评论管理列表")
    public GraceJSONResult mng(@RequestParam String writerId,@RequestParam Integer page,@RequestParam Integer pageSize);

    @PostMapping("/delete")
    @ApiOperation(value="作者删除评论",notes = "作者删除评论",httpMethod = "POST")
    public GraceJSONResult delete(@RequestParam String writerId,
                                         @RequestParam String commentId);





}
