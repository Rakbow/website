package com.rakbow.website.data.common;

import com.alibaba.fastjson2.JSONObject;
import com.github.mustachejava.Code;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-12-29 21:30
 * @Description:
 */
public enum Language {

    UNCLASSIFIED("Unclassified", "未分类", "Unknown"),
    JAPANESE("Japanese", "日语", "ja-JP"),
    SIMPLIFIED_CHINESE("Simplified Chinese", "简体中文", "zh-CN"),
    TRADITIONAL_CHINESE("Traditional Chinese", "繁体中文", "zh-TW"),
    ENGLISH("English", "英语", "en-US");

    private String nameEn;
    private String nameZh;
    private String code;

    Language(String nameEn, String nameZh, String code) {
        this.nameEn = nameEn;
        this.nameZh = nameZh;
        this.code = code;
    }

    public static String languageCode2NameZh (String code) {
        String nameZh = UNCLASSIFIED.nameZh;
        for (Language language : Language.values()) {
            if (StringUtils.equals(language.code, code)) {
                nameZh = language.nameZh;
            }
        }
        return nameZh;
    }

    /**
    * 获取语言数组
    *
    * @return list
    * @author rakbow
    * */
    public static List<JSONObject> getLanguageSet() {
        List<JSONObject> list = new ArrayList<>();
        for (Language language : Language.values()) {
            JSONObject jo = new JSONObject();
            jo.put("nameEn", language.nameZh);
            jo.put("nameZh", language.nameZh);
            jo.put("code", language.code);
            list.add(jo);
        }
        return list;
    }


    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
