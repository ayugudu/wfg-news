package com.wfg.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @program: imooc-news-dev
 * @description: 文章分类列表
 * @author: wfg
 * @create: 2021-11-27 19:28
 */


public class Category {
    @TableId(type = IdType.AUTO)
    private  Integer id;
    private  String name;
    private  String tagColor;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTagColor() {
        return tagColor;
    }

    public void setTagColor(String tagColor) {
        this.tagColor = tagColor;
    }
}
