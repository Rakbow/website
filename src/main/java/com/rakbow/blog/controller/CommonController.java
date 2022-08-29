package com.rakbow.blog.controller;

import com.alibaba.fastjson2.JSONObject;
import com.rakbow.blog.data.ProductClass;
import com.rakbow.blog.entity.Product;
import com.rakbow.blog.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * @Project_name: blog
 * @Author: Rakbow
 * @Create: 2022-08-07 22:36
 * @Description:
 */
@Controller
@RequestMapping(path = "/common")
public class CommonController {

    @Autowired
    private ProductService productService;

    /**
     * 根据系列id获取该系列所有产品
     * */
    @RequestMapping(path = "/getProducts/{seriesId}", method = RequestMethod.GET)
    @ResponseBody
    public List<JSONObject> getAllProductBySeriesId(@PathVariable("seriesId") int seriesId){
        List<JSONObject> productSet = new ArrayList<>();
        for(Product product : productService.selectAllProductsBySeriesId(seriesId)){
            JSONObject jo = new JSONObject();
            jo.put("label",product.getNameZh() +
                    "(" + ProductClass.getNameByIndex(product.getClassification()) + ")");
            jo.put("value",product.getId());
            productSet.add(jo);
        }
        return productSet;
    }

}
