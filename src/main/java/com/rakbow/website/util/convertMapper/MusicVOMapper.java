package com.rakbow.website.util.convertMapper;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.data.CommonConstant;
import com.rakbow.website.data.emun.music.AudioType;
import com.rakbow.website.data.vo.music.MusicVO;
import com.rakbow.website.data.vo.music.MusicVOAlpha;
import com.rakbow.website.entity.Music;
import com.rakbow.website.util.common.CommonUtil;
import com.rakbow.website.util.file.QiniuImageUtil;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-01-12 10:45
 * @Description: Product VO转换接口
 */
@Mapper(componentModel = "spring")
public interface MusicVOMapper {

    MusicVOMapper INSTANCES = Mappers.getMapper(MusicVOMapper.class);

    /**
     * Music转VO对象，用于详情页面，转换量最大的
     *
     * @param music 音乐
     * @return MusicVO
     * @author rakbow
     */
    default MusicVO music2VO(Music music, String coverUrl) {
        if (music == null) {
            return null;
        }

        MusicVO musicVO = new MusicVO();

        musicVO.setId(music.getId());
        musicVO.setName(music.getName());
        musicVO.setNameEn(music.getNameEn());
        musicVO.setArtists(JSON.parseArray(music.getArtists()));
        musicVO.setDiscSerial(music.getDiscSerial());
        musicVO.setTrackSerial(music.getTrackSerial());

        JSONObject audioTypeObj = new JSONObject();
        audioTypeObj.put("value", music.getAudioType());
        audioTypeObj.put("label", AudioType.getNameByIndex(music.getAudioType()));
        audioTypeObj.put("labelEn", AudioType.getNameEnByIndex(music.getAudioType()));
        musicVO.setAudioType(audioTypeObj);

        musicVO.setFiles(JSON.parseArray(music.getFiles()));
        musicVO.setUploadDisabled(musicVO.getFiles().size() >= 2);

        if (StringUtils.isBlank(coverUrl)) {
            musicVO.setCover(QiniuImageUtil.getThumbUrl(CommonConstant.EMPTY_IMAGE_URL, 80));
        }else {
            musicVO.setCover(QiniuImageUtil.getThumbUrl(coverUrl, 80));
        }
        musicVO.setLrcText(music.getLrcText());
        musicVO.setAudioLength(music.getAudioLength());
        musicVO.setRemark(music.getRemark());

        return musicVO;
    }


    /**
     * Music转VO对象，用于list和index页面，转换量较少
     *
     * @param music 音乐
     * @return MusicVOAlpha
     * @author rakbow
     */
    default MusicVOAlpha music2VOAlpha(Music music, String coverUrl) {
        if (music == null) {
            return null;
        }

        MusicVOAlpha musicVOAlpha = new MusicVOAlpha();

        musicVOAlpha.setId(music.getId());
        musicVOAlpha.setName(music.getName());
        musicVOAlpha.setNameEn(music.getNameEn());
        musicVOAlpha.setDiscSerial(music.getDiscSerial());
        musicVOAlpha.setTrackSerial(music.getTrackSerial());

        if (StringUtils.isBlank(coverUrl)) {
            musicVOAlpha.setCover(QiniuImageUtil.getThumbUrl(CommonConstant.EMPTY_IMAGE_URL, 50));
        }else {
            musicVOAlpha.setCover(QiniuImageUtil.getThumbUrl(coverUrl, 50));
        }

        musicVOAlpha.setAudioLength(music.getAudioLength());

        musicVOAlpha.setAddedTime(CommonUtil.timestampToString(music.getAddedTime()));
        musicVOAlpha.setEditedTime(CommonUtil.timestampToString(music.getEditedTime()));

        return musicVOAlpha;
    }


    /**
     * 列表，Music转VO对象，用于list和index页面，转换量较少
     *
     * @param musics 音乐列表
     * @return List<MusicVOAlpha>
     * @author rakbow
     */
    default List<MusicVOAlpha> music2VOAlpha(List<Music> musics, String coverUrl) {
        List<MusicVOAlpha> musicVOAlphas = new ArrayList<>();
        if (!musics.isEmpty()) {
            musics.forEach(music -> musicVOAlphas.add(music2VOAlpha(music, coverUrl)));
        }
        return musicVOAlphas;
    }

}
