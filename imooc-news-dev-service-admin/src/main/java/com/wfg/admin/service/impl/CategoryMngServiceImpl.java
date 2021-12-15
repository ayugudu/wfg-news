package com.wfg.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wfg.admin.mapper.CategoryMngMapper;
import com.wfg.admin.service.CategoryMngService;
import com.wfg.api.service.BaseService;
import com.wfg.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-22 17:17
 */
@Service
public class CategoryMngServiceImpl extends BaseService implements CategoryMngService {

    @Autowired
    CategoryMngMapper categoryMngMapper;

    @Transactional
    @Override
    public void insertOrUpdateCategory(Category category) {
        if(category.getId()==null){
            categoryMngMapper.insert(category);
        }else{
            categoryMngMapper.updateById(category);
        }

        /**
         *由于此数据被缓存
         * 在更新时需要刷新redis，可以通过删除redis，让查询数据走数据库进行更新
         */
        redis.del(REDIS_ALL_CATEGORY);
    }



    @Override
    public List<Category> queryCategory() {
        return categoryMngMapper.selectList(null);
    }

    @Override
    public Category queryCategoryByName(Category category) {
        QueryWrapper<Category> wrapper = new QueryWrapper();
        wrapper.eq("name",category.getName());
       return categoryMngMapper.selectOne(wrapper);
    }
}
