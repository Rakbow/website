package com.rakbow.website.util.Image;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.data.common.EntityType;
import com.rakbow.website.data.common.ImageType;
import com.rakbow.website.data.common.segmentImagesResult;
import com.rakbow.website.util.common.ActionResult;
import com.rakbow.website.util.common.ApiInfo;
import com.rakbow.website.util.common.CommonConstant;
import com.rakbow.website.util.common.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-12-31 1:18
 * @Description:
 */
@Component
public class CommonImageUtils {

    @Autowired
    private QiniuImageUtils qiniuImageUtils;

    /**
     * 通用新增图片
     *
     * @param entityId                 实体id
     * @param entityType               实体类型
     * @param images             新增图片文件数组
     * @param originalImagesJson 数据库中现存的图片json数据
     * @param imageInfos         新增图片json数据
     * @return finalImageJson 最终保存到数据库的json信息
     * @author rakbow
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public JSONArray commonAddImages(int entityId, EntityType entityType, MultipartFile[] images,
                                     JSONArray originalImagesJson, JSONArray imageInfos) throws IOException {
        //最终保存到数据库的json信息
        JSONArray finalImageJson = new JSONArray();

        //新增图片信息json
        JSONArray addImageJson = new JSONArray();

        //创建存储链接前缀
        String filePath = entityType.getNameEn().toLowerCase() + "/" + entityId + "/";

        for (int i = 0; i < images.length; i++) {
            //上传图片
            ActionResult ar = qiniuImageUtils.uploadImageToQiniu(images[i], filePath);
            if (ar.state) {
                JSONObject jo = new JSONObject();
                jo.put("url", ar.data.toString());
                jo.put("nameEn", imageInfos.getJSONObject(i).getString("nameEn"));
                jo.put("nameZh", imageInfos.getJSONObject(i).getString("nameZh"));
                jo.put("type", imageInfos.getJSONObject(i).getString("type"));
                jo.put("uploadTime", CommonUtils.getCurrentTime());
                if (imageInfos.getJSONObject(i).getString("description") == null) {
                    jo.put("description", "");
                } else {
                    jo.put("description", imageInfos.getJSONObject(i).getString("description"));
                }
                addImageJson.add(jo);
            }
        }

        //汇总
        finalImageJson.addAll(originalImagesJson);
        finalImageJson.addAll(addImageJson);

        return finalImageJson;
    }

    /**
     * 通用删除图片
     *
     * @param entityId     实体id
     * @param images       原始图片json数组
     * @param deleteImages 需要删除的图片jsonArray
     * @return images 最终保存到数据库的json信息
     * @author rakbow
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public JSONArray commonDeleteImages(int entityId, JSONArray images, JSONArray deleteImages) throws Exception {

        //从七牛云上删除
        //删除结果
        List<String> deleteResult = new ArrayList<>();
        //若删除的图片只有一张，调用单张删除方法
        if (deleteImages.size() == 1) {
            ActionResult ar = qiniuImageUtils.deleteImageFromQiniu
                    (deleteImages.getJSONObject(0).getString("url"));
            if (!ar.state) {
                throw new Exception(ar.message);
            }
            deleteResult.add(deleteImages.getJSONObject(0).getString("url"));
        } else {
            String[] fullImageUrlList = new String[deleteImages.size()];
            for (int i = 0; i < deleteImages.size(); i++) {
                fullImageUrlList[i] = deleteImages.getJSONObject(i).getString("url");
            }
            ActionResult ar = qiniuImageUtils.deleteImagesFromQiniu(fullImageUrlList);
            deleteResult = (List<String>) ar.data;
        }

        //根据删除结果循环删除图片信息json数组
        // 迭代器

        for (String s : deleteResult) {
            Iterator<Object> iterator = images.iterator();
            while (iterator.hasNext()) {
                JSONObject itJson = (JSONObject) iterator.next();
                if (StringUtils.equals(itJson.getString("url"), s)) {
                    // 删除数组元素
                    iterator.remove();
                }
            }
        }

        return images;
    }

    /**
     * 通用删除所有图片
     *
     * @param images 删除图片合集
     * @param entityType 实体类型
     * @author rakbow
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public String commonDeleteAllImages(EntityType entityType, JSONArray images) {

        String[] deleteImageKeyList = new String[images.size()];
        //图片文件名
        String deleteImageUrl;
        for (int i = 0; i < images.size(); i++) {
            JSONObject image = images.getJSONObject(i);
            deleteImageUrl = image.getString("url");
            //删除七牛服务器上对应图片文件
            deleteImageKeyList[i] = deleteImageUrl;
        }
        qiniuImageUtils.deleteImagesFromQiniu(deleteImageKeyList);

        return String.format(ApiInfo.DELETE_IMAGES_SUCCESS, entityType.getNameZh());
    }

    /**
     * 将图片切分为封面、展示和其他图片
     *
     * @param imagesJson 数据库原图片合集的JSON字符串
     * @param coverSize 封面图尺寸
     * @return segmentImagesResult
     * @author rakbow
     */
    public segmentImagesResult segmentImages (String imagesJson, int coverSize) {

        segmentImagesResult result = new segmentImagesResult();

        JSONArray images = JSON.parseArray(imagesJson);

        //添加缩略图
        if (!images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                JSONObject image = images.getJSONObject(i);
                image.put("thumbUrl", QiniuImageHandleUtils.getThumbUrl(image.getString("url"), 100));
                image.put("thumbUrl50", QiniuImageHandleUtils.getThumbUrl(image.getString("url"), 50));
            }
        }

        //对封面图片进行处理
        JSONObject cover = new JSONObject();
        cover.put("url", QiniuImageHandleUtils.getThumbUrl(CommonConstant.EMPTY_IMAGE_URL, coverSize));
        cover.put("name", "404");
        if (images.size() != 0) {
            for (int i = 0; i < images.size(); i++) {
                JSONObject image = images.getJSONObject(i);
                if (Objects.equals(image.getString("type"), "1")) {
                    cover.put("url", QiniuImageHandleUtils.getThumbUrl(image.getString("url"), coverSize));
                    cover.put("name", image.getString("nameEn"));
                }
            }
        }

        //对展示图片进行封装
        List<JSONObject> displayImages = new ArrayList<>();
        if (images.size() != 0) {
            for (int i = 0; i < images.size(); i++) {
                JSONObject image = images.getJSONObject(i);
                if (image.getIntValue("type") == ImageType.DISPLAY.getIndex()
                        || image.getIntValue("type") == ImageType.COVER.getIndex()) {
                    displayImages.add(image);
                }
            }
        }

        //对其他图片进行封装
        List<JSONObject> otherImages = new ArrayList<>();
        if (images.size() != 0) {
            for (int i = 0; i < images.size(); i++) {
                JSONObject image = images.getJSONObject(i);
                if (Objects.equals(image.getString("type"), "2")) {
                    otherImages.add(image);
                }
            }
        }

        result.images = images;
        result.cover = cover;
        result.displayImages = displayImages;
        result.otherImages = otherImages;

        return result;

    }

    /**
     * 获取index首页封面图片
     *
     * @param imagesJson 数据库原图片合集的JSON字符串
     * @return JSONObject
     * @author rakbow
     */
    public JSONObject getIndexCover (String imagesJson) {

        JSONArray images = JSONArray.parseArray(imagesJson);

        //对图片封面进行处理
        JSONObject cover = new JSONObject();
        cover.put("url", QiniuImageHandleUtils.getThumbBlackBackgroundUrl(CommonConstant.EMPTY_IMAGE_URL, 200));
        cover.put("thumbUrl", QiniuImageHandleUtils.getThumbUrl(CommonConstant.EMPTY_IMAGE_URL, 50));
        cover.put("thumbUrl70", QiniuImageHandleUtils.getThumbUrl(CommonConstant.EMPTY_IMAGE_URL, 70));
        cover.put("name", "404");
        if (images.size() != 0) {
            for (int i = 0; i < images.size(); i++) {
                JSONObject image = images.getJSONObject(i);
                if (Objects.equals(image.getString("type"), "1")) {
                    cover.put("url", QiniuImageHandleUtils.getThumbBlackBackgroundUrl(image.getString("url"), 200));
                    cover.put("thumbUrl", QiniuImageHandleUtils.getThumbUrl(image.getString("url"), 50));
                    cover.put("thumbUrl70", QiniuImageHandleUtils.getThumbUrl(image.getString("url"), 70));
                    cover.put("name", image.getString("nameEn"));
                }
            }
        }
        return cover;
    }

    /**
     * 获取list封面图片
     *
     * @param imagesJson 数据库原图片合集的JSON字符串
     * @return JSONObject
     * @author rakbow
     */
    public JSONObject getCover(String imagesJson, int size) {
        JSONObject cover = new JSONObject();
        JSONArray images = JSONArray.parseArray(imagesJson);
        cover.put("url", QiniuImageHandleUtils.getThumbUrl(CommonConstant.EMPTY_IMAGE_URL, size));
        cover.put("name", "404");
        if (images.size() != 0) {
            for (int i = 0; i < images.size(); i++) {
                JSONObject image = images.getJSONObject(i);
                if (Objects.equals(image.getString("type"), "1")) {
                    cover.put("url", QiniuImageHandleUtils.getThumbUrl(image.getString("url"), size));
                    cover.put("name", image.getString("nameEn"));
                }
            }
        }
        return cover;
    }

}
