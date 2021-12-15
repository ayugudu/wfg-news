package com.wfg.article.service;

import com.wfg.pojo.Article;
import com.wfg.pojo.vo.ArticleDetailVO;
import com.wfg.result.PagedGridResult;

import java.util.List;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-28 16:08
 */
public interface CommentPortalService {




    /**
     * 发表评论
     * @return
     */
    public void  createComment(String articleId,
                               String fatherCommentId,
                               String content,
                               String userId,
                               String nickName,
                               String face);



    /**
     * 查询评论列表
     * @return
     */
    public PagedGridResult queryArticleComments(String articleId, Integer page, Integer pageSize);

    /**
     * 查询评论列表
     * @return
     */
    public PagedGridResult mngArticleComments(String writerId, Integer page, Integer pageSize);

    /**
     * 作家删除评论
     */
    public void deleteComment(String writerId,String commentId);

}
