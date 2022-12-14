package com.rakbow.website.data.game;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public enum GamePlatform {

    UNKNOWN(0, "Unknown", "未知"),
    PC(1, "PC", "PC"),
    WEB(2, "Web", "网页端"),
    ANDROID(3, "Android", "安卓"),
    APPLE_IOS(4, "Apple iOS", "苹果");

    @Getter
    private final int index;
    @Getter
    private final String nameEn;
    @Getter
    private final String nameZh;

    public static String index2Name (int index) {
        String nameEn = UNKNOWN.nameEn;
        for (GamePlatform gamePlatform : GamePlatform.values()) {
            if (gamePlatform.index == index) {
                nameEn = gamePlatform.nameEn;
            }
        }
        return nameEn;
    }

    public static List<JSONObject> getGamePlatformSet () {
        List<JSONObject> list = new ArrayList<>();
        for (GamePlatform platform : GamePlatform.values()) {
            JSONObject jo = new JSONObject();
            jo.put("labelEn", platform.nameEn);
            jo.put("labelZh", platform.nameZh);
            jo.put("value", platform.index);
            list.add(jo);
        }
        return list;
    }

}
