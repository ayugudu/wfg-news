package com.wfg.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mongodb.client.gridfs.GridFSBucket;
import com.wfg.api.config.RabbitMQConfig;
import com.wfg.api.config.RabbitMQDelayConfig;
import com.wfg.api.service.BaseService;
import com.wfg.article.mapper.ArticleMapper;
import com.wfg.article.service.ArticleService;
import com.wfg.enums.ArticleAppointType;
import com.wfg.enums.ArticleReviewLevel;
import com.wfg.enums.ArticleReviewStatus;
import com.wfg.enums.YesOrNo;
import com.wfg.exection.GraceException;
import com.wfg.pojo.Article;
import com.wfg.pojo.Category;
import com.wfg.pojo.bo.NewArticleBO;
import com.wfg.pojo.vo.ArticleDetailVO;
import com.wfg.result.GraceJSONResult;
import com.wfg.result.PagedGridResult;
import com.wfg.result.ResponseStatusEnum;
import com.wfg.utils.DateUtil;
import com.wfg.utils.JsonUtils;
import com.wfg.utils.extend.AliTextReviewUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.checkerframework.checker.units.qual.A;
import org.n3r.idworker.Sid;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Watchable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-28 16:12
 */
@Service
public class ArticleServiceImpl extends BaseService implements ArticleService {
   @Autowired
   private ArticleMapper articleMapper;
    @Autowired
    protected RestTemplate restTemplate;

   @Autowired
   private AliTextReviewUtils aliTextReviewUtils;

   @Autowired
   private Sid sid;
  @Autowired
  private RabbitTemplate rabbitTemplate;

    //???????????????????????????
    @Value("${freemarker.html.article}")
    private String articlePath;

    @Transactional
    @Override
    public String createArticle(NewArticleBO newArticleBO, Category category) {

        String resHtmlId=null;
        String articleId= sid.nextShort();

        Article article =new Article();
       BeanUtils.copyProperties(newArticleBO,article);
       article.setId(articleId);
       article.setCategoryId(category.getId());
       article.setCommentCounts(0);
       article.setReadCounts(0);
       article.setIsDelete(YesOrNo.NO.type);
       article.setArticleStatus(ArticleReviewStatus.REVIEWING.type);
       article.setCreateTime(new Date());
       article.setUpdateTime(new Date());
       //??????????????????
       if (article.getIsAppoint()== ArticleAppointType.TIMING.type) {
        article.setPublishTime(newArticleBO.getPublishTime());
       }else if(article.getIsAppoint()==ArticleAppointType.IMMEDIATELY.type){
           article.setPublishTime(new Date());
       }
       int res= articleMapper.insert(article);

       //?????????????????????mq????????????????????????????????????????????????????????????????????????
       if(article.getIsAppoint()==ArticleAppointType.TIMING.type){
           Date endDate = newArticleBO.getPublishTime();
           Date startDate = new Date();
           int delayTime = (int)(endDate.getTime()-startDate.getTime());

           System.out.println(DateUtil.timeBetween(startDate, endDate));
           // ?????????
           MessagePostProcessor messagePostProcessor = new MessagePostProcessor(){
               @Override
               public Message postProcessMessage(Message message) throws AmqpException {
                   //?????????????????????
                   message.getMessageProperties()
                           .setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                   //????????????????????????????????????ms??????
                   message.getMessageProperties()
                           .setDelay(delayTime);
                   return  message;
               }
           };

           rabbitTemplate.convertAndSend(
                   RabbitMQDelayConfig.EXCHANGE_DELAY,
                   "publish.delay.display",articleId,messagePostProcessor

           );
           System.out.println("????????????-??????????????????"+new Date());


       }


       if(res!=1){
           GraceException.display(ResponseStatusEnum.ARTICLE_CREATE_ERROR);
       }
        // ??????AL??????????????????
       String review=  aliTextReviewUtils.reviewTextContent(newArticleBO.getContent());
       // String review = ArticleReviewLevel.REVIEW.type;
       if(review.equalsIgnoreCase(ArticleReviewLevel.PASS.type)){
         //???????????????????????????????????????????????????
           this.updateArticleStatus(articleId,ArticleReviewStatus.SUCCESS.type);
           //??????????????????????????????
           resHtmlId=article.getId();
       }else if(review.equalsIgnoreCase(ArticleReviewLevel.REVIEW.type)){
        // ??????????????????????????????????????????????????????
           this.updateArticleStatus(articleId,ArticleReviewStatus.WAITING_MANUAL.type);

       }else if(review.equalsIgnoreCase(ArticleReviewLevel.BLOCK.type)){
           //???????????????????????????????????????????????????
           this.updateArticleStatus(articleId,ArticleReviewStatus.FAILED.type);
       }


       return  resHtmlId;

    }




    // ????????????Html

    @Override
    public void createArticleHTML(String articleId) throws IOException, TemplateException {
        // 0 ??????freemarker ????????????
        Configuration cfg = new Configuration(Configuration.getVersion());
        // ??????freeMARKER???????????????????????????????????????
        String classPath = this.getClass().getResource("/").getPath();
        cfg.setDirectoryForTemplateLoading(new File(classPath+"templates"));

        Template template = cfg.getTemplate("detail.ftl","utf-8");

        //???????????????????????????
        ArticleDetailVO detailVO = getArticleDetail(articleId);
        Map<String,Object> map  = new HashMap<>();
        map.put("articleDetail",detailVO);

        File tempDic = new File(articlePath);
        if(!tempDic.exists()){
            tempDic.mkdirs();
        }
        articlePath= articlePath+File.separator+detailVO.getId()+".html";
        Writer out  = new FileWriter(articlePath);
        template.process(map,out);
        out.close();


    }

    //??????html ???????????????mangodb??????GridFs
    @Autowired
    private GridFSBucket gridFSBucket;
    @Override
    public String createArticleHTMLToGridFs(String articleId) throws IOException, TemplateException {
        // 0 ??????freemarker ????????????
        Configuration cfg = new Configuration(Configuration.getVersion());
        // ??????freeMARKER???????????????????????????????????????
        String classPath = this.getClass().getResource("/").getPath();
        cfg.setDirectoryForTemplateLoading(new File(classPath+"templates"));

        Template template = cfg.getTemplate("detail.ftl","utf-8");

        //???????????????????????????
        ArticleDetailVO detailVO = getArticleDetail(articleId);
        Map<String,Object> map  = new HashMap<>();
        map.put("articleDetail",detailVO);
       //??????html??????
       String htmlContent= FreeMarkerTemplateUtils.processTemplateIntoString(template,map);
       InputStream inputStream= IOUtils.toInputStream(htmlContent);

       //?????????mongodb
       ObjectId fileId= gridFSBucket.uploadFromStream(detailVO.getId()+".html",inputStream);
       return fileId.toString();
    }

    //?????????????????? ????????????????????????
    private ArticleDetailVO getArticleDetail(String articleId) {
        String url = "http://www.imoocnews.com:8001/portal/article/detail?articleId=" + articleId;
        ResponseEntity<GraceJSONResult> responseEntity
                = restTemplate.getForEntity(url, GraceJSONResult.class);
        GraceJSONResult bodyResult = responseEntity.getBody();
        ArticleDetailVO detailVO = null;
        if (bodyResult.getStatus() == 200) {
            String detail = JsonUtils.objectToJson(bodyResult.getData());
            detailVO = JsonUtils.jsonToPojo(detail, ArticleDetailVO.class);
        }
        return  detailVO;
    }


       @Transactional
    @Override
    public void updateAppointToPublish() {
        articleMapper.updateAppointToPublish();
    }

    @Transactional
    @Override
    public void updateArticleToPublish(String articleId) {
        Article article = new Article();
        article.setId(articleId);
        article.setIsAppoint(ArticleAppointType.IMMEDIATELY.type);
        articleMapper.updateById(article);

    }

    @Override
    public PagedGridResult queryMyArticleList(String userId,
                                              String keyword,
                                              Integer status,
                                              Date startDate,
                                              Date endDate,
                                              Integer page,
                                              Integer pageSize) {
        QueryWrapper<Article> wrapper = new QueryWrapper();
        wrapper.orderByDesc("create_time");
        wrapper.eq("publish_user_id",userId);
        if(StringUtils.isNotBlank(keyword)){
            wrapper.like("title",keyword);
        }

        if(ArticleReviewStatus.isArticleStatusValid(status)){
            wrapper.eq("article_status",status);
        }
        if(status!=null &&status==12){
            wrapper.eq("article_status",ArticleReviewStatus.REVIEWING.type).or().eq("article_status",ArticleReviewStatus.WAITING_MANUAL.type);
        }
          wrapper.eq("is_delete",YesOrNo.NO.type);
        if(startDate !=null){
            wrapper.ge("publish_time",startDate);
        }
        if(endDate !=null){
            wrapper.le("publish_time",endDate);
        }
        Page<Article> articlePage =  new Page<>(page,pageSize);

        return new PagedGridResult( articleMapper.selectPage(articlePage,wrapper));
    }

    @Transactional
    @Override
    public void updateArticleStatus(String articleId, Integer pendingStatus) {
         Article article= new Article();
         article.setId(articleId);
         article.setArticleStatus(pendingStatus);
        int res= articleMapper.updateById(article);
        if(res!=1){
            GraceException.display(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }


    }

    @Override
    public PagedGridResult queryByStatus(Integer status, Integer page, Integer pageSize) {
         QueryWrapper<Article> wrapper = new QueryWrapper<>();
         wrapper.orderByDesc("create_time");
         wrapper.eq("is_delete",YesOrNo.NO.type);
         if(ArticleReviewStatus.isArticleStatusValid(status)){
             wrapper.eq("article_status",status);
         }
         if(status!=null && status==12){
             wrapper.eq("article_status",ArticleReviewStatus.REVIEWING.type).or().eq("article_status",ArticleReviewStatus.WAITING_MANUAL.type);
         }


        Page<Article> articlePage = new Page<>(page,pageSize);

        return new PagedGridResult(articleMapper.selectPage(articlePage,wrapper));

    }

    @Override
    public void deleteArticle(String userId, String articleId) {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("id",articleId);
        wrapper.eq("publish_user_id",userId);
        Article article=new Article();
        article.setIsDelete(YesOrNo.YES.type);
        int res=articleMapper.update(article,wrapper);
        if(res!=1){
            GraceException.display(ResponseStatusEnum.ARTICLE_DELETE_ERROR);
        }
        //????????????
       deleteHtml(articleId);
    }

    /**
     * ??????????????????????????????????????????html
     */
    private void deleteHtml(String articleId){
    //1 ???????????????mongoId
     Article article=   articleMapper.selectById(articleId);
     String mongoId = article.getMongoFileId();
    // ??????GridFs????????????
        gridFSBucket.delete(new ObjectId(mongoId));
    //???????????????????????????
  //  doDeleteArticleHTML(articleId);
    //mq ?????????????????????????????????
        doDeleteArticleHtmlByMQ(articleId);
    }

   //???????????????????????????
    private void doDeleteArticleHTML(String articleId) {
        String url = "http://html.imoocnews.com:8002/article/html/delete?articleId=" + articleId;
        ResponseEntity<Integer> responseEntity = restTemplate.getForEntity(url, Integer.class);
        int status = responseEntity.getBody();
        if (status != HttpStatus.OK.value()) {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }

    //mq????????? ??????id ???????????????????????????
    private void doDeleteArticleHtmlByMQ(String articleId){
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ARTICLE,
                "article.delete.do",
                articleId);

    }
    @Override
    public void withdrawArticle(String userId, String articleId) {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("id",articleId);
        wrapper.eq("publish_user_id",userId);
        Article article=new Article();
        article.setArticleStatus(ArticleReviewStatus.WITHDRAW.type);
        int res=articleMapper.update(article,wrapper);
        if(res!=1){
            GraceException.display(ResponseStatusEnum.ARTICLE_WITHDRAW_ERROR);
        }
    }

    @Transactional
    @Override
    public void updateArticleToGridFs(String articleId, String mongoId) {
      Article article = new Article();
      article.setId(articleId);
      article.setMongoFileId(mongoId);
      articleMapper.updateById(article);
    }


}

