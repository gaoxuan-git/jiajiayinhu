package com.lidexin.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lidexin.business.dto.AppointmentDTO;
import com.lidexin.business.po.Appointment;
import com.lidexin.business.service.IAppointmentService;
import com.lidexin.business.vo.AppointmentVO;
import com.lidexin.business.vo.UserInfoVO;
import com.lidexin.mymodle.common.R;
import com.lidexin.mymodle.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 预约表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Api(tags = "预约管理")
@RestController
@RequestMapping("/api")
public class AppointmentController {

    @Autowired
    private IAppointmentService appointmentService;

    @Autowired
    private AuthService authService;

    // ==================== 家长端接口 ====================

    /**
     * 新增预约（家长）
     */
    @ApiOperation("新增预约")
    @PostMapping("/appointment")
    public R<Long> createAppointment(
            @Validated @RequestBody AppointmentDTO dto,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 1) {
            return R.fail("只有家长可以创建预约");
        }

        try {
            Long appointmentId = appointmentService.createAppointment(dto, userInfo.getUserId());
            return R.success("预约创建成功", appointmentId);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 获取预约详情
     */
    @ApiOperation("获取预约详情")
    @GetMapping("/appointment/{id}")
    public R<AppointmentVO> getAppointmentById(
            @ApiParam(value = "预约ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        // 检查权限
        if (!appointmentService.hasPermission(id, userInfo.getUserType(), userInfo.getUserId())) {
            return R.fail("无权查看该预约");
        }

        AppointmentVO vo = appointmentService.getAppointmentById(id);
        return vo != null ? R.success(vo) : R.fail("预约不存在");
    }

    /**
     * 修改预约（家长）
     */
    @ApiOperation("修改预约")
    @PutMapping("/appointment/{id}")
    public R<Boolean> updateAppointment(
            @ApiParam(value = "预约ID", required = true) @PathVariable Long id,
            @Validated @RequestBody AppointmentDTO dto,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 1) {
            return R.fail("只有家长可以修改预约");
        }

        try {
            boolean result = appointmentService.updateAppointment(id, dto, userInfo.getUserId());
            return result ? R.success("修改成功", true) : R.fail("修改失败，预约不存在或无权限");
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 取消预约（家长）
     */
    @ApiOperation("取消预约")
    @DeleteMapping("/appointment/{id}")
    public R<Boolean> cancelAppointment(
            @ApiParam(value = "预约ID", required = true) @PathVariable Long id,
            @ApiParam(value = "取消原因") @RequestParam(required = false) String cancelReason,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 1) {
            return R.fail("只有家长可以取消预约");
        }

        try {
            boolean result = appointmentService.cancelAppointment(id, userInfo.getUserId(), cancelReason);
            return result ? R.success("取消成功", true) : R.fail("取消失败，预约不存在或无权限");
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 获取预约列表（通用）
     */
    @ApiOperation("获取预约列表")
    @GetMapping("/appointment/list")
    public R<List<AppointmentVO>> getAppointmentList(
            @ApiParam(value = "状态筛选（0-待确认，1-已确认，2-进行中，3-已完成，4-已取消）") @RequestParam(required = false) Byte status,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        List<AppointmentVO> list;
        if (userInfo.getUserType() == 1) {
            // 家长
            list = appointmentService.getAppointmentsByParentId(userInfo.getUserId(), status);
        } else if (userInfo.getUserType() == 2) {
            // 保姆
            list = appointmentService.getAppointmentsByNannyId(userInfo.getUserId(), status);
        } else {
            return R.fail("无权访问");
        }

        return R.success(list);
    }

    /**
     * 获取我的预约（家长/保姆）
     */
    @ApiOperation("获取我的预约")
    @GetMapping("/appointment/my-appointments")
    public R<List<AppointmentVO>> getMyAppointments(
            @ApiParam(value = "状态筛选") @RequestParam(required = false) Byte status,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        List<AppointmentVO> list;
        if (userInfo.getUserType() == 1) {
            list = appointmentService.getAppointmentsByParentId(userInfo.getUserId(), status);
        } else if (userInfo.getUserType() == 2) {
            list = appointmentService.getAppointmentsByNannyId(userInfo.getUserId(), status);
        } else {
            return R.fail("无权访问");
        }

        return R.success(list);
    }

    /**
     * 获取可预约时间段
     */
    @ApiOperation("获取可预约时间段")
    @GetMapping("/appointment/available-slots")
    public R<List<String>> getAvailableSlots(
            @ApiParam(value = "机构ID", required = true) @RequestParam Long institutionId,
            @ApiParam(value = "保姆ID（可选）") @RequestParam(required = false) Long nannyId,
            @ApiParam(value = "日期", required = true) @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<String> slots = appointmentService.getAvailableSlots(institutionId, nannyId, date);
        return R.success(slots);
    }

    // ==================== 机构端接口 ====================

    /**
     * 获取机构的预约列表（机构）
     */
    @ApiOperation("获取机构的预约列表")
    @GetMapping("/institution/appointments")
    public R<IPage<AppointmentVO>> getInstitutionAppointments(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam(value = "状态筛选") @RequestParam(required = false) Byte status,
            @ApiParam(value = "服务类型筛选") @RequestParam(required = false) Byte serviceType,
            @ApiParam(value = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam(value = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @ApiParam(value = "关键词搜索") @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 3) {
            return R.fail("只有机构可以访问");
        }

        Page<Appointment> page = new Page<>(pageNum, pageSize);
        IPage<AppointmentVO> result = appointmentService.getAppointmentsByInstitution(
                page, userInfo.getUserId(), status, serviceType, startDate, endDate, keyword);
        return R.success(result);
    }

    /**
     * 修改预约状态（机构）
     */
    @ApiOperation("修改预约状态")
    @PutMapping("/appointment/{id}/status")
    public R<Boolean> updateAppointmentStatus(
            @ApiParam(value = "预约ID", required = true) @PathVariable Long id,
            @ApiParam(value = "新状态", required = true) @RequestParam Byte status,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 3) {
            return R.fail("只有机构可以修改预约状态");
        }

        try {
            boolean result = appointmentService.updateAppointmentStatus(id, status, userInfo.getUserId());
            return result ? R.success("状态更新成功", true) : R.fail("状态更新失败");
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 为预约分配保姆（机构）
     */
    @ApiOperation("为预约分配保姆")
    @PostMapping("/appointment/{id}/assign-nanny")
    public R<Boolean> assignNanny(
            @ApiParam(value = "预约ID", required = true) @PathVariable Long id,
            @ApiParam(value = "保姆ID", required = true) @RequestParam Long nannyId,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 3) {
            return R.fail("只有机构可以分配保姆");
        }

        try {
            boolean result = appointmentService.assignNanny(id, nannyId, userInfo.getUserId());
            return result ? R.success("分配成功", true) : R.fail("分配失败");
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 获取预约统计数据（机构）
     */
    @ApiOperation("获取预约统计数据")
    @GetMapping("/institution/appointments/stats")
    public R<IAppointmentService.AppointmentStatsVO> getAppointmentStats(
            @ApiParam(value = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam(value = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 3) {
            return R.fail("只有机构可以访问");
        }

        IAppointmentService.AppointmentStatsVO stats = appointmentService.getAppointmentStats(
                userInfo.getUserId(), startDate, endDate);
        return R.success(stats);
    }

    // ==================== 私有方法 ====================

    /**
     * 从请求中获取当前登录用户
     */
    private UserInfoVO getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || token.isEmpty()) {
            return null;
        }
        return authService.getUserByToken(token, UserInfoVO.class);
    }
}
