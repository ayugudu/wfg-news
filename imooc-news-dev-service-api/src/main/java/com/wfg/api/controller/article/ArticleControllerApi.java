package com.wfg.api.controller.article;

import com.wfg.pojo.bo.NewArticleBO;
import com.wfg.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
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
@Api(value="文章业务的controller",tags = "文章业务的controller")
@RequestMapping("article")
public interface ArticleControllerApi {

@PostMapping("createArticle")
@ApiOperation(value="用户发文",notes = "用户发文")
public GraceJSONResult saveOrUpdateCategory(@RequestBody @Valid NewArticleBO newArticleBO,
                                            BindingResult result);



    @PostMapping("queryMyList")
    @ApiOperation(value="查询用户所有文章列表",notes = "查询用户所有文章列表")
    public GraceJSONResult queryMyList(String userId,
                                       String keyword,
                                       Integer status,
                                       Date startDate,
                                       Date endDate,
                                       Integer page,
                                       Integer pageSize
                                       );

    @PostMapping("queryAllList")
    @ApiOperation(value="管理员查询用户所有文章列表",notes = "管理员查询用户所有文章列表")
    public GraceJSONResult queryAllList(@RequestParam Integer status,
                                         @ApiParam(name="page",value = "查询分页")
                                         @RequestParam Integer page,
                                         @ApiParam(name = "pageSize",value = "分页数据")
                                         @RequestParam Integer pageSize
                                        );


    @PostMapping("doReview")
    @ApiOperation(value ="管理员对文章进行审核失败或者通过",notes = "管理员对文章进行审核失败或者通过")
    public GraceJSONResult doReview(
               @RequestParam String articleId,
               @RequestParam Integer passOrNot
    );


    @PostMapping("withdraw")
    @ApiOperation(value ="对文章进行撤回",notes = "对文章进行撤回")
    public GraceJSONResult withdraw(
            @RequestParam @NotEmpty(message = "用户id不能为空") String userId,
            @RequestParam @NotEmpty(message = "文章id不能为空") String articleId
    );

    @PostMapping("delete")
    @ApiOperation(value ="对文章进行删除",notes = "对文章进行删除")
    public GraceJSONResult delete(
            @RequestParam @NotEmpty(message = "用户id不能为空") String userId,
            @RequestParam @NotEmpty(message = "文章id不能为空") String articleId
    );
}
