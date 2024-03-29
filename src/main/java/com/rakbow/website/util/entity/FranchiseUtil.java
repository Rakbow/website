package com.rakbow.website.util.entity;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.rakbow.website.data.entity.franchise.MetaInfo;
import com.rakbow.website.entity.Franchise;
import com.rakbow.website.service.FranchiseService;
import com.rakbow.website.util.common.SpringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-01-07 20:38
 * @Description:
 */
public class FranchiseUtil {

    /**
     * 判断该franchise是否为meta-franchise
     *
     * @param franchise franchise
     * @return boolean
     * @author rakbow
     */
    public static boolean isMetaFranchise(Franchise franchise) {
        MetaInfo metaInfo = JSON.to(MetaInfo.class, franchise.getMetaInfo());
        return StringUtils.equals(metaInfo.isMeta, "1");
    }

    /**
     * 获取该meta-franchise的子系列
     *
     * @param originFranchise franchise
     * @return JSONArray
     * @author rakbow
     */
    public static JSONArray getChildFranchises(Franchise originFranchise) {
        FranchiseService franchiseService = SpringUtil.getBean("franchiseService");

        return JSON.parseArray(JSON.toJSONString(
                franchiseService.getFranchisesByParentId(Integer.parseInt(Integer.toString(originFranchise.getId())))));
    }

    /**
     * 获取该meta-franchise的子系列Ids
     *
     * @param originFranchise franchise
     * @return int[]
     * @author rakbow
     */
    public static List<Integer> getChildFranchiseIds(Franchise originFranchise) {
        MetaInfo metaInfo = JSON.to(MetaInfo.class, originFranchise.getMetaInfo());

        return metaInfo.childFranchises;
    }

    /**
     * 获取该franchise的父级系列
     *
     * @param franchise franchise
     * @return int[]
     * @author rakbow
     */
    public static Franchise getParentFranchise(Franchise franchise) {
        int parentFranchiseId = Integer.parseInt(new MetaInfo(franchise.getMetaInfo()).metaId);
        FranchiseService franchiseService = SpringUtil.getBean("franchiseService");

        return franchiseService.getFranchise(parentFranchiseId);

    }

}
