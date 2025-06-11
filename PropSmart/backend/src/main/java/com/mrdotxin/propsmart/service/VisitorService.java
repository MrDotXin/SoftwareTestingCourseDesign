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
     */
    long addVisitor(Visitor visitor, Long userId);

    /**
     * 处理访客申请
     *
     */
    boolean reviewVisitor(Visitor visitor, Long reviewerId);

    /**
     * 生成电子通行证
     *
     */
    String generatePassCode(String idCardNumber, Date visitTime, Date visitEndTime);

    /**
     * 获取查询条件
     *
     */
    QueryWrapper<Visitor> getQueryWrapper(VisitorQueryRequest visitorQueryRequest);

    /**
     * 验证身份并提取信息
     */
    String validatePassCode(String token, User loginUser);

    /**
     *
     * 查看同一个人访问时间是否冲突
     */
    boolean existContradictionVisit(String identity, Date beginTime, Date endTime);
} 