package com.rakbow.website.data.emun.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-08-13 22:16
 * @Description:
 */
@AllArgsConstructor
public enum Entity {

    ENTRY(0,"Entry", "Entry"),
    ALBUM(1,"专辑", "Album"),
    BOOK(2,"书籍", "Book"),
    DISC(3,"碟片", "Disc"),
    GAME(4,"游戏", "Game"),
    MUSIC(5,"音乐", "Music"),
    GOODS(6,"周边", "Goods"),
    FIGURE(7,"手办", "Figure");

    @Getter
    private final int id;
    @Getter
    private final String nameZh;
    @Getter
    private final String nameEn;

    public static String getTableName(int id) {
        String nameEn = Arrays.stream(Entity.values())
                .filter(entity -> entity.getId() == id)
                .findFirst()
                .map(Entity::getNameEn)
                .orElse(null);
        assert nameEn != null;
        return nameEn.toLowerCase();
    }

}
