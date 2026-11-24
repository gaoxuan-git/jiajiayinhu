package com.lidexin.business.controller;

import com.lidexin.business.service.IAdminDashboardService;
import com.lidexin.business.vo.UserInfoVO;
import com.lidexin.mymodle.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 管理员数据分析控制器
 *
 * @author 张三
 * @since 2025-12-25
 */
@Api(tags = "管理员数据分析")
@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    @Autowired
    private IAdminDashboardService adminDashboardService;

    @ApiOperation("获取管理后台首页数据")
    @GetMapping("/dashboard")
    public R<IAdminDashboardService.DashboardVO> getDashboard(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.fail("无权限访问");
        }
        IAdminDashboardService.DashboardVO data = adminDashboardService.getDashboardData();
        return R.success(data);
    }

    @ApiOperation("获取用户统计")
    @GetMapping("/user-stats")
    public R<IAdminDashboardService.UserStatsVO> getUserStats(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.fail("无权限访问");
        }
        IAdminDashboardService.UserStatsVO stats = adminDashboardService.getUserStats();
        return R.success(stats);
    }

    @ApiOperation("获取机构统计")
    @GetMapping("/institution-stats")
    public R<IAdminDashboardService.InstitutionStatsVO> getInstitutionStats(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.fail("无权限访问");
        }
        IAdminDashboardService.InstitutionStatsVO stats = adminDashboardService.getInstitutionStats();
        return R.success(stats);
    }

    @ApiOperation("获取订单统计")
    @GetMapping("/order-stats")
    public R<IAdminDashboardService.OrderStatsVO> getOrderStats(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.fail("无权限访问");
        }
        IAdminDashboardService.OrderStatsVO stats = adminDashboardService.getOrderStats();
        return R.success(stats);
    }

    @ApiOperation("获取收入统计")
    @GetMapping("/revenue-stats")
    public R<IAdminDashboardService.RevenueStatsVO> getRevenueStats(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return R.fail("无权限访问");
        }
        IAdminDashboardService.RevenueStatsVO stats = adminDashboardService.getRevenueStats();
        return R.success(stats);
    }

    @ApiOperation("生成报表")
    @GetMapping("/reports")
    public R<IAdminDashboardService.ReportVO> getReports(
            HttpServletRequest request,
            @ApiParam(value = "报表类型（order-订单报表，revenue-收入报表，user-用户报表）", required = true) @RequestParam String type,
            @ApiParam(value = "开始日期（yyyy-MM-dd）", required = true) @RequestParam String startDate,
            @ApiParam(value = "结束日期（yyyy-MM-dd）", required = true) @RequestParam String endDate) {
        if (!isAdmin(request)) {
            return R.fail("无权限访问");
        }
        try {
            IAdminDashboardService.ReportVO report = adminDashboardService.getReports(type, startDate, endDate);
            return R.success(report);
        } catch (Exception e) {
            return R.fail("生成报表失败：" + e.getMessage());
        }
    }

    // ==================== 私有方法 ====================

    @SuppressWarnings("unchecked")
    private boolean isAdmin(HttpServletRequest request) {
        Object userInfo = request.getAttribute("userInfo");
        if (userInfo == null) {
            return false;
        }
        try {
            if (userInfo instanceof UserInfoVO) {
                UserInfoVO vo = (UserInfoVO) userInfo;
                // userType: 4-管理员
                return vo.getUserType() != null && vo.getUserType() == 4;
            }
            if (userInfo instanceof Map) {
                Map<String, Object> userMap = (Map<String, Object>) userInfo;
                Object userType = userMap.get("userType");
                // userType: 4-管理员
                return userType != null && Integer.valueOf(userType.toString()) == 4;
            }
        } catch (Exception e) {
            // 解析失败
        }
        return false;
    }
}
