package com.rakbow.website.data;

import com.alibaba.fastjson2.JSONArray;
import com.rakbow.website.util.common.CommonUtil;
import com.rakbow.website.util.common.DateUtil;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-02-25 4:08
 * @Description:
 */
@Data
public class SimpleSearchResult {

    public int total;//查询结果数
    public JSONArray data;//查询结果数据
    public String keyword;//查询关键字
    public int entityType;//查询实体类型
    public String entityName;//查询实体类型
    public int offset;//开始行数
    public int limit;//限制行数
    public String searchTime;//查询时间

    public SimpleSearchResult(String keyword, int entityType, String entityName, int offset, int limit) {
        this.total = 0;
        this.data = new JSONArray();
        this.keyword = keyword;
        this.entityType = entityType;
        this.entityName = entityName;
        this.offset = offset;
        this.limit = limit;
        this.searchTime = DateUtil.timestampToString(DateUtil.NOW_TIMESTAMP);
    }
}