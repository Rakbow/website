package com.rakbow.website.dao;

import com.rakbow.website.entity.Book;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-12-28 22:19
 * @Description:
 */
@Mapper
public interface BookMapper {

    //通过id查询Book
    Book getBookById(int id);

    //获取所有Book
    List<Book> getAllBook();

    //根据过滤条件搜索Book
    List<Book> getBooksByFilterList(String title, String isbn10, String isbn13, String publisher, String area,
                                    String publishLanguage, int bookType, int series, List<Integer> products,
                                    String hasBonus, String sortField, int sortOrder, int first, int row);

    //超详细查询条数
    int getBooksRowsByFilterList(String title, String isbn10, String isbn13, String publisher, String area,
                                 String publishLanguage, int bookType, int series, List<Integer> products,
                                 String hasBonus);

    //新增Book
    int addBook (Book book);

    //更新Book基础信息
    int updateBook (int id, Book book);

    //删除单个Book
    int deleteBookById(int id);

    //更新图片
    int updateBookImages(int id, String images, Timestamp editedTime);

    //更新作者信息
    int updateBookAuthors(int id, String authors, Timestamp editedTime);

    //更新规格信息
    int updateBookSpec(int id, String spec, Timestamp editedTime);

    //更新描述信息
    int updateBookDescription(int id, String description, Timestamp editedTime);

    //更新特典信息
    int updateBookBonus(int id, String bonus, Timestamp editedTime);

    int updateStatusById(int id);

}