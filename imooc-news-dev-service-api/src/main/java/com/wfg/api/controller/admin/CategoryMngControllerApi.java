package com.wfg.api.controller.admin;

import com.wfg.pojo.bo.CategoryBO;
import com.wfg.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @program: imooc-news-dev
 * @description: / s
 * @author: wfg
 * @create: 2021-11-27 19:25
 */
@Api(value ="文章分类接口",tags = {"文章分类接口Api"})
@RequestMapping("/categoryMng")
public interface CategoryMngControllerApi {

    @ApiOperation(value="新增或更新文章分类接口",notes = "新增或更新接口")
    @PostMapping("/saveOrUpdateCategory")
    public GraceJSONResult saveOrUpdateCategory(@RequestBody @Valid CategoryBO categoryBO, BindingResult result);

    @ApiOperation(value="查询文章分类接口",notes = "查询文章分类接口")
    @PostMapping("/getCatList")
    public GraceJSONResult getCatList();


    @ApiOperation(value="用户端查询文章分类列表",notes = "用户端查询文章分类列表")
    @GetMapping ("/getCats")
    public GraceJSONResult getCats();
}
