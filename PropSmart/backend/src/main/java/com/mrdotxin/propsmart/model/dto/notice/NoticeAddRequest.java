package com.mrdotxin.propsmart.model.dto.notice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "公告添加请求")
public class NoticeAddRequest implements Serializable {

    @ApiModelProperty(value = "公告标题", required = true)
    private String title;

    @ApiModelProperty(value = "公告内容", required = true)
    private String content;

    @ApiModelProperty(value = "发布时间（默认当前时间）")
    private Date publishTime;

    @ApiModelProperty(value = "过期时间（可选）")
    private Date expireTime;

    private static final long serialVersionUID = 1L;
}