package com.rakbow.website.data.vo.game;

import com.alibaba.fastjson2.JSONArray;
import com.rakbow.website.data.Attribute;
import com.rakbow.website.data.vo.RegionVO;
import lombok.Data;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-01-12 10:42
 * @Description: 转换量最大的VO，一般用于详情页面
 */
@Data
public class GameVO {

    //基础信息
    private int id;//主键编号
    private String name;//游戏名（原文）
    private String nameZh;//游戏名（中文）
    private String nameEn;//游戏名（英文）
    private String barcode;//商品条形码
    private String releaseDate;//发售日期
    private boolean hasBonus;//是否包含特典
    private String remark;//备注

    //复杂字段
    private Attribute releaseType;//发售类型
    private Attribute platform;//平台
    private RegionVO region;//地区
    private JSONArray organizations;//相关组织
    private JSONArray staffs;//开发制作人员
    private String bonus;//特典信息

}
