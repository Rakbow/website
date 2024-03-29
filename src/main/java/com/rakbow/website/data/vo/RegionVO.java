package com.rakbow.website.data.vo;

import lombok.Data;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-05-16 23:00
 * @Description:
 */
@Data
public class RegionVO {

    private String code;
    private String name;

    public RegionVO(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
