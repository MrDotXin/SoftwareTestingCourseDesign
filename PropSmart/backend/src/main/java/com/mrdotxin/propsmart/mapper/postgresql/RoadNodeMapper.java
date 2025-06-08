package com.mrdotxin.propsmart.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mrdotxin.propsmart.model.geo.entity.RoadNode;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface RoadNodeMapper extends BaseMapper<RoadNode> {

    @Select("SELECT id, ST_Distance(node.geom, ST_SetSRID(ST_Point(13526082.4510065243, 3612703.3132876349), 3857)) AS distance FROM road_nodes_v2 as node  ORDER BY distance LIMIT 1")
    RoadNode getClosestNode(@Param("longitude") Double longitude_EPSG3857, @Param("latitude") Double latitude_EPSG3857);

}

