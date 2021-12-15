package com.wfg.api.controller.article;

import com.wfg.pojo.bo.NewArticleBO;
import com.wfg.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.FileNotFoundException;
import java.util.Date;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-08 20:14
 */
@Api(value="静态话文章业务的controller",tags = "静态化章业务的controller")
@RequestMapping("article/html")
public interface ArticleHtmlControllerApi {
    @GetMapping("download")
    @ApiOperation(value="下载html",notes ="下载html",httpMethod="GET")
    public Integer download(String articleId,String articleMongoId) throws FileNotFoundException;



    @GetMapping("delete")
    @ApiOperation(value="删除html",notes ="删除html",httpMethod="GET")
    public Integer delete(String articleId) ;


}
