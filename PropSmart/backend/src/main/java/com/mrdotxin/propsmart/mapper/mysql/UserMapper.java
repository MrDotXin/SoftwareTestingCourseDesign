package com.mrdotxin.propsmart.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mrdotxin.propsmart.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select id from user where user.userIdCardNumber in ( select UNIQUE ownerIdentity FROM building WHERE building.id = #{buildingId})")
    List<Long> selectUserByBuildingId(Long buildingId);

    @Select("select id from user where userRole = 'ROLE_ADMIN'")
    List<Long> selectAllAdminId();
}




