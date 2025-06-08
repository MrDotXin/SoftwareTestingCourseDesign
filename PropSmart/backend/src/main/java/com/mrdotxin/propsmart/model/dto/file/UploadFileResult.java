package com.mrdotxin.propsmart.model.dto.file;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UploadFileResult {

    /**
     * 图片 url
     */
    @ApiModelProperty(value = "图片 url")
    private String url;

    /**
     * 缩略图url
     */
    @ApiModelProperty(value = "缩略图url")
    private String thumbnailUrl;

    /**
     * 图片名称
     */
    @ApiModelProperty(value = "图片名称")
    private String name;

    /**
     * 文件体积
     */
    @ApiModelProperty(value = "文件体积")
    private Long picSize;

    /**
     * 图片宽度
     */
    @ApiModelProperty(value = "图片宽度")
    private Integer picWidth;

    /**
     * 图片高度
     */
    @ApiModelProperty(value = "图片高度")
    private Integer picHeight;

    /**
     * 图片比例
     */
    @ApiModelProperty(value = "图片比例")
    private Double picScale;

    /**
     * 图片格式
     */
    @ApiModelProperty(value = "图片格式")
    private String picFormat;

    /**
     * 原图像的类型
     */
    @ApiModelProperty(value = "原图像的类型")
    private String rawFormat;

}
