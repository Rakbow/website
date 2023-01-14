package com.rakbow.website.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.data.ApiInfo;
import com.rakbow.website.data.ApiResult;
import com.rakbow.website.data.emun.common.DataActionType;
import com.rakbow.website.data.emun.common.EntityType;
import com.rakbow.website.entity.Franchise;
import com.rakbow.website.entity.Visit;
import com.rakbow.website.service.FranchiseService;
import com.rakbow.website.service.UserService;
import com.rakbow.website.service.VisitService;
import com.rakbow.website.util.common.HostHolder;
import com.rakbow.website.util.convertMapper.FranchiseVOMapper;
import com.rakbow.website.util.image.CommonImageUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-01-14 17:07
 * @Description:
 */
@Controller
@RequestMapping("/db/franchise")
public class FranchiseController {

    @Autowired
    private FranchiseService franchiseService;
    @Autowired
    private VisitService visitService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    private final FranchiseVOMapper franchiseVOMapper = FranchiseVOMapper.INSTANCES;

    //region ------获取页面------

    //获取单个系列详细信息页面
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public String getFranchiseDetail(@PathVariable("id") int id, Model model) {
        if (franchiseService.getFranchise(id) == null) {
            model.addAttribute("errorMessage", String.format(ApiInfo.GET_DATA_FAILED_404, EntityType.FRANCHISE.getNameZh()));
            return "/error/404";
        }
        //访问数+1
        visitService.increaseVisit(EntityType.FRANCHISE.getId(), id);

        Franchise franchise = franchiseService.getFranchise(id);

        model.addAttribute("franchise", franchiseVOMapper.franchise2VO(franchise));
        model.addAttribute("user", hostHolder.getUser());
        //获取页面访问量
        model.addAttribute("visitNum", visitService.getVisit(EntityType.FRANCHISE.getId(), id).getVisitNum());
        return "/franchise/franchise-detail";
    }

    //endregion

    //region ------增删改查------

    //根据搜索条件获取专辑
//    @RequestMapping(value = "/get-albums", method = RequestMethod.POST)
//    @ResponseBody
//    public String getAlbumsByFilterList(@RequestBody String json) {
//
//        JSONObject param = JSON.parseObject(json);
//        JSONObject queryParams = param.getJSONObject("queryParams");
//        String pageLabel = param.getString("pageLabel");
//
//        List<AlbumVOAlpha> albums = new ArrayList<>();
//
//        SearchResult searchResult = albumService.getAlbumsByFilter(queryParams);
//
//        if (StringUtils.equals(pageLabel, "list")) {
//            albums = AlbumVOMapper.INSTANCES.album2VOAlpha((List<Album>) searchResult.data);
//        }
//        if (StringUtils.equals(pageLabel, "index")) {
//            albums = AlbumVOMapper.INSTANCES.album2VOAlpha((List<Album>) searchResult.data);
//        }
//
//        JSONObject result = new JSONObject();
//        result.put("data", albums);
//        result.put("total", searchResult.total);
//
//        return JSON.toJSONString(result);
//    }

    //新增
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addFranchise(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        JSONObject param = JSON.parseObject(json);
        try {
            if (userService.checkAuthority(request).state) {

                //检测数据
                if(!StringUtils.isBlank(franchiseService.checkFranchiseJson(param))) {
                    res.setErrorMessage(franchiseService.checkFranchiseJson(param));
                    return JSON.toJSONString(res);
                }

                Franchise franchise = franchiseService.json2Franchise(franchiseService.handleFranchiseJson(param));

                //保存新增
                franchiseService.addFranchise(franchise);

                //将新增的专辑保存到Elasticsearch服务器索引中
                // elasticsearchService.saveAlbum(album);

                //新增访问量实体
                visitService.insertVisit(new Visit(EntityType.FRANCHISE.getId(), franchise.getId()));

                res.message = String.format(ApiInfo.INSERT_DATA_SUCCESS, EntityType.FRANCHISE.getNameZh());

            } else {
                res.setErrorMessage(userService.checkAuthority(request).message);
            }
        } catch (Exception ex) {
            res.setErrorMessage(ex.getMessage());
        }
        return JSON.toJSONString(res);
    }

//    //删除专辑(单个/多个)
//    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
//    @ResponseBody
//    public String deleteAlbum(@RequestBody String json, HttpServletRequest request) {
//        ApiResult res = new ApiResult();
//        JSONArray albums = JSON.parseArray(json);
//        try {
//            if (userService.checkAuthority(request).state) {
//                for (int i = 0; i < albums.size(); i++) {
//
//                    int id = albums.getJSONObject(i).getInteger("id");
//
//                    //从数据库中删除专辑
//                    albumService.deleteAlbumById(id);
//
//                    //删除专辑对应的music
//                    musicService.deleteMusicByAlbumId(id);
//
//                    //从Elasticsearch服务器索引中删除专辑
//                    // elasticsearchService.deleteAlbum(albums.getJSONObject(i).getInteger("id"));
//
//                    //删除访问量实体
//                    visitService.deleteVisit(EntityType.ALBUM.getId(), id);
//                }
//                res.message = String.format(ApiInfo.DELETE_DATA_SUCCESS, EntityType.ALBUM.getNameZh());
//            } else {
//                res.setErrorMessage(userService.checkAuthority(request).message);
//            }
//        } catch (Exception ex) {
//            res.setErrorMessage(ex.getMessage());
//        }
//        return JSON.toJSONString(res);
//    }

    //更新基础信息
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public String updateFranchise(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        JSONObject param = JSON.parseObject(json);
        try {
            if (userService.checkAuthority(request).state) {
                //检测数据
                if(!StringUtils.isBlank(franchiseService.checkFranchiseJson(param))) {
                    res.setErrorMessage(franchiseService.checkFranchiseJson(param));
                    return JSON.toJSONString(res);
                }

                Franchise franchise = franchiseService.json2Franchise(franchiseService.handleFranchiseJson(param));

                //修改编辑时间
                franchise.setEditedTime(new Timestamp(System.currentTimeMillis()));

                franchiseService.updateFranchise(franchise.getId(), franchise);

                //将更新的专辑保存到Elasticsearch服务器索引中
                // elasticsearchService.saveAlbum(album);

                res.message = String.format(ApiInfo.UPDATE_DATA_SUCCESS, EntityType.FRANCHISE.getNameZh());

            } else {
                res.setErrorMessage(userService.checkAuthority(request).message);
            }
        } catch (Exception ex) {
            res.setErrorMessage(ex.getMessage());
        }
        return JSON.toJSONString(res);
    }

    //更新描述信息
    @RequestMapping(path = "/update-description", method = RequestMethod.POST)
    @ResponseBody
    public String updateFranchiseDescription(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        try {
            if (userService.checkAuthority(request).state) {
                int id = JSON.parseObject(json).getInteger("id");
                String description = JSON.parseObject(json).get("description").toString();
                franchiseService.updateFranchiseDescription(id, description);
                res.message = ApiInfo.UPDATE_GAME_DESCRIPTION_SUCCESS;
                //更新elasticsearch中的游戏
                // elasticsearchService.saveAlbum(albumService.getAlbumById(id));
            } else {
                res.setErrorMessage(userService.checkAuthority(request).message);
            }
            return JSON.toJSONString(res);
        } catch (Exception e) {
            res.setErrorMessage(e);
            return JSON.toJSONString(res);
        }
    }

    //endregion

    //region ------图片操作------

    //新增图片
    @RequestMapping(path = "/add-images", method = RequestMethod.POST)
    @ResponseBody
    public String addFranchiseImages(int id, MultipartFile[] images, String imageInfos, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        try {
            if (userService.checkAuthority(request).state) {

                if (images == null || images.length == 0) {
                    res.setErrorMessage(ApiInfo.INPUT_IMAGE_EMPTY);
                    return JSON.toJSONString(res);
                }

                //原始图片信息json数组
                JSONArray imagesJson = JSON.parseArray(franchiseService.getFranchise(id).getImages());
                //新增图片的信息
                JSONArray imageInfosJson = JSON.parseArray(imageInfos);

                //检测数据合法性
                String errorMessage = CommonImageUtils.checkAddImages(imageInfosJson, imagesJson);
                if (!StringUtils.equals("", errorMessage)) {
                    res.setErrorMessage(errorMessage);
                    return JSON.toJSONString(res);
                }

                franchiseService.addFranchiseImages(id, images, imagesJson, imageInfosJson);

                //更新elasticsearch中的游戏
                // elasticsearchService.saveAlbum(albumService.getAlbumById(id));

                res.message = String.format(ApiInfo.INSERT_IMAGES_SUCCESS, EntityType.FRANCHISE.getNameZh());

            } else {
                res.setErrorMessage(userService.checkAuthority(request).message);
            }
        } catch (Exception e) {
            res.setErrorMessage(e);
        }
        return JSON.toJSONString(res);
    }

    //更新图片，删除或更改信息
    @RequestMapping(path = "/update-images", method = RequestMethod.POST)
    @ResponseBody
    public String updateFranchiseImages(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        try {
            if (userService.checkAuthority(request).state) {

                //获取id
                int id = JSON.parseObject(json).getInteger("id");
                JSONArray images = JSON.parseObject(json).getJSONArray("images");
                for (int i = 0; i < images.size(); i++) {
                    images.getJSONObject(i).remove("thumbUrl");
                }

                //更新图片信息
                if (JSON.parseObject(json).getInteger("action") == DataActionType.UPDATE.getId()) {

                    //检测是否存在多张封面
                    String errorMessage = CommonImageUtils.checkUpdateImages(images);
                    if (!StringUtils.equals("", errorMessage)) {
                        res.setErrorMessage(errorMessage);
                        return JSON.toJSONString(res);
                    }

                    res.message = franchiseService.updateFranchiseImages(id, images.toJSONString());
                }//删除图片
                else if (JSON.parseObject(json).getInteger("action") == DataActionType.REAL_DELETE.getId()) {
                    res.message = franchiseService.deleteFranchiseImages(id, images);
                }else {
                    res.setErrorMessage(ApiInfo.NOT_ACTION);
                }

            } else {
                res.setErrorMessage(userService.checkAuthority(request).message);
            }
        } catch (Exception e) {
            res.setErrorMessage(e);
        }
        return JSON.toJSONString(res);
    }

    //endregion

}