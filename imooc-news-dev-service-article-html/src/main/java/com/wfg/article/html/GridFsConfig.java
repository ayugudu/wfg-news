package com.wfg.article.html;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @program: imooc-news-dev
 * @description: //整合mongodb的gridfsBucket 实现文件上传
 * @author: wfg
 * @create: 2021-11-24 11:20
 */
@Component
public class GridFsConfig {
   
    @Value("${spring.data.mongodb.database}")
    private String mongodb;
   @Bean
    public GridFSBucket gridFSBucket(MongoClient mongoClient){
       MongoDatabase database = mongoClient.getDatabase(mongodb);
       GridFSBucket bucket = GridFSBuckets.create(database);
       return  bucket;
   }

}
