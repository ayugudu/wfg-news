package com.wfg.admin.controller;

import com.wfg.admin.service.CategoryMngService;
import com.wfg.api.BaseController;
import com.wfg.api.controller.admin.CategoryMngControllerApi;
import com.wfg.pojo.Category;
import com.wfg.pojo.bo.CategoryBO;
import com.wfg.result.GraceJSONResult;
import com.wfg.result.ResponseStatusEnum;
import com.wfg.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-08 19:51
 */
@RestController
public class CategoryMngController extends BaseController implements CategoryMngControllerApi {

   final static Logger logger = LoggerFactory.getLogger(CategoryMngController.class);

  @Autowired
  private CategoryMngService categoryMngService;

    @Override
    public GraceJSONResult saveOrUpdateCategory(CategoryBO categoryBO, BindingResult result) {
       //校验
        if(result.hasErrors()){
           Map<String,String>  map= getErrors(result);
           return  GraceJSONResult.errorMap(map);
       }
        Category category = new Category();
        BeanUtils.copyProperties(categoryBO,category);


        //查询是否有重复，进行更新
        if(null!=categoryMngService.queryCategoryByName(category)){
            return  GraceJSONResult.errorCustom(ResponseStatusEnum.CATEGORY_EXIST_ERROR);
        }
        categoryMngService.insertOrUpdateCategory(category);
        return GraceJSONResult.ok();
    }


    @Override
    public GraceJSONResult getCatList() {
        return GraceJSONResult.ok(categoryMngService.queryCategory());
    }

    @Override
    public GraceJSONResult getCats() {
        // 分类显示进行优化，将数据存储到redis中减少mysql压力
        //1 先从redis中查询，如果有则返回，没有则查询数据库后先放缓存
       String allCatJson=redis.get(REDIS_ALL_CATEGORY);
        List<Category> categories = null;
       if(allCatJson==null){
           categories= categoryMngService.queryCategory();
           redis.set(REDIS_ALL_CATEGORY, JsonUtils.objectToJson(categories));
       }else{
           categories=JsonUtils.jsonToList(allCatJson,Category.class);
       }


        return GraceJSONResult.ok(categories);
    }
}
