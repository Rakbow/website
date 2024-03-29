package com.rakbow.website.data.vo.disc;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.data.Attribute;
import com.rakbow.website.data.vo.RegionVO;
import lombok.Data;

import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-01-11 10:42
 * @Description: 转换量较少的VO，一般用于list index页面
 */
@Data
public class DiscVOAlpha {

    //基础信息
    private int id;//主键
    private String catalogNo;//商品型番
    private String name;//商品名(原语言)
    private String nameZh;//商品译名(中)
    private String nameEn;//商品译名(英)
    private String barcode;//商品条形码
    private RegionVO region;//地区
    private String releaseDate;//发行日期
    private int price;//发行价格
    private String currencyUnit;//货币单位
    private boolean limited;//发售版本是否为限定版 0-否 1-是
    private boolean hasBonus;//是否包含特典
    private String remark;//备注

    //关联信息
    private List<Attribute> franchises;//所属系列id
    private JSONArray products;//所属作品id

    //复杂字段
    private JSONArray mediaFormat;//媒体格式 0-未分类 1-DVD 2-Blu-ray

    //图片相关
    private JSONObject cover;

    //审计字段
    private String addedTime;//收录时间
    private String editedTime;//编辑时间
    private boolean status;//状态

    private long visitNum;//浏览量

}
