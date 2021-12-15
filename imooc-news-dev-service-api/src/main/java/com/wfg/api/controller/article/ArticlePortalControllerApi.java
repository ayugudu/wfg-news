package com.wfg.api.controller.article;

import com.wfg.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-29 17:32
 */
@Api(value ="门户端文章业务的controller",tags ={"门户端文章业务的controller"})
@RequestMapping("/portal/article")
public interface ArticlePortalControllerApi {

    @GetMapping("/list")
    @ApiOperation(value="首页查询文章列表",notes = "首页查询文章列表",httpMethod = "GET")
    public GraceJSONResult list(@RequestParam(required = false) String keyword,
                                @RequestParam(required = false) Integer category,
                                @ApiParam(name="page",value="查询下一分页列表")
                                @RequestParam Integer page,
                                @ApiParam(name="pageSize",value = "分页查询页数")
                                @RequestParam Integer pageSize);

    @GetMapping("/hotList")
    @ApiOperation(value="首页查询热点列表",notes = "首页查询热点列表",httpMethod = "GET")
    public GraceJSONResult hotList();

    @GetMapping("/queryArticleListOfWriter")
    @ApiOperation(value="查询作家文章",notes = "查询作家文章",httpMethod = "GET")
    public GraceJSONResult queryArticleListOfWriter(@RequestParam String writerId,
                                                    @RequestParam Integer page,
                                                     @RequestParam Integer pageSize
                                                    );



    @GetMapping("/queryGoodArticleListOfWriter")
    @ApiOperation(value="查询作家热点文章",notes = "查询作家热点文章",httpMethod = "GET")
    public GraceJSONResult queryGoodArticleListOfWritert(@RequestParam  String writerId);


    @GetMapping("/detail")
    @ApiOperation(value="查询文章详情页",notes = "查询文章详情页",httpMethod = "GET")
    public GraceJSONResult detail(@RequestParam  String articleId);

    @GetMapping("/readCounts")
    @ApiOperation(value="获得文章阅读数",notes = "获得文章阅读数",httpMethod = "GET")
    public Integer readCounts(@RequestParam  String articleId);

    @PostMapping("/readArticle")
    @ApiOperation(value="阅读文章，文章阅读量累加",notes = "阅读文章，文章阅读量累加",httpMethod = "POST")
    public GraceJSONResult readArticle(@RequestParam  String articleId, HttpServletRequest request);





}
