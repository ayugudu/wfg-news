package com.wfg.files.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-21 10:24
 */
public interface UploaderService {
  /**
   * @Description:  fdfs 上传文件
   * @Author: wfg
   */
  public String uploadFdfs(MultipartFile file,String fileExtName) throws IOException;

   /**
    *  使用oss上传文件
    * @param file
    * @param fileExtName
    * @return
    * @throws IOException
    */

   public String uploadOss(MultipartFile file,String userId,String fileExtName) throws IOException;

}
