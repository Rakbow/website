package com.rakbow.website.dao;

import org.apache.ibatis.annotations.Mapper;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-10-07 8:13
 * @Description:
 */
@Mapper
public interface EntityMapper {

    //修改状态(单个)
    void updateItemStatus(String tableName, int entityId, int status);
    //修改状态(批量)
    void updateItemsStatus(String tableName, List<Integer> ids, int status);
    //通用更新描述
    void updateItemDescription(String tableName, int entityId, String description, Timestamp editedTime);
    //通用更新特典信息
    void updateItemBonus(String tableName, int entityId, String bonus, Timestamp editedTime);
    //通用更新规格信息
    void updateItemSpecs(String tableName, int entityId, String specs, Timestamp editedTime);
    //通用更新关联企业信息
    void updateItemCompanies(String tableName, int entityId, String companies, Timestamp editedTime);
    //通用相关人员信息
    void updateItemPersonnel(String tableName, String fieldName, int entityId, String personnel, Timestamp editedTime);

    String getItemImages(String tableName, int entityId);
    //更新图片
    void updateItemImages(String tableName, int entityId, String images, Timestamp editedTime);
    //获取数据数
    int getItemAmount(String tableName);

}
