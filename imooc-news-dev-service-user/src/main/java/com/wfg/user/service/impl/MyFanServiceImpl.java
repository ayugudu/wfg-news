package com.wfg.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wfg.api.service.BaseService;
import com.wfg.enums.Sex;
import com.wfg.pojo.AppUser;
import com.wfg.pojo.Fans;
import com.wfg.pojo.vo.RegionRatioVO;
import com.wfg.result.PagedGridResult;
import com.wfg.user.mapper.FansMapper;
import com.wfg.user.service.MyFanService;
import com.wfg.user.service.UserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.FileNameMap;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-30 17:21
 */
@Service
public class MyFanServiceImpl extends BaseService implements MyFanService {

    @Autowired
   private FansMapper fansMapper;
   @Autowired
   private UserService userService;
   @Autowired
   private Sid sid;

    @Override
    public boolean isMeFollowThisWriter(String writerId, String fanId) {


        //后期会保存到es中
        QueryWrapper<Fans> wrapper= new QueryWrapper<>();
        wrapper.eq("writer_id",writerId).eq("fan_id",fanId);
        return fansMapper.selectCount(wrapper)>0;
    }

    @Transactional
    @Override
    public void flow(String writerId, String fanId) {
         //获得粉丝用户的信息
        AppUser fanInfo= userService.getUser(fanId);
        String  fanPkId = sid.nextShort();
        Fans fans = new Fans();
        fans.setId(fanPkId);
        fans.setFanId(fanId);
        fans.setWriterId(writerId);

        //冗余信息，使用的是宽表
        fans.setFace(fanInfo.getFace());
        fans.setFanNickname(fanInfo.getNickname());
        fans.setSex(fanInfo.getSex());
        fans.setProvince(fanInfo.getProvince());


        fansMapper.insert(fans);
        // 不使用数据库的conunt进行更新而是放到缓存里
        // redis 作家粉丝数增加
        redis.increment(REDIS_WRITER_FANS_COUNTS+":"+writerId,1);
        //redis 当前用户的关注数累加
        redis.increment(REDIS_MY_FOLLOW_COUNTS+":"+fanId,1);


    }

    @Transactional
    @Override
    public void unfollow(String writerId, String fanId) {

        QueryWrapper<Fans> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("writer_id",writerId);
        queryWrapper.eq("fan_id",fanId);
        fansMapper.delete(queryWrapper);
        // redis 作家粉丝数累简
        redis.decrement(REDIS_WRITER_FANS_COUNTS+":"+writerId,1);
        //redis 当前用户的关注数累减
        redis.decrement(REDIS_MY_FOLLOW_COUNTS+":"+fanId,1);

    }

    @Override
    public PagedGridResult queryMyFansList(String writerId, Integer page,Integer pageSize) {

        QueryWrapper<Fans> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("writer_id",writerId);

        Page<Fans> fansPage = new Page<>(page,pageSize);
        fansMapper.selectPage(fansPage,queryWrapper);
        return new PagedGridResult(fansPage);
    }

    @Override
    public Integer queryFansCounts(String writerId, Sex sex) {
        QueryWrapper <Fans> wrapper = new QueryWrapper<>();
        wrapper.eq("writer_id",writerId).eq("sex",sex.type);

        int count = fansMapper.selectCount(wrapper);
        return count;
    }

    public static final String[] regions = {"北京", "天津", "上海", "重庆",
            "河北", "山西", "辽宁", "吉林", "黑龙江", "江苏", "浙江", "安徽", "福建", "江西", "山东",
            "河南", "湖北", "湖南", "广东", "海南", "四川", "贵州", "云南", "陕西", "甘肃", "青海", "台湾",
            "内蒙古", "广西", "西藏", "宁夏", "新疆",
            "香港", "澳门"};

    @Override
    public List<RegionRatioVO> queryRegionRatioCounts(String writerId) {


        List<RegionRatioVO> list = new ArrayList<>();
        for(String r:regions){
            QueryWrapper<Fans> wrapper = new QueryWrapper<>();
            wrapper.eq("writer_id",writerId);
            wrapper.eq("province",r);
            Integer count = fansMapper.selectCount(wrapper);
            RegionRatioVO regionRatioVO = new RegionRatioVO();
            regionRatioVO.setName(r);
            regionRatioVO.setValue(count);
            list.add(regionRatioVO);
        }
        return list;
    }


}
