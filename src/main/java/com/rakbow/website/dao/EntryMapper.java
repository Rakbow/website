package com.rakbow.website.dao;

import com.rakbow.website.entity.Book;
import com.rakbow.website.entity.Entry;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-04-29 17:38
 * @Description:
 */
@Mapper
public interface EntryMapper {

    Entry getEntry(int id);

    List<Entry> getEntries(List<Integer> ids);

    List<Entry> getAll();

    void addEntry(Entry entry);

    void updateEntry(int id, Entry entry);

    void deleteEntry(int id);

    List<Entry> getEntryByCategory(int category);

    //根据过滤条件搜索Entry
    List<Entry> getEntriesByFilter(String name, String nameZh, String nameEn, int category,
                                   String sortField, int sortOrder, int first, int row);

    //超详细查询条数
    int getEntriesRowsByFilter(String name, String nameZh, String nameEn, int category);

}
