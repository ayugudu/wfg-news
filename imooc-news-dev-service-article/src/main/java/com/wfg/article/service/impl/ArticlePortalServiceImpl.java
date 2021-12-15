package com.wfg.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wfg.article.mapper.ArticleMapper;
import com.wfg.article.service.ArticlePortalService;
import com.wfg.enums.ArticleReviewStatus;
import com.wfg.enums.YesOrNo;
import com.wfg.pojo.Article;
import com.wfg.pojo.vo.ArticleDetailVO;
import com.wfg.result.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-29 17:45
 */
@Service
public class ArticlePortalServiceImpl implements ArticlePortalService {
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public PagedGridResult queryIndexArticleList(String keyword, Integer category, Integer page, Integer pageSize) {
       QueryWrapper<Article> wrapper = setDefaultArticleWrapper();
        if(StringUtils.isNotBlank(keyword)){
            wrapper.like("title",keyword);
        }
        if(category!=null){
            wrapper.eq("category_id",category);
        }
        Page<Article> articlePage = new Page<>(page,pageSize);
        return new PagedGridResult( articleMapper.selectPage(articlePage,wrapper));
    }

    @Override
    public List<Article> queryHotList() {
        QueryWrapper<Article> wrapper = setDefaultArticleWrapper();
        //分页数据 只查5调
        Page<Article> page = new Page<>(1,5);
         Page<Article> res= articleMapper.selectPage(page,wrapper);
        return  res.getRecords();
    }

    @Override
    public PagedGridResult queryArticleListOfWriter(String writerId, Integer page, Integer pageSize) {
        QueryWrapper<Article> wrapper = setDefaultArticleWrapper();
        wrapper.eq("publish_user_id",writerId);
        Page<Article>  articlePage= new Page<>(page,pageSize);
        Page<Article> res= articleMapper.selectPage(articlePage,wrapper);
        return  new PagedGridResult(res);
    }

    @Override
    public PagedGridResult queryHotListOfWriter(String writerId) {
        QueryWrapper<Article> wrapper = setDefaultArticleWrapper();
        wrapper.eq("publish_user_id",writerId);
        //分页数据 只查5调
        Page<Article> page = new Page<>(1,5);
        Page<Article> res= articleMapper.selectPage(page,wrapper);
        return new PagedGridResult(res);
    }

    @Override
    public ArticleDetailVO queryDetail(String articleId) {
        QueryWrapper<Article> wrapper= setDefaultArticleWrapper();
        wrapper.eq("id",articleId);
        Article res=  articleMapper.selectOne(wrapper);
        ArticleDetailVO detailVO=new ArticleDetailVO();
        //属性不匹配的解决方案
        // 1修改Article属性或手动赋值（推荐）
        BeanUtils.copyProperties(res,detailVO);
        // 手动为属性赋值
        detailVO.setCover(res.getArticleCover());

        return  detailVO;
    }


    private QueryWrapper<Article> setDefaultArticleWrapper(){
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("publish_time");
        /**
         * 查询文章的自带隐性查询条件
         * isAppoint=即时发布，表示文章已经直接发布
         * isDelete=未删除，表示文章只能显示未删除
         * articleStatus=审核通过，表示只有文章经过机审/人工审核后才能展示
         */

        wrapper.eq("is_delete", YesOrNo.NO.type).eq("is_appoint",YesOrNo.NO.type).eq("article_status", ArticleReviewStatus.SUCCESS.type);

      return  wrapper;

    }
}
