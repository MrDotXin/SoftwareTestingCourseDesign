package com.mrdotxin.propsmart.model.dto.notice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "公告更新请求")
public class NoticeUpdateRequest implements Serializable {

    @ApiModelProperty(value = "公告ID", required = true)
    private Long id;

    @ApiModelProperty(value = "公告标题")
    private String title;

    @ApiModelProperty(value = "公告内容")
    private String content;

    @ApiModelProperty(value = "发布时间")
    private Date publishTime;

    @ApiModelProperty(value = "过期时间")
    private Date expireTime;

    private static final long serialVersionUID = 1L;
}
