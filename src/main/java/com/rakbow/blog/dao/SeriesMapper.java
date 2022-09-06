package com.rakbow.blog.dao;

import com.rakbow.blog.entity.Series;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Project_name: blog
 * @Author: Rakbow
 * @Create: 2022-08-20 1:06
 * @Description: 系列CRUD
 */
@Mapper
public interface SeriesMapper {

    //新增系列
    int insertSeries(Series series);

    //通过id查找系列
    Series selectSeriesById(int id);

    //修改系列信息
    int updateSeries(int id, Series series);

    //删除系列
    int deleteSeriesById(int id);

    //获取所有系列
    List<Series> getAll();

}