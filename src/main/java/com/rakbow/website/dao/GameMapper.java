package com.rakbow.website.dao;

import com.rakbow.website.entity.Game;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-01-06 14:43
 * @Description:
 */
@Mapper
public interface GameMapper {

    //通过id查询Game
    Game getGame(int id);

    //根据过滤条件搜索Game
    List<Game> getGamesByFilter(String name, String hasBonus, List<Integer> franchises, List<Integer> products, int platform,
                                String region, String sortField, int sortOrder, int first, int row);

    //超详细查询条数
    int getGamesRowsByFilter(String name, String hasBonus, List<Integer> franchises, List<Integer> products, int platform,
                             String region);

    //新增Game
    int addGame (Game game);

    //更新Game基础信息
    int updateGame (int id, Game game);

    //删除单个Game
    int deleteGame(int id);

    //更新相关组织
    int updateGameOrganizations(int id, String organizations, Timestamp editedTime);

    //更新开发制作人员
    int updateGameStaffs(int id, String staffs, Timestamp editedTime);

    //更新图片
    int updateGameImages(int id, String images, Timestamp editedTime);

    //更新描述信息
    int updateGameDescription(int id, String description, Timestamp editedTime);

    //更新特典信息
    int updateGameBonus(int id, String bonus, Timestamp editedTime);

    int updateStatusById(int id);

    //获取最新添加Game, limit
    List<Game> getGamesOrderByAddedTime(int limit);

    //获取最新编辑Game, limit
    List<Game> getGamesOrderByEditedTime(int limit);

}
