package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mrdotxin.propsmart.model.dto.notice.NoticeQueryRequest;
import com.mrdotxin.propsmart.model.entity.Notice;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【notices(小区公告)】的数据库操作Service
* @createDate 2025-06-03 22:56:00
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
