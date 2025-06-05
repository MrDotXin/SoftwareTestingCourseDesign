package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mrdotxin.propsmart.model.dto.notice.NoticeQueryRequest;
import com.mrdotxin.propsmart.model.entity.Notice;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* 小区公告服务
*/
public interface NoticeService extends IService<Notice> {

    /**
     * 验证公告参数合法性
     * @param notice 公告对象
     */
    void validateNotice(Notice notice);

    /**
     * 获取查询条件包装器
     * @param queryRequest 查询请求
     * @return QueryWrapper
     */
    QueryWrapper<Notice> getQueryWrapper(NoticeQueryRequest queryRequest);
}
