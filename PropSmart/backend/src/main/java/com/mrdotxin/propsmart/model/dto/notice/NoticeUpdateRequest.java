package com.mrdotxin.propsmart.model.dto.notice;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Tag(name = "公告更新请求")
public class NoticeUpdateRequest implements Serializable {

    @Schema(description = "公告ID", required = true)
    private Long id;

    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "公告内容")
    private String content;

    @Schema(description = "发布时间")
    private Date publishTime;

    @Schema(description = "过期时间")
    private Date expireTime;

    private static final long serialVersionUID = 1L;
}
