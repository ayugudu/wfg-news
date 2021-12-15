package com.wfg.pojo.bo;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-27 19:30
 */
@Data
public class CategoryBO {
    Integer id;
    @NotBlank(message = "文章分类名称不能为空！")
    private  String name;
    @NotBlank(message = "文章分类颜色属性不能为空！")
    private  String tagColor;
}
