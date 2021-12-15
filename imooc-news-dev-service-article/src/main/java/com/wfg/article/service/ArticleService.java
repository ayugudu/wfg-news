package com.wfg.article.service;

import com.wfg.pojo.Category;
import com.wfg.pojo.bo.NewArticleBO;
import com.wfg.result.PagedGridResult;
import freemarker.template.TemplateException;
import io.swagger.models.auth.In;

import java.io.IOException;
import java.util.Date;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-28 16:08
 */
public interface ArticleService {

    /**
     * 发布文章
     */
    public String createArticle(NewArticleBO newArticleBO, Category category);

    /**
     * 更新定时发布为即时发布
     */
    public void updateAppointToPublish();

    /**
     * 更新定时发布为即时发布：用于MQ
     */
    public void updateArticleToPublish(String articleId);


    /**
     * 用户中心 --- 查询我的文章列表
     * @return
     */
    public PagedGridResult queryMyArticleList(String userId,
                                              String keyword,
                                              Integer status,
                                              Date startDate,
                                              Date endDate,
                                              Integer page,
                                              Integer pageSize);

    /**
     * 修改文章状态
     * @param articleId
     * @param pendingStatus
     */
    public void updateArticleStatus(String articleId, Integer pendingStatus);


    /**
     * 根据文章状态查询文章
     */

    public PagedGridResult queryByStatus(Integer status,Integer page,Integer pageSize);

    /**
     * 删除用户文章
     */
    public void deleteArticle(String userId,String articleId);


    /**
     * 撤回用户文章
     */
    public void withdrawArticle(String userId,String articleId);


    /**
     * 关联文章和gridfs的html文件Id
     */
    public void updateArticleToGridFs(String articleId,String mongoId);

    /**
     * 自动生成用户文章html
     */
    public void createArticleHTML(String articleId)throws IOException, TemplateException;
    /**
     * 自动生成用户文章html并发送到数据库mongodb
     */
    public String createArticleHTMLToGridFs(String articleId)throws IOException, TemplateException;






}
