package com.wfg.article.controller;


import com.wfg.api.BaseController;
import com.wfg.api.controller.article.ArticlePortalControllerApi;
import com.wfg.api.controller.user.UserControllerApi;
import com.wfg.article.service.ArticlePortalService;
import com.wfg.article.service.ArticleService;
import com.wfg.pojo.Article;
import com.wfg.pojo.vo.AppUserVO;
import com.wfg.pojo.vo.ArticleDetailVO;
import com.wfg.pojo.vo.IndexArticleVO;
import com.wfg.result.GraceJSONResult;
import com.wfg.result.PagedGridResult;
import com.wfg.utils.IPUtil;
import com.wfg.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-29 17:40
 */
@RestController
public class ArticlePortalController extends BaseController implements ArticlePortalControllerApi {

    @Autowired
    private ArticlePortalService  articlePortalService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public GraceJSONResult list(String keyword, Integer category, Integer page, Integer pageSize) {
        if(page==null){
            page=COMMON_START_PAGE;
        }
        if(pageSize==null){
            pageSize=COMMON_PAGE_SIZE;
        }
        PagedGridResult res=articlePortalService.queryIndexArticleList(keyword, category, page, pageSize+5);

        /*  //Start
        List<Article> list = (List<Article>) res.getRows();
         //1 构建发布者id列表
        Set<String> idSet = new HashSet<>();

        for(Article a:list){
          idSet.add(a.getPublishUserId());
        }
        //2发起远程调用（restTemplate），请求用户服务获得用户列表
         String  userServerUrlExecute="http://user.imoocnews.com:8003/user/queryByIds?userIds="+ JsonUtils.objectToJson(idSet);
         ResponseEntity<GraceJSONResult> responseEntity
                 =restTemplate.getForEntity(userServerUrlExecute,GraceJSONResult.class);
         GraceJSONResult bodyResult=responseEntity.getBody();

         List<AppUserVO> publisherList =null;
         if(bodyResult.getStatus()==200){
             String userJson=JsonUtils.objectToJson(bodyResult.getData());
             publisherList=JsonUtils.jsonToList(userJson,AppUserVO.class);
         }



        // 3.拼接两个list，重组文章列表
         List<IndexArticleVO> indexArticleVOList = new ArrayList<>();
         for(Article a:list){
             IndexArticleVO indexArticleVO = new IndexArticleVO();
             BeanUtils.copyProperties(a,indexArticleVO);
             //3.1 从publishList获取基本信息
          AppUserVO publisher=  getUserIfPublisher(a.getPublishUserId(), publisherList);
          indexArticleVO.setPublisherVO(publisher);
          indexArticleVOList.add(indexArticleVO);
         }
        // End*/
        res=getIndexArticleVO(res);
        return GraceJSONResult.ok(res);
    }

    @Override
    public GraceJSONResult hotList() {

        return GraceJSONResult.ok(articlePortalService.queryHotList());
    }

    @Override
    public GraceJSONResult queryArticleListOfWriter(String writerId, Integer page, Integer pageSize) {
      if(page==null){
           page=COMMON_START_PAGE;
      }
      if(pageSize==null){
          pageSize=COMMON_PAGE_SIZE;
      }
       // 首先查出作家文章列表
        PagedGridResult res = articlePortalService.queryArticleListOfWriter(writerId, page, pageSize);
      // 在查询出用户信息，并对此进行拼接后返回信息
        res=getIndexArticleVO(res);
        return GraceJSONResult.ok(res);
    }

    @Override
    public GraceJSONResult queryGoodArticleListOfWritert(String writerId) {


        return GraceJSONResult.ok(articlePortalService.queryHotListOfWriter(writerId));
    }

    @Override
    public GraceJSONResult detail(String articleId) {
        ArticleDetailVO articleDetailVO= articlePortalService.queryDetail(articleId);
        HashSet <String> set = new HashSet<>();
        set.add(articleDetailVO.getPublishUserId());
        List<AppUserVO> list =getPublisherList(set);
        if(!list.isEmpty()){
            articleDetailVO.setPublishUserName(list.get(0).getNickname());
        }
        articleDetailVO.setReadCounts(getCountsFromRedis(REDIS_ARTICLE_READ_COUNTS+":"+articleId));
        return GraceJSONResult.ok(articleDetailVO);
    }

    @Override
    public Integer readCounts(String articleId) {

        return  getCountsFromRedis(REDIS_ARTICLE_READ_COUNTS+":"+articleId);
    }

    @Override
    public GraceJSONResult readArticle(String articleId, HttpServletRequest request) {
        String ip = IPUtil.getRequestIp(request);
        //redis 中当前的ip永久存在的key，存入到redis中，表示该ip的用户已经阅读过了，无法累加阅读量
        redis.setnx(REDIS_ALREADY_READ+":"+articleId+":"+ip,ip);
        redis.increment(REDIS_ARTICLE_READ_COUNTS+":"+articleId,1);
        return null;
    }

    private AppUserVO getUserIfPublisher(String publishId,List<AppUserVO> publisherList){
      for(AppUserVO user:publisherList){
          if(user.getId().equalsIgnoreCase(publishId)){
                return  user;
          }
      }
      return  null;
    }
    // 在查询出用户信息，并对此进行拼接后返回信息
     private PagedGridResult getIndexArticleVO(PagedGridResult res){

         //Start
         List<Article> list = (List<Article>) res.getRows();
         //1 构建发布者id列表
         Set<String> idSet = new HashSet<>();
         List<String> articleIdList = new ArrayList<>();
         for(Article a:list){
             //1.1 存储发布者id
             idSet.add(a.getPublishUserId());
             //1.2  存储文章id
             articleIdList.add(REDIS_ARTICLE_READ_COUNTS+":"+a.getId());
         }
             //1.3 redis的mget的批量查询 获取文章阅读数

         List<String> readCountsReadList =redis.mget(articleIdList);
        //2 远程调用 获取用户基本信息
         List<AppUserVO> publisherList =getPublisherList(idSet);

         // 3.拼接两个list，重组文章列表
         List<IndexArticleVO> indexArticleVOList = new ArrayList<>();
         for(int i =0;i<list.size();i++){
             Article a = list.get(i);
             IndexArticleVO indexArticleVO = new IndexArticleVO();
             BeanUtils.copyProperties(a,indexArticleVO);
             //3.1 从publishList获取基本信息
             AppUserVO publisher=  getUserIfPublisher(a.getPublishUserId(), publisherList);
             indexArticleVO.setPublisherVO(publisher);
             // 3.2 重新组装文章阅读数
            String redisCountStr = readCountsReadList.get(i);
            int readCounts=0;
            if(StringUtils.isNotBlank(redisCountStr)){
                readCounts=Integer.valueOf(redisCountStr);
            }
             indexArticleVO.setReadCounts(readCounts);

             indexArticleVOList.add(indexArticleVO);
         }
         // End
         res.setRows(indexArticleVOList);
         return  res;
     }

     //注入服务发现，可以获得已经注册的服务相关
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private UserControllerApi userControllerApi;
     //发起远程调用，获得用户的基本信息
     public List<AppUserVO> getPublisherList(Set idSet){

       /* String serviceId="SERVICE-USER";
         String  userServerUrlExecute=
                 "http://"+serviceId+"/user/queryByIds?userIds="+ JsonUtils.objectToJson(idSet);
         */
         GraceJSONResult bodyResult=  userControllerApi.queryByIds(JsonUtils.objectToJson(idSet));
/*
         List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        ServiceInstance userService= instances.get(0);

        String  userServerUrlExecute=
                 "http://"+userService.getHost()+":"+userService.getPort()+"/user/queryByIds?userIds="+ JsonUtils.objectToJson(idSet);


         ResponseEntity<GraceJSONResult> responseEntity
                   =  restTemplate.getForEntity(userServerUrlExecute,GraceJSONResult.class);
         GraceJSONResult bodyResult = responseEntity.getBody();

         */
         List<AppUserVO> publisherList =null;
         if(bodyResult.getStatus()==200){
           String userJson= JsonUtils.objectToJson(bodyResult.getData());
           publisherList=JsonUtils.jsonToList(userJson,AppUserVO.class);
         }
           return  publisherList;
     }




}
