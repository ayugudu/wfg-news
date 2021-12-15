package com.wfg.result;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;


import java.util.List;

/**
 * @program: imooc-news-dev
 * @description: 页面结果集封装
 * @author: wfg
 * @create: 2021-11-23 21:54
 */
@Data
public class PagedGridResult {
    //当前页数
    private long page;
    //总页数
    private long total;
    //总记录数
    private long records;
    //每行显示内容
    private List<?> rows;

    //页面结果集封装
    public PagedGridResult(Page<?> page){

        //设置当前页数
       this.page=page.getCurrent();
        //设置总页数
       this.total=page.getPages();
        //设置总记录数
        this.records=page.getTotal();
        //设置查询结果
        this.rows=page.getRecords();

    }


    public PagedGridResult(){

    }

}
