package com.mrdotxin.propsmart.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mrdotxin.propsmart.model.geo.dto.DijkstraResult;
import com.mrdotxin.propsmart.model.geo.entity.RoadSegment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoadNetWorkMapper extends BaseMapper<RoadSegment> {
    @Select("SELECT * FROM pgr_dijkstra('SELECT osm_id AS id, source, target, cost FROM road_network',#{source}, #{target}, directed := false)")
    List<DijkstraResult> findShortestPath(
            @Param("source") long start,
            @Param("target") long end
    );

}
