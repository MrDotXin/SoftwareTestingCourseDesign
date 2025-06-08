package com.mrdotxin.propsmart;

import com.mrdotxin.propsmart.model.entity.FacilityReservation;
import com.mrdotxin.propsmart.model.enums.GeoCoordinationEnum;
import com.mrdotxin.propsmart.model.geo.GeoPoint;
import com.mrdotxin.propsmart.model.geo.vo.PathNodeVO;
import com.mrdotxin.propsmart.service.FacilityReservationService;
import com.mrdotxin.propsmart.service.RouteService;
import com.mrdotxin.propsmart.utils.GeoUtil;
import org.junit.jupiter.api.Test;
import org.osgeo.proj4j.*;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PropSmartApplicationTests {
    @Resource
    private FacilityReservationService facilityReservationService;
    @Test
    void contextLoads() {
    }


    @Test
    void transferTest() {
        GeoPoint geoPoint = new GeoPoint(121.506912, 30.844986);

        GeoPoint resultPoint = GeoUtil.convertCoordinate(geoPoint, GeoCoordinationEnum.EPSG4326, GeoCoordinationEnum.EPSG3857);

        System.out.printf("(%.6f, %.6f)", resultPoint.getX(), resultPoint.getY());
    }

    @Test
    void transferTest2() {
 // 定义坐标系
        String sourceCrs = "EPSG:4326";  // 源坐标系：地理坐标（经纬度）
        String targetCrs = "EPSG:3857"; // 目标坐标系：投影坐标（Web Mercator）

        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem sourceSys = crsFactory.createFromName(sourceCrs);
        CoordinateReferenceSystem targetSys = crsFactory.createFromName(targetCrs);

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform transform = ctFactory.createTransform(sourceSys, targetSys); // 地理→投影

        // 输入：经纬度（经度, 纬度）
        double lon = 121.511251; // 经度
        double lat = 30.842836; // 纬度

        // 创建源坐标（地理坐标的 X=经度，Y=纬度）
        ProjCoordinate sourceProj = new ProjCoordinate(lon, lat);
        ProjCoordinate targetProj = new ProjCoordinate();

        // 执行转换
        transform.transform(sourceProj, targetProj);

        // 输出：投影坐标（X=东向米值，Y=北向米值）
        System.out.printf("EPSG:3857 坐标: (%.6f, %.6f)%n", targetProj.x, targetProj.y);
    }


    @Resource
    private RouteService routeService;

    @Test
    void testDijkstra() {
        Long start = 101L;
        Long end = 742L;

        List<PathNodeVO> pathNodeVOS = routeService.calculateShortestPath(start, end);
        pathNodeVOS.forEach(pathNodeVO -> {
            System.out.printf("Seq: %d Node: %d AggCost: %.3f Cost: %.3f Edge: %d Point: %.6f %.6f\n", pathNodeVO.getSeq(), pathNodeVO.getNode(), pathNodeVO.getAggCost(), pathNodeVO.getCost(), pathNodeVO.getEdge(), pathNodeVO.getPoint().getX(), pathNodeVO.getPoint().getY());
        });
    }
}
