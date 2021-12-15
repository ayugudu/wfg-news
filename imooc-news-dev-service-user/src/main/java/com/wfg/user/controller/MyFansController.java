package com.wfg.user.controller;

import com.wfg.api.BaseController;
import com.wfg.api.controller.user.MyFansControllerApi;
import com.wfg.enums.Sex;
import com.wfg.pojo.vo.FansCountsVO;
import com.wfg.result.GraceJSONResult;
import com.wfg.user.service.MyFanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-30 17:19
 */
@RestController
public class MyFansController extends BaseController implements MyFansControllerApi {
   @Autowired
   private MyFanService myFanService;
    @Override
    public GraceJSONResult isMeFollowThisWriter(String writerId, String fanId) {
      boolean res= myFanService.isMeFollowThisWriter(writerId,fanId);
      return  GraceJSONResult.ok(res);
    }

    @Override
    public GraceJSONResult follow(String writerId, String fanId) {
        myFanService.flow(writerId, fanId);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult unfollow(String writerId, String fanId) {
        myFanService.unfollow(writerId,fanId);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult queryAll(String writerId, Integer page, Integer pageSize) {
        if(page==null){
            page=COMMON_START_PAGE;
        }
        if(pageSize==null){
            pageSize=COMMON_PAGE_SIZE;
        }

        return GraceJSONResult.ok( myFanService.queryMyFansList(writerId, page, pageSize));
    }

    @Override
    public GraceJSONResult queryRatio(String writerId) {
       int manCount = myFanService.queryFansCounts(writerId, Sex.man);
        int womanCount = myFanService.queryFansCounts(writerId, Sex.woman);
        FansCountsVO fansCountsVO = new FansCountsVO();
        fansCountsVO.setManCounts(manCount);
        fansCountsVO.setWomanCounts(womanCount);
        return GraceJSONResult.ok(fansCountsVO);
    }

    @Override
    public GraceJSONResult queryRatioByRegion(String writerId) {
          return GraceJSONResult.ok(myFanService.queryRegionRatioCounts(writerId));
    }
}
