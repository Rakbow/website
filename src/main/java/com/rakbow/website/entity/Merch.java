package com.rakbow.website.entity;

import com.rakbow.website.entity.common.MetaEntity;
import com.rakbow.website.util.common.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-01-04 10:26
 * @Description: 周边实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Merch extends MetaEntity {

    private int id;//主键编号
    private String name;//商品名（原文）
    private String nameZh;//商品名（中文）
    private String nameEn;//商品名（英文）
    private String barcode;//商品条形码
    private String franchises;//所属系列
    private String products;//所属产品
    private int category;//商品分类
    private String region;//地区
    private Date releaseDate;//发售日
    private int price;//价格
    private String currencyUnit;//价格单位
    private int notForSale;//是否非卖品
    private String spec;//规格

    public Merch() {
        this.id = 0;
        this.name = "";
        this.nameEn = "";
        this.nameZh = "";
        this.barcode = "";
        this.franchises = "[]";
        this.products = "[]";
        this.category = 0;
        this.releaseDate = null;
        this.price = 0;
        this.currencyUnit = "";
        this.region = "";
        this.notForSale = 0;
        this.spec = "[]";
        this.setDescription("");
        this.setImages("[]");
        this.setRemark("");
        this.setAddedTime(DateUtil.NOW_TIMESTAMP);;
        this.setEditedTime(DateUtil.NOW_TIMESTAMP);;
        this.setStatus(1);
    }

}
