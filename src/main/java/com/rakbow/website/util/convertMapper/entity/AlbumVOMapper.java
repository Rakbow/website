package com.rakbow.website.util.convertMapper.entity;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.data.emun.entity.album.AlbumFormat;
import com.rakbow.website.data.emun.entity.album.PublishFormat;
import com.rakbow.website.data.emun.common.EntityType;
import com.rakbow.website.data.emun.common.MediaFormat;
import com.rakbow.website.data.vo.album.AlbumVO;
import com.rakbow.website.data.vo.album.AlbumVOAlpha;
import com.rakbow.website.data.vo.album.AlbumVOBeta;
import com.rakbow.website.data.vo.album.AlbumVOGamma;
import com.rakbow.website.entity.Album;
import com.rakbow.website.entity.Entry;
import com.rakbow.website.entity.Music;
import com.rakbow.website.service.MusicService;
import com.rakbow.website.util.common.DateUtil;
import com.rakbow.website.util.common.LikeUtil;
import com.rakbow.website.util.common.SpringUtil;
import com.rakbow.website.util.common.VisitUtil;
import com.rakbow.website.util.entity.AlbumUtil;
import com.rakbow.website.util.entity.FranchiseUtil;
import com.rakbow.website.util.entity.ProductUtil;
import com.rakbow.website.util.entry.EntryUtil;
import com.rakbow.website.util.file.CommonImageUtil;
import com.rakbow.website.util.file.QiniuImageUtil;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-01-11 16:13
 * @Description: album VO转换接口
 */
@Mapper(componentModel = "spring")
public interface AlbumVOMapper {

    AlbumVOMapper INSTANCES = Mappers.getMapper(AlbumVOMapper.class);

    /**
     * Album转VO对象，用于详情页面，转换量最大的
     *
     * @param album 专辑
     * @return AlbumVO
     * @author rakbow
     */
    default AlbumVO album2VO(Album album) {
        if (album == null) {
            return null;
        }
        AlbumVO albumVo = new AlbumVO();

        MusicService musicService = SpringUtil.getBean("musicService");
        List<Music> musics = musicService.getMusicsByAlbumId(album.getId());

        //基础信息
        albumVo.setId(album.getId());
        albumVo.setCatalogNo(album.getCatalogNo());
        albumVo.setName(album.getName());
        albumVo.setNameEn(album.getNameEn());
        albumVo.setNameZh(album.getNameZh());
        albumVo.setBarcode(album.getBarcode());
        albumVo.setPrice(album.getPrice());
        albumVo.setCurrencyUnit(album.getCurrencyUnit());
        albumVo.setRemark(album.getRemark());
        albumVo.setReleaseDate(DateUtil.dateToString(album.getReleaseDate()));
        albumVo.setHasBonus(album.getHasBonus() == 1);

        //企业信息
        albumVo.setCompanies(EntryUtil.getCompanies(album.getCompanies()));
        //可供编辑的企业信息
        albumVo.setEditCompanies(JSONArray.parseArray(album.getCompanies()));

        //规格信息
        albumVo.setPublishFormat(PublishFormat.getAttribute(album.getPublishFormat()));
        albumVo.setAlbumFormat(AlbumFormat.getAttributes(album.getAlbumFormat()));
        albumVo.setMediaFormat(MediaFormat.getAttributes(album.getMediaFormat()));

        //大文本字段
        albumVo.setBonus(album.getBonus());
        albumVo.setArtists(JSONArray.parseArray(album.getArtists()));

        //可供编辑的editDiscList
        JSONArray editDiscList = AlbumUtil.getEditDiscList(album.getTrackInfo(), musics);
        //音轨信息
        JSONObject trackInfo = AlbumUtil.getFinalTrackInfo(album.getTrackInfo(), musics);

        albumVo.setEditDiscList(editDiscList);
        albumVo.setTrackInfo(trackInfo);

        return albumVo;
    }

    /**
     * Album转VO，供album-list和album-index界面使用，信息量较少
     *
     * @param album 专辑
     * @return AlbumVOAlpha
     * @author rakbow
     */
    default AlbumVOAlpha album2VOAlpha(Album album) {
        if (album == null) {
            return null;
        }
        AlbumVOAlpha albumVOAlpha = new AlbumVOAlpha();

        //基础信息
        albumVOAlpha.setId(album.getId());
        albumVOAlpha.setCatalogNo(album.getCatalogNo());
        albumVOAlpha.setName(album.getName());
        albumVOAlpha.setNameEn(album.getNameEn());
        albumVOAlpha.setNameZh(album.getNameZh());
        albumVOAlpha.setBarcode(album.getBarcode());
        albumVOAlpha.setPrice(album.getPrice());
        albumVOAlpha.setCurrencyUnit(album.getCurrencyUnit());
        albumVOAlpha.setRemark(album.getRemark());
        albumVOAlpha.setReleaseDate(DateUtil.dateToString(album.getReleaseDate()));
        albumVOAlpha.setHasBonus(album.getHasBonus() == 1);

        //图片相关
        albumVOAlpha.setCover(CommonImageUtil.generateCover(album.getImages(), EntityType.ALBUM));

        //关联信息
        albumVOAlpha.setProducts(ProductUtil.getProductList(album.getProducts()));
        albumVOAlpha.setFranchises(FranchiseUtil.getFranchiseList(album.getFranchises()));

        //规格信息
        albumVOAlpha.setPublishFormat(PublishFormat.getAttribute(album.getPublishFormat()));
        albumVOAlpha.setAlbumFormat(AlbumFormat.getAttributes(album.getAlbumFormat()));
        albumVOAlpha.setMediaFormat(MediaFormat.getAttributes(album.getMediaFormat()));

        //审计字段
        albumVOAlpha.setAddedTime(DateUtil.timestampToString(album.getAddedTime()));
        albumVOAlpha.setEditedTime(DateUtil.timestampToString(album.getEditedTime()));
        albumVOAlpha.setStatus(album.getStatus() == 1);

        return albumVOAlpha;
    }

    /**
     * 列表转换, Album转VO对象，供album-list和album-index界面使用，信息量较少
     *
     * @param albums 专辑列表
     * @return List<AlbumVOAlpha>
     * @author rakbow
     */
    default List<AlbumVOAlpha> album2VOAlpha(List<Album> albums) {
        List<AlbumVOAlpha> albumVOAlphas = new ArrayList<>();

        if (!albums.isEmpty()) {
            albums.forEach(album -> {
                albumVOAlphas.add(album2VOAlpha(album));
            });
        }

        return albumVOAlphas;
    }

    /**
     * Album转VO对象，信息量最少
     *
     * @param album 专辑
     * @return AlbumVOBeta
     * @author rakbow
     */
    default AlbumVOBeta album2VOBeta(Album album) {
        if (album == null) {
            return null;
        }
        AlbumVOBeta albumVOBeta = new AlbumVOBeta();

        //基础信息
        albumVOBeta.setId(album.getId());
        albumVOBeta.setCatalogNo(album.getCatalogNo());
        albumVOBeta.setName(album.getName());
        albumVOBeta.setNameEn(album.getNameEn());
        albumVOBeta.setNameZh(album.getNameZh());
        albumVOBeta.setReleaseDate(DateUtil.dateToString(album.getReleaseDate()));
        albumVOBeta.setCover(CommonImageUtil.generateThumbCover(album.getImages(), EntityType.ALBUM, 50));
        albumVOBeta.setAlbumFormat(AlbumFormat.getAttributes(album.getAlbumFormat()));
        albumVOBeta.setAddedTime(DateUtil.timestampToString(album.getAddedTime()));
        albumVOBeta.setEditedTime(DateUtil.timestampToString(album.getEditedTime()));

        return albumVOBeta;
    }

    /**
     * 列表转换, Album转VO对象，信息量最少
     *
     * @param albums 专辑列表
     * @return List<AlbumVOBeta>
     * @author rakbow
     */
    default List<AlbumVOBeta> album2VOBeta(List<Album> albums) {
        List<AlbumVOBeta> albumVOBetas = new ArrayList<>();

        if (!albums.isEmpty()) {
            albums.forEach(album -> {
                albumVOBetas.add(album2VOBeta(album));
            });
        }

        return albumVOBetas;
    }

    /**
     * Album转VO对象，用于存储到搜索引擎
     *
     * @param album 专辑
     * @return AlbumVOGamma
     * @author rakbow
     */
    default AlbumVOGamma album2VOGamma(Album album) {
        if (album == null) {
            return null;
        }

        VisitUtil visitUtil = SpringUtil.getBean("visitUtil");
        LikeUtil likeUtil = SpringUtil.getBean("likeUtil");

        AlbumVOGamma albumVOGamma = new AlbumVOGamma();
        albumVOGamma.setId(album.getId());
        albumVOGamma.setCatalogNo(album.getCatalogNo());
        albumVOGamma.setName(album.getName());
        albumVOGamma.setNameEn(album.getNameEn());
        albumVOGamma.setNameZh(album.getNameZh());
        albumVOGamma.setReleaseDate(DateUtil.dateToString(album.getReleaseDate()));
        albumVOGamma.setHasBonus(album.getHasBonus() == 1);

        //关联信息
        albumVOGamma.setProducts(ProductUtil.getProductList(album.getProducts()));
        albumVOGamma.setFranchises(FranchiseUtil.getFranchiseList(album.getFranchises()));
        albumVOGamma.setAlbumFormat(AlbumFormat.getAttributes(album.getAlbumFormat()));

        albumVOGamma.setCover(QiniuImageUtil.getThumb70Url(album.getImages()));

        albumVOGamma.setVisitCount(visitUtil.getVisit(EntityType.ALBUM.getId(), album.getId()));
        albumVOGamma.setLikeCount(likeUtil.getLike(EntityType.ALBUM.getId(), album.getId()));

        return albumVOGamma;
    }

    /**
     * 列表转换, Album转VO对象，用于存储到搜索引擎
     *
     * @param albums 专辑列表
     * @return List<AlbumVOGamma>
     * @author rakbow
     */
    default List<AlbumVOGamma> album2VOGamma(List<Album> albums) {
        List<AlbumVOGamma> albumVOGammas = new ArrayList<>();

        if (!albums.isEmpty()) {
            albums.forEach(album -> {
                albumVOGammas.add(album2VOGamma(album));
            });
        }

        return albumVOGammas;
    }

}