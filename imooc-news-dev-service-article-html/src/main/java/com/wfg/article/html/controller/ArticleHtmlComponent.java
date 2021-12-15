package com.wfg.article.html.controller;

import com.mongodb.client.gridfs.GridFSBucket;
import com.wfg.api.controller.article.ArticleHtmlControllerApi;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-12-04 15:16
 */
@Component
public class ArticleHtmlComponent  {
    @Autowired
    private GridFSBucket gridFSBucket;

    @Value("${freemarker.html.article}")
    private String articlePath;

    public Integer download(String articleId, String articleMongoId) throws FileNotFoundException {
        //拼接最终文件的保存的地址
        String path= articlePath+File.separator+articleId+".html";
        // 获取文件流，定义存放的位置和名称
        File file = new File(path);

        // 创建输出流
        OutputStream outputStream = new FileOutputStream(file);
        //执行下载
        gridFSBucket.downloadToStream(new ObjectId(articleMongoId),outputStream);


        return HttpStatus.OK.value();
    }


    public Integer delete(String articleId) {
        //拼接最终文件的保存的地址
        String path= articlePath+File.separator+articleId+".html";
        // 获取文件流，定义存放的位置和名称
        File file = new File(path);
        //删除文章
        if(file.exists()){
            file.delete();
        }
        return HttpStatus.OK.value();
    }
}
