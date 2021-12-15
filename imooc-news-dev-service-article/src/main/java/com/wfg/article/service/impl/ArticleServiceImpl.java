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

    //静态页面生成的路径
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
       //设置发布时间
       if (article.getIsAppoint()== ArticleAppointType.TIMING.type) {
        article.setPublishTime(newArticleBO.getPublishTime());
       }else if(article.getIsAppoint()==ArticleAppointType.IMMEDIATELY.type){
           article.setPublishTime(new Date());
       }
       int res= articleMapper.insert(article);

       //发送延迟消息到mq，计算定时发布时间和当前时间的时间差，即延迟时间
       if(article.getIsAppoint()==ArticleAppointType.TIMING.type){
           Date endDate = newArticleBO.getPublishTime();
           Date startDate = new Date();
           int delayTime = (int)(endDate.getTime()-startDate.getTime());

           System.out.println(DateUtil.timeBetween(startDate, endDate));
           // 生产者
           MessagePostProcessor messagePostProcessor = new MessagePostProcessor(){
               @Override
               public Message postProcessMessage(Message message) throws AmqpException {
                   //设置消息的持久
                   message.getMessageProperties()
                           .setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                   //设置消息延迟的时间，单位ms毫秒
                   message.getMessageProperties()
                           .setDelay(delayTime);
                   return  message;
               }
           };

           rabbitTemplate.convertAndSend(
                   RabbitMQDelayConfig.EXCHANGE_DELAY,
                   "publish.delay.display",articleId,messagePostProcessor

           );
           System.out.println("延迟消息-定时发布文章"+new Date());


       }


       if(res!=1){
           GraceException.display(ResponseStatusEnum.ARTICLE_CREATE_ERROR);
       }
        // 阿里AL检测文本内容
       String review=  aliTextReviewUtils.reviewTextContent(newArticleBO.getContent());
       // String review = ArticleReviewLevel.REVIEW.type;
       if(review.equalsIgnoreCase(ArticleReviewLevel.PASS.type)){
         //修改当前的文章，状态标记为审核通过
           this.updateArticleStatus(articleId,ArticleReviewStatus.SUCCESS.type);
           //用于生成文章静态页面
           resHtmlId=article.getId();
       }else if(review.equalsIgnoreCase(ArticleReviewLevel.REVIEW.type)){
        // 修改当前文章，状态标记为需要人工审核
           this.updateArticleStatus(articleId,ArticleReviewStatus.WAITING_MANUAL.type);

       }else if(review.equalsIgnoreCase(ArticleReviewLevel.BLOCK.type)){
           //修改当前文章，状态标记为审核未通过
           this.updateArticleStatus(articleId,ArticleReviewStatus.FAILED.type);
       }


       return  resHtmlId;

    }




    // 文章生成Html

    @Override
    public void createArticleHTML(String articleId) throws IOException, TemplateException {
        // 0 配置freemarker 基本环境
        Configuration cfg = new Configuration(Configuration.getVersion());
        // 声明freeMARKER模板所需要的加载目录的位置
        String classPath = this.getClass().getResource("/").getPath();
        cfg.setDirectoryForTemplateLoading(new File(classPath+"templates"));

        Template template = cfg.getTemplate("detail.ftl","utf-8");

        //获取文章的详情信息
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

    //生成html 并且保存到mangodb中的GridFs
    @Autowired
    private GridFSBucket gridFSBucket;
    @Override
    public String createArticleHTMLToGridFs(String articleId) throws IOException, TemplateException {
        // 0 配置freemarker 基本环境
        Configuration cfg = new Configuration(Configuration.getVersion());
        // 声明freeMARKER模板所需要的加载目录的位置
        String classPath = this.getClass().getResource("/").getPath();
        cfg.setDirectoryForTemplateLoading(new File(classPath+"templates"));

        Template template = cfg.getTemplate("detail.ftl","utf-8");

        //获取文章的详情信息
        ArticleDetailVO detailVO = getArticleDetail(articleId);
        Map<String,Object> map  = new HashMap<>();
        map.put("articleDetail",detailVO);
       //获取html内容
       String htmlContent= FreeMarkerTemplateUtils.processTemplateIntoString(template,map);
       InputStream inputStream= IOUtils.toInputStream(htmlContent);

       //上传到mongodb
       ObjectId fileId= gridFSBucket.uploadFromStream(detailVO.getId()+".html",inputStream);
       return fileId.toString();
    }

    //发起远程调用 获取文章详情数据
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
        //删除文章
       deleteHtml(articleId);
    }

    /**
     * 文章撤回删除后，删除静态化的html
     */
    private void deleteHtml(String articleId){
    //1 查询文章的mongoId
     Article article=   articleMapper.selectById(articleId);
     String mongoId = article.getMongoFileId();
    // 删除GridFs上的文件
        gridFSBucket.delete(new ObjectId(mongoId));
    //删除服务器中的文章
  //  doDeleteArticleHTML(articleId);
    //mq 生产消息做接口间的解耦
        doDeleteArticleHtmlByMQ(articleId);
    }

   //删除服务器上的文章
    private void doDeleteArticleHTML(String articleId) {
        String url = "http://html.imoocnews.com:8002/article/html/delete?articleId=" + articleId;
        ResponseEntity<Integer> responseEntity = restTemplate.getForEntity(url, Integer.class);
        int status = responseEntity.getBody();
        if (status != HttpStatus.OK.value()) {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }

    //mq生产者 发送id 删除服务器上的文章
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

