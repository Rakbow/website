package com.rakbow.website.util.convertMapper.entry;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.data.Attribute;
import com.rakbow.website.data.emun.common.Region;
import com.rakbow.website.data.emun.entry.EntryCategory;
import com.rakbow.website.data.vo.entry.EntryVOAlpha;
import com.rakbow.website.entity.Entry;
import com.rakbow.website.entity.common.Company;
import com.rakbow.website.entity.common.Merchandise;
import com.rakbow.website.entity.common.Personnel;
import com.rakbow.website.entity.common.Role;
import com.rakbow.website.util.common.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-05-02 1:12
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface EntryConvertMapper {

    EntryConvertMapper INSTANCES = Mappers.getMapper(EntryConvertMapper.class);

    @Mapping(target = "category", source = "category", qualifiedByName = "getCategory")
    @Mapping(target = "links", source = "links", qualifiedByName = "getStringList")
    @Mapping(target = "alias", source = "alias", qualifiedByName = "getStringList")
    @Mapping(target = "addedTime", source = "addedTime", qualifiedByName = "getVOTime")
    @Mapping(target = "editedTime", source = "editedTime", qualifiedByName = "getVOTime")
    @Named("toEntryVOAlpha")
    EntryVOAlpha toEntryVOAlpha(Entry entry);

    @Mapping(target = "category", source = "category", qualifiedByName = "getCategory")
    @Mapping(target = "links", source = "links", qualifiedByName = "getStringList")
    @Mapping(target = "alias", source = "alias", qualifiedByName = "getStringList")
    @Mapping(target = "addedTime", source = "addedTime", qualifiedByName = "getVOTime")
    @Mapping(target = "editedTime", source = "editedTime", qualifiedByName = "getVOTime")
    @Mapping(target = "region", source = "detail", qualifiedByName = "getRegion")
    @Named("toCompany")
    Company toCompany(Entry entry);

    @Mapping(target = "category", source = "category", qualifiedByName = "getCategory")
    @Mapping(target = "links", source = "links", qualifiedByName = "getStringList")
    @Mapping(target = "alias", source = "alias", qualifiedByName = "getStringList")
    @Mapping(target = "addedTime", source = "addedTime", qualifiedByName = "getVOTime")
    @Mapping(target = "editedTime", source = "editedTime", qualifiedByName = "getVOTime")
    @Named("toPersonnel")
    Personnel toPersonnel(Entry entry);

    @Mapping(target = "category", source = "category", qualifiedByName = "getCategory")
    @Mapping(target = "links", source = "links", qualifiedByName = "getStringList")
    @Mapping(target = "alias", source = "alias", qualifiedByName = "getStringList")
    @Mapping(target = "addedTime", source = "addedTime", qualifiedByName = "getVOTime")
    @Mapping(target = "editedTime", source = "editedTime", qualifiedByName = "getVOTime")
    @Named("toRole")
    Role toRole(Entry entry);

    @Mapping(target = "category", source = "category", qualifiedByName = "getCategory")
    @Mapping(target = "links", source = "links", qualifiedByName = "getStringList")
    @Mapping(target = "alias", source = "alias", qualifiedByName = "getStringList")
    @Mapping(target = "addedTime", source = "addedTime", qualifiedByName = "getVOTime")
    @Mapping(target = "editedTime", source = "editedTime", qualifiedByName = "getVOTime")
    @Named("toMerchandise")
    Merchandise toMerchandise(Entry entry);

    @IterableMapping(qualifiedByName = "toEntryVOAlpha")
    List<EntryVOAlpha> toEntryVOAlpha(List<Entry> entries);

    @IterableMapping(qualifiedByName = "toCompany")
    List<Company> toCompany(List<Entry> entries);

    @IterableMapping(qualifiedByName = "toPersonnel")
    List<Personnel> toPersonnel(List<Entry> entries);

    @IterableMapping(qualifiedByName = "toRole")
    List<Role> toRole(List<Entry> entries);

    @IterableMapping(qualifiedByName = "toMerchandise")
    List<Merchandise> toMerchandise(List<Entry> entries);

    //region get property method

    @Named("getCategory")
    default Attribute getVOCategory(int category) {
        return EntryCategory.getAttribute(category);
    }

    @Named("getStringList")
    default List<String> getVOStringList(String json) {
        return JSON.parseArray(json).toJavaList(String.class);
    }

    @Named("getVOTime")
    default String getVOTime(Timestamp timestamp) {
        return DateUtil.timestampToString(timestamp);
    }

    @Named("getRegion")
    default JSONObject getVORegion(String detail) {
        String code = StringUtils.isBlank(detail)
                ? Region.GLOBAL.getCode()
                : JSON.parseObject(detail).getString("region");
        if(StringUtils.isBlank(code)) {
            code = Region.GLOBAL.getCode();
        }
        return Region.getRegion(code);
    }

    //endregion
}
