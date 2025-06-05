package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mrdotxin.propsmart.model.dto.building.BuildingQueryRequest;
import com.mrdotxin.propsmart.model.entity.Building;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【building(楼栋信息)】的数据库操作Service
* @createDate 2025-06-03 18:45:18
*/
public interface BuildingService extends IService<Building> {

    Boolean isBuildingExist(String buildingName);

    Building getByBuildingName(String buildingName);

    /**
     *
     * @param buildingQueryRequest
     * @return
     */
    QueryWrapper<Building> getQueryWrapper(BuildingQueryRequest buildingQueryRequest);

    /**
     *
      * @param building
     */
    void validateBuilding(Building building);
    /**
     *
     * @param fieldName
     * @param value
     * @return
     */
    Boolean existsWithField(String fieldName, Object value);

    /**
     *
     * @param fieldName
     * @param value
     * @return
     */
    Building getByFiled(String fieldName, Object value);
}
