package com.rakbow.website.entity;

import lombok.Data;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-10-07 8:00
 * @Description:
 */
@Data
public class EntityStatistic {

    private int entityType;//实体类型
    private int entityId;//实体id
    private long visitCount;//访问数
    private long likeCount;//点赞数
    private long collectCount;//收藏数

    private int status;//状态

    public EntityStatistic(int entityType, int entityId, int status) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.visitCount = 0;
        this.likeCount = 0;
        this.collectCount = 0;
        this.status = status;
    }
}
