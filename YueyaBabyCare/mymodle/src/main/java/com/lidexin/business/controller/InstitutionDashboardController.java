package com.lidexin.business.controller;

import com.lidexin.business.po.Institution;
import com.lidexin.business.service.IInstitutionDashboardService;
import com.lidexin.business.service.IInstitutionService;
import com.lidexin.mymodle.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 机构数据分析控制器
 *
 * @author 张三
 * @since 2025-12-25
 */
@Api(tags = "机构数据分析")
@RestController
@RequestMapping("/api/institution/dashboard")
public class InstitutionDashboardController {

    @Autowired
    private IInstitutionDashboardService dashboardService;

    @Autowired
    private IInstitutionService institutionService;

    @ApiOperation("获取机构仪表板数据")
    @GetMapping("")
    public R<IInstitutionDashboardService.DashboardVO> getDashboard(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Institution institution = getInstitution(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        IInstitutionDashboardService.DashboardVO data = dashboardService.getDashboardData(institution.getId());
        return R.success(data);
    }

    @ApiOperation("获取保姆绩效排名")
    @GetMapping("/nanny-ranking")
    public R<List<IInstitutionDashboardService.NannyRankVO>> getNannyRanking(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @ApiParam(value = "排名类型（order-订单数，revenue-收入，rating-评分）") @RequestParam(defaultValue = "order") String type) {
        Institution institution = getInstitution(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        List<IInstitutionDashboardService.NannyRankVO> ranking = dashboardService.getNannyRanking(institution.getId(), type);
        return R.success(ranking);
    }

    @ApiOperation("获取服务数据统计")
    @GetMapping("/service-stats")
    public R<IInstitutionDashboardService.ServiceStatsVO> getServiceStats(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Institution institution = getInstitution(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        IInstitutionDashboardService.ServiceStatsVO stats = dashboardService.getServiceStats(institution.getId());
        return R.success(stats);
    }

    @ApiOperation("获取评价分析")
    @GetMapping("/evaluation-stats")
    public R<IInstitutionDashboardService.EvaluationStatsVO> getEvaluationStats(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Institution institution = getInstitution(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        IInstitutionDashboardService.EvaluationStatsVO stats = dashboardService.getEvaluationStats(institution.getId());
        return R.success(stats);
    }

    @ApiOperation("获取财务数据")
    @GetMapping("/finance-stats")
    public R<IInstitutionDashboardService.FinanceStatsVO> getFinanceStats(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Institution institution = getInstitution(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        IInstitutionDashboardService.FinanceStatsVO stats = dashboardService.getFinanceStats(institution.getId());
        return R.success(stats);
    }

    @ApiOperation("导出报表数据")
    @GetMapping("/export-report")
    public R<IInstitutionDashboardService.ReportDataVO> exportReport(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @ApiParam(value = "报表类型（order-订单报表，revenue-收入报表，service-服务报表）", required = true) @RequestParam String type,
            @ApiParam(value = "开始日期（yyyy-MM-dd）", required = true) @RequestParam String startDate,
            @ApiParam(value = "结束日期（yyyy-MM-dd）", required = true) @RequestParam String endDate) {
        Institution institution = getInstitution(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        try {
            IInstitutionDashboardService.ReportDataVO report = dashboardService.exportReport(institution.getId(), type, startDate, endDate);
            return R.success(report);
        } catch (Exception e) {
            return R.fail("导出报表失败：" + e.getMessage());
        }
    }

    // ==================== 私有方法 ====================

    private Institution getInstitution(String authorization) {
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        return institutionService.getInstitutionByToken(token);
    }
}
