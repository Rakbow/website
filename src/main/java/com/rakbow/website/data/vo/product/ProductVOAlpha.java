package com.rakbow.website.data.vo.product;

import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.data.Attribute;
import lombok.Data;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-01-13 9:42
 * @Description: 转换量较少的VO，一般用于list index页面
 */
@Data
public class ProductVOAlpha {

    //基础信息
    private int id;//主键
    private String name;//原名
    private String nameZh;//中文译名
    private String nameEn;//英文译名
    private String releaseDate;//发售日期
    private Attribute category;//作品分类
    private String remark;//备注

    //关联信息
    private JSONObject franchise;//所属系列id

    //图片
    private JSONObject cover;

    //审计字段
    private String addedTime;//收录时间
    private String editedTime;//编辑时间
    private boolean status;//状态

    private long visitNum;//浏览数

}
