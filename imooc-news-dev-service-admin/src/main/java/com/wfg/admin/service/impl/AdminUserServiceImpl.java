package com.wfg.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.wfg.admin.mapper.AdminUserMapper;
import com.wfg.admin.service.AdminUserService;
import com.wfg.exection.GraceException;
import com.wfg.pojo.AdminUser;
import com.wfg.pojo.bo.NewAdminBO;
import com.wfg.result.PagedGridResult;
import com.wfg.result.ResponseStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-22 17:19
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {
    @Autowired
    AdminUserMapper adminUserMapper;

    @Autowired
    private Sid sid;
    @Override
    public AdminUser queryAdminByUsername(String userName) {
        QueryWrapper<AdminUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username",userName);
        return adminUserMapper.selectOne(wrapper);
    }


    @Transactional
    @Override
    public void createAdminUser(NewAdminBO newAdminBO) {
    String adminId= sid.nextShort();
    AdminUser adminUser =new AdminUser();
    adminUser.setId(adminId);
    adminUser.setUsername(newAdminBO.getUsername());
    adminUser.setAdminName(newAdminBO.getAdminName());
    //密码不我空 则加密存入数据库
    if(StringUtils.isNotBlank(newAdminBO.getPassword())){
        String pwd= BCrypt.hashpw(newAdminBO.getPassword(),BCrypt.gensalt());
        adminUser.setPassword(pwd);
    }
    //如果人脸上传以后，则有faceid,需要和admin信息管理
    if(StringUtils.isNotBlank(newAdminBO.getFaceId())){
        adminUser.setFaceId(newAdminBO.getFaceId());
    }
    adminUser.setCreatedTime(new Date());
    adminUser.setUpdatedTime(new Date());
    int result = adminUserMapper.insert(adminUser);
    if(result!=1){
        GraceException.display(ResponseStatusEnum.ADMIN_CREATE_ERROR);
    }
    }

    @Override
    public PagedGridResult queryAdminList(Integer page, Integer pageSize) {
        //设置查询条件
        QueryWrapper<AdminUser> wrapper =new QueryWrapper<>();
        wrapper.orderByDesc("created_time");
        //查询第page页，每页具有pageSize条数据
        Page<AdminUser> mpPage = new Page<>(page,pageSize);
        //执行分页查询
          Page<AdminUser>userPage= adminUserMapper.selectPage(mpPage,wrapper);
        //获取分页查询结果
        return new PagedGridResult(userPage);

    }


}
