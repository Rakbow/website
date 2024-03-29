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
    Book getBook(int id, boolean status);

    List<Book> getBooks(List<Integer> ids);

    List<Book> getAll();

    //根据过滤条件搜索Book
    List<Book> getBooksByFilter(String title, String isbn10, String isbn13, List<Integer> serials, String region,
                                String lang, int bookType, List<Integer> franchises, List<Integer> products,
                                String hasBonus, boolean status, String sortField, int sortOrder, int first, int row);

    //超详细查询条数
    int getBooksRowsByFilter(String title, String isbn10, String isbn13, List<Integer> serials, String region,
                             String lang, int bookType, List<Integer> franchises, List<Integer> products,
                             String hasBonus, boolean status);

    //新增Book
    int addBook (Book book);

    //更新Book基础信息
    int updateBook (int id, Book book);

    //删除单个Book
    int deleteBook(int id);

    //更新作者信息
    int updateBookAuthors(int id, String authors, Timestamp editedTime);

    //获取最新添加Book, limit
    List<Book> getBooksOrderByAddedTime(int limit);

    //简单搜索
    List<Book> simpleSearch(String keyWorld, int limit, int offset);

    int simpleSearchCount(String keyWorld);

}
