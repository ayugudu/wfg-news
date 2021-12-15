package com.wfg.api.controller.files;

import com.wfg.pojo.bo.NewAdminBO;
import com.wfg.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: imooc-news-dev
 * @description: 文件上传的controller
 * @author: wfg
 * @create: 2021-11-21 10:39
 */

@Api(value="文件上传的controller",tags={"文件上传功能api"})
@RequestMapping("/fs")
public interface FileUploaderControllerApi {

    /**
     * 上传单文件（用户头像）
     * @param userId
     * @param multipartFile
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadFace")
    public GraceJSONResult uploadFace(@RequestParam String userId,
                                      MultipartFile file
                                      ) throws IOException;


    /**
     * 上传多个文件
     * @param userId
     * @param files
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadSomeFiles")
    public GraceJSONResult uploadSomeFiles(@RequestParam String userId,
                                      MultipartFile[] files
    ) throws IOException;







    @ApiOperation(value = "文件上传到mongodb的Gridfs",notes="文件上传到mongodb的gridfs",httpMethod = "POST")
    @PostMapping("/uploadToGridFS")
    public GraceJSONResult uploadGridFs(@RequestBody NewAdminBO newAdminBO,
                                      MultipartFile multipartFile) throws IOException;


    @ApiOperation(value = "读取人脸信息",notes="读取人脸信息",httpMethod = "GET")
    @GetMapping("/readInGridFS")
    public void readInGridFS(@RequestParam String faceId, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 读取64
     * @param faceId
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/readFace64InGridFS")
    public GraceJSONResult readFace64InGridFS(@RequestParam String faceId, HttpServletRequest request, HttpServletResponse response) throws IOException;



}
