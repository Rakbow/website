package com.rakbow.website.data.emun.entity.game;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.data.Attribute;
import com.rakbow.website.data.emun.system.SystemLanguage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

@AllArgsConstructor
public enum GamePlatform {

    UNKNOWN(0, "Unknown", "Unknown"),
    PC(1, "PC", "PC"),
    WEB(2, "Web", "Web"),
    ANDROID(3, "Android", "Android"),
    APPLE_IOS(4, "Apple iOS", "Apple iOS"),
    PS(5, "PS", "PS"),
    PS2(6, "PS2", "PS2"),
    PS3(7, "PS3", "PS3"),
    PS4(8, "PS4", "PS4"),
    PS5(9, "PS5", "PS5"),
    PSV(10, "PSV", "PSV"),
    PSP(11, "PSP", "PSP"),
    XBOX_360(12, "Xbox 360", "Xbox 360"),
    XBOX_ONE(13, "Xbox One", "Xbox One"),
    XBOX_SERIES_X(14, "Xbox Series X", "Xbox Series X"),
    XBOX_SERIES_S(15, "Xbox Series S", "Xbox Series S"),
    SWITCH(16, "NS", "NS"),
    N64(17, "N64", "N64"),
    NGC(18, "NGC", "NGC"),
    GBC(19, "GBC", "GBC"),
    NDS(20, "NDS", "NDS"),
    NINTENDO_3DS(21, "3DS", "3DS"),
    NINTENDO_WII(22, "Wii", "Wii"),
    NINTENDO_WII_U(23, "Wii U", "Wii U"),
    ARCADE(24, "Arcade", "Arcade"),
    NEC_PC_ENGINE(25, "NEC PC-Engine", "NEC PC-Engine"),
    SEGA_MEGA_DRIVE_GENESIS(26, "Genesis/MD", "Genesis/MD"),
    SEGA_SATURN(27, "Saturn", "Saturn"),
    SEGA_DREAM_CAST(28, "DreamCast", "DreamCast");

    @Getter
    private final int id;
    @Getter
    private final String nameEn;
    @Getter
    private final String nameZh;

    public static String getNameById(int id, String lang) {
        for (GamePlatform item : GamePlatform.values()) {
            if (item.getId() == id) {
                if(StringUtils.equals(lang, SystemLanguage.ENGLISH.getCode())) {
                    return item.getNameEn();
                }else {
                    return item.getNameZh();
                }
            }
        }
        return null;
    }

    public static JSONArray getAttributeSet(String lang) {
        JSONArray set = new JSONArray();
        if(StringUtils.equals(lang, SystemLanguage.ENGLISH.getCode())) {
            for (GamePlatform item : GamePlatform.values()) {
                set.add(new Attribute(item.id, item.nameEn));
            }
        }else if(StringUtils.equals(lang, SystemLanguage.CHINESE.getCode())) {
            for (GamePlatform item : GamePlatform.values()) {
                set.add(new Attribute(item.id, item.nameZh));
            }
        }
        return set;
    }

    public static Attribute getAttribute(int id) {
        String lang = LocaleContextHolder.getLocale().getLanguage();
        return new Attribute(id, getNameById(id, lang));
    }

}