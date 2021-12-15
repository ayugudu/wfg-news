package com.wfg.article.controller;

import com.wfg.api.BaseController;
import com.wfg.api.controller.article.CommentControllerApi;
import com.wfg.article.service.CommentPortalService;
import com.wfg.pojo.bo.CommentReplyBO;
import com.wfg.pojo.vo.AppUserVO;
import com.wfg.result.GraceJSONResult;
import com.wfg.result.PagedGridResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-08 19:51
 */
@RestController
public class CommentController extends BaseController implements CommentControllerApi {

    @Autowired
    private CommentPortalService commentPortalService;

   final static Logger logger = LoggerFactory.getLogger(CommentController.class);




    @Override
    public GraceJSONResult saveOrUpdateCategory(CommentReplyBO commentReplyBO, BindingResult result) {
       // 0 判断BindResult是否保存错误的验证信息，如果有 则直接return
        if(result.hasErrors()){
            Map<String,String> errorMap = getErrors(result);
            return  GraceJSONResult.errorMap(errorMap);
        }
       //1.根据留言用户的id查询他的昵称，用于存入到数据表进行子段的冗余处理，从而避免多表关联查询性能影响
        String userId= commentReplyBO.getCommentUserId();
        HashSet<String> set=new HashSet<>();
        set.add(userId);
        // 发起远程调用，获取用户基本信息---用户姓名,用户头像
        String nickName=getBasicUserList(set).get(0).getNickname();
        String face=getBasicUserList(set).get(0).getNickname();
        // 3 保存用户评论的信息到数据库
          commentPortalService.createComment(commentReplyBO.getArticleId(),
                                             commentReplyBO.getFatherId(),
                                             commentReplyBO.getContent(),userId,nickName,face);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult commentCounts(String articleId) {
        Integer counts = getCountsFromRedis(REDIS_ARTICLE_COMMENT_COUNTS+":"+articleId);

        return GraceJSONResult.ok(counts);
    }

    @Override
    public GraceJSONResult list(String articleId, Integer page, Integer pageSize) {
        if(page==null){
            page =COMMON_START_PAGE;
        }
        if(pageSize==null){
            pageSize=COMMON_PAGE_SIZE;
        }
       PagedGridResult res= commentPortalService.queryArticleComments(articleId, page, pageSize);
        return GraceJSONResult.ok(res);
    }

    @Override
    public GraceJSONResult mng(String writerId, Integer page, Integer pageSize) {
        if(page==null){
            page=COMMON_START_PAGE;
        }
        if(pageSize==null){
            page=COMMON_PAGE_SIZE;
        }
        return GraceJSONResult.ok(commentPortalService.mngArticleComments(writerId, page, pageSize));
    }

    @Override
    public GraceJSONResult delete(String writerId, String commentId) {
        commentPortalService.deleteComment(writerId, commentId);

        return GraceJSONResult.ok();
    }


}
