package com.lidexin.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lidexin.business.po.FinanceRecord;
import com.lidexin.business.po.Institution;
import com.lidexin.business.service.IFinanceRecordService;
import com.lidexin.business.service.IFinanceRecordService.*;
import com.lidexin.business.service.IInstitutionService;
import com.lidexin.mymodle.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 财务记录控制器
 *
 * @author 张三
 * @since 2025-12-21
 */
@Api(tags = "财务管理")
@RestController
@RequestMapping("/api/finance-record")
public class FinanceRecordController {

    @Autowired
    private IFinanceRecordService financeRecordService;

    @Autowired
    private IInstitutionService institutionService;

    @ApiOperation("新增财务记录")
    @PostMapping("")
    public R<Void> addFinanceRecord(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody FinanceRecord record) {
        Institution institution = getInstitution(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        record.setInstitutionId(institution.getId());
        financeRecordService.addFinanceRecord(record);
        return R.success(null);
    }

    @ApiOperation("获取财务记录列表")
    @GetMapping("/list")
    public R<Page<FinanceRecordVO>> getFinanceRecordList(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @ApiParam(value = "记录类型（1-收入，2-支出）") @RequestParam(required = false) Byte recordType,
            @ApiParam(value = "开始日期（yyyy-MM-dd）") @RequestParam(required = false) String startDate,
            @ApiParam(value = "结束日期（yyyy-MM-dd）") @RequestParam(required = false) String endDate,
            @ApiParam(value = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Institution institution = getInstitution(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        Page<FinanceRecordVO> page = financeRecordService.getFinanceRecordList(
                institution.getId(), recordType, startDate, endDate, pageNum, pageSize);
        return R.success(page);
    }

    @ApiOperation("获取财务统计")
    @GetMapping("/stats")
    public R<FinanceStatsVO> getFinanceStats(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @ApiParam(value = "开始日期（yyyy-MM-dd）") @RequestParam(required = false) String startDate,
            @ApiParam(value = "结束日期（yyyy-MM-dd）") @RequestParam(required = false) String endDate) {
        Institution institution = getInstitution(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        FinanceStatsVO stats = financeRecordService.getFinanceStats(institution.getId(), startDate, endDate);
        return R.success(stats);
    }

    @ApiOperation("获取月度汇总")
    @GetMapping("/monthly-summary")
    public R<List<MonthlySummaryVO>> getMonthlySummary(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @ApiParam(value = "年份") @RequestParam(required = false) Integer year) {
        Institution institution = getInstitution(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        List<MonthlySummaryVO> summary = financeRecordService.getMonthlySummary(institution.getId(), year);
        return R.success(summary);
    }

    @ApiOperation("导出财务报表")
    @GetMapping("/export")
    public R<FinanceExportVO> exportFinanceReport(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @ApiParam(value = "开始日期（yyyy-MM-dd）", required = true) @RequestParam String startDate,
            @ApiParam(value = "结束日期（yyyy-MM-dd）", required = true) @RequestParam String endDate) {
        Institution institution = getInstitution(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        try {
            FinanceExportVO report = financeRecordService.exportFinanceReport(institution.getId(), startDate, endDate);
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
