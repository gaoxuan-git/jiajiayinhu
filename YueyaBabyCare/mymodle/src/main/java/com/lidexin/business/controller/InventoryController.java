package com.lidexin.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lidexin.business.dto.InventoryDTO;
import com.lidexin.business.po.Product;
import com.lidexin.business.service.IInventoryService;
import com.lidexin.business.vo.InventoryVO;
import com.lidexin.business.vo.UserInfoVO;
import com.lidexin.mymodle.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 库存表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Api(tags = "库存管理")
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private IInventoryService inventoryService;

    @ApiOperation("新增库存操作记录")
    @PostMapping
    public R<Boolean> addInventoryRecord(
            HttpServletRequest request,
            @Validated @RequestBody InventoryDTO dto) {
        Long institutionId = getInstitutionIdFromRequest(request);
        if (institutionId == null) {
            return R.fail("请先登录");
        }
        try {
            boolean success = inventoryService.addInventoryRecord(institutionId, institutionId, dto);
            return R.success(success);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("获取库存操作记录列表")
    @GetMapping("/list")
    public R<IPage<InventoryVO>> getInventoryList(
            HttpServletRequest request,
            @ApiParam(value = "商品ID") @RequestParam(required = false) Long productId,
            @ApiParam(value = "操作类型（1-入库，2-出库，3-盘点）") @RequestParam(required = false) Byte operateType,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
        Long institutionId = getInstitutionIdFromRequest(request);
        if (institutionId == null) {
            return R.fail("请先登录");
        }
        IPage<InventoryVO> page = inventoryService.getInventoryList(institutionId, productId, operateType, pageNum, pageSize);
        return R.success(page);
    }

    @ApiOperation("获取当前库存列表")
    @GetMapping("/current")
    public R<IPage<Product>> getCurrentStock(
            HttpServletRequest request,
            @ApiParam(value = "搜索关键词") @RequestParam(required = false) String keyword,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
        Long institutionId = getInstitutionIdFromRequest(request);
        if (institutionId == null) {
            return R.fail("请先登录");
        }
        IPage<Product> page = inventoryService.getCurrentStock(institutionId, keyword, pageNum, pageSize);
        return R.success(page);
    }

    @ApiOperation("获取库存不足商品列表")
    @GetMapping("/low-stock")
    public R<List<Product>> getLowStockProducts(HttpServletRequest request) {
        Long institutionId = getInstitutionIdFromRequest(request);
        if (institutionId == null) {
            return R.fail("请先登录");
        }
        List<Product> list = inventoryService.getLowStockProducts(institutionId);
        return R.success(list);
    }

    @ApiOperation("设置库存预警值")
    @PutMapping("/set-alert")
    public R<Boolean> setStockAlert(
            HttpServletRequest request,
            @ApiParam(value = "商品ID", required = true) @RequestParam Long productId,
            @ApiParam(value = "预警值", required = true) @RequestParam Integer alertValue) {
        Long institutionId = getInstitutionIdFromRequest(request);
        if (institutionId == null) {
            return R.fail("请先登录");
        }
        try {
            boolean success = inventoryService.setStockAlert(institutionId, productId, alertValue);
            return R.success(success);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("获取库存统计数据")
    @GetMapping("/stats")
    public R<IInventoryService.InventoryStatsVO> getInventoryStats(HttpServletRequest request) {
        Long institutionId = getInstitutionIdFromRequest(request);
        if (institutionId == null) {
            return R.fail("请先登录");
        }
        IInventoryService.InventoryStatsVO stats = inventoryService.getInventoryStats(institutionId);
        return R.success(stats);
    }

    // ==================== 私有方法 ====================

    @SuppressWarnings("unchecked")
    private Long getInstitutionIdFromRequest(HttpServletRequest request) {
        Object userInfo = request.getAttribute("userInfo");
        if (userInfo == null) {
            return null;
        }
        try {
            if (userInfo instanceof UserInfoVO) {
                UserInfoVO vo = (UserInfoVO) userInfo;
                // userType: 3-机构
                if (vo.getUserType() != null && vo.getUserType() == 3) {
                    return vo.getUserId();
                }
            }
            if (userInfo instanceof Map) {
                Map<String, Object> userMap = (Map<String, Object>) userInfo;
                Object userType = userMap.get("userType");
                // userType: 3-机构
                if (userType != null && Integer.valueOf(userType.toString()) == 3) {
                    Object userId = userMap.get("userId");
                    if (userId != null) {
                        return Long.valueOf(userId.toString());
                    }
                }
            }
        } catch (Exception e) {
            // 解析失败
        }
        return null;
    }
}
