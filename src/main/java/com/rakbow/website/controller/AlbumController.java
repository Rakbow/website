package com.rakbow.website.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.rakbow.website.data.DataActionType;
import com.rakbow.website.data.EntityType;
import com.rakbow.website.entity.Album;
import com.rakbow.website.entity.Visit;
import com.rakbow.website.service.*;
import com.rakbow.website.util.AlbumUtil;
import com.rakbow.website.util.common.ApiInfo;
import com.rakbow.website.util.common.ApiResult;
import com.rakbow.website.util.common.CommonUtil;
import com.rakbow.website.util.common.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

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

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private AlbumService albumService;
    @Autowired
    private UserService userService;
    @Autowired
    private SeriesService seriesService;
    @Autowired
    private ProductService productService;
    @Autowired
    private VisitService visitService;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private TagService tagService;
    @Value("${website.path.upload}")
    private String uploadPath;
    @Value("${website.path.img}")
    private String imgPath;
    @Value("${website.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private HostHolder hostHolder;
    //endregion

    //region ------获取页面------

    @RequestMapping(path = "/list", method = RequestMethod.GET)
    public ModelAndView getAlbumList(Model model) {
        ModelAndView view = new ModelAndView();
        model.addAttribute("mediaFormatSet", AlbumUtil.getMediaFormatSet());
        model.addAttribute("albumFormatSet", AlbumUtil.getAlbumFormatSet());
        model.addAttribute("publishFormatSet", AlbumUtil.getPublishFormatSet());
        model.addAttribute("seriesSet", seriesService.getAllSeriesSet());
        view.setViewName("/album/album-list");
        return view;
    }

    @RequestMapping(path = "/card", method = RequestMethod.GET)
    public String getAlbumCard(Model model) {
        List<JSONObject> albums = new ArrayList<>();
        albumService.getAll().forEach(i -> {
            albums.add(albumService.album2JsonDisplay(i));
        });
        model.addAttribute("albums", albums);
        model.addAttribute("justAddedAlbums", albumService.getJustAddedAlbums(5));
        model.addAttribute("justEditedAlbums", albumService.getJustEditedAlbums(5));
        model.addAttribute("popularAlbums", albumService.getPopularAlbums(10));
        model.addAttribute("seriesSet", seriesService.getAllSeriesSet());
        model.addAttribute("mediaFormatSet", AlbumUtil.getMediaFormatSet());
        model.addAttribute("albumFormatSet", AlbumUtil.getAlbumFormatSet());
        model.addAttribute("publishFormatSet", AlbumUtil.getPublishFormatSet());
        return "/album/album-card";
    }

    //获取单个专辑详细信息页面
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public String getAlbumDetail(@PathVariable("id") int albumId, Model model) {
        if (albumService.findAlbumById(albumId) == null) {
            model.addAttribute("errorMessage", String.format(ApiInfo.GET_DATA_FAILED_404, EntityType.ALBUM.getName()));
            return "/error/404";
        }
        //访问数+1
        visitService.increaseVisit(EntityType.ALBUM.getId(), albumId);

        Album album = albumService.findAlbumById(albumId);

        model.addAttribute("mediaFormatSet", AlbumUtil.getMediaFormatSet());
        model.addAttribute("albumFormatSet", AlbumUtil.getAlbumFormatSet());
        model.addAttribute("publishFormatSet", AlbumUtil.getPublishFormatSet());
        model.addAttribute("productSet", productService.getAllProductSetBySeriesId(album.getSeries()));

        model.addAttribute("seriesSet", seriesService.getAllSeriesSet());

        model.addAttribute("album", albumService.album2Json(album));
        model.addAttribute("user", hostHolder.getUser());
        //获取页面访问量
        model.addAttribute("visitNum", visitService.getVisit(EntityType.ALBUM.getId(), albumId).getVisitNum());
        //获取相关专辑
        model.addAttribute("relatedAlbums", albumService.getRelatedAlbums(albumId));
        return "/album/album-detail";
    }

    //endregion

    //region ------增删改查------

    //获得所有专辑
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ResponseBody
    public String getAllAlbum() {
        JSONArray albums = new JSONArray();

        albumService.getAll().forEach(i -> albums.add(albumService.album2Json(i)));

        return JSON.toJSONString(albums);
    }

    //获取单个专辑信息
    @RequestMapping(value = "/getAlbum", method = RequestMethod.GET)
    @ResponseBody
    public String getAlbum(@RequestBody String json) {
        ApiResult res = new ApiResult();
        try {
            int id = JSONObject.parseObject(json).getInteger("id");
            Album album = albumService.findAlbumById(id);
            if (album == null) {
                res.setErrorMessage(String.format(ApiInfo.GET_DATA_FAILED, EntityType.ALBUM.getName()));
                return JSON.toJSONString(res);
            }
            res.data = JSON.toJSONString(albumService.album2Json(album));
            return JSON.toJSONString(res);
        } catch (Exception ex) {
            ex.printStackTrace();
            res.state = 0;
            res.message = ex.getMessage();
            return JSON.toJSONString(res);
        }
    }

    //根据搜索条件获取专辑
    @RequestMapping(value = "/getAlbums", method = RequestMethod.POST)
    @ResponseBody
    public String getAlbumsByFilter(@RequestBody String json){
        ApiResult res = new ApiResult();
        try {
            JSONObject queryParam = new JSONObject();
            String series = JSONObject.parseObject(json).getString("seriesId");
            queryParam.put("seriesId", series);

            JSONArray productId = JSONObject.parseObject(json).getJSONArray("productId");
            if(productId.size() != 0){
                String productIdString = productId.toString();
                queryParam.put("productId", productIdString.substring(productIdString.indexOf("[")+1, productIdString.indexOf("]")));
            }else {
                queryParam.put("productId", null);
            }

            JSONArray publishFormat = JSONObject.parseObject(json).getJSONArray("publishFormat");
            if(publishFormat.size() != 0){
                String publishFormatString = publishFormat.toString();
                queryParam.put("publishFormat", publishFormatString.substring(publishFormatString.indexOf("[")+1, publishFormatString.indexOf("]")));
            }else {
                queryParam.put("publishFormat", null);
            }

            JSONArray albumFormat = JSONObject.parseObject(json).getJSONArray("albumFormat");
            if(albumFormat.size() != 0){
                String albumFormatString = albumFormat.toString();
                queryParam.put("albumFormat", albumFormatString.substring(albumFormatString.indexOf("[")+1, albumFormatString.indexOf("]")));
            }else {
                queryParam.put("albumFormat", null);
            }

            JSONArray mediaFormat = JSONObject.parseObject(json).getJSONArray("mediaFormat");
            if(mediaFormat.size() != 0){
                String mediaFormatString = mediaFormat.toString();
                queryParam.put("mediaFormat", mediaFormatString.substring(mediaFormatString.indexOf("[")+1, mediaFormatString.indexOf("]")));
            }else {
                queryParam.put("mediaFormat", null);
            }

            String hasBonus = JSONObject.parseObject(json).getString("hasBonus");
            if(hasBonus == null || hasBonus.equals("")){
                queryParam.put("hasBonus", null);
            }else {
                if(hasBonus.equals("all")){
                    queryParam.put("hasBonus", null);
                }else if (hasBonus.equals("has")) {
                    queryParam.put("hasBonus", "1");
                }else {
                    queryParam.put("hasBonus", "0");
                }
            }
            List<JSONObject> albums = new ArrayList<>();
            List<Album> searchResult = albumService.selectAlbumBySuperFilter(queryParam.toJSONString());
            if(searchResult.size() != 0){
                searchResult.forEach(i -> albums.add(albumService.album2JsonDisplay(i)));
            }
            res.data = albums;
            return JSON.toJSONString(res);
        } catch (Exception ex) {
            ex.printStackTrace();
            res.state = 0;
            res.message = ex.getMessage();
            return JSON.toJSONString(res);
        }
    }

    //新增专辑
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public String insertAlbum(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        JSONObject param = JSON.parseObject(json);
        try {
            if (userService.checkAuthority(request).state) {
                Album album = albumService.json2Album(param);

                //保存新增专辑
                albumService.insertAlbum(album);

                //将新增的专辑保存到Elasticsearch服务器索引中
                elasticsearchService.saveAlbum(album);

                //新增访问量实体
                visitService.insertVisit(new Visit(EntityType.ALBUM.getId(), album.getId()));

                res.message = String.format(ApiInfo.INSERT_DATA_SUCCESS, EntityType.ALBUM.getName());
            } else {
                res.setErrorMessage(userService.checkAuthority(request).message);
            }
        } catch (Exception ex) {
            res.setErrorMessage(ex.getMessage());
        }
        return JSON.toJSONString(res);
    }

    //删除专辑(单个/多个)
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteAlbum(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        JSONArray albums = JSON.parseArray(json);
        try {
            if (userService.checkAuthority(request).state) {
                for (int i = 0; i < albums.size(); i++) {

                    int id = albums.getJSONObject(i).getInteger("id");

                    //从数据库中删除专辑
                    albumService.deleteAlbumById(id);

                    //从Elasticsearch服务器索引中删除专辑
                    elasticsearchService.deleteAlbum(albums.getJSONObject(i).getInteger("id"));

                    //删除访问量实体
                    visitService.deleteVisit(EntityType.ALBUM.getId(), id);
                }
                res.message = String.format(ApiInfo.DELETE_DATA_SUCCESS, EntityType.ALBUM.getName());
            } else {
                res.setErrorMessage(userService.checkAuthority(request).message);
            }
        } catch (Exception ex) {
            res.setErrorMessage(ex.getMessage());
        }
        return JSON.toJSONString(res);
    }

    //列表界面的更新专辑操作
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public String updateAlbum(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        JSONObject param = JSON.parseObject(json);
        try {
            if (userService.checkAuthority(request).state) {
                Album album = albumService.json2Album(param);

                Album originAlbum = albumService.findAlbumById(album.getId());
                //修改编辑时间
                album.setEditedTime(new Timestamp(System.currentTimeMillis()));

                //列表编辑界面不涉及编辑的内容
                album.setTrackInfo(originAlbum.getTrackInfo());
                album.setImages(originAlbum.getImages());
                album.setArtists(originAlbum.getArtists());
                album.setBonus(originAlbum.getBonus());
                album.setDescription(originAlbum.getDescription());

                albumService.updateAlbum(album.getId(), album);

                //将更新的专辑保存到Elasticsearch服务器索引中
                elasticsearchService.saveAlbum(album);

                res.message = String.format(ApiInfo.UPDATE_DATA_SUCCESS, EntityType.ALBUM.getName());
            } else {
                res.setErrorMessage(userService.checkAuthority(request).message);
            }
        } catch (Exception ex) {
            res.setErrorMessage(ex.getMessage());
        }
        return JSON.toJSONString(res);
    }

    //获取专辑图像
    @RequestMapping(path = "/{id}/{fileName}", method = RequestMethod.GET)
    public void getAlbumImg(@PathVariable("fileName") String fileName, @PathVariable("id") int albumId, HttpServletResponse response) {
        // 服务器存放路径
        fileName = imgPath + "/album/" + albumId + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取图片失败: " + e.getMessage());
        }
    }

    //endregion

    //region 新增图片、Artists和音轨信息等

    //新增专辑图片
    @RequestMapping(path = "/insertAlbumImages", method = RequestMethod.POST)
    @ResponseBody
    public String insertAlbumImages(int id, MultipartFile[] images, String imageInfos, HttpServletRequest request) {

        ApiResult res = new ApiResult();
        try {
            if (userService.checkAuthority(request).state) {

                if (images.length == 0 || images == null) {
                    res.setErrorMessage(ApiInfo.INPUT_IMAGE_EMPTY);
                    return JSON.toJSONString(res);
                }

                JSONArray imageInfosTmp = JSON.parseArray(imageInfos);

                for (Object o : imageInfosTmp) {
                    JSONObject jo = (JSONObject) o;
                }

                //创建存储专辑图片的文件夹
                Path albumImgPath = Paths.get(imgPath + "/album/" + id);

                //存储图片链接的json
                JSONArray imgJson = new JSONArray();

                if (Files.notExists(albumImgPath)) {
                    try {
                        Files.createDirectory(albumImgPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < images.length; i++) {
                    //获取json中的jo对象
                    JSONObject imageInfo = imageInfosTmp.getJSONObject(i);

                    String fileName = images[i].getOriginalFilename();
                    String suffix = fileName.substring(fileName.lastIndexOf("."));
                    if (StringUtils.isBlank(suffix)) {
                        res.setErrorMessage(ApiInfo.INCORRECT_FILE_FORMAT);
                        return JSON.toJSONString(res);
                    }
                    fileName = (imageInfo.getString("nameEn") + suffix).replaceAll(" ", "");
                    // 确定文件存放的路径
                    File dest = new File(albumImgPath + "/" + fileName);
                    try {
                        // 存储文件
                        images[i].transferTo(dest);
                    } catch (IOException e) {
                        logger.error("上传文件失败: " + e.getMessage());
                        // throw new RuntimeException("上传文件失败,服务器发生异常!", e);
                        res.setErrorMessage(ApiInfo.UPLOAD_EXCEPTION);
                        return JSON.toJSONString(res);
                    }

                    //将数据存至数据库
                    JSONObject jo = new JSONObject();
                    jo.put("url", "/db/album/" + id + "/" + fileName);
                    jo.put("nameEn", imageInfo.getString("nameEn"));
                    jo.put("nameZh", imageInfo.getString("nameZh"));
                    jo.put("type", imageInfo.getString("type"));
                    jo.put("uploadTime", CommonUtil.getCurrentTime());
                    if (imageInfo.getString("description") == null) {
                        jo.put("description", "");
                    }
                    imgJson.add(jo);
                }
                JSONArray imagesJson = JSON.parseArray(albumService.findAlbumById(id).getImages());
                imagesJson.addAll(imgJson);
                albumService.insertAlbumImages(id, imagesJson.toJSONString());

                //更新elasticsearch中的专辑
                elasticsearchService.saveAlbum(albumService.findAlbumById(id));

                res.message = ApiInfo.INSERT_ALBUM_IMAGES_SUCCESS;

            } else {
                res.setErrorMessage(userService.checkAuthority(request).message);
            }
        } catch (Exception e) {
            res.setErrorMessage(e);
        }
        return JSON.toJSONString(res);
    }

    //更新专辑图片，删除或更改信息
    @RequestMapping(path = "/updateAlbumImages", method = RequestMethod.POST)
    @ResponseBody
    public String updateAlbumImages(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        try {
            if (userService.checkAuthority(request).state) {

                //获取专辑id
                int id = JSON.parseObject(json).getInteger("id");

                //更改信息
                if (JSON.parseObject(json).getInteger("action") == DataActionType.UPDATE.id) {
                    String image = JSON.parseObject(json).getString("image");
                    res.message = albumService.updateAlbumImages(id, image);
                    //更新elasticsearch中的专辑
                    elasticsearchService.saveAlbum(albumService.findAlbumById(id));
                }//删除
                else if (JSON.parseObject(json).getInteger("action") == DataActionType.REAL_DELETE.id) {
                    String imageUrl = JSON.parseObject(JSON.parseObject(json).getString("image")).getString("url");
                    res.message = albumService.deleteAlbumImages(id, imageUrl);
                    //更新elasticsearch中的专辑
                    elasticsearchService.saveAlbum(albumService.findAlbumById(id));
                } else {
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

    //更新专辑音乐创作相关Artists
    @RequestMapping(path = "/updateAlbumArtists", method = RequestMethod.POST)
    @ResponseBody
    public String updateAlbumArtists(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        try {
            if (userService.checkAuthority(request).state) {
                int id = JSON.parseObject(json).getInteger("id");
                String artists = JSON.parseObject(json).getJSONArray("artists").toString();
                if (StringUtils.isBlank(artists)) {
                    res.state = 0;
                    res.message = "输入信息为空";
                    return JSON.toJSONString(res);
                }
                albumService.updateAlbumArtists(id, artists);
                res.message = ApiInfo.UPDATE_ALBUM_ARTISTS_SUCCESS;
                //更新elasticsearch中的专辑
                elasticsearchService.saveAlbum(albumService.findAlbumById(id));
            } else {
                res.setErrorMessage(userService.checkAuthority(request).message);
            }
            return JSON.toJSONString(res);
        } catch (Exception e) {
            res.setErrorMessage(e);
            return JSON.toJSONString(res);
        }
    }

    //更新专辑音轨信息TrackInfo
    @RequestMapping(path = "/updateAlbumTrackInfo", method = RequestMethod.POST)
    @ResponseBody
    public String updateAlbumTrackInfo(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        try {
            System.out.println(json);
            if (userService.checkAuthority(request).state) {
                int id = JSON.parseObject(json).getInteger("id");
                String discList = JSON.parseObject(json).get("discList").toString();
                if (Objects.equals(discList, "[]")) {
                    res.setErrorMessage(ApiInfo.INPUT_TEXT_EMPTY);
                    return JSON.toJSONString(res);
                }
                albumService.updateAlbumTrackInfo(id, discList);
                res.message = ApiInfo.UPDATE_ALBUM_TRACK_INFO_SUCCESS;
                //更新elasticsearch中的专辑
                elasticsearchService.saveAlbum(albumService.findAlbumById(id));
            } else {
                res.setErrorMessage(userService.checkAuthority(request).message);
            }
            return JSON.toJSONString(res);
        } catch (Exception e) {
            res.setErrorMessage(e);
            return JSON.toJSONString(res);
        }
    }

    //更新专辑描述信息
    @RequestMapping(path = "/updateAlbumDescription", method = RequestMethod.POST)
    @ResponseBody
    public String updateAlbumDescription(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        try {
            if (userService.checkAuthority(request).state) {
                int id = JSON.parseObject(json).getInteger("id");
                String description = JSON.parseObject(json).get("description").toString();
                System.out.println(description);
                albumService.updateAlbumDescription(id, description);
                res.message = ApiInfo.UPDATE_ALBUM_DESCRIPTION_SUCCESS;
                //更新elasticsearch中的专辑
                elasticsearchService.saveAlbum(albumService.findAlbumById(id));
            } else {
                res.setErrorMessage(userService.checkAuthority(request).message);
            }
            return JSON.toJSONString(res);
        } catch (Exception e) {
            res.setErrorMessage(e);
            return JSON.toJSONString(res);
        }
    }

    //更新专辑特典信息
    @RequestMapping(path = "/updateAlbumBonus", method = RequestMethod.POST)
    @ResponseBody
    public String updateAlbumBonus(@RequestBody String json, HttpServletRequest request) {
        ApiResult res = new ApiResult();
        try {
            if (userService.checkAuthority(request).state) {
                int id = JSON.parseObject(json).getInteger("id");
                String bonus = JSON.parseObject(json).get("bonus").toString();
                albumService.updateAlbumBonus(id, bonus);
                res.message = ApiInfo.UPDATE_ALBUM_BONUS_SUCCESS;
                //更新elasticsearch中的专辑
                elasticsearchService.saveAlbum(albumService.findAlbumById(id));
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

    @RequestMapping(path = "/test", method = RequestMethod.GET)
    @ResponseBody
    public List<JSONObject> test(int id) {
        return albumService.getRelatedAlbums(id);
    }
}
