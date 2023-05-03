package com.rakbow.website.entity.common;

import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.data.emun.common.Region;
import com.rakbow.website.data.emun.entry.EntryCategory;
import com.rakbow.website.util.common.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-04-29 17:29
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Company extends MetaEntry {

    private JSONObject region;//国家或地区编码

    public Company() {
        this.setId(0);
        this.setCategory(EntryCategory.getAttribute(EntryCategory.COMPANY.getId()));
        this.setName("");
        this.setNameEn("");
        this.setNameZh("");
        this.setAlias(new ArrayList<>());
        this.setLinks(new ArrayList<>());
        this.setDescription("");
        this.setRemark("");
        this.setAddedTime(DateUtil.getCurrentTime());
        this.setEditedTime(DateUtil.getCurrentTime());

        this.region = Region.getRegion(Region.GLOBAL.getCode());
    }

}