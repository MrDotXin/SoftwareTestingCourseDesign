package com.mrdotxin.propsmart.model.dto.notice;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Tag(name = "公告查询请求")
public class NoticeQueryRequest implements Serializable {

    @Schema(description = "公告ID")
    private Long id;

    @Schema(description = "公告标题（模糊查询）")
    private String title;

    @Schema(description = "内容关键词（模糊查询）")
    private String contentKeyword;

    @Schema(description = "发布者ID")
    private Long publisherId;

    @Schema(description = "发布时间开始")
    private Date publishStart;

    @Schema(description = "发布时间结束")
    private Date publishEnd;

    @Schema(description = "当前页号", required = true)
    private long current = 1;

    @Schema(description = "页面大小", required = true)
    private long pageSize = 10;

    @Schema(description = "排序字段")
    private String sortField;

    @Schema(description = "排序顺序（asc/desc）")
    private String sortOrder;

    private static final long serialVersionUID = 1L;
}
