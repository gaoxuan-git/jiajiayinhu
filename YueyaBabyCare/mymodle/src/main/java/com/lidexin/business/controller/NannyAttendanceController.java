package com.lidexin.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lidexin.business.po.Institution;
import com.lidexin.business.service.IInstitutionService;
import com.lidexin.business.service.INannyAttendanceService;
import com.lidexin.business.vo.NannyAttendanceVO;
import com.lidexin.mymodle.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Api(tags = "考勤管理")
@RestController
@RequestMapping("/api/nanny-attendance")
public class NannyAttendanceController {

    @Autowired
    private INannyAttendanceService nannyAttendanceService;

    @Autowired
    private IInstitutionService institutionService;

    @ApiOperation("分页查询考勤记录")
    @GetMapping("/list")
    public R<Page<NannyAttendanceVO>> list(
            @ApiParam("保姆ID") @RequestParam(required = false) Long nannyId,
            @ApiParam("状态") @RequestParam(required = false) Byte status,
            @ApiParam("开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Institution institution = getInstitutionFromToken(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        Page<NannyAttendanceVO> page = nannyAttendanceService.getAttendancePage(
                institution.getId(), nannyId, status, startDate, endDate, pageNum, pageSize);
        return R.success(page);
    }

    @ApiOperation("员工签到")
    @PostMapping("/check-in")
    public R<String> checkIn(
            @RequestBody Map<String, Object> params,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Institution institution = getInstitutionFromToken(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        Long nannyId = Long.valueOf(params.get("nannyId").toString());
        String remark = params.get("remark") != null ? params.get("remark").toString() : null;
        try {
            nannyAttendanceService.checkIn(institution.getId(), nannyId, remark);
            return R.success("签到成功", null);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("员工签退")
    @PostMapping("/check-out")
    public R<String> checkOut(
            @RequestBody Map<String, Object> params,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Institution institution = getInstitutionFromToken(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        Long nannyId = Long.valueOf(params.get("nannyId").toString());
        String remark = params.get("remark") != null ? params.get("remark").toString() : null;
        try {
            nannyAttendanceService.checkOut(institution.getId(), nannyId, remark);
            return R.success("签退成功", null);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("修改考勤状态")
    @PutMapping("/{id}/status")
    public R<String> updateStatus(
            @ApiParam("考勤ID") @PathVariable Long id,
            @RequestBody Map<String, Object> params,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Institution institution = getInstitutionFromToken(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        Byte status = Byte.valueOf(params.get("status").toString());
        String remark = params.get("remark") != null ? params.get("remark").toString() : null;
        try {
            nannyAttendanceService.updateStatus(institution.getId(), id, status, remark);
            return R.success("状态更新成功", null);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("考勤统计")
    @GetMapping("/statistics")
    public R<Map<String, Object>> statistics(
            @ApiParam("开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Institution institution = getInstitutionFromToken(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        Map<String, Object> stats = nannyAttendanceService.getStatistics(institution.getId(), startDate, endDate);
        return R.success(stats);
    }

    private Institution getInstitutionFromToken(String authorization) {
        if (authorization == null || authorization.isEmpty()) {
            return null;
        }
        String token = authorization.replace("Bearer ", "").trim();
        return institutionService.getInstitutionByToken(token);
    }
}
