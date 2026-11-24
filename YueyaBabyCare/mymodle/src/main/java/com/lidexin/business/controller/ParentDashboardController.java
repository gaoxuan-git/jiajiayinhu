package com.lidexin.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lidexin.business.po.Parent;
import com.lidexin.business.service.IParentDashboardService;
import com.lidexin.business.service.IParentDashboardService.*;
import com.lidexin.business.service.IParentService;
import com.lidexin.mymodle.common.R;
import com.lidexin.mymodle.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 家长数据统计控制器
 *
 * @author 张三
 * @since 2025-12-25
 */
@Slf4j
@Api(tags = "家长数据统计")
@RestController
@RequestMapping("/api/parent")
public class ParentDashboardController {

    @Autowired
    private IParentDashboardService parentDashboardService;

    @Autowired
    private IParentService parentService;

    @Autowired
    private AuthService authService;

    /**
     * 获取婴幼儿数据统计
     */
    @ApiOperation("获取婴幼儿数据统计")
    @GetMapping("/baby-stats")
    public R<BabyStatsVO> getBabyStats(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extractToken(authorization);
        Parent parent = parentService.getParentByToken(token);
        if (parent == null) {
            return R.fail("未登录或登录已过期");
        }

        log.info("获取婴幼儿数据统计，家长ID: {}", parent.getId());
        BabyStatsVO stats = parentDashboardService.getBabyStats(parent.getId());
        return R.success(stats);
    }

    /**
     * 获取护理数据统计
     */
    @ApiOperation("获取护理数据统计")
    @GetMapping("/care-stats")
    public R<CareStatsVO> getCareStats(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extractToken(authorization);
        Parent parent = parentService.getParentByToken(token);
        if (parent == null) {
            return R.fail("未登录或登录已过期");
        }

        log.info("获取护理数据统计，家长ID: {}", parent.getId());
        CareStatsVO stats = parentDashboardService.getCareStats(parent.getId());
        return R.success(stats);
    }

    /**
     * 获取订单历史
     */
    @ApiOperation("获取订单历史")
    @GetMapping("/order-history")
    public R<Page<OrderHistoryVO>> getOrderHistory(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extractToken(authorization);
        Parent parent = parentService.getParentByToken(token);
        if (parent == null) {
            return R.fail("未登录或登录已过期");
        }

        log.info("获取订单历史，家长ID: {}, 页码: {}, 每页数量: {}", parent.getId(), pageNum, pageSize);
        Page<OrderHistoryVO> page = parentDashboardService.getOrderHistory(parent.getId(), pageNum, pageSize);
        return R.success(page);
    }

    /**
     * 提取Token
     */
    private String extractToken(String authorization) {
        if (authorization != null && !authorization.isEmpty()) {
            return authorization.replace("Bearer ", "").trim();
        }
        return "";
    }
}
