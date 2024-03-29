package com.rakbow.website.data.vo;

import lombok.Data;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-02-09 10:42
 * @Description: 图片
 */
@Data
public class ImageVO {

    private String url;//图片url
    private String thumbUrl;//缩略图url(70px)
    private String thumbUrl50;//缩略图url(50px)
    private String nameEn;//图片名(英)
    private String nameZh;//图片名(中)
    private int type;//图片类型
    private String description;//图片描述
    private String uploadTime;//图片上传时间

}
