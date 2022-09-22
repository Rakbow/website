package com.rakbow.website.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.dao.AlbumMapper;
import com.rakbow.website.data.ProductClass;
import com.rakbow.website.data.album.AlbumFormat;
import com.rakbow.website.data.album.MediaFormat;
import com.rakbow.website.data.album.PublishFormat;
import com.rakbow.website.entity.Album;
import com.rakbow.website.service.util.common.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-07-25 1:42
 * @Description:
 */
@Service
public class AlbumService {

    @Autowired
    private AlbumMapper albumMapper;
    @Autowired
    private SeriesService seriesService;
    @Autowired
    private ProductService productService;

    //新增专辑
    // REQUIRED: 支持当前事务(外部事务),如果不存在则创建新事务.
    // REQUIRES_NEW: 创建一个新事务,并且暂停当前事务(外部事务).
    // NESTED: 如果当前存在事务(外部事务),则嵌套在该事务中执行(独立的提交和回滚),否则就会REQUIRED一样.
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public int insertAlbum(Album album) {
        return albumMapper.insertAlbum(album);
    }

    //获取表中所有数据
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public List<Album> getAll() {
        return albumMapper.getAll();
    }

    //根据具体搜索条件查询
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public List<Album> selectAlbumByFilter(String queryParam) {
        JSONObject param = JSON.parseObject(queryParam);
        String publishFormat = (param.get("publishFormat") == null) ? null : param.get("publishFormat").toString();
        String albumFormat = (param.get("albumFormat") == null) ? null : param.get("albumFormat").toString();
        String mediaFormat = (param.get("mediaFormat") == null) ? null : param.get("mediaFormat").toString();
        String productId = (param.get("productId") == null) ? null : param.get("productId").toString();
        return albumMapper.selectAlbumByFilter(publishFormat, albumFormat, mediaFormat, productId);
    }

    public int findAlbumRows() {
        return albumMapper.selectAlbumRows();
    }

    //根据Id获取专辑
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public Album findAlbumById(int id) {
        return albumMapper.selectAlbumById(id);
    }

    //根据Id删除专辑
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public int deleteAlbumById(int id) {
        return albumMapper.deleteAlbumById(id);
    }

    //更新专辑
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public int updateAlbum(int id, Album album) {
        return albumMapper.updateAlbum(id, album);
    }

    //Album转Json对象，以便前端展示
    public JSONObject album2Json(Album album) {

        JSONObject albumJson = (JSONObject) JSON.toJSON(album);

        albumJson.put("releaseDate", CommonUtil.dateToString(album.getReleaseDate(), "yyyy/MM/dd"));

        List<String> publishFormat = new ArrayList();
        Arrays.stream(album.getPublishFormat().split(","))
                .forEach(i -> publishFormat.add(PublishFormat.getNameByIndex(Integer.parseInt(i))));

        List<String> albumFormat = new ArrayList();
        Arrays.stream(album.getAlbumFormat().split(","))
                .forEach(i -> albumFormat.add(AlbumFormat.getNameByIndex(Integer.parseInt(i))));

        List<String> mediaFormat = new ArrayList();
        Arrays.stream(album.getMediaFormat().split(","))
                .forEach(i -> mediaFormat.add(MediaFormat.getNameByIndex(Integer.parseInt(i))));

        List<String> product = new ArrayList();
        Arrays.stream(album.getProductId().split(","))
                .forEach(i -> product.add(productService.selectProductById(Integer.parseInt(i)).getNameZh() + "(" +
                        ProductClass.getNameByIndex(productService.selectProductById(Integer.parseInt(i)).getClassification()) + ")"));
        List<JSONObject> productMap = new ArrayList<>();
        Arrays.stream(album.getProductId().split(","))
                .forEach(i -> {
                    JSONObject jo = new JSONObject();
                    jo.put("id", Integer.parseInt(i));
                    jo.put("name", productService.selectProductById(Integer.parseInt(i)).getNameZh() + "(" +
                            ProductClass.getNameByIndex(productService.selectProductById(Integer.parseInt(i)).getClassification()) + ")");
                    productMap.add(jo);
                });

        JSONArray artists = JSONArray.parseArray(album.getArtists());

        JSONArray imgUrl = JSONArray.parseArray(album.getImgUrl());

        JSONObject trackInfo = JSONObject.parseObject(album.getTrackInfo());

        albumJson.put("publishFormat", publishFormat);
        albumJson.put("publishFormat", publishFormat);
        albumJson.put("albumFormat", albumFormat);
        albumJson.put("mediaFormat", mediaFormat);
        albumJson.put("artists",artists);
        albumJson.put("imgUrl",imgUrl);
        albumJson.put("trackInfo",trackInfo);
        albumJson.put("series", seriesService.selectSeriesById(album.getSeriesId()).getNameZh());
        albumJson.put("product", product);
        albumJson.put("products", productMap);
        albumJson.put("addedTime", CommonUtil.dateToString(album.getAddedTime(), "yyyy/MM/dd hh:mm:ss"));
        albumJson.put("editedTime", CommonUtil.dateToString(album.getEditedTime(), "yyyy/MM/dd hh:mm:ss"));
        return albumJson;
    }

    //json对象转Album，以便保存到数据库
    public Album json2Album(JSONObject albumJson) throws ParseException {
        albumJson.put("releaseDate", CommonUtil.stringToDate(albumJson.getString("releaseDate"), "yyyy/MM/dd"));
        Album album = albumJson.toJavaObject(Album.class);
        album.setAddedTime(new Timestamp(System.currentTimeMillis()));
        album.setEditedTime(new Timestamp(System.currentTimeMillis()));
        return album;
    }

    //设置所有字段信息
    public List<String> getAlbumFields() {
        List<String> AlbumFields = null;
        Field[] f = Album.class.getClass().getFields();
        for (int i = 0; i < f.length; i++) {
            AlbumFields.add(f[i].getName());
        }
        return AlbumFields;
    }

    //更新专辑图片
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public void updateAlbumImgUrl(int id, String imgUrl) {
        albumMapper.updateAlbumImgUrl(id, imgUrl);
    }

    //更新专辑Artists
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public void updateAlbumArtists(int id, String credits) {
        albumMapper.updateAlbumArtists(id, credits);
    }

    //更新音轨信息
    public void updateAlbumTrackInfo(int id, String discList){

        JSONObject trackInfo = new JSONObject();

        JSONArray disc_list = new JSONArray();

        List<String> times = new ArrayList<>();

        int totalTrack = 0;

        int serial = 1;

        for(Object _disc : JSON.parseArray(discList)){
            JSONObject disc = new JSONObject();
            JSONArray track_list = new JSONArray();
            List<String> _times = new ArrayList<>();
            int i = 1;
            for(Object _track : JSON.parseArray(JSON.parseObject(_disc.toString()).getString("track_list"))){
                JSONObject track = JSON.parseObject(_track.toString());
                track.put("serial","0"+ i);
                track_list.add(track);
                _times.add(track.get("length").toString());
                i++;
            }
            times.addAll(_times);
            totalTrack += track_list.size();

            disc.put("serial", serial);
            disc.put("media_format", JSON.parseObject(_disc.toString()).get("media_format").toString());
            disc.put("album_format", JSON.parseObject(_disc.toString()).get("album_format").toString());
            disc.put("catalog_no", albumMapper.selectAlbumById(id).getCatalogNo() + '-' + serial);
            disc.put("disc_length",CommonUtil.countTotalTime(_times));
            disc.put("track_list",track_list);

            serial++;
            disc_list.add(disc);
        }
        trackInfo.put("disc_list",disc_list);
        trackInfo.put("total_tracks",totalTrack);
        trackInfo.put("total_length",CommonUtil.countTotalTime(times));
        albumMapper.updateAlbumTrackInfo(id, trackInfo.toJSONString());
    }

    //根据产品id获取所有该产品的专辑
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public List<Album> getAlbumsByProductId(int productId){
        List<Album> albums = new ArrayList<>();
        albumMapper.selectAlbumByFilter(null, null, null, Integer.toString(productId))
                .stream().forEach(i -> {
                    Arrays.stream(i.getProductId().split(",")).forEach(j -> {
                        if(j.equals(productId)){
                            albums.add(i);
                        }
                    });
                });
        return albums;
    }

    //获取相关专辑
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public List<JSONObject> getRelatedAlbums(int id) {
        String[] productIds = findAlbumById(id).getProductId().split(",");
        List<JSONObject> relatedAlbums = new ArrayList<>();
        if (productIds.length == 1) {
            List<Album> tmp = selectAlbumByFilter("{\"productId\":\"" + productIds[0] + "\"}");
            for (int i = 0; i < 4; i++) {
                relatedAlbums.add(album2Json(tmp.get(i)));
            }
        } else if (productIds.length > 1 && productIds.length <= 4) {
            relatedAlbums.add(album2Json(selectAlbumByFilter("{\"productId\":\"" + productIds[0] + "\"}").get(0)));
            relatedAlbums.add(album2Json(selectAlbumByFilter("{\"productId\":\"" + productIds[0] + "\"}").get(1)));
            relatedAlbums.add(album2Json(selectAlbumByFilter("{\"productId\":\"" + productIds[1] + "\"}").get(0)));
            relatedAlbums.add(album2Json(selectAlbumByFilter("{\"productId\":\"" + productIds[1] + "\"}").get(1)));
        } else {
            for (int i = 0; i < 4; i++) {
                relatedAlbums.add(album2Json(selectAlbumByFilter("{\"productId\":\"" + productIds[i] + "\"}").get(i)));
            }
        }



        for (JSONObject album : relatedAlbums) {


            JSONArray imgUrl = JSONArray.parseArray(album.get("imgUrl").toString());

            if (imgUrl.isEmpty()) {
                JSONObject tmp = new JSONObject();
                tmp.put("url", "/img/404.jpg");
                tmp.put("name", "Cover");
                album.put("imgUrl", tmp);
            } else {
                for (int i = 0; i < imgUrl.size(); i++) {
                    JSONObject img = imgUrl.getJSONObject(i);
                    if (img.get("name").toString().equals("Cover") || img.get("name").toString().equals("Front")) {
                        album.put("imgUrl",img);
                    }
                }
            }
        }
        return relatedAlbums;
    }
}