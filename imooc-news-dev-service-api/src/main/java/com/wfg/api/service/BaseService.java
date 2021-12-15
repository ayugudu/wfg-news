package com.wfg.api.service;

import com.github.pagehelper.PageInfo;

import com.wfg.result.PagedGridResult;
import com.wfg.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

public class BaseService {

    public static final String REDIS_ALL_CATEGORY = "redis_all_category";

    public static final String REDIS_WRITER_FANS_COUNTS = "redis_writer_fans_counts";
    public static final String REDIS_MY_FOLLOW_COUNTS = "redis_my_follow_counts";

    public static final String REDIS_ARTICLE_COMMENT_COUNTS = "redis_article_comment_counts";

    @Autowired
    public RedisOperator redis;



}
