package com.mrdotxin.propsmart;

import cn.hutool.core.date.DateUtil;
import com.mrdotxin.propsmart.common.BaseResponse;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.constant.UserConstant;
import com.mrdotxin.propsmart.controller.*;
import com.mrdotxin.propsmart.mapper.mysql.FacilityReservationMapper;
import com.mrdotxin.propsmart.model.dto.bill.BillQueryRequest;
import com.mrdotxin.propsmart.model.dto.complaint.ComplaintSuggestionAddRequest;
import com.mrdotxin.propsmart.model.dto.notice.NoticeAddRequest;
import com.mrdotxin.propsmart.model.dto.user.UserLoginRequest;
import com.mrdotxin.propsmart.model.dto.user.UserRealInfoBindRequest;
import com.mrdotxin.propsmart.model.dto.visitor.VisitorAddRequest;
import com.mrdotxin.propsmart.model.entity.FacilityReservation;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.model.enums.GeoCoordinationEnum;
import com.mrdotxin.propsmart.model.geo.GeoPoint;
import com.mrdotxin.propsmart.model.geo.vo.PathNodeVO;
import com.mrdotxin.propsmart.model.vo.LoginUserVO;
import com.mrdotxin.propsmart.service.FacilityReservationService;
import com.mrdotxin.propsmart.service.RouteService;
import com.mrdotxin.propsmart.service.UserService;
import com.mrdotxin.propsmart.utils.GeoUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgeo.proj4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @Resource
    private FacilityReservationMapper facilityReservationMapper;
    @Test
    void testReservation() {
        Date d1 = DateUtil.parse("2023-01-01 09:00:00", "yyyy-MM-dd HH:mm:ss");
        Date d2 = DateUtil.parse("2023-01-01 17:00:00", "yyyy-MM-dd HH:mm:ss");
        Long maxConcurrentReservationId = facilityReservationMapper.getMaxConcurrentReservationId(1932466768058851329L, d1, d2);
        System.out.println(maxConcurrentReservationId);
    }
// @Autowired
//    private UserController userController;
//    @Autowired
//    private NoticeController noticeController;
//    @Autowired
//    private ComplaintSuggestionController complaintController;
//    @Autowired
//    private VisitorController visitorController;
//    @Autowired
//    private FacilityController facilityController;
//    @Autowired
//    private BillController billController;
//
//    @Resource
//    private UserService userService;
//
//    User getAdminUser() {
//        return userService.getById(1);
//    }
//
//    User getNotOnwerUser() {
//        return userService.getById(3);
//    }
//
//    User getOnwerUser() {
//        return userService.getById(2);
//    }
//
//    // 修复的请求上下文创建方法
//    HttpServletRequest createMockRequest(User user) {
//        // 创建真实的 MockHttpServletRequest 而不是 mock 对象
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        MockHttpSession session = new MockHttpSession();
//
//        // 如果提供了用户，将其设置为登录状态
//        if (user != null) {
//            session.setAttribute(UserConstant.USER_LOGIN_STATE, user);
//        }
//
//        request.setSession(session);
//
//        // 设置请求上下文
//        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
//
//        return request;
//    }
//
//    HttpServletRequest getNotLoginServletRequest() {
//        return createMockRequest(null);
//    }
//
//    HttpServletRequest getAdminLoginServletRequest() {
//        return createMockRequest(getAdminUser());
//    }
//
//    HttpServletRequest getOwnerLoginServletRequest() {
//        return createMockRequest(getOnwerUser());
//    }
//
//    HttpServletRequest getNotOwnerLoginServletRequest() {
//        return createMockRequest(getNotOnwerUser());
//    }
//
//    @AfterEach
//    void tearDown() {
//        // 清理请求上下文
//        RequestContextHolder.resetRequestAttributes();
//    }
//
//    // 用户登录测试
//    @Test
//    void testUserLoginSuccess() {
//        // U-001 成功登录
//        HttpServletRequest request = getNotLoginServletRequest();
//
//        BaseResponse<?> result = userController.userLogin(new UserLoginRequest("admin", "12345678"), request);
//        assertEquals(ErrorCode.SUCCESS.getCode(), result.getCode());
//        assertTrue(result.getData() instanceof LoginUserVO);
//    }
//
//    @Test
//    void testUserLoginPassword() {
//        // U-002 密码错误
//        HttpServletRequest request = getNotLoginServletRequest();
//
//        BaseResponse<?> result = userController.userLogin(new UserLoginRequest("admin", "12345678"), request);
//        // 添加断言
//        assertEquals(ErrorCode.SUCCESS.getCode(), result.getCode());
//    }
//
//    @Test
//    void testUserLoginWrongPassword() {
//        // U-002 密码错误
//        HttpServletRequest request = getNotLoginServletRequest();
//        BaseResponse<?> result = userController.userLogin(new UserLoginRequest("admin", "123abcde"), request);
//        // 添加断言
//        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), result.getCode());
//    }
//
//    @Test
//    void testUserLoginInvalidUser() {
//        // U-003 用户不存在
//        HttpServletRequest request = getNotLoginServletRequest();
//        BaseResponse<?> result = userController.userLogin(new UserLoginRequest("admssssin", "12345678"), request);
//        // 添加断言
//        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), result.getCode());
//    }
//
//    @Test
//    void testPropertyBindingInvalidID() {
//        // U-005 身份证错误
//        HttpServletRequest servletRequest = getNotOwnerLoginServletRequest();
//        UserRealInfoBindRequest request = new UserRealInfoBindRequest(1L, "xxx", "123", "18160955905");
//        BaseResponse<Boolean> result = userController.bindUserRealInfo(request, servletRequest);
//        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), result.getCode());
//        assertEquals("身份证信息错误", result.getMessage());
//    }
//
//    // 房产绑定测试
//    @Test
//    void testPropertyBindingSuccess() {
//        // U-004 成功绑定
//        HttpServletRequest servletRequest = getNotOwnerLoginServletRequest();
//        UserRealInfoBindRequest request = new UserRealInfoBindRequest(3L, "xxx", "130000195709209285", "17890492529");
//        BaseResponse<Boolean> result = userController.bindUserRealInfo(request, servletRequest);
//        // 添加断言
//        assertEquals(ErrorCode.SUCCESS.getCode(), result.getCode());
//    }
//
//    // 公告发布测试
//    @Test
//    void testPublishNoticeAdmin() {
//        // N-001 管理员发布公告
//        HttpServletRequest httpServletRequest = getAdminLoginServletRequest();
//        NoticeAddRequest request = new NoticeAddRequest("小区公告", "小区公告测试",
//                DateUtil.parse("2025-06-10T13:02:28"), DateUtil.parse("2025-06-13T13:02:28"));
//        BaseResponse<Long> result = noticeController.addNotice(request, httpServletRequest);
//        // 添加断言
//        assertEquals(ErrorCode.SUCCESS.getCode(), result.getCode());
//    }
//
//    @Test
//    void testPublishNoticeNonAdmin() {
//        // N-002 非管理员发布公告
//        HttpServletRequest httpServletRequest = getNotOwnerLoginServletRequest();
//        NoticeAddRequest request = new NoticeAddRequest("小区公告", "小区公告测试",
//                DateUtil.parse("2025-06-10T13:02:28"), DateUtil.parse("2025-06-13T13:02:28"));
//        BaseResponse<Long> result = noticeController.addNotice(request, httpServletRequest);
//        // 添加断言
//    }
//
//    // 投诉建议测试
//    @Test
//    void testSubmitComplaintValid() {
//        // C-001 提交有效投诉
//        HttpServletRequest httpServletRequest = getOwnerLoginServletRequest();
//        ComplaintSuggestionAddRequest request = new ComplaintSuggestionAddRequest("测试内容", "complaint");
//        BaseResponse<Long> result = complaintController.submitComplaint(request, httpServletRequest);
//        // 添加断言
//        assertEquals(ErrorCode.SUCCESS.getCode(), result.getCode());
//    }
//
//    @Test
//    void testSubmitInvalidComplaintType() {
//        // C-002 无效投诉类型
//        HttpServletRequest httpServletRequest = getOwnerLoginServletRequest();
//        ComplaintSuggestionAddRequest request = new ComplaintSuggestionAddRequest("测试内容", "xxx");
//        BaseResponse<Long> result = complaintController.submitComplaint(request, httpServletRequest);
//        // 添加断言
//        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), result.getCode());
//    }
//
//    @Test
//    void testSubmitVisitRequest() {
//        // C-002 无效投诉类型
//        HttpServletRequest httpServletRequest = getOwnerLoginServletRequest();
//        VisitorAddRequest visitorAddRequest = new VisitorAddRequest("xxx", "45000020001119485X", "11", new Date(), DateUtil.parse("2025-06-13T13:02:28"));
//        BaseResponse<Long> result = visitorController.submitVisitRequest(visitorAddRequest, httpServletRequest);
//    }
//
//
//    @Test
//    void testMyBillRequest() {
//        // C-002 无效投诉类型
//        HttpServletRequest httpServletRequest = getOwnerLoginServletRequest();
//        BillQueryRequest billQueryRequest = new BillQueryRequest();
//        billQueryRequest.setCurrent(0);
//        billQueryRequest.setPageSize(10);
//        billController.listMyBillByPage(billQueryRequest, httpServletRequest);
//    }
}
