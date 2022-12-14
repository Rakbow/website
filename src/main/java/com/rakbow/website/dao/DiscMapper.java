package com.rakbow.website.dao;

import com.rakbow.website.entity.Disc;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-11-27 19:43
 * @Description:
 */
@Mapper
public interface DiscMapper {

    //通过id查询disc
    Disc getDisc(int id);

    //根据过滤条件搜索disc
    List<Disc> getDiscsByFilter(String catalogNo, String name, List<Integer> franchises, List<Integer> products,
                                List<Integer> mediaFormat, String isLimited, String hasBonus, String sortField,
                                int sortOrder, int first, int row);

    //超详细查询条数
    int getDiscsRowsByFilter(String catalogNo, String name, List<Integer> franchises, List<Integer> products,
                             List<Integer> mediaFormat, String isLimited, String hasBonus);

    //新增disc
    int addDisc (Disc disc);

    //更新disc基础信息
    int updateDisc (int id, Disc disc);

    //删除单个disc
    int deleteDisc(int id);

    //更新图片
    int updateDiscImages(int id, String images, Timestamp editedTime);

    //更新规格信息
    int updateDiscSpec(int id, String spec, Timestamp editedTime);

    //更新描述信息
    int updateDiscDescription(int id, String description, Timestamp editedTime);

    //更新特典信息
    int updateDiscBonus(int id, String bonus, Timestamp editedTime);

    int updateStatusById(int id);

    //获取最新添加Disc, limit
    List<Disc> getDiscsOrderByAddedTime(int limit);

    //获取最新编辑Disc, limit
    List<Disc> getDiscsOrderByEditedTime(int limit);

}
