package com.rakbow.website.entity.common;

import com.rakbow.website.data.Attribute;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-05-02 7:48
 * @Description:
 */
@Data
public class MetaEntry {

    private int id;
    private Attribute category;//分类
    private String name;//原名
    private String nameZh;//中文名
    private String nameEn;//英文名
    private List<String> alias;//别名 json数组
    private String description;//描述
    private String remark;//备注
    private String addedTime;//数据新增时间
    private String editedTime;//数据更新时间

}
