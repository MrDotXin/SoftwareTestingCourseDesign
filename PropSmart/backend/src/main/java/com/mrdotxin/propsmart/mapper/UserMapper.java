package com.mrdotxin.propsmart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mrdotxin.propsmart.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<Long> selectUserByBuildingId(Long buildingId);

    List<Long> selectAllAdminId();
}




