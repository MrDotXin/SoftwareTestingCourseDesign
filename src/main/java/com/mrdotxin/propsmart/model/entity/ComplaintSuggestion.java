package com.mrdotxin.propsmart.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 投诉建议
 * @TableName complaintSuggestion
 */
@TableName(value ="complaintSuggestion")
@Data
public class ComplaintSuggestion implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 提交用户ID
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 类型
     */
    @TableField(value = "type")
    private Object type;

    /**
     * 状态
     */
    @TableField(value = "status")
    private Object status;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     * 处理人ID（管理员）
     */
    @TableField(value = "reviewerId")
    private Long reviewerId;

    /**
     * 回复内容
     */
    @TableField(value = "reviewMessage")
    private String reviewMessage;

    /**
     * 回复时间
     */
    @TableField(value = "reviewTime")
    private Date reviewTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}