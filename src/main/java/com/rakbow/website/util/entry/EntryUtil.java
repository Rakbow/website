package com.rakbow.website.util.entry;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.data.Attribute;
import com.rakbow.website.data.emun.common.CompanyRole;
import com.rakbow.website.data.emun.entry.EntryCategory;
import com.rakbow.website.data.emun.temp.EnumUtil;
import com.rakbow.website.util.common.DataFinder;
import com.rakbow.website.util.common.RedisUtil;
import com.rakbow.website.util.common.SpringUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-05-02 1:04
 * @Description:
 */
@Component
public class EntryUtil {

    public static JSONArray getCompanies(String json) {
        JSONArray orgCompanies = JSON.parseArray(json);
        if(orgCompanies.isEmpty()) return new JSONArray();

        List<Attribute> allCompanies = getAttributesForRedis(EntryCategory.COMPANY);

        JSONArray companies = new JSONArray();
        for (int i = 0; i < orgCompanies.size(); i++) {
            JSONObject orgCompany = orgCompanies.getJSONObject(i);
            JSONObject company = new JSONObject();

            company.put("role", EnumUtil.getAttribute(CompanyRole.class, orgCompany.getIntValue("role")));

            List<Attribute> members = new ArrayList<>();
            List<Integer> memberIds = orgCompany.getList("members", Integer.class);
            memberIds.forEach(id -> {
                Attribute attribute = DataFinder.findAttributeByValue(id, allCompanies);
                if (attribute != null) {
                    members.add(attribute);
                }
            });

            company.put("members", members);
            companies.add(company);
        }

        return companies;
    }

    public static JSONArray getPersonnel(String json) {
        JSONArray orgPersonnel = JSON.parseArray(json);
        if(orgPersonnel.isEmpty()) return new JSONArray();
        List<Attribute> allPersonnel = getAttributesForRedis(EntryCategory.PERSONNEL);
        List<Attribute> allRole = getAttributesForRedis(EntryCategory.ROLE);

        JSONArray personnel = new JSONArray();
        for (int i = 0; i < orgPersonnel.size(); i++) {
            JSONObject orgItem = orgPersonnel.getJSONObject(i);
            JSONObject newItem = new JSONObject();

            if(orgItem.getIntValue("main") == 1) {
                newItem.put("main", 1);
            }

            newItem.put("role", DataFinder.findAttributeByValue(orgItem.getIntValue("role"), allRole));

            List<Attribute> members = new ArrayList<>();
            List<Integer> memberIds = orgItem.getList("members", Integer.class);
            memberIds.forEach(id -> {
                Attribute attribute = DataFinder.findAttributeByValue(id, allPersonnel);
                if (attribute != null) {
                    members.add(attribute);
                }
            });

            newItem.put("members", members);
            personnel.add(newItem);
        }

        return personnel;
    }

    public static JSONArray getSpecs(String json) {
        JSONArray specs = JSON.parseArray(json);
        if(specs.isEmpty()) return new JSONArray();
        List<Attribute> allSpecParameter = getAttributesForRedis(EntryCategory.SPEC_PARAM);
        for (int i = 0; i < specs.size(); i++) {
            JSONObject item = specs.getJSONObject(i);

            item.put("label", DataFinder.findAttributeByValue(item.getIntValue("label"), allSpecParameter));
        }

        return specs;
    }

    public static List<Attribute> getSerials(String json) {

        List<Attribute> serials = new ArrayList<>();

        int[] ids = JSON.parseObject(json, int[].class);

        if(ids.length == 0) return serials;

        List<Attribute> allPublications = getAttributesForRedis(EntryCategory.PUBLICATION);

        serials.addAll(DataFinder.findAttributesByValues(ids, allPublications));

        return serials;
    }

    public static List<Attribute> getClassifications(String json) {

        List<Attribute> classifications = new ArrayList<>();

        int[] ids = JSON.parseObject(json, int[].class);
        List<Attribute> allClassifications = getAttributesForRedis(EntryCategory.CLASSIFICATION);

        for(int id : ids) {
            classifications.add(DataFinder.findAttributeByValue(id, allClassifications));
        }

        return classifications;
    }

    public static List<Attribute> getFranchises(String json) {

        List<Attribute> franchises = new ArrayList<>();

        int[] ids = JSON.parseObject(json, int[].class);
        List<Attribute> allFranchises = getAttributesForRedis(EntryCategory.FRANCHISE);

        for(int id : ids) {
            franchises.add(DataFinder.findAttributeByValue(id, allFranchises));
        }

        return franchises;
    }

    public static Attribute getFranchise(int id) {

        List<Attribute> allFranchises = getAttributesForRedis(EntryCategory.FRANCHISE);

        return DataFinder.findAttributeByValue(id, allFranchises);
    }

    @SuppressWarnings("unchecked")
    public static List<Attribute> getAttributesForRedis(EntryCategory category) {
        RedisUtil redisUtil = SpringUtil.getBean("redisUtil");

        List<Attribute> attributes = new ArrayList<>();

        String key = category.getLocaleKey();

        Object res = redisUtil.get(key);
        if(res != null) {
            attributes.addAll((List<Attribute>) redisUtil.get(key));
        }

        return attributes;
    }

}
