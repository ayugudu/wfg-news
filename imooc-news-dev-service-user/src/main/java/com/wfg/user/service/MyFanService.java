package com.wfg.user.service;

import com.wfg.enums.Sex;
import com.wfg.pojo.vo.RegionRatioVO;
import com.wfg.result.PagedGridResult;

import java.util.List;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-30 17:20
 */
public interface MyFanService {

    /**
     * 查询当前用户是否关注作家
     */

    public boolean isMeFollowThisWriter(String writerId,String fanId);

    /**
     * 用户关注作家
     */

    public void flow(String writerId,String fanId);
    /**
     * 用户取消关注作家
     */

    public void unfollow(String writerId,String fanId);

    /**
     * 查询我的粉丝列表
     */

    public PagedGridResult queryMyFansList(String writerId,Integer page,Integer pageSize);


    /**
     * 查询粉丝数
     */

    public Integer queryFansCounts(String writerId, Sex sex);

    /**
     * 查询粉丝数
     */

    public List<RegionRatioVO> queryRegionRatioCounts(String writerId);




}
