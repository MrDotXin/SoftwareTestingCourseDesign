package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mrdotxin.propsmart.model.geo.entity.RoadSegment;
import com.mrdotxin.propsmart.model.geo.vo.PathNodeVO;

import java.util.List;

public interface RouteService extends IService<RoadSegment> {

    /**
     * 根据传入的节点来返回规划的路线结点集合
     * @param startId 开始节点
     * @param endId 结束节点
     * @return 途径的所有节点列表
     */
    List<PathNodeVO> calculateShortestPath(Long startId, Long endId);

}
