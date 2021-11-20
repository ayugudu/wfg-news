package com.wfg.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wfg.enums.Sex;
import com.wfg.enums.UserStatus;
import com.wfg.exection.GraceException;
import com.wfg.pojo.AppUser;
import com.wfg.pojo.bo.UpdateUserInfoBO;
import com.wfg.result.ResponseStatusEnum;
import com.wfg.user.mapper.AppUserMapper;
import com.wfg.user.service.UserService;
import com.wfg.utils.DateUtil;
import com.wfg.utils.DesensitizationUtil;
import com.wfg.utils.JsonUtils;
import com.wfg.utils.RedisOperator;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-16 12:29
 */
@Service
public class UserServiceImpl   implements UserService {
    @Autowired
    public AppUserMapper appUserMapper;
    @Autowired
    public Sid sid;
    @Autowired
    protected RedisOperator redis;

    public static final String  REDIS_USER_INFO="redis_user_info";

    private final static String USER_FACE="https://p6-tt-ipv6.byteimg.com/origin/pgc-image/f861bab814ac41a1a128e6fe09902642";
    @Override
    public AppUser queryMobileIsExist(String mobile) {
        /**
         * mp 根据mobile 查询是否含有手机号
         */
        QueryWrapper<AppUser> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        return appUserMapper.selectOne(wrapper);
    }

    /**
     * 新增增加 事务
     * @param mobile
     * @return
     */
    @Transactional
    @Override
    public AppUser createUser(String mobile) {
        /**
         * 考虑扩展性
         * 未来业务激增，需要分库分表
         * 数据库表主键id 必须保证全局唯一，不得重复
         */
        AppUser user = new AppUser();
        String userId =sid.nextShort();
        user.setId(userId);
        user.setMobile(mobile);
        user.setNickname("用户:"+ DesensitizationUtil.commonDisplay(mobile));
        user.setFace(USER_FACE);

        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
        user.setSex(Sex.secret.type);
        user.setActiveStatus(UserStatus.INACTIVE.type);


        user.setTotalIncome(0);
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());
        appUserMapper.insert(user);
        return  user;
    }

    @Override
    public AppUser getUser(String userId) {
        return appUserMapper.selectById(userId);
    }

    @Override
    public void updateUserInfo(UpdateUserInfoBO updateUserInfoBO) {
        String userId = updateUserInfoBO.getId();
        // 保证双写一致，先删除redis中的数据，后更新数据库
        redis.del(REDIS_USER_INFO+":"+userId);
        AppUser userInfo = new AppUser();
        BeanUtils.copyProperties(updateUserInfoBO,userInfo);
        userInfo.setUpdatedTime(new Date());
        userInfo.setActiveStatus(UserStatus.ACTIVE.type);
        int result=appUserMapper.updateById(userInfo);

        if(result!=1){
            GraceException.display(ResponseStatusEnum.USER_UPDATE_ERROR);
        }
        // 再次查询用户的最新信息，放入到redis中
        AppUser user  = getUser(userId);
        redis.set(REDIS_USER_INFO+":"+userId, JsonUtils.objectToJson(user));

        // 缓存双删策略
        try{
            Thread.sleep(100);
             redis.del(REDIS_USER_INFO+":"+userId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
