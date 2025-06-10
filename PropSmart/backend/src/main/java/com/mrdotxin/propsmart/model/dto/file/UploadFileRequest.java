package com.mrdotxin.propsmart.model.dto.file;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 */
@Data
public class UploadFileRequest implements Serializable {

    /**
     * 业务
     */
    @Schema(description = "业务")
    private String biz;

    private static final long serialVersionUID = 1L;
}