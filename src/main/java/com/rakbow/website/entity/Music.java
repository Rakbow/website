package com.rakbow.website.entity;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-11-05 22:18
 * @Description:
 */
@Data
public class Music {

    private int id;
    private String name;// 曲名（原文）
    private String nameEn;// 曲名（英语）
    private String artists;// 创作人员名单 [{"pos":"","name":""}]
    private int audioType;// 音频类型 0-未分类 1-歌曲 2-歌曲（无伴奏） 3-原声 4-广播剧
    private int albumId;// 所属专辑id
    private int discSerial;// 所属碟片的顺序
    private String trackSerial;// 在碟片内的顺序
    private String audioUrl;// 音频文件url （mp3）
    private String coverUrl;// 封面图片url
    private String lrcUrl;// 歌词文件url
    private String lrcText;// 歌词文本 markdown格式
    private String audioLength;// 音频长度
    private String description;// 描述
    private String remark;// 备注
    private Timestamp addedTime;// 收录时间
    private Timestamp editedTime;// 编辑时间
    private int _s;// 状态

    public Music(){
        this.id = 0;
        this.name = "";
        this.nameEn = "";
        this.artists = "[]";
        this.audioType = 0;
        this.albumId = 0;
        this.discSerial = 1;
        this.trackSerial = "01";
        this.audioUrl = "";
        this.coverUrl = "";
        this.lrcUrl = "";
        this.lrcText = "";
        this.audioLength = "00:00";
        this.description = "";
        this.remark = "";
        this.addedTime = new Timestamp(System.currentTimeMillis());
        this.editedTime = new Timestamp(System.currentTimeMillis());
        this._s = 1;
    }

}
