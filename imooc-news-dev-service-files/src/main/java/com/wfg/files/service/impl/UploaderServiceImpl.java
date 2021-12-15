package com.wfg.files.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.fasterxml.jackson.databind.BeanProperty;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.wfg.files.resource.FileResource;
import com.wfg.files.service.UploaderService;
import com.wfg.utils.extend.AliyunResource;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-21 10:25
 */
@Service
public class UploaderServiceImpl implements UploaderService {
   @Autowired
   public FastFileStorageClient fastFileStorageClient;
   @Autowired
   public FileResource fileResource;

   @Autowired
   public AliyunResource aliyunResource;

   @Autowired
   public Sid sid;
    /**
     * 文件上传
     * @param file
     * @param fileExtName
     * @return
     * @throws IOException
     */
    @Override
    public String uploadFdfs(MultipartFile file,String fileExtName) throws IOException {
       StorePath storePath= fastFileStorageClient.uploadFile(file.getInputStream(),
                                              file.getSize(),
                                                fileExtName,
                                                null);

       return  storePath.getFullPath();

    }

    @Override
    public String uploadOss(MultipartFile file, String userId, String fileExtName) throws IOException {
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = fileResource.getEndpoint();
       // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = aliyunResource.getAccessKeyID();
        String accessKeySecret = aliyunResource.getAccessKeySecret();

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 填写网络流地址。
        InputStream inputStream = file.getInputStream();


        // 依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir/exampleobject.txt）。Object完整路径中不能包含Bucket名称。
        String  fileName=sid.nextShort();
        //images/userId/dog.png
        String myObjectName =fileResource.getObjectName()+"/"+userId+"/"+fileName+"."+fileExtName;
        ossClient.putObject(fileResource.getBucketName(), myObjectName, inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
        return  myObjectName;
    }
}
