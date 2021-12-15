package com.wfg.admin.repository;

import com.wfg.pojo.mo.FriendLinkMO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: imooc-news-dev
 * @description: mongodb 持久层交互
 * @author: wfg
 * @create: 2021-11-27 17:54
 */
@Repository
public interface FriendLinkRepository extends MongoRepository<FriendLinkMO,String> {
     public List<FriendLinkMO>  getAllByIsDelete(Integer isDelete);
}
