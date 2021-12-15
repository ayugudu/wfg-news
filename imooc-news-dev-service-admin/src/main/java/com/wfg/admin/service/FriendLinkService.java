package com.wfg.admin.service;

import com.wfg.pojo.mo.FriendLinkMO;

import java.util.List;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-27 18:00
 */
public interface FriendLinkService {

    /**
     * 新增或者更新友情链接
     */
    public void saveOrUpdateFriendLink(FriendLinkMO friendLinkMO);

    /**
     * 查询友情链接
     */
    public List<FriendLinkMO> queryAllFriendLinkedList();

    /**
     * 删除友情链接
     */
    public void delete(String linkId);

    /**
     * 首页查询友情链接
     */
    public List<FriendLinkMO>  queryIndexAllFriendLinkList();

}
