package com.lidexin.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lidexin.business.dto.BabyHealthRecordDTO;
import com.lidexin.business.po.BabyHealthRecord;
import com.lidexin.business.service.IBabyHealthRecordService;
import com.lidexin.business.vo.BabyHealthRecordVO;
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
 * 婴幼儿健康档案表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Api(tags = "婴幼儿健康档案管理")
@RestController
@RequestMapping("/api")
public class BabyHealthRecordController {

    @Autowired
    private IBabyHealthRecordService healthRecordService;

    @Autowired
    private AuthService authService;

    /**
     * 新增健康记录
     */
    @ApiOperation("新增健康记录")
    @PostMapping("/baby/{babyId}/health-record")
    public R<Long> addHealthRecord(
            @ApiParam(value = "宝宝ID", required = true) @PathVariable Long babyId,
            @Validated @RequestBody BabyHealthRecordDTO dto,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        Byte recorderType = getRecorderType(userInfo.getUserType());
        Long recorderId = userInfo.getUserId();

        // 检查权限
        if (!healthRecordService.hasPermission(babyId, recorderType, recorderId)) {
            return R.fail("无权限操作该宝宝的健康记录");
        }

        Long recordId = healthRecordService.addHealthRecord(babyId, dto, recorderType, recorderId);
        return R.success("新增成功", recordId);
    }

    /**
     * 获取健康记录列表
     */
    @ApiOperation("获取宝宝的健康记录列表")
    @GetMapping("/baby/{babyId}/health-records")
    public R<List<BabyHealthRecordVO>> getHealthRecords(
            @ApiParam(value = "宝宝ID", required = true) @PathVariable Long babyId,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        Byte recorderType = getRecorderType(userInfo.getUserType());
        Long recorderId = userInfo.getUserId();

        // 检查权限
        if (!healthRecordService.hasPermission(babyId, recorderType, recorderId)) {
            return R.fail("无权限查看该宝宝的健康记录");
        }

        List<BabyHealthRecordVO> list = healthRecordService.getHealthRecordsByBabyId(babyId);
        return R.success(list);
    }

    /**
     * 修改健康记录
     */
    @ApiOperation("修改健康记录")
    @PutMapping("/baby-health-record/{id}")
    public R<Boolean> updateHealthRecord(
            @ApiParam(value = "记录ID", required = true) @PathVariable Long id,
            @Validated @RequestBody BabyHealthRecordDTO dto,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        Byte recorderType = getRecorderType(userInfo.getUserType());
        Long recorderId = userInfo.getUserId();

        boolean result = healthRecordService.updateHealthRecord(id, dto, recorderType, recorderId);
        return result ? R.success("修改成功", true) : R.fail("修改失败，记录不存在或无权限");
    }

    /**
     * 删除健康记录
     */
    @ApiOperation("删除健康记录")
    @DeleteMapping("/baby-health-record/{id}")
    public R<Boolean> deleteHealthRecord(
            @ApiParam(value = "记录ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        Byte recorderType = getRecorderType(userInfo.getUserType());
        Long recorderId = userInfo.getUserId();

        boolean result = healthRecordService.deleteHealthRecord(id, recorderType, recorderId);
        return result ? R.success("删除成功", true) : R.fail("删除失败，记录不存在或无权限");
    }

    /**
     * 获取健康记录详情
     */
    @ApiOperation("获取健康记录详情")
    @GetMapping("/baby-health-record/{id}")
    public R<BabyHealthRecordVO> getHealthRecordById(
            @ApiParam(value = "记录ID", required = true) @PathVariable Long id) {
        BabyHealthRecordVO record = healthRecordService.getHealthRecordById(id);
        return record != null ? R.success(record) : R.fail("记录不存在");
    }

    /**
     * 获取健康数据图表
     */
    @ApiOperation("获取健康数据图表")
    @GetMapping("/baby/{babyId}/health-records/chart")
    public R<Map<String, Object>> getHealthChartData(
            @ApiParam(value = "宝宝ID", required = true) @PathVariable Long babyId,
            @ApiParam(value = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam(value = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        Byte recorderType = getRecorderType(userInfo.getUserType());
        Long recorderId = userInfo.getUserId();

        // 检查权限
        if (!healthRecordService.hasPermission(babyId, recorderType, recorderId)) {
            return R.fail("无权限查看该宝宝的健康数据");
        }

        Map<String, Object> chartData = healthRecordService.getHealthChartData(babyId, startDate, endDate);
        return R.success(chartData);
    }

    /**
     * 获取最新健康记录
     */
    @ApiOperation("获取宝宝的最新健康记录")
    @GetMapping("/baby/{babyId}/latest-health-record")
    public R<BabyHealthRecordVO> getLatestHealthRecord(
            @ApiParam(value = "宝宝ID", required = true) @PathVariable Long babyId,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        Byte recorderType = getRecorderType(userInfo.getUserType());
        Long recorderId = userInfo.getUserId();

        // 检查权限
        if (!healthRecordService.hasPermission(babyId, recorderType, recorderId)) {
            return R.fail("无权限查看该宝宝的健康记录");
        }

        BabyHealthRecordVO record = healthRecordService.getLatestHealthRecord(babyId);
        return R.success(record);
    }

    // ==================== 管理员接口 ====================

    /**
     * 分页查询健康记录（管理员用）
     */
    @ApiOperation("分页查询健康记录（管理员）")
    @GetMapping("/admin/baby-health-records")
    public R<IPage<BabyHealthRecordVO>> pageHealthRecords(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam(value = "宝宝ID") @RequestParam(required = false) Long babyId,
            @ApiParam(value = "家长ID") @RequestParam(required = false) Long parentId,
            @ApiParam(value = "记录人类型（1-家长，2-保姆）") @RequestParam(required = false) Byte recorderType,
            @ApiParam(value = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam(value = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Page<BabyHealthRecord> page = new Page<>(pageNum, pageSize);
        IPage<BabyHealthRecordVO> result = healthRecordService.pageHealthRecords(page, babyId, parentId, recorderType, startDate, endDate);
        return R.success(result);
    }

    /**
     * 根据家长ID查询健康记录（管理员用）
     */
    @ApiOperation("根据家长ID查询健康记录（管理员）")
    @GetMapping("/admin/parent/{parentId}/health-records")
    public R<List<BabyHealthRecordVO>> getHealthRecordsByParentId(
            @ApiParam(value = "家长ID", required = true) @PathVariable Long parentId) {
        List<BabyHealthRecordVO> list = healthRecordService.getHealthRecordsByParentId(parentId);
        return R.success(list);
    }

    /**
     * 根据宝宝ID查询健康记录（管理员用）
     */
    @ApiOperation("根据宝宝ID查询健康记录（管理员）")
    @GetMapping("/admin/baby/{babyId}/health-records")
    public R<List<BabyHealthRecordVO>> getHealthRecordsByBabyIdForAdmin(
            @ApiParam(value = "宝宝ID", required = true) @PathVariable Long babyId) {
        List<BabyHealthRecordVO> list = healthRecordService.getHealthRecordsByBabyId(babyId);
        return R.success(list);
    }

    /**
     * 获取健康数据图表（管理员用）
     */
    @ApiOperation("获取健康数据图表（管理员）")
    @GetMapping("/admin/baby/{babyId}/health-records/chart")
    public R<Map<String, Object>> getHealthChartDataForAdmin(
            @ApiParam(value = "宝宝ID", required = true) @PathVariable Long babyId,
            @ApiParam(value = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam(value = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Map<String, Object> chartData = healthRecordService.getHealthChartData(babyId, startDate, endDate);
        return R.success(chartData);
    }

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

    /**
     * 根据用户类型获取记录人类型
     * 用户类型：1-家长，2-保姆，3-机构，4-管理员
     * 记录人类型：1-家长，2-保姆
     */
    private Byte getRecorderType(Integer userType) {
        if (userType == null) {
            return 1; // 默认家长
        }
        switch (userType) {
            case 1:
                return 1; // 家长
            case 2:
                return 2; // 保姆
            default:
                return 1; // 其他类型默认为家长
        }
    }
}
