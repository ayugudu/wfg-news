package com.wfg.admin.controller;

import com.wfg.admin.service.AdminUserService;
import com.wfg.api.BaseController;
import com.wfg.api.controller.admin.AdminMngControllerApi;
import com.wfg.enums.FaceVerifyType;
import com.wfg.exection.GraceException;
import com.wfg.pojo.AdminUser;
import com.wfg.pojo.bo.AdminLoginBO;
import com.wfg.pojo.bo.NewAdminBO;
import com.wfg.result.GraceJSONResult;
import com.wfg.result.PagedGridResult;
import com.wfg.result.ResponseStatusEnum;
import com.wfg.utils.FaceVerifyUtils;
import com.wfg.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-22 18:29
 */
@RestController
public class AdminMngController  extends BaseController implements AdminMngControllerApi {

    final static Logger logger = LoggerFactory.getLogger(AdminMngController.class);

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private RedisOperator redis;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    FaceVerifyUtils faceVerifyUtils;
    @Override
    public GraceJSONResult adminLogin(@Valid AdminLoginBO adminLoginBO,BindingResult result, HttpServletRequest request, HttpServletResponse response) {
        // TODO 验证Bo中用户名和密码不为空
        if(result.hasErrors()){
            Map<String,String> map = getErrors(result);
            return  GraceJSONResult.errorMap(map);
        }
        //1 .查询admin

        AdminUser  adminUser  =adminUserService.queryAdminByUsername(adminLoginBO.getUsername());
         // 判断admin 不为空，为空则失败
        if(adminUser==null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);
        }
        // 校验密码是否匹配
        boolean isPwdMatch = BCrypt.checkpw(adminLoginBO.getPassword(),adminUser.getPassword());
        if(isPwdMatch){
            doLoginSetting(adminUser,request,response);
            return  GraceJSONResult.ok();

        }else{
            return  GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);
        }


    }

    @Override
    public GraceJSONResult adminIsExist(String userName) {
        checkAdminExist(userName);
        return  GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult addNewAdmin(@Valid NewAdminBO newAdminBO,
                                       BindingResult result,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {

        // TODO 校验用户名和密码非空

        if(result.hasErrors()){
            Map<String,String> map = getErrors(result);
            return GraceJSONResult.errorMap(map);

        }
                // base64 不为空 则为人脸入库
        if(StringUtils.isBlank(newAdminBO.getImg64() )){
           if(StringUtils.isBlank(newAdminBO.getPassword())|| StringUtils.isBlank(newAdminBO.getConfirmPassword())){
               return  GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_PASSWORD_ERROR);
           }
        }
        //2 密码不为空，则必须判断两次输入一致
        if(StringUtils.isNotBlank(newAdminBO.getPassword())){
            if(!newAdminBO.getPassword().equalsIgnoreCase(newAdminBO.getConfirmPassword())){
                return  GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_PASSWORD_ERROR);
            }
        }
        // 3.校验用户名唯一
         checkAdminExist(newAdminBO.getUsername());
        //4 调用service存入admin信息
         adminUserService.createAdminUser(newAdminBO);
         return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult getAdminList(Integer page, Integer pageSize) {
        if(page==null){
            page=COMMON_START_PAGE;
        }
        if(pageSize==null){
            pageSize=COMMON_PAGE_SIZE;
        }
       PagedGridResult result=adminUserService.queryAdminList(page,pageSize);

        return GraceJSONResult.ok(result);
    }

    /**
     * 注销用户
     * @param adminId
     * @param request
     * @param response
     * @return
     */
    @Override
    public GraceJSONResult adminLogout(String adminId, HttpServletRequest request, HttpServletResponse response) {
        //从redis中删除
        redis.del(REDIS_ADMIN_TOKEN+":"+adminId);
        // 从cookie中清理admin
       delCookie(request,response,"atoken");
       delCookie(request,response,"aid");
       delCookie(request,response,"aname");
       return  GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult adminFaceLogin(AdminLoginBO adminLoginBO, HttpServletRequest request, HttpServletResponse response) {
        // 0判断用户名和人脸信息不能为空
         if(StringUtils.isBlank(adminLoginBO.getUsername())){
             return  GraceJSONResult.exception(ResponseStatusEnum.ADMIN_USERNAME_NULL_ERROR);
         }
         String  tempFace64 =adminLoginBO.getImg64();
         if(StringUtils.isBlank(tempFace64)){
             return  GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_FACE_NULL_ERROR);
         }

        // 1 从数据库中查询出faceId
        AdminUser adminUser= adminUserService.queryAdminByUsername(adminLoginBO.getUsername());
        String adminFaceId= adminUser.getFaceId();
        // 2 请求文件服务，获得人脸base64数据
        if(StringUtils.isBlank(adminFaceId)){
           return  GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_FACE_LOGIN_ERROR);
        }

        String fileServerUrl ="http://files.imoocnews.com:8004/fs/readFace64InGridFS?faceId="+adminFaceId;
        ResponseEntity<GraceJSONResult> responseEntity= restTemplate.getForEntity(fileServerUrl,GraceJSONResult.class);
        GraceJSONResult bodyResult=responseEntity.getBody();
        String base64= (String) bodyResult.getData();
        // 3 调用阿里al进行人脸对比识别
        base64=base64.replaceAll("\r|\n","");
       boolean result=  faceVerifyUtils.faceVerify(tempFace64,base64,60);
      if(!result){
          return  GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_FACE_LOGIN_ERROR);
      }
    // admin 登录后的设置 redis 与token'
    doLoginSetting(adminUser,request,response);

        return GraceJSONResult.ok();
    }

    /**
     * 用于admin 用户登录后的会话信息配置
     * @param adminUser
     * @param request
     * @param response
     */
    private void doLoginSetting(AdminUser adminUser,
                                HttpServletRequest request,HttpServletResponse response){

        //保存token放入到redis中
        String token = UUID.randomUUID().toString();
        redis.set(REDIS_ADMIN_TOKEN+":"+ adminUser.getId(),token);
        //保存admin登录基本token信息到cookie中
        setCookie(request,response,"atoken",token,COOKIE_MONTH);
        setCookie(request,response,"aid",adminUser.getId(),COOKIE_MONTH);
        setCookie(request,response,"aname",adminUser.getAdminName(),COOKIE_MONTH);
    }

    /**
     * 检测用户名是否存在
     * @param username
     */
    private  void checkAdminExist(String username){
        AdminUser adminUser = adminUserService.queryAdminByUsername(username);
        if(adminUser!=null){
            GraceException.display(ResponseStatusEnum.ADMIN_USERNAME_EXIST_ERROR);
        }
    }

}
