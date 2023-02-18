package com.rakbow.website.controller.entity;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.controller.UserController;
import com.rakbow.website.data.emun.common.DataActionType;
import com.rakbow.website.data.emun.common.EntityType;
import com.rakbow.website.data.SearchResult;
import com.rakbow.website.data.vo.disc.DiscVOAlpha;
import com.rakbow.website.entity.Disc;
import com.rakbow.website.service.DiscService;
import com.rakbow.website.service.UserService;
import com.rakbow.website.data.ApiInfo;
import com.rakbow.website.data.ApiResult;
import com.rakbow.website.util.common.EntityUtils;
import com.rakbow.website.util.convertMapper.DiscVOMapper;
import com.rakbow.website.util.file.CommonImageUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-12-15 20:57
 * @Description:
 */
@Controller
@RequestMapping("/db/disc")
public class DiscController {

    //region ------引入实例------

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private DiscService discService;
    @Autowired
    private UserService userService;
    @Autowired
    private EntityUtils entityUtils;

    private final DiscVOMapper discVOMapper = DiscVOMapper.INSTANCES;

    //endregion

    //region ------获取页面------

    //获取单个专辑详细信息页面
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public String getAlbumDetail(@PathVariable("id") int id, Model model, HttpServletRequest request) {
        Disc disc = discService.getDiscWithAuth(id, userService.getUserOperationAuthority(userService.getUserByRequest(request)));
        if (discService.getDisc(id) == null) {
            model.addAttribute("errorMessage", String.format(ApiInfo.GET_DATA_FAILED_404, EntityType.DISC.getNameZh()));
            return "/error/404";
        }

        model.addAttribute("disc", discVOMapper.disc2VO(disc));
        //前端选项数据
        model.addAttribute("options", entityUtils.getDetailOptions(EntityType.DISC.getId()));
        //实体类通用信息
        model.addAttribute("detailInfo", entityUtils.getItemDetailInfo(disc, EntityType.DISC.getId()));
        //获取页面数据
        model.addAttribute("pageInfo", entityUtils.getPageInfo(EntityType.DISC.getId(), id, disc.getAddedTime(), disc.getEditedTime()));
        //图片相关
        model.addAttribute("itemImageInfo", CommonImageUtil.segmentImages(disc.getImages(), 200, EntityType.DISC, false));
        //获取相关碟片
        model.addAttribute("relatedDiscs", discService.getRelatedDiscs(id));
        return "/database/itemDetail/disc-detail";
    }

    //endregion

    //region ------增删改查------

    //根据搜索条件获取碟片--列表界面
    @RequestMapping(value = "/get-discs", method = RequestMethod.POST)
    @ResponseBody
    public String getDiscsByFilterList(@RequestBody String json, HttpServletRequest request) {

        JSONObject param = JSON.parseObject(json);
        JSONObject queryParams = param.getJSONObject("queryParams");
        String pageLabel = param.getString("pageLabel");

        List<DiscVOAlpha> discs = new ArrayList<>();

        SearchResult searchResult = discService.getDiscsByFilterList(queryParams,
                userService.getUserOperationAuthority(userService.getUserByRequest(request)));

        if (StringUtils.equals(pageLabel, "list")) {
            discs = discVOMapper.disc2VOAlpha((List<Disc>) searchResult.data);
        }
        if (StringUtils.equals(pageLabel, "index")) {
            discs = discVOMapper.disc2VOAlpha((List<Disc>) searchResult.data);
        }

        JSONObject result = new JSONObject();
        result.put("data", discs);
        result.put("total", searchResult.total);

        return JSON.toJSONString(result);
    }

    //新增碟片
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDisc(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        JSONObject param = JSON.parseObject(json);
        try {
            //检测数据
            String errorMsg = discService.checkDiscJson(param);
            if(!StringUtils.isBlank(errorMsg)) {
                res.setErrorMessage(errorMsg);
                return JSON.toJSONString(res);
            }

            Disc disc = discService.json2Disc(discService.handleDiscJson(param));

            //保存新增专辑

            res.message = discService.addDisc(disc);
        } catch (Exception ex) {
            res.setErrorMessage(ex.getMessage());
        }
        return JSON.toJSONString(res);
    }

    //删除碟片(单个/多个)
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteDisc(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        List<Disc> discs = JSON.parseArray(json).toJavaList(Disc.class);
        try {
            for (Disc disc : discs) {
                //从数据库中删除专辑
                discService.deleteDisc(disc);
            }
            res.message = String.format(ApiInfo.DELETE_DATA_SUCCESS, EntityType.DISC.getNameZh());
        } catch (Exception ex) {
            res.setErrorMessage(ex.getMessage());
        }
        return JSON.toJSONString(res);
    }

    //更新碟片基础信息
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public String updateDisc(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        try {
            JSONObject param = JSON.parseObject(json);
            //检测数据
            String errorMsg = discService.checkDiscJson(param);
            if(!StringUtils.isBlank(errorMsg)) {
                res.setErrorMessage(errorMsg);
                return JSON.toJSONString(res);
            }

            Disc disc = discService.json2Disc(discService.handleDiscJson(param));

            //修改编辑时间
            disc.setEditedTime(new Timestamp(System.currentTimeMillis()));

            res.message = discService.updateDisc(disc.getId(), disc);
        } catch (Exception ex) {
            res.setErrorMessage(ex.getMessage());
        }
        return JSON.toJSONString(res);
    }

    //endregion

    //region ------进阶信息增删改查------

    //新增图片
    @RequestMapping(path = "/add-images", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscImages(int id, MultipartFile[] images, String imageInfos, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        try {
            if (images == null || images.length == 0) {
                res.setErrorMessage(ApiInfo.INPUT_FILE_EMPTY);
                return JSON.toJSONString(res);
            }

            Disc disc = discService.getDisc(id);

            //原始图片信息json数组
            JSONArray imagesJson = JSON.parseArray(disc.getImages());
            //新增图片的信息
            JSONArray imageInfosJson = JSON.parseArray(imageInfos);

            //检测数据合法性
            String errorMsg = CommonImageUtil.checkAddImages(imageInfosJson, imagesJson);
            if (!StringUtils.isBlank(errorMsg)) {
                res.setErrorMessage(errorMsg);
                return JSON.toJSONString(res);
            }

            res.message = discService.addDiscImages(id, images, imagesJson, imageInfosJson, userService.getUserByRequest(request));
        } catch (Exception e) {
            res.setErrorMessage(e);
        }
        return JSON.toJSONString(res);
    }

    //更新图片，删除或更改信息
    @RequestMapping(path = "/update-images", method = RequestMethod.POST)
    @ResponseBody
    public String updateDiscImages(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        try {
            //获取专辑id
            int id = JSON.parseObject(json).getInteger("id");
            int action = JSON.parseObject(json).getInteger("action");
            JSONArray images = JSON.parseObject(json).getJSONArray("images");
            for (int i = 0; i < images.size(); i++) {
                images.getJSONObject(i).remove("thumbUrl");
            }

            Disc disc = discService.getDisc(id);

            //更新图片信息
            if (action == DataActionType.UPDATE.getId()) {

                //检测是否存在多张封面
                String errorMsg = CommonImageUtil.checkUpdateImages(images);
                if (!StringUtils.isBlank(errorMsg)) {
                    res.setErrorMessage(errorMsg);
                    return JSON.toJSONString(res);
                }

                res.message = discService.updateDiscImages(id, images.toJSONString());
            }//删除图片
            else if (action == DataActionType.REAL_DELETE.getId()) {
                res.message = discService.deleteDiscImages(disc, images);
            }else {
                res.setErrorMessage(ApiInfo.NOT_ACTION);
            }
        } catch (Exception e) {
            res.setErrorMessage(e);
        }
        return JSON.toJSONString(res);
    }

    //更新专辑规格信息
    @RequestMapping(path = "/update-spec", method = RequestMethod.POST)
    @ResponseBody
    public String updateDiscSpec(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        try {
            int id = JSON.parseObject(json).getInteger("id");
            String spec = JSON.parseObject(json).get("spec").toString();
            if (StringUtils.isBlank(spec)) {
                res.setErrorMessage(ApiInfo.INPUT_TEXT_EMPTY);
                return JSON.toJSONString(res);
            }

            res.message = discService.updateDiscSpec(id, spec);
        } catch (Exception e) {
            res.setErrorMessage(e);
        }
        return JSON.toJSONString(res);
    }

    //更新专辑描述信息
    @RequestMapping(path = "/update-description", method = RequestMethod.POST)
    @ResponseBody
    public String updateDiscDescription(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        try {
            int id = JSON.parseObject(json).getInteger("id");
            String description = JSON.parseObject(json).get("description").toString();
            discService.updateDiscDescription(id, description);
            res.message = ApiInfo.UPDATE_DESCRIPTION_SUCCESS;
        } catch (Exception e) {
            res.setErrorMessage(e);
        }
        return JSON.toJSONString(res);
    }

    //更新专辑特典信息
    @RequestMapping(path = "/update-bonus", method = RequestMethod.POST)
    @ResponseBody
    public String updateDiscBonus(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        try {
            int id = JSON.parseObject(json).getInteger("id");
            String bonus = JSON.parseObject(json).get("bonus").toString();

            res.message = discService.updateDiscBonus(id, bonus);
        } catch (Exception e) {
            res.setErrorMessage(e);
        }
        return JSON.toJSONString(res);
    }

    //endregion

}