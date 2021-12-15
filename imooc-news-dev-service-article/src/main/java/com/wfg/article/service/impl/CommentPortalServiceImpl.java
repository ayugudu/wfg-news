package com.wfg.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wfg.api.service.BaseService;
import com.wfg.article.mapper.CommentsMapper;
import com.wfg.article.service.ArticlePortalService;
import com.wfg.article.service.CommentPortalService;
import com.wfg.pojo.Comments;
import com.wfg.pojo.vo.ArticleDetailVO;
import com.wfg.pojo.vo.CommentsVO;
import com.wfg.result.PagedGridResult;
import org.checkerframework.checker.units.qual.C;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-12-02 14:43
 */
@Service
public class CommentPortalServiceImpl extends BaseService implements CommentPortalService {
    @Autowired
    private ArticlePortalService articlePortalService;

    @Autowired
    private CommentsMapper commentsMapper;


    @Autowired
    private Sid sid;


    @Transactional
    @Override
    public void createComment(                   String articleId,
                                                 String fatherCommentId,
                                                 String content,
                                                 String userId,
                                                 String nickName,
                                                  String face) {

        ArticleDetailVO articleDetailVO = articlePortalService.queryDetail(articleId);
        Comments comments = new Comments();
        comments.setId(sid.nextShort());
        comments.setWriterId(articleDetailVO.getPublishUserId());
        comments.setArticleTitle(articleDetailVO.getTitle());
        comments.setArticleCover(articleDetailVO.getCover());
        comments.setArticleId(articleId);

        comments.setFatherId(fatherCommentId);
        comments.setCommentUserId(userId);
        comments.setCommentUserNickname(nickName);
        comments.setCommentUserFace(face);


        comments.setContent(content);
        comments.setCreateTime(new Date());

        commentsMapper.insert(comments);
        //评论数累加
        redis.increment(REDIS_ARTICLE_COMMENT_COUNTS+":"+articleId,1);
    }

    /**
     * 采用缓存，代替连表查询 提高并发性能
     * @param articleId
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PagedGridResult queryArticleComments(String articleId, Integer page, Integer pageSize) {

        //查询文章评论
        QueryWrapper wrapper= new QueryWrapper<>().eq("article_id",articleId);
        wrapper.orderByAsc("create_time");
        Page<Comments> page1 = new Page<>(page,pageSize);
        PagedGridResult pagedGridResult = new PagedGridResult (commentsMapper.selectPage(page1,wrapper));
        List<Comments> comments = (List<Comments>) pagedGridResult.getRows();
        // 用于存储并修改评论数据
        Map<String,CommentsVO> map = new HashMap<>();
        for(Comments c: comments){
            CommentsVO cv = new CommentsVO();
            BeanUtils.copyProperties(c,cv);
            cv.setCommentId(c.getId());
            map.put(cv.getCommentId(),cv);
        }

        //返回树形结果集
        List<CommentsVO> res = new ArrayList<>();
        for(Comments c:comments){
            if(c.getFatherId().equals("0")){
                res.add(map.get(c.getId()));
            }else{
               CommentsVO parent= map.get(c.getFatherId());
               parent.setQuoteContent(c.getCommentUserNickname());
               parent.setQuoteContent(c.getContent());
            }
        }
       pagedGridResult.setRows(res);

        return pagedGridResult;
    }

    @Override
    public PagedGridResult mngArticleComments(String writerId, Integer page, Integer pageSize) {
       Page<Comments> commentsPage = new Page<>(page,pageSize);
       QueryWrapper<Comments> wrapper = new QueryWrapper();
       wrapper.eq("writer_id",writerId);
       Page<Comments> res =commentsMapper.selectPage(commentsPage,wrapper);
        return new PagedGridResult(res);
    }

    @Override
    public void deleteComment(String writerId, String commentId) {
        QueryWrapper<Comments> wrapper = new QueryWrapper<>();
        wrapper.eq("id",commentId).eq("writer_id",writerId);
        commentsMapper.delete(wrapper);
    }
}
