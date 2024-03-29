package com.rakbow.website.data.vo.game;

import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.data.Attribute;
import com.rakbow.website.data.vo.RegionVO;
import lombok.Data;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-01-11 16:13
 * @Description: VO 信息量最少
 */
@Data
public class GameVOBeta {

    //基础信息
    private int id;//主键编号
    private String name;//游戏名（原文）
    private String nameZh;//游戏名（中文）
    private String nameEn;//游戏名（英文）
    private String releaseDate;//发售日期

    //复杂字段
    private Attribute releaseType;//发售类型
    private Attribute platform;//平台
    private RegionVO region;//地区

    //图片相关
    private JSONObject cover;

    //审计字段
    private String addedTime;//收录时间
    private String editedTime;//编辑时间

}
