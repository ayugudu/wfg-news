package com.wfg.api.controller.admin;

import com.wfg.pojo.bo.AdminLoginBO;
import com.wfg.pojo.bo.NewAdminBO;
import com.wfg.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-08 19:51
 */

@Api(value = "管理员admin维护",tags={"管理员维护功能的controller"})
@RequestMapping("/adminMng")
public interface AdminMngControllerApi  {

   final static Logger logger = LoggerFactory.getLogger(AdminMngControllerApi.class);

 @ApiOperation(value="",notes="",httpMethod = "POST")
 @PostMapping("/adminLogin")
   public GraceJSONResult adminLogin(@RequestBody AdminLoginBO adminLoginBO ,

                                     BindingResult result,
                            HttpServletRequest request,
                            HttpServletResponse response);


    @ApiOperation(value="查询admin用户是否存在",notes="查询admin用户是否存在",httpMethod = "POST")
    @PostMapping("/adminIsExist")
    public GraceJSONResult adminIsExist(@RequestBody String userName);


    @ApiOperation(value="创建admin",notes="创建admin",httpMethod = "POST")
    @PostMapping("/addNewAdmin")
    public GraceJSONResult addNewAdmin(@RequestBody NewAdminBO newAdminBO,BindingResult result, HttpServletRequest request, HttpServletResponse response);



    @ApiOperation(value="查询admin列表",notes="查询admin列表",httpMethod = "POST")
    @PostMapping("/getAdminList")
    public GraceJSONResult getAdminList(
            @ApiParam(name="page",value = "查询下一页的第几页",required = false)
              @RequestParam  Integer page,
            @ApiParam(name="pageSize",value = "分页查询每一页的条数",required = false)
             @RequestParam   Integer pageSize);

    @ApiOperation(value="admin退出登录",notes="admin退出登录",httpMethod = "POST")
    @PostMapping("/adminLogout")
    public GraceJSONResult adminLogout(@RequestParam String adminId,HttpServletRequest request,HttpServletResponse response);

    @ApiOperation(value="admin管理员的人脸登录",notes="admin管理员人脸登录",httpMethod = "POST")
    @PostMapping("/adminFaceLogin")
    public GraceJSONResult adminFaceLogin(@RequestBody AdminLoginBO adminLoginBO,HttpServletRequest request,HttpServletResponse response);


}
