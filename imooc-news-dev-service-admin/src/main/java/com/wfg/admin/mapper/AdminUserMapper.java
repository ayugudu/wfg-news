package com.wfg.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wfg.pojo.AdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-22 17:15
 */
@Repository
public interface AdminUserMapper extends BaseMapper<AdminUser>{
}
