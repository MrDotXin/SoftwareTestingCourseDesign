package com.mrdotxin.propsmart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.mapper.postgresql.RoadNetWorkMapper;
import com.mrdotxin.propsmart.mapper.postgresql.RoadNodeMapper;
import com.mrdotxin.propsmart.model.geo.dto.DijkstraResult;
import com.mrdotxin.propsmart.model.geo.entity.RoadNode;
import com.mrdotxin.propsmart.model.geo.entity.RoadSegment;
import com.mrdotxin.propsmart.model.geo.vo.PathNodeVO;
import com.mrdotxin.propsmart.service.RouteService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl extends ServiceImpl<RoadNetWorkMapper, RoadSegment> implements RouteService {
    @Resource
    private RoadNodeMapper roadNodeMapper;

    @Override
    public List<PathNodeVO> calculateShortestPath(Long startId, Long endId) {
        List<DijkstraResult> dijkstraResults = this.baseMapper.findShortestPath(startId, endId);

        return dijkstraResults.stream().map(
                dijkstraNode -> {
                    PathNodeVO pathNodeVO = new PathNodeVO();
                    BeanUtils.copyProperties(dijkstraNode, pathNodeVO);

                    RoadNode roadNode = roadNodeMapper.selectById(dijkstraNode.getNode());
                    pathNodeVO.setPoint(roadNode.getGeom());
                    return pathNodeVO;
                }).collect(Collectors.toList());
    }
}
