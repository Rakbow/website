package com.rakbow.website.util.common;

import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.data.Attribute;
import com.rakbow.website.entity.Album;
import com.rakbow.website.entity.Music;
import com.rakbow.website.entity.Product;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-11-06 5:54
 * @Description: 数据查找
 */
public class DataFinder {

    //region album

    public static Album findAlbumById(int id, List<Album> albums) {
        Album finder = new Album();
        finder.setId(id);
        int idx = Collections.binarySearch(albums, finder, DataSorter.albumSortById);
        if (idx >= 0) {
            return albums.get(idx);
        }else {
            return null;
        }
    }

    //endregion

    //region music

    /**
     * 根据musicId从指定music列表中查找
     *
     * @param id,musics 查找id和music列表
     * @return music
     * @author rakbow
     */
    public static Music findMusicById(int id, List<Music> musics) {
        Music musicFinder = new Music();
        musicFinder.setId(id);
        int idx = Collections.binarySearch(musics, musicFinder, DataSorter.musicSortById);
        return idx >= 0 ? musics.get(idx) : null;
    }

    public static List<Music> findMusicByDiscSerial(int discSerial, List<Music> musics) {

        return musics.stream().filter(music -> music.getDiscSerial() == discSerial)
                .sorted(DataSorter.musicSortByTrackSerial).collect(Collectors.toList());
    }

    public static Music findMusicByNameAndAlbumId(String name, String nameType, int albumId, List<Music> musics) {
        if (StringUtils.equals(nameType, "nameJp")) {
            for (Music music : musics) {
                if (music.getAlbumId() == albumId && StringUtils.equals(music.getName(), name)) {
                    return music;
                }
            }
        } else {
            for (Music music : musics) {
                if (music.getAlbumId() == albumId && StringUtils.equals(music.getNameEn(), name)) {
                    return music;
                }
            }
        }

        return null;
    }

    //endregion

    //region product

    public static List<Product> findProductsByClassification(int classification, List<Product> products) {
        List<Product> result = new ArrayList<>();
        for (Product product : products) {
            if (product.getCategory() == classification) {
                if (!result.contains(product)) {
                    result.add(product);
                }
            }
        }
        return result;
    }

    /**
     * 根据jsonId从指定json列表中查找
     *
     * @param id,jsons 查找id和json列表
     * @return json
     * @author rakbow
     */
    public static JSONObject findJsonById(int id, List<JSONObject> jsons) {
        JSONObject json = new JSONObject();
        json.put("id", id);
        int idx = Collections.binarySearch(jsons, json, DataSorter.jsonSortById);
        return idx >= 0 ? jsons.get(idx) : null;
    }

    /**
     * 根据jsonValue从指定json列表中查找
     *
     * @param id,jsons 查找id和json列表
     * @return json
     * @author rakbow
     */
    public static JSONObject findJsonByIdInSet(int id, List<JSONObject> jsons) {
        JSONObject json = new JSONObject();
        json.put("value", id);
        int idx = Collections.binarySearch(jsons, json, DataSorter.jsonSetSortByValue);
        return idx >= 0 ? jsons.get(idx) : null;
    }

    /**
     * 根据Value从指定attributes列表中查找
     *
     * @param value,attributes 查找id和json列表
     * @return json
     * @author rakbow
     */
    public static Attribute findAttributeByValue(int value, List<Attribute> attributes) {
        Attribute finder = new Attribute();
        finder.setValue(value);
        int idx = Collections.binarySearch(attributes, finder, DataSorter.attributesSortByValue);
        return idx >= 0 ? attributes.get(idx) : null;
    }

    public static List<Attribute> findAttributesByValues(int[] values, List<Attribute> attributes) {

        List<Attribute> res = new ArrayList<>();

        if(values.length == 0) return res;

        Attribute finder = new Attribute();
        for(int value : values) {
            finder.setValue(value);
            int idx = Collections.binarySearch(attributes, finder, DataSorter.attributesSortByValue);
            if(idx >= 0) {
                res.add(attributes.get(idx));
            }
        }
        return res;
    }

    //endregion

}
