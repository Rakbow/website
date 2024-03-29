package com.rakbow.website.util.common;

import com.rakbow.website.data.EntityInfo;
import com.rakbow.website.data.RedisKey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-04-03 2:11
 * @Description:
 */
@Component
public class RelatedInfoUtil {

    @Resource
    private RedisUtil redisUtil;

    /**
     * 新增关联信息存入redis缓存
     * @param entityType,entityId 实体类型，实体id
     * @Author Rakbow
     */
    public void addRelatedInfo(int entityType, int entityId, List<Integer> relatedItems) {
        String key = getRelatedKey(entityType, entityId);
        redisUtil.set(key, relatedItems);
    }

    /**
     * 获取关联信息
     * @param entityType,entityId 实体类型，实体id
     * @Author Rakbow
     */
    public List<Integer> getRelatedInfo(int entityType, int entityId) {
        String key = getRelatedKey(entityType, entityId);
        if(!redisUtil.hasKey(key)) {
            return null;
        }
        return (List<Integer>) redisUtil.get(key);
    }

    /**
     * 获取关联信息
     * @param key key
     * @Author Rakbow
     */
    public List<Integer> getRelatedInfo(String key) {
        if(!redisUtil.hasKey(key)) {
            return null;
        }
        return (List<Integer>) redisUtil.get(key);
    }

    /**
     * 删除关联信息
     * @param entityType,entityId 实体类型，实体id
     * @Author Rakbow
     */
    public void deleteRelatedInfo(int entityType, int entityId) {
        String key = getRelatedKey(entityType, entityId);
        redisUtil.delete(key);
    }

    /**
     * 通过key获取实体信息
     * @param key key
     * @return entityInfo
     * @Author Rakbow
     */
    public EntityInfo getEntityInfo(String key) {
        EntityInfo info = new EntityInfo();
        if(StringUtils.isBlank(key)) {
            return info;
        }
        String[] tmp = key.split(":");
        info.setEntityType(Integer.parseInt(tmp[1]));
        info.setEntityId(Integer.parseInt(tmp[2]));
        return info;
    }

    /**
     * 获取key,用于记录
     * */
    public String getRelatedKey(int entityType, int entityId) {
        return RedisKey.ENTITY_RELATED_ITEM + RedisKey.SPLIT + entityType + RedisKey.SPLIT + entityId;
    }

}