package com.wfg.article.service;

import com.wfg.pojo.Article;
import com.wfg.pojo.Category;
import com.wfg.pojo.bo.NewArticleBO;
import com.wfg.pojo.vo.ArticleDetailVO;
import com.wfg.result.PagedGridResult;

import java.util.Date;
import java.util.List;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-28 16:08
 */
public interface ArticlePortalService {




    /**
     * 用户中心 --- 查询所有文章列表
     * @return
     */
    public PagedGridResult queryIndexArticleList(String keyword, Integer category, Integer page, Integer pageSize);

    /**
     * 用户中心 --- 查询热文列表
     * * @return
     */
    public List<Article> queryHotList();


    /**
     * 查询作家文章
     */

    public PagedGridResult queryArticleListOfWriter(String writerId, Integer page, Integer pageSize);



    /**
     * 用户中心 --- 查询作家热文列表
     * * @return
     */
    public PagedGridResult queryHotListOfWriter(String writerId);


    /**
     * 查询文章详情页
     */
   public ArticleDetailVO queryDetail(String articleId);

}
