package com.rakbow.website.util.convertMapper.entity;

import com.rakbow.website.data.Attribute;
import com.rakbow.website.data.emun.common.Entity;
import com.rakbow.website.data.emun.entity.album.AlbumFormat;
import com.rakbow.website.data.emun.entity.album.PublishFormat;
import com.rakbow.website.data.emun.temp.EnumUtil;
import com.rakbow.website.data.vo.album.AlbumVO;
import com.rakbow.website.data.vo.album.AlbumVOAlpha;
import com.rakbow.website.data.vo.album.AlbumVOBeta;
import com.rakbow.website.data.vo.album.AlbumVOGamma;
import com.rakbow.website.entity.Album;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

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
    Entity ENTITY = Entity.ALBUM;
    int entityTypeId = Entity.ALBUM.getId();

    //region single convert interface

    /**
     * Album转VO对象，用于详情页面，转换量最大的
     *
     * @param album 专辑
     * @return AlbumVO
     * @author rakbow
     */
    @Mapping(target = "releaseDate", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getDate(album.getReleaseDate()))")
    @Mapping(target = "hasBonus", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getBool(album.getHasBonus()))")
    @Mapping(target = "companies", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getCompanies(album.getCompanies()))")
    @Mapping(target = "artists", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getPersonnel(album.getArtists()))")
    @Mapping(target = "publishFormat", source = "publishFormat", qualifiedByName = "getPublishFormat")
    @Mapping(target = "albumFormat", source = "albumFormat", qualifiedByName = "getAlbumFormat")
    @Mapping(target = "mediaFormat", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getMediaFormat(album.getMediaFormat()))")
    @Mapping(target = "trackInfo", ignore = true)
    @Mapping(target = "editDiscList", ignore = true)
    @Mapping(target = "editCompanies", ignore = true)
    @Mapping(target = "editArtists", ignore = true)
    AlbumVO toVO(Album album);

    /**
     * Album转VO，供album-list和album-index界面使用，信息量较少
     *
     * @param album 专辑
     * @return AlbumVOAlpha
     * @author rakbow
     */
    @Mapping(target = "releaseDate", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getDate(album.getReleaseDate()))")
    @Mapping(target = "hasBonus", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getBool(album.getHasBonus()))")
    @Mapping(target = "cover", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getCover(album.getImages(), ENTITY))")
    @Mapping(target = "products", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getProducts(album.getProducts()))")
    @Mapping(target = "franchises", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getFranchises(album.getFranchises()))")
    @Mapping(target = "publishFormat", source = "publishFormat", qualifiedByName = "getPublishFormat")
    @Mapping(target = "albumFormat", source = "albumFormat", qualifiedByName = "getAlbumFormat")
    @Mapping(target = "mediaFormat", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getMediaFormat(album.getMediaFormat()))")
    @Mapping(target = "addedTime", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getVOTime(album.getAddedTime()))")
    @Mapping(target = "editedTime", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getVOTime(album.getEditedTime()))")
    @Mapping(target = "status", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getBool(album.getStatus()))")
    @Mapping(target = "visitNum", ignore = true)
    @Named("toVOAlpha")
    AlbumVOAlpha toVOAlpha(Album album);

    /**
     * Album转VO对象，信息量最少
     *
     * @param album 专辑
     * @return AlbumVOBeta
     * @author rakbow
     */
    @Mapping(target = "releaseDate", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getDate(album.getReleaseDate()))")
    @Mapping(target = "cover", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getThumbCover(album.getImages(), ENTITY, 50))")
    @Mapping(target = "albumFormat", source = "albumFormat", qualifiedByName = "getAlbumFormat")
    @Mapping(target = "addedTime", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getVOTime(album.getAddedTime()))")
    @Mapping(target = "editedTime", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getVOTime(album.getEditedTime()))")
    @Named("toVOBeta")
    AlbumVOBeta toVOBeta(Album album);

    /**
     * Album转VO对象，用于存储到搜索引擎
     *
     * @param album 专辑
     * @return AlbumVOGamma
     * @author rakbow
     */
    @Mapping(target = "hasBonus", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getBool(album.getHasBonus()))")
    @Mapping(target = "products", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getProducts(album.getProducts()))")
    @Mapping(target = "franchises", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getFranchises(album.getFranchises()))")
    @Mapping(target = "albumFormat", source = "albumFormat", qualifiedByName = "getAlbumFormat")
    @Mapping(target = "cover", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getThumb70Cover(album.getImages()))")
    @Mapping(target = "visitCount", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getVisitCount(entityTypeId, album.getId()))")
    @Mapping(target = "likeCount", expression = "java(com.rakbow.website.util.convertMapper.entity.EntityConverter.getLikeCount(entityTypeId, album.getId()))")
    @Named("toVOGamma")
    AlbumVOGamma toVOGamma(Album album);

    //endregion

    //region multi convert interface

    /**
     * 列表转换, Album转VO对象，供album-list和album-index界面使用，信息量较少
     *
     * @param albums 专辑列表
     * @return List<AlbumVOAlpha>
     * @author rakbow
     */
    @IterableMapping(qualifiedByName = "toVOAlpha")
    List<AlbumVOAlpha> toVOAlpha(List<Album> albums);

    /**
     * 列表转换, Album转VO对象，信息量最少
     *
     * @param albums 专辑列表
     * @return List<AlbumVOBeta>
     * @author rakbow
     */
    @IterableMapping(qualifiedByName = "toVOBeta")
    List<AlbumVOBeta> toVOBeta(List<Album> albums);

    /**
     * 列表转换, Album转VO对象，用于存储到搜索引擎
     *
     * @param albums 专辑列表
     * @return List<AlbumVOGamma>
     * @author rakbow
     */
    @IterableMapping(qualifiedByName = "toVOGamma")
    List<AlbumVOGamma> toVOGamma(List<Album> albums);

    //endregion

    //region get property method

    @Named("getAlbumFormat")
    default List<Attribute> getAlbumFormat(String formats) {
        return AlbumFormat.getAttributes(formats);
    }

    @Named("getPublishFormat")
    default List<Attribute> getPublishFormat(String formats) {
        return EnumUtil.getAttributes(PublishFormat.class, formats);
    }

    //endregion

}
