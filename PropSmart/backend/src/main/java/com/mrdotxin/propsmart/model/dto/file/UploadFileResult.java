package com.mrdotxin.propsmart.model.dto.file;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UploadFileResult {

    /**
     * 图片 url
     */
    @Schema(description = "图片 url")
    private String url;

    /**
     * 缩略图url
     */
    @Schema(description = "缩略图url")
    private String thumbnailUrl;

    /**
     * 图片名称
     */
    @Schema(description = "图片名称")
    private String name;

    /**
     * 文件体积
     */
    @Schema(description = "文件体积")
    private Long picSize;

    /**
     * 图片宽度
     */
    @Schema(description = "图片宽度")
    private Integer picWidth;

    /**
     * 图片高度
     */
    @Schema(description = "图片高度")
    private Integer picHeight;

    /**
     * 图片比例
     */
    @Schema(description = "图片比例")
    private Double picScale;

    /**
     * 图片格式
     */
    @Schema(description = "图片格式")
    private String picFormat;

    /**
     * 原图像的类型
     */
    @Schema(description = "原图像的类型")
    private String rawFormat;

}
