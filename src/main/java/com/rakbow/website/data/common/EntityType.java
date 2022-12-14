package com.rakbow.website.data.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-08-13 22:16
 * @Description:
 */
@AllArgsConstructor
public enum EntityType {

    UNCLASSIFIED(0,"未分类", "Unclassified"),
    ALBUM(1,"专辑", "Album"),
    DISC(2,"碟片", "Disc"),
    BOOK(3,"书籍", "Book"),
    MERCH(4,"周边", "Goods"),
    GAME(5,"游戏", "Game"),
    FRANCHISE(6,"系列", "Franchise"),
    PRODUCT(7,"作品", "Product"),
    ARTICLE(8,"文章", "Article"),
    MUSIC(9,"音乐", "Music");

    @Getter
    private final int id;
    @Getter
    private final String nameZh;
    @Getter
    private final String nameEn;

    public static String getItemNameZhByIndex(int id){
        for (EntityType itemType : EntityType.values()) {
            if (itemType.getId() == id) {
                return itemType.nameZh;
            }
        }
        return "未分类";
    }

    public static String getItemNameEnByIndex(int id){
        for (EntityType itemType : EntityType.values()) {
            if (itemType.getId() == id) {
                return itemType.nameEn;
            }
        }
        return "Unclassified";
    }

    public static int getItemIndexByNameZh(String nameZh){
        for (EntityType itemType : EntityType.values()) {
            if (itemType.getNameZh().equals(nameZh)) {
                return itemType.id;
            }
        }
        return 0;
    }

    public static List<String> getItemList(){
        List<String> items = new ArrayList<>();
        for (EntityType itemType : EntityType.values()) {
            items.add(itemType.getNameZh());
        }
        return items;
    }

}
