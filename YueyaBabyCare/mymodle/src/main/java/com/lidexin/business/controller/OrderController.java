package com.lidexin.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lidexin.business.dto.OrderDTO;
import com.lidexin.business.service.IOrderService;
import com.lidexin.business.vo.OrderVO;
import com.lidexin.business.vo.UserInfoVO;
import com.lidexin.mymodle.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-24
 */
@Api(tags = "订单管理")
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @ApiOperation("创建订单")
    @PostMapping
    public R<Long> createOrder(HttpServletRequest request, @Validated @RequestBody OrderDTO dto) {
        Long parentId = getParentIdFromRequest(request);
        if (parentId == null) {
            return R.fail("请先登录");
        }
        try {
            Long orderId = orderService.createOrder(parentId, dto);
            return R.success(orderId);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("获取订单详情")
    @GetMapping("/{id}")
    public R<OrderVO> getOrderDetail(
            HttpServletRequest request,
            @ApiParam(value = "订单ID", required = true) @PathVariable Long id) {
        Long parentId = getParentIdFromRequest(request);
        if (parentId == null) {
            return R.fail("请先登录");
        }
        OrderVO order = orderService.getOrderByIdForParent(parentId, id);
        if (order == null) {
            return R.fail("订单不存在");
        }
        return R.success(order);
    }

    @ApiOperation("获取订单列表")
    @GetMapping("/list")
    public R<IPage<OrderVO>> getOrderList(
            HttpServletRequest request,
            @ApiParam(value = "订单状态（0-待付款，1-待发货，2-待收货，3-已完成，4-已取消）") @RequestParam(required = false) Byte status,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
        Long parentId = getParentIdFromRequest(request);
        if (parentId == null) {
            return R.fail("请先登录");
        }
        IPage<OrderVO> page = orderService.getParentOrders(parentId, status, pageNum, pageSize);
        return R.success(page);
    }

    @ApiOperation("支付订单")
    @PostMapping("/{id}/pay")
    public R<Boolean> payOrder(
            HttpServletRequest request,
            @ApiParam(value = "订单ID", required = true) @PathVariable Long id,
            @ApiParam(value = "支付方式（1-微信，2-支付宝，3-余额）", required = true) @RequestParam Byte payType) {
        Long parentId = getParentIdFromRequest(request);
        if (parentId == null) {
            return R.fail("请先登录");
        }
        try {
            boolean success = orderService.payOrder(parentId, id, payType);
            return R.success(success);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("取消订单")
    @PostMapping("/{id}/cancel")
    public R<Boolean> cancelOrder(
            HttpServletRequest request,
            @ApiParam(value = "订单ID", required = true) @PathVariable Long id) {
        Long parentId = getParentIdFromRequest(request);
        if (parentId == null) {
            return R.fail("请先登录");
        }
        try {
            boolean success = orderService.cancelOrder(parentId, id);
            return R.success(success);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("确认收货")
    @PutMapping("/{id}/confirm-receipt")
    public R<Boolean> confirmReceipt(
            HttpServletRequest request,
            @ApiParam(value = "订单ID", required = true) @PathVariable Long id) {
        Long parentId = getParentIdFromRequest(request);
        if (parentId == null) {
            return R.fail("请先登录");
        }
        try {
            boolean success = orderService.confirmReceipt(parentId, id);
            return R.success(success);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("获取家长订单统计")
    @GetMapping("/stats")
    public R<Map<String, Integer>> getParentOrderStats(HttpServletRequest request) {
        Long parentId = getParentIdFromRequest(request);
        if (parentId == null) {
            return R.fail("请先登录");
        }
        Map<String, Integer> stats = orderService.getParentOrderStats(parentId);
        return R.success(stats);
    }

    // ==================== 机构端接口 ====================

    @ApiOperation("获取机构订单列表")
    @GetMapping("/institution/list")
    public R<IPage<OrderVO>> getInstitutionOrders(
            HttpServletRequest request,
            @ApiParam(value = "订单状态") @RequestParam(required = false) Byte status,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
        Long institutionId = getInstitutionIdFromRequest(request);
        if (institutionId == null) {
            return R.fail("请先登录");
        }
        IPage<OrderVO> page = orderService.getInstitutionOrders(institutionId, status, pageNum, pageSize);
        return R.success(page);
    }

    @ApiOperation("获取机构订单详情")
    @GetMapping("/institution/{id}")
    public R<OrderVO> getInstitutionOrderDetail(
            HttpServletRequest request,
            @ApiParam(value = "订单ID", required = true) @PathVariable Long id) {
        Long institutionId = getInstitutionIdFromRequest(request);
        if (institutionId == null) {
            return R.fail("请先登录");
        }
        OrderVO order = orderService.getOrderById(id);
        if (order == null || !order.getInstitutionId().equals(institutionId)) {
            return R.fail("订单不存在");
        }
        return R.success(order);
    }

    @ApiOperation("添加物流信息（发货）")
    @PostMapping("/{id}/logistics")
    public R<Boolean> addLogistics(
            HttpServletRequest request,
            @ApiParam(value = "订单ID", required = true) @PathVariable Long id,
            @ApiParam(value = "物流公司", required = true) @RequestParam String deliveryCompany,
            @ApiParam(value = "物流单号", required = true) @RequestParam String deliveryNo) {
        Long institutionId = getInstitutionIdFromRequest(request);
        if (institutionId == null) {
            return R.fail("请先登录");
        }
        try {
            boolean success = orderService.addLogistics(institutionId, id, deliveryCompany, deliveryNo);
            return R.success(success);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("获取机构订单统计")
    @GetMapping("/institution/stats")
    public R<IOrderService.OrderStatsVO> getInstitutionOrderStats(HttpServletRequest request) {
        Long institutionId = getInstitutionIdFromRequest(request);
        if (institutionId == null) {
            return R.fail("请先登录");
        }
        IOrderService.OrderStatsVO stats = orderService.getOrderStats(institutionId);
        return R.success(stats);
    }

    // ==================== 管理员接口 ====================

    @ApiOperation("修改订单状态（管理员）")
    @PutMapping("/{id}/status")
    public R<Boolean> updateOrderStatus(
            HttpServletRequest request,
            @ApiParam(value = "订单ID", required = true) @PathVariable Long id,
            @ApiParam(value = "订单状态", required = true) @RequestParam Byte status) {
        try {
            boolean success = orderService.updateOrderStatus(id, status);
            return R.success(success);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("获取全部订单统计（管理员）")
    @GetMapping("/admin/stats")
    public R<IOrderService.OrderStatsVO> getAllOrderStats(HttpServletRequest request) {
        IOrderService.OrderStatsVO stats = orderService.getOrderStats(null);
        return R.success(stats);
    }

    // ==================== 私有方法 ====================

    @SuppressWarnings("unchecked")
    private Long getParentIdFromRequest(HttpServletRequest request) {
        Object userInfo = request.getAttribute("userInfo");
        if (userInfo == null) {
            return null;
        }
        try {
            if (userInfo instanceof UserInfoVO) {
                UserInfoVO vo = (UserInfoVO) userInfo;
                return vo.getUserId();
            }
            if (userInfo instanceof Map) {
                Map<String, Object> userMap = (Map<String, Object>) userInfo;
                Object userId = userMap.get("userId");
                if (userId != null) {
                    return Long.valueOf(userId.toString());
                }
            }
        } catch (Exception e) {
            // 解析失败
        }
        return null;
    }

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
