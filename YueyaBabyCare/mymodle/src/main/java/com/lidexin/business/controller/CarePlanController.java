package com.lidexin.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lidexin.business.dto.CarePlanDTO;
import com.lidexin.business.po.CarePlan;
import com.lidexin.business.service.ICarePlanService;
import com.lidexin.business.vo.AppointmentVO;
import com.lidexin.business.vo.CarePlanVO;
import com.lidexin.business.vo.UserInfoVO;
import com.lidexin.mymodle.common.R;
import com.lidexin.mymodle.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 护理计划表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Api(tags = "护理计划管理")
@RestController
@RequestMapping("/api")
public class CarePlanController {

    @Autowired
    private ICarePlanService carePlanService;

    @Autowired
    private AuthService authService;

    // ==================== 机构端接口 ====================

    /**
     * 新增护理计划（机构）
     */
    @ApiOperation("新增护理计划")
    @PostMapping("/care-plan")
    public R<Long> createCarePlan(
            @Validated @RequestBody CarePlanDTO dto,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 3) {
            return R.fail("只有机构可以创建护理计划");
        }

        try {
            Long planId = carePlanService.createCarePlan(dto, userInfo.getUserId());
            return R.success("创建成功", planId);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 获取护理计划详情
     */
    @ApiOperation("获取护理计划详情")
    @GetMapping("/care-plan/{id}")
    public R<CarePlanVO> getCarePlanById(
            @ApiParam(value = "计划ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        // 检查权限
        if (!carePlanService.hasPermission(id, userInfo.getUserType(), userInfo.getUserId())) {
            return R.fail("无权查看该计划");
        }

        CarePlanVO vo = carePlanService.getCarePlanById(id);
        return vo != null ? R.success(vo) : R.fail("计划不存在");
    }

    /**
     * 修改护理计划（机构）
     */
    @ApiOperation("修改护理计划")
    @PutMapping("/care-plan/{id}")
    public R<Boolean> updateCarePlan(
            @ApiParam(value = "计划ID", required = true) @PathVariable Long id,
            @Validated @RequestBody CarePlanDTO dto,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 3) {
            return R.fail("只有机构可以修改护理计划");
        }

        try {
            boolean result = carePlanService.updateCarePlan(id, dto, userInfo.getUserId());
            return result ? R.success("修改成功", true) : R.fail("修改失败，计划不存在或无权限");
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 删除护理计划（机构）
     */
    @ApiOperation("删除护理计划")
    @DeleteMapping("/care-plan/{id}")
    public R<Boolean> deleteCarePlan(
            @ApiParam(value = "计划ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 3) {
            return R.fail("只有机构可以删除护理计划");
        }

        try {
            boolean result = carePlanService.deleteCarePlan(id, userInfo.getUserId());
            return result ? R.success("删除成功", true) : R.fail("删除失败，计划不存在或无权限");
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 获取护理计划列表（通用）
     */
    @ApiOperation("获取护理计划列表")
    @GetMapping("/care-plan/list")
    public R<List<CarePlanVO>> getCarePlanList(
            @ApiParam(value = "状态筛选") @RequestParam(required = false) Byte status,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        List<CarePlanVO> list;
        if (userInfo.getUserType() == 1) {
            // 家长
            list = carePlanService.getCarePlansByParentId(userInfo.getUserId(), status);
        } else if (userInfo.getUserType() == 2) {
            // 保姆
            list = carePlanService.getCarePlansByNannyId(userInfo.getUserId(), status);
        } else {
            return R.fail("请使用机构专用接口");
        }

        return R.success(list);
    }

    /**
     * 获取机构的护理计划列表（机构分页）
     */
    @ApiOperation("获取机构的护理计划列表")
    @GetMapping("/institution/care-plans")
    public R<IPage<CarePlanVO>> getInstitutionCarePlans(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam(value = "状态筛选") @RequestParam(required = false) Byte status,
            @ApiParam(value = "宝宝ID筛选") @RequestParam(required = false) Long babyId,
            @ApiParam(value = "保姆ID筛选") @RequestParam(required = false) Long nannyId,
            @ApiParam(value = "关键词搜索") @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 3) {
            return R.fail("只有机构可以访问");
        }

        Page<CarePlan> page = new Page<>(pageNum, pageSize);
        IPage<CarePlanVO> result = carePlanService.getCarePlansByInstitution(
                page, userInfo.getUserId(), status, babyId, nannyId, keyword);
        return R.success(result);
    }

    /**
     * 修改护理计划状态（机构）
     */
    @ApiOperation("修改护理计划状态")
    @PutMapping("/care-plan/{id}/status")
    public R<Boolean> updateCarePlanStatus(
            @ApiParam(value = "计划ID", required = true) @PathVariable Long id,
            @ApiParam(value = "新状态", required = true) @RequestParam Byte status,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 3) {
            return R.fail("只有机构可以修改状态");
        }

        try {
            boolean result = carePlanService.updateCarePlanStatus(id, status, userInfo.getUserId());
            return result ? R.success("状态更新成功", true) : R.fail("状态更新失败");
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 为护理计划分配保姆（机构）
     */
    @ApiOperation("为护理计划分配保姆")
    @PostMapping("/care-plan/{id}/assign-nanny")
    public R<Boolean> assignNanny(
            @ApiParam(value = "计划ID", required = true) @PathVariable Long id,
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
            boolean result = carePlanService.assignNanny(id, nannyId, userInfo.getUserId());
            return result ? R.success("分配成功", true) : R.fail("分配失败");
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 获取可创建护理计划的预约列表（机构）
     */
    @ApiOperation("获取可创建护理计划的预约列表")
    @GetMapping("/institution/care-plans/available-appointments")
    public R<List<AppointmentVO>> getAvailableAppointments(HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 3) {
            return R.fail("只有机构可以访问");
        }

        List<AppointmentVO> list = carePlanService.getAvailableAppointmentsForPlan(userInfo.getUserId());
        return R.success(list);
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
