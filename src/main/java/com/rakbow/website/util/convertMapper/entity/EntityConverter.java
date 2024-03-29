package com.rakbow.website.util.convertMapper.entity;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.data.Attribute;
import com.rakbow.website.data.emun.common.Entity;
import com.rakbow.website.data.emun.common.MediaFormat;
import com.rakbow.website.data.emun.common.Region;
import com.rakbow.website.data.vo.RegionVO;
import com.rakbow.website.util.common.DateUtil;
import com.rakbow.website.util.common.LikeUtil;
import com.rakbow.website.util.common.SpringUtil;
import com.rakbow.website.util.common.VisitUtil;
import com.rakbow.website.util.entry.EntryUtil;
import com.rakbow.website.util.file.CommonImageUtil;
import com.rakbow.website.util.file.QiniuImageUtil;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-05-08 9:42
 * @Description:
 */
public class EntityConverter {

    static String getDate(Date date) {
        return DateUtil.dateToString(date);
    }

    static List<Attribute> getProducts(String products) {
        return EntryUtil.getClassifications(products);
    }

    static List<Attribute> getFranchises(String franchises) {
        return EntryUtil.getFranchises(franchises);
    }

    static List<Attribute> getMediaFormat(String formats) {
        return MediaFormat.getAttributes(formats);
    }

    static String getVOTime(Timestamp timestamp) {
        return DateUtil.timestampToString(timestamp);
    }

    static boolean getBool(int bool) {
        return bool == 1;
    }

    static String getThumb70Cover(String images) {
        return QiniuImageUtil.getThumb70Url(images);
    }

    static JSONArray getCompanies(String json) {
        return EntryUtil.getCompanies(json);
    }

    static JSONArray getPersonnel(String json) {
        return EntryUtil.getPersonnel(json);
    }

    static JSONArray getSpecs(String json) {
        return EntryUtil.getSpecs(json);
    }

    static JSONObject getCover(String images, Entity entity) {
        return CommonImageUtil.generateCover(images, entity);
    }

    static JSONObject getThumbCover(String images, Entity entity, int size) {
        return CommonImageUtil.generateThumbCover(images, entity, size);
    }

    static long getVisitCount(int entityTypeId, int id) {
        VisitUtil visitUtil = SpringUtil.getBean("visitUtil");
        return visitUtil.getVisit(entityTypeId, id);
    }

    static long getLikeCount(int entityTypeId, int id) {
        LikeUtil likeUtil = SpringUtil.getBean("likeUtil");
        return likeUtil.getLike(entityTypeId, id);
    }

    static String getCurrencyUnitByCode(String region) {
        return Region.getCurrencyUnitByCode(region);
    }

    static RegionVO getRegion(String region) {
        return Region.getRegion(region);
    }

    static JSONArray getJSONArray(String json) {
        return JSON.parseArray(json);
    }

}
