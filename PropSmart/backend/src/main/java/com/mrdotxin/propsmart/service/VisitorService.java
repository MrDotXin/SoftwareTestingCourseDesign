package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mrdotxin.propsmart.model.dto.visitor.VisitorQueryRequest;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.model.entity.Visitor;

import java.util.Date;

/**
 * 访客管理服务
 */
public interface VisitorService extends IService<Visitor> {

    /**
     * 添加访客申请
     *
     * @param visitor
     * @param userId
     * @return
     */
    long addVisitor(Visitor visitor, Long userId);

    /**
     * 处理访客申请
     *
     * @param visitor
     * @param reviewerId
     * @return
     */
    boolean reviewVisitor(Visitor visitor, Long reviewerId);

    /**
     * 生成电子通行证
     *
     */
    String generatePassCode(String idCardNumber, Date visitTime, Integer duration);

    /**
     * 获取查询条件
     *
     * @param visitorQueryRequest
     * @return
     */
    QueryWrapper<Visitor> getQueryWrapper(VisitorQueryRequest visitorQueryRequest);

    /**
     * 验证身份并提取信息
     */
    String validatePassCode(String token, User loginUser);
} 