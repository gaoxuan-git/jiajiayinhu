package com.lidexin.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lidexin.business.dto.CheckinDTO;
import com.lidexin.business.po.CarePlanCheckin;
import com.lidexin.business.po.Nanny;
import com.lidexin.business.service.ICarePlanCheckinService;
import com.lidexin.business.service.INannyService;
import com.lidexin.business.vo.CarePlanCheckinVO;
import com.lidexin.business.vo.CheckoutResultVO;
import com.lidexin.mymodle.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 预约签到表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-24
 */
@Api(tags = "预约签到管理")
@RestController
@RequestMapping("/api")
public class CarePlanCheckinController {

    @Autowired
    private ICarePlanCheckinService carePlanCheckinService;

    @Autowired
    private INannyService nannyService;

    /**
     * 获取当前登录保姆
     */
    private Nanny getCurrentNanny(String authorization) {
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        return nannyService.getNannyByToken(token);
    }

    // ==================== 保姆端接口 ====================

    /**
     * 签到（保姆）
     */
    @ApiOperation("签到")
    @PostMapping("/appointment/{id}/checkin")
    public R<Long> checkin(
            @ApiParam(value = "预约ID", required = true) @PathVariable Long id,
            @Validated @RequestBody CheckinDTO dto,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Nanny nanny = getCurrentNanny(authorization);
        if (nanny == null) {
            return R.fail("未登录或登录已过期");
        }

        try {
            Long checkinId = carePlanCheckinService.checkin(id, nanny.getId(), dto);
            return R.success("签到成功", checkinId);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 签退（保姆）
     */
    @ApiOperation("签退")
    @PostMapping("/appointment/{id}/checkout")
    public R<CheckoutResultVO> checkout(
            @ApiParam(value = "预约ID", required = true) @PathVariable Long id,
            @Validated @RequestBody CheckinDTO dto,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Nanny nanny = getCurrentNanny(authorization);
        if (nanny == null) {
            return R.fail("未登录或登录已过期");
        }

        try {
            CheckoutResultVO result = carePlanCheckinService.checkout(id, nanny.getId(), dto);
            String msg = result.getMessage() != null 
                ? "签退成功，" + result.getMessage()
                : "签退成功";
            return R.success(msg, result);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 获取预约的签到记录列表
     */
    @ApiOperation("获取预约的签到记录")
    @GetMapping("/appointment/{id}/checkin-records")
    public R<List<CarePlanCheckinVO>> getCheckinRecords(
            @ApiParam(value = "预约ID", required = true) @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Nanny nanny = getCurrentNanny(authorization);
        if (nanny == null) {
            return R.fail("未登录或登录已过期");
        }

        List<CarePlanCheckinVO> list = carePlanCheckinService.getCheckinRecordsByAppointmentId(id);
        return R.success(list);
    }

    /**
     * 获取预约今日签到状态（保姆）
     */
    @ApiOperation("获取预约今日签到状态")
    @GetMapping("/appointment/{id}/today-checkin")
    public R<CarePlanCheckinVO> getTodayCheckin(
            @ApiParam(value = "预约ID", required = true) @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Nanny nanny = getCurrentNanny(authorization);
        if (nanny == null) {
            return R.fail("未登录或登录已过期");
        }

        CarePlanCheckinVO vo = carePlanCheckinService.getTodayLastCheckin(id);
        return R.success(vo);
    }

    /**
     * 获取签到记录详情
     */
    @ApiOperation("获取签到记录详情")
    @GetMapping("/checkin/{id}")
    public R<CarePlanCheckinVO> getCheckinById(
            @ApiParam(value = "签到记录ID", required = true) @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Nanny nanny = getCurrentNanny(authorization);
        if (nanny == null) {
            return R.fail("未登录或登录已过期");
        }

        CarePlanCheckinVO vo = carePlanCheckinService.getCheckinById(id);
        return vo != null ? R.success(vo) : R.fail("签到记录不存在");
    }
}
