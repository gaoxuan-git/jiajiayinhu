package com.lidexin.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lidexin.business.dto.BabyGrowthRecordDTO;
import com.lidexin.business.po.BabyGrowthRecord;
import com.lidexin.business.service.IBabyGrowthRecordService;
import com.lidexin.business.vo.BabyGrowthRecordVO;
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
 * 婴幼儿成长记录表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Api(tags = "婴幼儿成长记录管理")
@RestController
@RequestMapping("/api")
public class BabyGrowthRecordController {

    @Autowired
    private IBabyGrowthRecordService growthRecordService;

    @Autowired
    private AuthService authService;

    // ==================== 家长/保姆端接口 ====================

    /**
     * 新增成长记录
     */
    @ApiOperation("新增成长记录")
    @PostMapping("/baby/{babyId}/growth-record")
    public R<Long> addGrowthRecord(
            @ApiParam(value = "宝宝ID", required = true) @PathVariable Long babyId,
            @Validated @RequestBody BabyGrowthRecordDTO dto,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        Byte recorderType = getRecorderType(userInfo.getUserType());
        Long recorderId = userInfo.getUserId();

        // 检查权限
        if (!growthRecordService.hasPermission(babyId, recorderType, recorderId)) {
            return R.fail("无权限操作该宝宝的成长记录");
        }

        Long recordId = growthRecordService.addGrowthRecord(babyId, dto, recorderType, recorderId);
        return R.success("新增成功", recordId);
    }

    /**
     * 获取成长记录列表
     */
    @ApiOperation("获取宝宝的成长记录列表")
    @GetMapping("/baby/{babyId}/growth-records")
    public R<List<BabyGrowthRecordVO>> getGrowthRecords(
            @ApiParam(value = "宝宝ID", required = true) @PathVariable Long babyId,
            @ApiParam(value = "记录类型（1-饮食，2-睡眠，3-排便，4-活动，5-其他）") @RequestParam(required = false) Byte recordType,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        Byte recorderType = getRecorderType(userInfo.getUserType());
        Long recorderId = userInfo.getUserId();

        // 检查权限
        if (!growthRecordService.hasPermission(babyId, recorderType, recorderId)) {
            return R.fail("无权限查看该宝宝的成长记录");
        }

        List<BabyGrowthRecordVO> list = growthRecordService.getGrowthRecordsByBabyId(babyId, recordType);
        return R.success(list);
    }

    /**
     * 修改成长记录
     */
    @ApiOperation("修改成长记录")
    @PutMapping("/baby-growth-record/{id}")
    public R<Boolean> updateGrowthRecord(
            @ApiParam(value = "记录ID", required = true) @PathVariable Long id,
            @Validated @RequestBody BabyGrowthRecordDTO dto,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        Byte recorderType = getRecorderType(userInfo.getUserType());
        Long recorderId = userInfo.getUserId();

        boolean result = growthRecordService.updateGrowthRecord(id, dto, recorderType, recorderId);
        return result ? R.success("修改成功", true) : R.fail("修改失败，记录不存在或无权限");
    }

    /**
     * 删除成长记录
     */
    @ApiOperation("删除成长记录")
    @DeleteMapping("/baby-growth-record/{id}")
    public R<Boolean> deleteGrowthRecord(
            @ApiParam(value = "记录ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        Byte recorderType = getRecorderType(userInfo.getUserType());
        Long recorderId = userInfo.getUserId();

        boolean result = growthRecordService.deleteGrowthRecord(id, recorderType, recorderId);
        return result ? R.success("删除成功", true) : R.fail("删除失败，记录不存在或无权限");
    }

    /**
     * 获取成长记录详情
     */
    @ApiOperation("获取成长记录详情")
    @GetMapping("/baby-growth-record/{id}")
    public R<BabyGrowthRecordVO> getGrowthRecordById(
            @ApiParam(value = "记录ID", required = true) @PathVariable Long id) {
        BabyGrowthRecordVO record = growthRecordService.getGrowthRecordById(id);
        return record != null ? R.success(record) : R.fail("记录不存在");
    }

    /**
     * 获取成长记录时间线
     */
    @ApiOperation("获取成长记录时间线")
    @GetMapping("/baby/{babyId}/growth-records/timeline")
    public R<Map<String, List<BabyGrowthRecordVO>>> getGrowthRecordsTimeline(
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
        if (!growthRecordService.hasPermission(babyId, recorderType, recorderId)) {
            return R.fail("无权限查看该宝宝的成长记录");
        }

        Map<String, List<BabyGrowthRecordVO>> timeline = growthRecordService.getGrowthRecordsTimeline(babyId, startDate, endDate);
        return R.success(timeline);
    }

    /**
     * 为成长记录上传图片
     */
    @ApiOperation("为成长记录上传图片")
    @PostMapping("/baby-growth-record/{id}/images")
    public R<Boolean> uploadImages(
            @ApiParam(value = "记录ID", required = true) @PathVariable Long id,
            @ApiParam(value = "图片URL列表", required = true) @RequestBody List<String> imageUrls,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        if (imageUrls == null || imageUrls.isEmpty()) {
            return R.fail("图片列表不能为空");
        }

        Byte recorderType = getRecorderType(userInfo.getUserType());
        Long recorderId = userInfo.getUserId();

        boolean result = growthRecordService.uploadImages(id, imageUrls, recorderType, recorderId);
        return result ? R.success("上传成功", true) : R.fail("上传失败，记录不存在或无权限");
    }

    // ==================== 管理员接口 ====================

    /**
     * 分页查询成长记录（管理员用）
     */
    @ApiOperation("分页查询成长记录（管理员）")
    @GetMapping("/admin/baby-growth-records")
    public R<IPage<BabyGrowthRecordVO>> pageGrowthRecords(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam(value = "宝宝ID") @RequestParam(required = false) Long babyId,
            @ApiParam(value = "家长ID") @RequestParam(required = false) Long parentId,
            @ApiParam(value = "记录类型（1-饮食，2-睡眠，3-排便，4-活动，5-其他）") @RequestParam(required = false) Byte recordType,
            @ApiParam(value = "记录人类型（1-家长，2-保姆）") @RequestParam(required = false) Byte recorderType,
            @ApiParam(value = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam(value = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Page<BabyGrowthRecord> page = new Page<>(pageNum, pageSize);
        IPage<BabyGrowthRecordVO> result = growthRecordService.pageGrowthRecords(page, babyId, parentId, recordType, recorderType, startDate, endDate);
        return R.success(result);
    }

    /**
     * 根据家长ID查询成长记录（管理员用）
     */
    @ApiOperation("根据家长ID查询成长记录（管理员）")
    @GetMapping("/admin/parent/{parentId}/growth-records")
    public R<List<BabyGrowthRecordVO>> getGrowthRecordsByParentId(
            @ApiParam(value = "家长ID", required = true) @PathVariable Long parentId) {
        List<BabyGrowthRecordVO> list = growthRecordService.getGrowthRecordsByParentId(parentId);
        return R.success(list);
    }

    /**
     * 根据宝宝ID查询成长记录（管理员用）
     */
    @ApiOperation("根据宝宝ID查询成长记录（管理员）")
    @GetMapping("/admin/baby/{babyId}/growth-records")
    public R<List<BabyGrowthRecordVO>> getGrowthRecordsByBabyIdForAdmin(
            @ApiParam(value = "宝宝ID", required = true) @PathVariable Long babyId,
            @ApiParam(value = "记录类型") @RequestParam(required = false) Byte recordType) {
        List<BabyGrowthRecordVO> list = growthRecordService.getGrowthRecordsByBabyId(babyId, recordType);
        return R.success(list);
    }

    /**
     * 获取成长记录时间线（管理员用）
     */
    @ApiOperation("获取成长记录时间线（管理员）")
    @GetMapping("/admin/baby/{babyId}/growth-records/timeline")
    public R<Map<String, List<BabyGrowthRecordVO>>> getGrowthRecordsTimelineForAdmin(
            @ApiParam(value = "宝宝ID", required = true) @PathVariable Long babyId,
            @ApiParam(value = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam(value = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Map<String, List<BabyGrowthRecordVO>> timeline = growthRecordService.getGrowthRecordsTimeline(babyId, startDate, endDate);
        return R.success(timeline);
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
