package com.mrdotxin.propsmart.model.dto.notice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "公告查询请求")
public class NoticeQueryRequest implements Serializable {

    @ApiModelProperty(value = "公告ID")
    private Long id;

    @ApiModelProperty(value = "公告标题（模糊查询）")
    private String title;

    @ApiModelProperty(value = "内容关键词（模糊查询）")
    private String contentKeyword;

    @ApiModelProperty(value = "发布者ID")
    private Long publisherId;

    @ApiModelProperty(value = "发布时间开始")
    private Date publishStart;

    @ApiModelProperty(value = "发布时间结束")
    private Date publishEnd;

    @ApiModelProperty(value = "当前页号", required = true)
    private long current = 1;

    @ApiModelProperty(value = "页面大小", required = true)
    private long pageSize = 10;

    @ApiModelProperty(value = "排序字段")
    private String sortField;

    @ApiModelProperty(value = "排序顺序（asc/desc）")
    private String sortOrder;

    private static final long serialVersionUID = 1L;
}
