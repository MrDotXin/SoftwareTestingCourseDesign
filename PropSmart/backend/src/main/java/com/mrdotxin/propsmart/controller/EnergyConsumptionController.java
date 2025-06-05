package com.mrdotxin.propsmart.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mrdotxin.propsmart.annotation.AuthCheck;
import com.mrdotxin.propsmart.common.BaseResponse;
import com.mrdotxin.propsmart.common.DeleteRequest;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.common.ResultUtils;
import com.mrdotxin.propsmart.constant.UserConstant;
import com.mrdotxin.propsmart.exception.ThrowUtils;
import com.mrdotxin.propsmart.model.dto.energyConsumption.EnergyConsumptionAddRequest;
import com.mrdotxin.propsmart.model.dto.energyConsumption.EnergyConsumptionQueryRequest;
import com.mrdotxin.propsmart.model.dto.energyConsumption.EnergyConsumptionUpdateRequest;
import com.mrdotxin.propsmart.model.entity.EnergyConsumption;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.model.vo.EnergyConsumptionVO;
import com.mrdotxin.propsmart.model.vo.EnergyMonthlyStatsVO;
import com.mrdotxin.propsmart.service.EnergyConsumptionService;
import com.mrdotxin.propsmart.service.PropertyService;
import com.mrdotxin.propsmart.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/energy")
@Api(tags = "能耗流水管理")
public class EnergyConsumptionController {

    @Resource
    private EnergyConsumptionService energyConsumptionService;

    @Resource
    private UserService userService;

    @Resource
    private PropertyService propertyService;

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/add")
    @ApiOperation(value = "添加能耗记录")
    public BaseResponse<Long> addEnergyConsumption(@RequestBody EnergyConsumptionAddRequest addRequest,
                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(ObjectUtil.isNull(addRequest), ErrorCode.PARAMS_ERROR);

        User loginUser = userService.getLoginUser(request);
        EnergyConsumption energy = new EnergyConsumption();
        BeanUtils.copyProperties(addRequest, energy);

        energyConsumptionService.validateEnergyConsumption(energy);
        boolean result = energyConsumptionService.save(energy);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "添加失败");

        return ResultUtils.success(energy.getId());
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/update")
    @ApiOperation(value = "更新能耗记录")
    public BaseResponse<Boolean> updateEnergyConsumption(@RequestBody EnergyConsumptionUpdateRequest updateRequest,
                                                       HttpServletRequest request) {
        ThrowUtils.throwIf(ObjectUtil.isNull(updateRequest), ErrorCode.PARAMS_ERROR);

        EnergyConsumption energy = new EnergyConsumption();
        BeanUtils.copyProperties(updateRequest, energy);

        energyConsumptionService.validateEnergyConsumption(energy);
        boolean result = energyConsumptionService.updateById(energy);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新失败");

        return ResultUtils.success(true);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/delete")
    @ApiOperation(value = "删除能耗记录")
    public BaseResponse<Boolean> deleteEnergyConsumption(@RequestBody DeleteRequest deleteRequest,
                                                       HttpServletRequest request) {
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);

        boolean result = energyConsumptionService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除失败");

        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    @ApiOperation(value = "获取能耗记录详情")
    public BaseResponse<EnergyConsumptionVO> getEnergyConsumptionById(@RequestParam Long id,
                                                                    HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);

        EnergyConsumption energy = energyConsumptionService.getById(id);
        ThrowUtils.throwIf(ObjectUtil.isNull(energy), ErrorCode.NOT_FOUND_ERROR, "能耗记录不存在");

        EnergyConsumptionVO vo = energyConsumptionService.getEnergyConsumptionVO(energy);
        return ResultUtils.success(vo);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/list/page")
    @ApiOperation(value = "分页查询能耗记录")
    public BaseResponse<Page<EnergyConsumptionVO>> listEnergyConsumptionByPage(@RequestBody EnergyConsumptionQueryRequest queryRequest,
                                                                               HttpServletRequest request) {
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();

        Page<EnergyConsumption> energyPage = energyConsumptionService.page(new Page<>(current, size),
                energyConsumptionService.getQueryWrapper(queryRequest));

        Page<EnergyConsumptionVO> voPage = new Page<>(current, size, energyPage.getTotal());
        List<EnergyConsumptionVO> voList = energyConsumptionService.getEnergyConsumptionVOList(energyPage.getRecords());
        voPage.setRecords(voList);

        return ResultUtils.success(voPage);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/stats/monthly")
    @ApiOperation(value = "按月统计能耗数据")
    public BaseResponse<List<EnergyMonthlyStatsVO>> getMonthlyEnergyStats(@RequestParam(required = false) Long propertyId,
                                                                          @RequestParam String energyType,
                                                                          @RequestParam String yearMonth) {
        List<EnergyMonthlyStatsVO> stats = energyConsumptionService.getMonthlyStats(propertyId, energyType, yearMonth);
        return ResultUtils.success(stats);
    }
}
