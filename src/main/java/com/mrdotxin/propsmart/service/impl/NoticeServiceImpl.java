package com.mrdotxin.propsmart.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.constant.CommonConstant;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.exception.ThrowUtils;
import com.mrdotxin.propsmart.model.dto.notice.NoticeQueryRequest;
import com.mrdotxin.propsmart.model.entity.Notice;
import com.mrdotxin.propsmart.service.NoticeService;
import com.mrdotxin.propsmart.mapper.NoticeMapper;
import com.mrdotxin.propsmart.utils.SqlUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author Administrator
* @description 针对表【notices(小区公告)】的数据库操作Service实现
* @createDate 2025-06-03 22:56:00
*/
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice>
        implements NoticeService {

    @Override
    public void validateNotice(Notice notice) {
        ThrowUtils.throwIf(ObjectUtil.isNull(notice), ErrorCode.PARAMS_ERROR);

        String title = notice.getTitle();
        String content = notice.getContent();
        Date publishTime = notice.getPublishTime();
        Date expireTime = notice.getExpireTime();
        Long publisherId = notice.getPublisherId();

        // 基本参数校验
        ThrowUtils.throwIf(StrUtil.isBlank(title), ErrorCode.PARAMS_ERROR, "标题不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(content), ErrorCode.PARAMS_ERROR, "内容不能为空");
        ThrowUtils.throwIf(ObjectUtil.isNull(publisherId), ErrorCode.PARAMS_ERROR, "发布者不能为空");

        // 标题长度限制
        ThrowUtils.throwIf(title.length() > 100, ErrorCode.PARAMS_ERROR, "标题过长");

        // 过期时间校验
        if (ObjectUtil.isNotNull(expireTime)) {
            ThrowUtils.throwIf(expireTime.before(publishTime),
                ErrorCode.PARAMS_ERROR, "过期时间不能早于发布时间");
        }
    }

    @Override
    public QueryWrapper<Notice> getQueryWrapper(NoticeQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Long id = queryRequest.getId();
        String title = queryRequest.getTitle();
        String contentKeyword = queryRequest.getContentKeyword();
        Long publisherId = queryRequest.getPublisherId();
        Date publishStart = queryRequest.getPublishStart();
        Date publishEnd = queryRequest.getPublishEnd();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();

        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(id), "id", id);
        queryWrapper.eq(ObjectUtil.isNotNull(publisherId), "publisherId", publisherId);
        queryWrapper.like(StrUtil.isNotBlank(title), "title", title);
        queryWrapper.like(StrUtil.isNotBlank(contentKeyword), "content", contentKeyword);

        // 发布时间范围查询
        if (ObjectUtil.isNotNull(publishStart)) {
            queryWrapper.ge("publishTime", publishStart);
        }
        if (ObjectUtil.isNotNull(publishEnd)) {
            queryWrapper.le("publishTime", publishEnd);
        }
        // 排序处理
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                          sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                          sortField);

        return queryWrapper;
    }
}




