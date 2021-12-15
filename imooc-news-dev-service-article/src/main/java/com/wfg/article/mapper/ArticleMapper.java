package com.wfg.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wfg.pojo.Article;
import org.springframework.stereotype.Component;

/**
 * @program: imooc-news-dev
 * @description: mp 自定义mapper
 * @author: wfg
 * @create: 2021-11-28 16:06
 */
@Component
public interface ArticleMapper extends BaseMapper<Article> {

    public void updateAppointToPublish();
}
