package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mrdotxin.propsmart.model.dto.visitor.VisitorQueryRequest;
import com.mrdotxin.propsmart.model.entity.Visitor;

/**
 * 访客管理服务
 */
public interface VisitorService extends IService<Visitor> {

    /**
     * 添加访客申请
     * @param visitor
     * @param userId
     * @return
     */
    long addVisitor(Visitor visitor, Long userId);
    
    /**
     * 处理访客申请
     * @param visitor
     * @param reviewerId
     * @return
     */
    boolean reviewVisitor(Visitor visitor, Long reviewerId);
    
    /**
     * 生成电子通行证
     * @param visitorId
     * @return
     */
    String generatePassCode(Long visitorId);
    
    /**
     * 获取查询条件
     * @param visitorQueryRequest
     * @return
     */
    QueryWrapper<Visitor> getQueryWrapper(VisitorQueryRequest visitorQueryRequest);
} 