package com.wfg.admin.controller;

import com.wfg.admin.service.FriendLinkService;
import com.wfg.api.BaseController;
import com.wfg.api.controller.admin.FriendLinkControllerApi;
import com.wfg.pojo.bo.SaveFriendLinkBO;
import com.wfg.pojo.mo.FriendLinkMO;
import com.wfg.result.GraceJSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.Map;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-08 19:51
 */
@RestController
public class FriendLinkController extends BaseController implements FriendLinkControllerApi {

   final static Logger logger = LoggerFactory.getLogger(FriendLinkControllerApi.class);
   @Autowired
    FriendLinkService friendLinkService;

    @Override
    public GraceJSONResult saveOrUpdateFriend(@Valid SaveFriendLinkBO saveFriendLinkBO, BindingResult result) {
        if(result.hasErrors()){
            Map<String,String> map=   getErrors(result);
             return  GraceJSONResult.errorMap(map);
        }
        FriendLinkMO saveFriendLinkMO =new FriendLinkMO();
        BeanUtils.copyProperties(saveFriendLinkBO,saveFriendLinkMO);
        saveFriendLinkMO.setCreateTime(new Date());
        saveFriendLinkMO.setUpdateTime(new Date());

        //更新
        friendLinkService.saveOrUpdateFriendLink(saveFriendLinkMO);
        return  GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult getFriendLinkList() {
        return GraceJSONResult.ok(friendLinkService.queryAllFriendLinkedList());
    }

    @Override
    public GraceJSONResult delete(String linkId) {
        friendLinkService.delete(linkId);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult queryPortalAllFriendLinkList() {
       return GraceJSONResult.ok(friendLinkService.queryIndexAllFriendLinkList());
    }
}
