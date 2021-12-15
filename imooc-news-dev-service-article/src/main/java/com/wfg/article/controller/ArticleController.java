package com.wfg.article.controller;

import com.wfg.api.BaseController;
import com.wfg.api.config.RabbitMQConfig;
import com.wfg.api.controller.article.ArticleControllerApi;
import com.wfg.article.service.ArticleService;
import com.wfg.enums.ArticleCoverType;
import com.wfg.enums.ArticleReviewStatus;
import com.wfg.enums.YesOrNo;
import com.wfg.exection.GraceException;
import com.wfg.pojo.Article;
import com.wfg.pojo.Category;
import com.wfg.pojo.bo.NewArticleBO;
import com.wfg.pojo.vo.AppUserVO;
import com.wfg.pojo.vo.ArticleDetailVO;
import com.wfg.result.GraceJSONResult;
import com.wfg.result.PagedGridResult;
import com.wfg.result.ResponseStatusEnum;
import com.wfg.utils.JsonUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-28 15:39
 */
@RestController
public class ArticleController extends BaseController implements ArticleControllerApi {


    @Autowired
    private ArticleService articleService;

    //静态页面生成的路径
    @Value("${freemarker.html.article}")
    private String articlePath;


    @Override
    public GraceJSONResult saveOrUpdateCategory(NewArticleBO newArticleBO,
                                                BindingResult result) {
      //进行参数校验
        if(result.hasErrors()){
            Map<String,String> errorMap = getErrors(result);
            return  GraceJSONResult.errorMap(errorMap);
        }
     //判断封面类型，单图必填,纯文字则设置为空
        if(newArticleBO.getArticleType()== ArticleCoverType.ONE_IMAGE.type){
           if(StringUtils.isBlank(newArticleBO.getArticleCover())){
             return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_COVER_NOT_EXIST_ERROR);
           }
        }else if(newArticleBO.getArticleType()==ArticleCoverType.WORDS.type){
            newArticleBO.setArticleCover("");
        }
       //判断分类id是否存在：可以通过判断查询缓存里面的内容即可
      String allCatJson= redis.get(REDIS_ALL_CATEGORY);
        Category temp=null;
      if(StringUtils.isBlank(allCatJson)){
          return  GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
      }else{
          List<Category> categories = JsonUtils.jsonToList(allCatJson,Category.class);
          for(Category category : categories){
             if( category.getId()== newArticleBO.getCategoryId()){
                 temp=category;
                 break;
             }
          }
         if(temp==null){
             return  GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_CATEGORY_NOT_EXIST_ERROR);
         }
      }
      //表示阿里云审核通过直接创建静态html
      String resHtmlId =articleService.createArticle(newArticleBO,temp);
      if(resHtmlId!=null){
          try {
          //    articleService.createArticleHTML(resHtmlId);

              //生成html 进行解耦
            String articleMongoId=articleService.createArticleHTMLToGridFs(resHtmlId);
             // 存储对应文章到数据库
              articleService.updateArticleToGridFs(resHtmlId,articleMongoId);
              //调用消费端，执行下载Html
//              doDownloadArticleHtml(resHtmlId,articleMongoId);
              //发送消息到mq队列 ，让消费者监听监听消息，执行下载html
              doDownloadArticleHtmlByMQ(resHtmlId,articleMongoId);
          } catch (IOException e) {
              e.printStackTrace();
          } catch (TemplateException e) {
              e.printStackTrace();
          }
      }
      return  GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult queryMyList(String userId, String keyword, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize) {

     if(StringUtils.isBlank(userId)){
         return  GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_QUERY_PARAMS_ERROR);
     }

     if(page==null){
       page =COMMON_START_PAGE;
     }

     if(pageSize==null){
         pageSize=COMMON_PAGE_SIZE;
     }


     //查询我的列表，调用service
      PagedGridResult gridResult= articleService.queryMyArticleList(userId, keyword, status, startDate, endDate, page, pageSize);
      return  GraceJSONResult.ok(gridResult);



    }

    @Override
    public GraceJSONResult queryAllList(Integer status, Integer page, Integer pageSize) {
        if(page==null){
            page=COMMON_START_PAGE;
        }
        if(pageSize==null){
            pageSize=COMMON_PAGE_SIZE;
        }
        // z



        return GraceJSONResult.ok(articleService.queryByStatus(status, page, pageSize));
    }

    @Override
    public GraceJSONResult doReview(String articleId, Integer passOrNot) {
        Integer pendingStatus=null;
        if(passOrNot== YesOrNo.YES.type){
            //审核成功
            pendingStatus=ArticleReviewStatus.SUCCESS.type;
        }else if(passOrNot == YesOrNo.NO.type){
            //审核失败
            pendingStatus=ArticleReviewStatus.FAILED.type;
        }else{
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }
        //更改文章状态
          articleService.updateArticleStatus(articleId,pendingStatus);

        if(pendingStatus ==ArticleReviewStatus.SUCCESS.type){
          // 审核成功 生成文章详情页 静态html
            try {
               // articleService.createArticleHTML(articleId);
                    //生成html 进行解耦
                String mongoId= articleService.createArticleHTMLToGridFs(articleId);
                //生成mongoId 将数据保存到mongo中
               articleService.updateArticleToGridFs(articleId,mongoId);
                // doDownloadArticleHtml(resHtmlId,articleMongoId);
                //发送消息到mq队列 ，让消费者监听监听消息，执行下载html
                doDownloadArticleHtmlByMQ(articleId,mongoId);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TemplateException e) {
                e.printStackTrace();
            }
        }


        return GraceJSONResult.ok();
    }

   private void doDownloadArticleHtml(String articleId,String articleMongoId){
        String url =
                "http://html.imoocnews.com:8002/article/html/download?articleId="+articleId+"&articleMongoId="+articleMongoId;
      ResponseEntity<Integer> responseEntity=  restTemplate.getForEntity(url,Integer.class);
       int status= responseEntity.getBody();
       if(status!= HttpStatus.OK.value()){
           GraceException.display(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
       }

    }
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private void doDownloadArticleHtmlByMQ(String articleId,String articleMongoId){
      rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ARTICLE,
                                            "article.download.do",
                                       articleId+","+articleMongoId);

    }


    private void doDeleteArticleHtml(String articleId,String articleMongoId){
        String url =
                "http://html.imoocnews.com:8002/article/html/delete?articleId="+articleId+"&articleMongoId="+articleMongoId;
        ResponseEntity<Integer> responseEntity=  restTemplate.getForEntity(url,Integer.class);
        int status= responseEntity.getBody();
        if(status!= HttpStatus.OK.value()){
            GraceException.display(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }

    }

    @Override
    public GraceJSONResult withdraw(String userId, String articleId) {

        articleService.withdrawArticle(userId,articleId);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult delete(String userId, String articleId) {

        articleService.deleteArticle(userId,articleId);


        return GraceJSONResult.ok();
    }


}

