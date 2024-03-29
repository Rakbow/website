package com.rakbow.website.controller.entity;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.annotation.UniqueVisitor;
import com.rakbow.website.controller.interceptor.AuthorityInterceptor;
import com.rakbow.website.data.ApiInfo;
import com.rakbow.website.data.ApiResult;
import com.rakbow.website.data.SearchResult;
import com.rakbow.website.data.dto.QueryParams;
import com.rakbow.website.data.emun.common.Entity;
import com.rakbow.website.data.vo.album.AlbumVO;
import com.rakbow.website.data.vo.album.AlbumVOAlpha;
import com.rakbow.website.entity.Album;
import com.rakbow.website.entity.Music;
import com.rakbow.website.service.AlbumService;
import com.rakbow.website.service.EntityService;
import com.rakbow.website.service.MusicService;
import com.rakbow.website.util.common.DateUtil;
import com.rakbow.website.util.common.EntityUtil;
import com.rakbow.website.util.convertMapper.entity.AlbumVOMapper;
import com.rakbow.website.util.entity.MusicUtil;
import com.rakbow.website.util.file.CommonImageUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-08-07 21:53
 * @Description:
 */
@Controller
@RequestMapping("/db/album")
public class AlbumController {

    //region ------引入实例------

    private static final Logger logger = LoggerFactory.getLogger(AlbumController .class);

    @Resource
    private AlbumService albumService;
    @Resource
    private MusicService musicService;
    @Resource
    private EntityUtil entityUtil;
    @Resource
    private EntityService entityService;

    private final AlbumVOMapper albumVOMapper = AlbumVOMapper.INSTANCES;
    //endregion

    //region ------获取页面------

    //获取单个专辑详细信息页面
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    @UniqueVisitor
    public String getAlbumDetail(@PathVariable("id") int id, Model model) {
        Album album = albumService.getAlbumWithAuth(id);
        if (album == null) {
            model.addAttribute("errorMessage", String.format(ApiInfo.GET_DATA_FAILED_404, Entity.ALBUM.getNameZh()));
            return "/error/404";
        }

        List<Music> musics = musicService.getMusicsByAlbumId(id);

        String coverUrl = CommonImageUtil.getCoverUrl(album.getImages());
        model.addAttribute("album", albumService.buildVO(album, musics));
        if(AuthorityInterceptor.isUser()) {
            model.addAttribute("audioInfos", MusicUtil.getMusicAudioInfo(musicService.getMusicsByAlbumId(id), coverUrl));
        }
        if(AuthorityInterceptor.isJunior()) {
            //前端选项数据
            model.addAttribute("options", entityUtil.getDetailOptions(Entity.ALBUM.getId()));
        }
        //实体类通用信息
        model.addAttribute("detailInfo", entityUtil.getItemDetailInfo(album, Entity.ALBUM.getId()));
        //获取页面数据
        model.addAttribute("pageInfo", entityService.getPageInfo(Entity.ALBUM.getId(), id, album));
        //图片相关
        model.addAttribute("itemImageInfo", CommonImageUtil.segmentImages(album.getImages(), 185, Entity.ALBUM, false));

        return "/database/itemDetail/album-detail";
    }

    //endregion

    //region ------基础增删改------

    @RequestMapping(path = "/get-album-detail", method = RequestMethod.POST)
    @ResponseBody
    @UniqueVisitor
    public String getAlbumDetailData(@RequestBody JSONObject param) {
        ApiResult res = new ApiResult();
        try {
            int id = param.getIntValue("id");
            Album album = albumService.getAlbumWithAuth(id);

            if (album == null) {
                res.setErrorMessage(String.format(ApiInfo.GET_DATA_FAILED_404, Entity.ALBUM.getNameZh()));
                return JSON.toJSONString(res);
            }

            List<Music> musics = musicService.getMusicsByAlbumId(id);

            String coverUrl = CommonImageUtil.getCoverUrl(album.getImages());

            AlbumVO albumVO = albumService.buildVO(album, musics);

            res.data = albumVO;
        }catch (Exception e) {
            res.setErrorMessage(e.getMessage());
        }
        return JSON.toJSONString(res);
    }

    //根据搜索条件获取专辑
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/get-albums", method = RequestMethod.POST)
    @ResponseBody
    public String getAlbumsByFilter(@RequestBody JSONObject param) {

        QueryParams queryParam = JSON.to(QueryParams.class, param.get("queryParams"));
        String pageLabel = param.getString("pageLabel");

        List<AlbumVOAlpha> albums = new ArrayList<>();

        SearchResult searchResult = albumService.getAlbumsByFilter(queryParam);

        if (StringUtils.equals(pageLabel, "list")) {
            albums = albumVOMapper.toVOAlpha((List<Album>) searchResult.data);
        }
        if (StringUtils.equals(pageLabel, "index")) {
            albums = albumVOMapper.toVOAlpha((List<Album>) searchResult.data);
        }

        JSONObject result = new JSONObject();
        result.put("data", albums);
        result.put("total", searchResult.total);

        return JSON.toJSONString(result);
    }

    //新增专辑
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addAlbum(@RequestBody JSONObject json) {
        ApiResult res = new ApiResult();
        try {
            //检测数据
            String checkMsg = albumService.checkAlbumJson(json);
            if(!StringUtils.isBlank(checkMsg)) {
                throw new Exception(checkMsg);
            }

            Album album = JSON.to(Album.class, albumService.handleAlbumJson(json));

            //保存新增专辑
            res.message = albumService.addAlbum(album);

            //将新增的专辑保存到meilisearch服务器索引中
            // meiliSearchUtils.saveSingleData(album, EntityType.ALBUM);

        } catch (Exception ex) {
            res.setErrorMessage(ex.getMessage());
        }
        return JSON.toJSONString(res);
    }

    //删除专辑(单个/多个)
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteAlbum(@RequestBody JSONArray json) {
        ApiResult res = new ApiResult();
        try {
            List<Integer> ids = new ArrayList<>();
            for (int i = 0; i < json.size(); i++) {
                ids.add(json.getJSONObject(i).getIntValue("id"));
            }
            if(!ids.isEmpty()) {
                //从数据库中删除专辑
                albumService.deleteAlbums(ids);

                //删除专辑对应的music
                musicService.deleteMusicsByAlbumIds(ids);
            }
            res.message = String.format(ApiInfo.DELETE_DATA_SUCCESS, Entity.ALBUM.getNameZh());
        } catch (Exception ex) {
            res.setErrorMessage(ex.getMessage());
        }
        return JSON.toJSONString(res);
    }

    //更新专辑基础信息
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public String updateAlbum(@RequestBody JSONObject json) {
        ApiResult res = new ApiResult();
        try {
            String checkMsg = albumService.checkAlbumJson(json);
            if(!StringUtils.isBlank(checkMsg)) {
                throw new Exception(checkMsg);
            }

            Album album = JSON.to(Album.class, albumService.handleAlbumJson(json));

            //修改编辑时间
            album.setEditedTime(DateUtil.NOW_TIMESTAMP);
            res.message =  albumService.updateAlbum(album.getId(), album);

        } catch (Exception ex) {
            res.setErrorMessage(ex.getMessage());
        }
        return JSON.toJSONString(res);
    }

    //endregion

    //region ------进阶增删改------

    //更新专辑音轨信息TrackInfo
    @RequestMapping(path = "/update-trackInfo", method = RequestMethod.POST)
    @ResponseBody
    public String updateAlbumTrackInfo(@RequestBody JSONObject json) {
        ApiResult res = new ApiResult();
        try {
            int id = json.getIntValue("id");
            String discList = json.getString("discList");

            albumService.updateAlbumTrackInfo(id, discList);

            res.message = ApiInfo.UPDATE_ALBUM_TRACK_INFO_SUCCESS;
        } catch (Exception e) {
            res.setErrorMessage(e);
        }
        return JSON.toJSONString(res);
    }

    @RequestMapping(value = "/get-related-albums", method = RequestMethod.POST)
    @ResponseBody
    public String getRelatedAlbums(@RequestBody JSONObject json) {
        ApiResult res = new ApiResult();
        try {
            int id = json.getIntValue("id");
            res.data = albumService.getRelatedAlbums(id);

        }catch (Exception e) {
            res.setErrorMessage(e);
        }
        return JSON.toJSONString(res);
    }

    //endregion

}
