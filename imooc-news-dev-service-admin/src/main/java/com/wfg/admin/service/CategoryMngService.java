package com.wfg.admin.service;

import com.wfg.pojo.AdminUser;
import com.wfg.pojo.Category;
import com.wfg.pojo.bo.NewAdminBO;
import com.wfg.result.PagedGridResult;

import java.util.List;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-22 17:17
 */
public interface CategoryMngService {

    /**
     * 新增或更改文章分类信息
     *
     * @return
     */
    public void insertOrUpdateCategory(Category category);
    /**
     * 查询文章分类信息
     *
     * @return
     */
    public List<Category> queryCategory();
    /**
     * 根据文章名称查询文章
     */
    public Category queryCategoryByName(Category category);

}
