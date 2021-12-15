package com.wfg.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wfg.admin.mapper.AdminUserMapper;
import com.wfg.admin.repository.FriendLinkRepository;
import com.wfg.admin.service.AdminUserService;
import com.wfg.admin.service.FriendLinkService;
import com.wfg.enums.YesOrNo;
import com.wfg.exection.GraceException;
import com.wfg.pojo.AdminUser;
import com.wfg.pojo.bo.NewAdminBO;
import com.wfg.pojo.mo.FriendLinkMO;
import com.wfg.result.PagedGridResult;
import com.wfg.result.ResponseStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class FriendLinkServiceImpl implements FriendLinkService {

    @Autowired
    private FriendLinkRepository friendLinkRepository;
    @Override
    public void saveOrUpdateFriendLink(FriendLinkMO friendLinkMO) {
        //有id就更新，无id则新增
      friendLinkRepository.save(friendLinkMO);
    }

    @Override
    public List<FriendLinkMO> queryAllFriendLinkedList() {

        return friendLinkRepository.findAll();
    }

    @Override
    public void delete(String linkId) {
        friendLinkRepository.deleteById(linkId);
    }

    @Override
    public List<FriendLinkMO> queryIndexAllFriendLinkList() {
      return  friendLinkRepository.getAllByIsDelete(YesOrNo.NO.type);
    }
}
