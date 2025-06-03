package com.mrdotxin.propsmart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.mapper.PropertyMapper;
import com.mrdotxin.propsmart.model.entity.Property;
import com.mrdotxin.propsmart.service.PropertyService;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【Property(房产信息)】的数据库操作Service实现
* @createDate 2025-06-03 18:27:32
*/
@Service
public class PropertyServiceImpl extends ServiceImpl<PropertyMapper, Property>
    implements PropertyService {

}




