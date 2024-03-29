package com.rakbow.website.data.vo;

import lombok.Data;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-05-16 22:59
 * @Description:
 */
@Data
public class LanguageVO {

    private String code;
    private String name;

    public LanguageVO(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
