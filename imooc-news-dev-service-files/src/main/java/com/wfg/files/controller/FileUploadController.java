package com.wfg.files.controller;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.wfg.api.controller.files.FileUploaderControllerApi;
import com.wfg.exection.GraceException;
import com.wfg.files.resource.FileResource;
import com.wfg.files.service.UploaderService;
import com.wfg.pojo.bo.NewAdminBO;
import com.wfg.result.GraceJSONResult;
import com.wfg.result.ResponseStatusEnum;
import com.wfg.utils.FileUtils;
import com.wfg.utils.extend.AliImageReviewUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-08 19:51
 */
@RestController
public class FileUploadController implements FileUploaderControllerApi {

   final static Logger logger = LoggerFactory.getLogger(HelloController.class);
  @Autowired
    UploaderService uploaderService;

  @Autowired
    FileResource fileResource;
  @Autowired
  private AliImageReviewUtils aliImageReviewUtils;


  @Autowired
  private GridFSBucket gridFSBucket;


    @Override
    public GraceJSONResult uploadFace(String userId, MultipartFile multipartFile) throws IOException {

        String path =null;
        if(multipartFile!=null){
         // 获得文件上传的路径名称
            String fileName =multipartFile.getOriginalFilename();

            //判断文件名不能为空
            if(StringUtils.isNotBlank(fileName)){
                String fileNameArr[] = fileName.split("\\.");
                //获得后缀
                String suffix = fileNameArr[fileNameArr.length-1];
                //判断后缀符合我们的预定义规范
                if(!suffix.equalsIgnoreCase("png")&&
                        !suffix.equalsIgnoreCase("jpg")&&!suffix.equalsIgnoreCase("jpeg") ){
                        return  GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_FORMATTER_FAILD);
                }
                //执行上传
                // path=uploaderService.uploadFdfs(multipartFile,suffix);
                path=uploaderService.uploadOss(multipartFile,userId,suffix);

            }else{
                return  GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
            }
        }else{
            return  GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        logger.info("path:{}",path);

        String finalPath="";
        if(StringUtils.isNotBlank(path)){
            finalPath=fileResource.getOssHost()+path;
        }else{
            return  GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }
       // return GraceJSONResult.ok(doAliImageReview(finalPath));

      return  GraceJSONResult.ok(finalPath);
    }

    @Override
    public GraceJSONResult uploadSomeFiles(String userId, MultipartFile[] files) throws IOException {
        //声明List，用于存方多个图片的地址路径，返回到前端
        List<String> imageUrlList = new ArrayList<>();
        if(files!=null && files.length>0){
            for(MultipartFile multipartFile:files){
                String path =null;
                if(multipartFile!=null){
                    // 获得文件上传的路径名称
                    String fileName =multipartFile.getOriginalFilename();

                    //判断文件名不能为空
                    if(StringUtils.isNotBlank(fileName)){
                        String fileNameArr[] = fileName.split("\\.");
                        //获得后缀
                        String suffix = fileNameArr[fileNameArr.length-1];
                        //判断后缀符合我们的预定义规范
                        if(!suffix.equalsIgnoreCase("png")&&
                                !suffix.equalsIgnoreCase("jpg")&&
                                !suffix.equalsIgnoreCase("jpeg") ){
                            continue;
                        }
                        //执行上传
                        // path=uploaderService.uploadFdfs(multipartFile,suffix);
                        path=uploaderService.uploadOss(multipartFile,userId,suffix);

                    }else{
                        continue;
                    }
                }else{
                    continue;
                }

                logger.info("path:{}",path);

                String finalPath="";
                if(StringUtils.isNotBlank(path)){
                    finalPath=fileResource.getOssHost()+path;
                    //FIXME:放入imagelist之前，需要做一次审核
                    imageUrlList.add(finalPath);
                }else{
                    continue;
                }
                // return GraceJSONResult.ok(doAliImageReview(finalPath));
            }
        }
        return  GraceJSONResult.ok(imageUrlList);
    }

    /**
     * 文件上传到Gridfs
     * @param newAdminBO
     * @param multipartFile
     * @return
     * @throws IOException
     */
    @Override
    public GraceJSONResult uploadGridFs(NewAdminBO newAdminBO, MultipartFile multipartFile) throws IOException {
       //获得图片的base64字符串
       String file64= newAdminBO.getImg64();
       //将base64字符串转换为byte64数组
        byte[] bytes=  new BASE64Decoder().decodeBuffer(file64.trim());
        //转换为输入流
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        //上传的到gridfs
        ObjectId fileId= gridFSBucket.uploadFromStream(newAdminBO.getUsername()+".png",byteArrayInputStream);
        //获得文件在gridfs中的主键id
        String fileIdStr =fileId.toString();

         return GraceJSONResult.ok(fileIdStr);
    }

    /**
     * 从Gridfs中读取图片内容
     * @param faceId
     * @return
     * @throws IOException
     */
    @Override
    public void readInGridFS(String faceId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(StringUtils.isBlank(faceId) || faceId.equalsIgnoreCase("null")){
             GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }
        // 1.从gridfs中读取
       File adminFace= readGridFSByFaceId(faceId);
        // 2把人脸图片输出到浏览器
        FileUtils.downloadFileByStream(response,adminFace);

    }

    @Override
    public GraceJSONResult readFace64InGridFS(String faceId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获得gridfs中人脸文件
          File myFace = readGridFSByFaceId(faceId);
       // 转换人脸为base64
          String base64Face=  FileUtils.fileToBase64(myFace);
        return GraceJSONResult.ok(base64Face);
    }


    private File readGridFSByFaceId(String faceId) throws FileNotFoundException {
        GridFSFindIterable gridFsFiles = gridFSBucket.find(Filters.eq("_id", new ObjectId(faceId)));
        GridFSFile gridFS = gridFsFiles.first();
        if(gridFS==null){
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }
        String fileName =gridFS.getFilename();
        System.out.println(fileName);
        //获取文件流保存文件或者服务器的临时目录
        File fileTmp = new File("/tmp/gridfs");
        //不存在则生成
        if(!fileTmp.exists()){
            fileTmp.mkdirs();
        }
        File myFile = new File("/tmp/gridfs/"+fileName);
         //创建文件输出流
        OutputStream os = new FileOutputStream(myFile);
        //下载到本地
        gridFSBucket.downloadToStream(new ObjectId(faceId),os);
        return myFile;
    }



    //阿里云图片检测
    public final String FAILED_IMAGE_URL="";
    private String doAliImageReview(String pendingImageUrl){
        boolean result=false;
        try {
            result=aliImageReviewUtils.reviewImage(pendingImageUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!result){
            return  FAILED_IMAGE_URL;
        }
        return   pendingImageUrl;
    }


}
