package com.rakbow.website.controller.entity;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.data.ApiInfo;
import com.rakbow.website.data.ApiResult;
import com.rakbow.website.data.SearchResult;
import com.rakbow.website.data.dto.QueryParams;
import com.rakbow.website.data.emun.common.Entity;
import com.rakbow.website.data.vo.entry.EntryVOAlpha;
import com.rakbow.website.entity.Entry;
import com.rakbow.website.service.EntityService;
import com.rakbow.website.service.EntryService;
import com.rakbow.website.util.common.DateUtil;
import com.rakbow.website.util.convertMapper.entry.EntryConvertMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-05-03 1:49
 * @Description:
 */
@Controller
@RequestMapping("/db/entry")
public class EntryController {

    @Resource
    private EntryService entryService;
    @Resource
    private EntityService entityService;

    private final EntryConvertMapper convertMapper = EntryConvertMapper.INSTANCES;

    //region ------基础增删改------

    //根据搜索条件获取专辑
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/get-entries", method = RequestMethod.POST)
    @ResponseBody
    public String getEntriesByFilter(@RequestBody String json, HttpServletRequest request) {

        JSONObject param = JSON.parseObject(json);

        QueryParams queryParam = JSON.to(QueryParams.class, param.getJSONObject("queryParams"));

        String pageLabel = param.getString("pageLabel");

        List<EntryVOAlpha> entries = new ArrayList<>();

        SearchResult searchResult = entryService.getEntriesByFilter(queryParam);

        if (StringUtils.equals(pageLabel, "list")) {
            entries = convertMapper.toEntryVOAlpha((List<Entry>) searchResult.data);
        }

        JSONObject result = new JSONObject();
        result.put("data", entries);
        result.put("total", searchResult.total);

        return JSON.toJSONString(result);
    }

    //新增专辑
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addEntry(@RequestBody String json) {
        ApiResult res = new ApiResult();
        try {
            JSONObject param = JSON.parseObject(json);
            //检测数据
            String checkMsg = entryService.checkEntryJson(param);
            if(!StringUtils.isBlank(checkMsg)) {
                throw new Exception(checkMsg);
            }

            Entry entry = entityService.json2Entity(param, Entry.class);

            //保存新增专辑
            res.message = entryService.addEntry(entry);

        } catch (Exception ex) {
            res.setErrorMessage(ex.getMessage());
        }
        return JSON.toJSONString(res);
    }

    //删除专辑(单个/多个)
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteEntry(@RequestBody String json) {
        ApiResult res = new ApiResult();
        try {
            List<Integer> ids = JSON.parseArray(json).toJavaList(Integer.class);
            List<Entry> entries = entryService.getEntries(ids);
            for (Entry entry : entries) {
                //从数据库中删除专辑
                entryService.deleteEntry(entry);
            }
            res.message = String.format(ApiInfo.DELETE_DATA_SUCCESS, Entity.ENTRY.getNameZh());
        } catch (Exception ex) {
            res.setErrorMessage(ex.getMessage());
        }
        return JSON.toJSONString(res);
    }

    //更新专辑基础信息
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public String updateEntry(@RequestBody String json) {
        ApiResult res = new ApiResult();
        try {
            JSONObject param = JSON.parseObject(json);
            String checkMsg = entryService.checkEntryJson(param);
            if(!StringUtils.isBlank(checkMsg)) {
                throw new Exception(checkMsg);
            }

            Entry entry = entityService.json2Entity(param, Entry.class);

            //修改编辑时间
            entry.setEditedTime(DateUtil.NOW_TIMESTAMP);
            res.message =  entryService.updateEntry(entry.getId(), entry);

        } catch (Exception ex) {
            res.setErrorMessage(ex.getMessage());
        }
        return JSON.toJSONString(res);
    }

    //endregion

    //region other
    @RequestMapping(value = "/refresh-redis-data", method = RequestMethod.POST)
    @ResponseBody
    public String refreshRedisEntryData(@RequestBody JSONObject json) {
        ApiResult res = new ApiResult();
        try {
            int entryCategory = json.getIntValue("entryCategory");
            entryService.refreshRedisEntries(entryCategory);
            res.message = ApiInfo.REFRESH_REDIS_DATA_SUCCESS;
        } catch (Exception ex) {
            res.setErrorMessage(ex.getMessage());
        }
        return JSON.toJSONString(res);
    }
    //endregion

}
