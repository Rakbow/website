package com.rakbow.website.data.entity.franchise;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.entity.Franchise;
import lombok.Data;

import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-02-08 18:04
 * @Description: 系列的元系列相关信息
 */
public class MetaInfo {
    public String isMeta;//是否为元系列
    public List<Integer> childFranchises;//子系列
    public String metaId;//所属元系列id

    public MetaInfo(String metaInfoJson) {
        JSONObject metaInfo = JSON.parseObject(metaInfoJson);
        this.isMeta = metaInfo.getString("isMeta");
        this.childFranchises = metaInfo.getList("childFranchises", Integer.class);
        this.metaId = metaInfo.getString("metaId");
    }

    public MetaInfo(String isMeta, List<Integer> childFranchises, String metaId) {
        this.isMeta = isMeta;
        this.childFranchises = childFranchises;
        this.metaId = metaId;
    }

}
