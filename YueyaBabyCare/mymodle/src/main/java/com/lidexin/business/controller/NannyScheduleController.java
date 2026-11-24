package com.lidexin.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lidexin.business.po.Institution;
import com.lidexin.business.po.Nanny;
import com.lidexin.business.po.NannySchedule;
import com.lidexin.business.service.IInstitutionService;
import com.lidexin.business.service.INannyService;
import com.lidexin.business.service.INannyScheduleService;
import com.lidexin.business.vo.NannyScheduleVO;
import com.lidexin.mymodle.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 保姆排班表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Slf4j
@Api(tags = "排班管理")
@RestController
@RequestMapping("/api/nanny-schedule")
public class NannyScheduleController {

    @Autowired
    private INannyScheduleService nannyScheduleService;
    
    @Autowired
    private IInstitutionService institutionService;
    
    @Autowired
    private INannyService nannyService;
    
    /**
     * 新增排班（机构）
     */
    @ApiOperation("新增排班")
    @PostMapping
    public R<String> createSchedule(
            @RequestBody NannySchedule schedule,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Institution institution = getInstitutionFromToken(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        
        nannyScheduleService.createSchedule(institution.getId(), schedule);
        return R.success("排班创建成功", null);
    }
    
    /**
     * 批量新增排班（机构）
     */
    @ApiOperation("批量新增排班")
    @PostMapping("/batch")
    public R<Integer> batchCreateSchedule(
            @RequestBody List<NannySchedule> schedules,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Institution institution = getInstitutionFromToken(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        
        int successCount = nannyScheduleService.batchCreateSchedule(institution.getId(), schedules);
        return R.success("成功创建 " + successCount + " 条排班", successCount);
    }
    
    /**
     * 获取排班列表（机构）
     */
    @ApiOperation("获取排班列表")
    @GetMapping("/list")
    public R<Page<NannyScheduleVO>> getScheduleList(
            @ApiParam("保姆ID") @RequestParam(required = false) Long nannyId,
            @ApiParam("开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Institution institution = getInstitutionFromToken(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        
        Page<NannyScheduleVO> page = nannyScheduleService.getScheduleList(
                institution.getId(), nannyId, startDate, endDate, pageNum, pageSize);
        return R.success(page);
    }
    
    /**
     * 修改排班（机构）
     */
    @ApiOperation("修改排班")
    @PutMapping("/{id}")
    public R<String> updateSchedule(
            @ApiParam("排班ID") @PathVariable Long id,
            @RequestBody NannySchedule schedule,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Institution institution = getInstitutionFromToken(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        
        nannyScheduleService.updateSchedule(institution.getId(), id, schedule);
        return R.success("排班修改成功", null);
    }
    
    /**
     * 删除排班（机构）
     */
    @ApiOperation("删除排班")
    @DeleteMapping("/{id}")
    public R<String> deleteSchedule(
            @ApiParam("排班ID") @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Institution institution = getInstitutionFromToken(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        
        nannyScheduleService.deleteSchedule(institution.getId(), id);
        return R.success("排班删除成功", null);
    }
    
    /**
     * 获取日历视图（机构）
     */
    @ApiOperation("获取日历视图")
    @GetMapping("/calendar")
    public R<Map<LocalDate, List<NannyScheduleVO>>> getCalendarView(
            @ApiParam("年份") @RequestParam Integer year,
            @ApiParam("月份") @RequestParam Integer month,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Institution institution = getInstitutionFromToken(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        
        Map<LocalDate, List<NannyScheduleVO>> calendar = nannyScheduleService.getCalendarView(
                institution.getId(), year, month);
        return R.success(calendar);
    }
    
    /**
     * 获取保姆的排班（保姆/机构）
     */
    @ApiOperation("获取保姆的排班")
    @GetMapping("/nanny/{nannyId}")
    public R<List<NannyScheduleVO>> getNannySchedule(
            @ApiParam("保姆ID") @PathVariable Long nannyId,
            @ApiParam("开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        // 先尝试机构身份
        Institution institution = getInstitutionFromToken(authorization);
        if (institution != null) {
            List<NannyScheduleVO> schedules = nannyScheduleService.getNannySchedule(nannyId, startDate, endDate);
            return R.success(schedules);
        }
        
        // 再尝试保姆身份
        Nanny nanny = getNannyFromToken(authorization);
        if (nanny != null) {
            // 保姆只能查看自己的排班
            if (!nanny.getId().equals(nannyId)) {
                return R.fail("无权查看其他保姆的排班");
            }
            List<NannyScheduleVO> schedules = nannyScheduleService.getNannySchedule(nannyId, startDate, endDate);
            return R.success(schedules);
        }
        
        return R.fail("未登录或登录已过期");
    }
    
    /**
     * 获取我的排班（保姆端）
     */
    @ApiOperation("获取我的排班")
    @GetMapping("/nanny/my")
    public R<List<NannyScheduleVO>> getMySchedule(
            @ApiParam("开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Nanny nanny = getNannyFromToken(authorization);
        if (nanny == null) {
            return R.fail("未登录或登录已过期");
        }
        
        List<NannyScheduleVO> schedules = nannyScheduleService.getNannySchedule(nanny.getId(), startDate, endDate);
        return R.success(schedules);
    }
    
    /**
     * 申请调班（保姆）
     */
    @ApiOperation("申请调班")
    @PostMapping("/{id}/swap")
    public R<String> applySwap(
            @ApiParam("排班ID") @PathVariable Long id,
            @RequestBody Map<String, Object> params,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Nanny nanny = getNannyFromToken(authorization);
        if (nanny == null) {
            return R.fail("未登录或登录已过期");
        }
        
        Long targetNannyId = params.get("targetNannyId") != null ? 
                Long.valueOf(params.get("targetNannyId").toString()) : null;
        String reason = (String) params.get("reason");
        
        nannyScheduleService.applySwap(nanny.getId(), id, targetNannyId, reason);
        return R.success("调班申请已提交", null);
    }
    
    /**
     * 获取可调班的保姆列表（保姆端）
     */
    @ApiOperation("获取可调班的保姆列表")
    @GetMapping("/{id}/swap-nannies")
    public R<List<Nanny>> getSwapNannies(
            @ApiParam("排班ID") @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Nanny nanny = getNannyFromToken(authorization);
        if (nanny == null) {
            return R.fail("未登录或登录已过期");
        }
        
        List<Nanny> nannies = nannyScheduleService.getSwapNannies(nanny.getId(), id);
        return R.success(nannies);
    }
    
    /**
     * 审核调班（机构）
     */
    @ApiOperation("审核调班")
    @PutMapping("/{id}/swap-approve")
    public R<String> approveSwap(
            @ApiParam("排班ID") @PathVariable Long id,
            @ApiParam("是否批准") @RequestParam Boolean approved,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Institution institution = getInstitutionFromToken(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        
        nannyScheduleService.approveSwap(institution.getId(), id, approved);
        return R.success(approved ? "调班申请已批准" : "调班申请已拒绝", null);
    }
    
    /**
     * 获取调班申请列表（机构）
     */
    @ApiOperation("获取调班申请列表")
    @GetMapping("/swap-requests")
    public R<Page<NannyScheduleVO>> getSwapRequests(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Institution institution = getInstitutionFromToken(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        
        Page<NannyScheduleVO> page = nannyScheduleService.getSwapRequests(
                institution.getId(), pageNum, pageSize);
        return R.success(page);
    }
    
    /**
     * 检测排班冲突（机构）
     */
    @ApiOperation("检测排班冲突")
    @GetMapping("/conflicts")
    public R<List<NannyScheduleVO>> checkConflicts(
            @ApiParam("保姆ID") @RequestParam Long nannyId,
            @ApiParam("排班日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate scheduleDate,
            @ApiParam("班次类型") @RequestParam Byte shiftType,
            @ApiParam("排除的排班ID") @RequestParam(required = false) Long excludeId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Institution institution = getInstitutionFromToken(authorization);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        
        List<NannyScheduleVO> conflicts = nannyScheduleService.checkConflicts(
                institution.getId(), nannyId, scheduleDate, shiftType, excludeId);
        return R.success(conflicts);
    }
    
    /**
     * 从token获取机构信息
     */
    private Institution getInstitutionFromToken(String authorization) {
        if (authorization == null || authorization.isEmpty()) {
            return null;
        }
        String token = authorization.replace("Bearer ", "").trim();
        return institutionService.getInstitutionByToken(token);
    }
    
    /**
     * 从token获取保姆信息
     */
    private Nanny getNannyFromToken(String authorization) {
        if (authorization == null || authorization.isEmpty()) {
            return null;
        }
        String token = authorization.replace("Bearer ", "").trim();
        return nannyService.getNannyByToken(token);
    }
}
