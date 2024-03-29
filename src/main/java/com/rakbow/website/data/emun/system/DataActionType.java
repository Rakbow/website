package com.rakbow.website.data.emun.system;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-10-07 4:02
 * @Description:
 */
@AllArgsConstructor
public enum DataActionType {
    INSERT(0,"新增"),
    UPDATE(1,"更新"),
    REAL_DELETE(2,"真删"),
    FAKE_DELETE(3,"假删"),
    ALL_DELETE(4,"全删");

    @Getter
    private final int id;
    @Getter
    private final String name;
}
