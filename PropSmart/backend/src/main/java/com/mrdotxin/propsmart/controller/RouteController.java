package com.mrdotxin.propsmart.controller;


import com.mrdotxin.propsmart.model.geo.vo.PathNodeVO;
import com.mrdotxin.propsmart.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/route")
public class RouteController {

    @Resource
    private RouteService routeService;

    @GetMapping("/shortest-path")
    public List<PathNodeVO> getShortestPath(
        @RequestParam Long sourceId,
        @RequestParam Long targetId
    ) {
        return routeService.calculateShortestPath(sourceId, targetId);
    }
}
