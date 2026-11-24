package com.lidexin.business.controller;

import com.lidexin.business.dto.CartDTO;
import com.lidexin.business.service.ICartService;
import com.lidexin.business.vo.CartVO;
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
 * 购物车表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-24
 */
@Api(tags = "购物车管理")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private ICartService cartService;

    @ApiOperation("添加商品到购物车")
    @PostMapping
    public R<Long> addToCart(HttpServletRequest request, @Validated @RequestBody CartDTO dto) {
        Long parentId = getParentIdFromRequest(request);
        if (parentId == null) {
            return R.fail("请先登录");
        }
        try {
            Long cartId = cartService.addToCart(parentId, dto);
            return R.success(cartId);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("获取购物车列表")
    @GetMapping
    public R<List<CartVO>> getCartList(HttpServletRequest request) {
        Long parentId = getParentIdFromRequest(request);
        if (parentId == null) {
            return R.fail("请先登录");
        }
        List<CartVO> list = cartService.getCartList(parentId);
        return R.success(list);
    }

    @ApiOperation("修改购物车商品数量")
    @PutMapping("/{id}")
    public R<Boolean> updateQuantity(
            HttpServletRequest request,
            @ApiParam(value = "购物车ID", required = true) @PathVariable Long id,
            @ApiParam(value = "数量", required = true) @RequestParam Integer quantity) {
        Long parentId = getParentIdFromRequest(request);
        if (parentId == null) {
            return R.fail("请先登录");
        }
        try {
            boolean success = cartService.updateQuantity(parentId, id, quantity);
            return R.success(success);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("删除购物车商品")
    @DeleteMapping("/{id}")
    public R<Boolean> deleteCartItem(
            HttpServletRequest request,
            @ApiParam(value = "购物车ID", required = true) @PathVariable Long id) {
        Long parentId = getParentIdFromRequest(request);
        if (parentId == null) {
            return R.fail("请先登录");
        }
        boolean success = cartService.deleteCartItem(parentId, id);
        return R.success(success);
    }

    @ApiOperation("批量删除购物车商品")
    @DeleteMapping("/batch")
    public R<Boolean> batchDeleteCartItems(
            HttpServletRequest request,
            @RequestBody List<Long> cartIds) {
        Long parentId = getParentIdFromRequest(request);
        if (parentId == null) {
            return R.fail("请先登录");
        }
        boolean success = cartService.batchDeleteCartItems(parentId, cartIds);
        return R.success(success);
    }

    @ApiOperation("清空购物车")
    @DeleteMapping("/clear")
    public R<Boolean> clearCart(HttpServletRequest request) {
        Long parentId = getParentIdFromRequest(request);
        if (parentId == null) {
            return R.fail("请先登录");
        }
        boolean success = cartService.clearCart(parentId);
        return R.success(success);
    }

    @ApiOperation("选中/取消选中商品")
    @PutMapping("/{id}/select")
    public R<Boolean> updateSelected(
            HttpServletRequest request,
            @ApiParam(value = "购物车ID", required = true) @PathVariable Long id,
            @ApiParam(value = "是否选中（0-否，1-是）", required = true) @RequestParam Byte selected) {
        Long parentId = getParentIdFromRequest(request);
        if (parentId == null) {
            return R.fail("请先登录");
        }
        boolean success = cartService.updateSelected(parentId, id, selected);
        return R.success(success);
    }

    @ApiOperation("全选/取消全选")
    @PutMapping("/selectAll")
    public R<Boolean> selectAll(
            HttpServletRequest request,
            @ApiParam(value = "是否选中（0-否，1-是）", required = true) @RequestParam Byte selected) {
        Long parentId = getParentIdFromRequest(request);
        if (parentId == null) {
            return R.fail("请先登录");
        }
        boolean success = cartService.selectAll(parentId, selected);
        return R.success(success);
    }

    @ApiOperation("获取购物车商品数量")
    @GetMapping("/count")
    public R<Integer> getCartCount(HttpServletRequest request) {
        Long parentId = getParentIdFromRequest(request);
        if (parentId == null) {
            return R.fail("请先登录");
        }
        Integer count = cartService.getCartCount(parentId);
        return R.success(count);
    }

    @ApiOperation("获取购物车选中商品总额")
    @GetMapping("/total")
    public R<ICartService.CartTotalVO> getCartTotal(HttpServletRequest request) {
        Long parentId = getParentIdFromRequest(request);
        if (parentId == null) {
            return R.fail("请先登录");
        }
        ICartService.CartTotalVO total = cartService.getCartTotal(parentId);
        return R.success(total);
    }

    /**
     * 从请求中获取家长ID
     * 对于家长用户（userType=1），从 userInfo 中获取 userId 作为 parentId
     */
    @SuppressWarnings("unchecked")
    private Long getParentIdFromRequest(HttpServletRequest request) {
        Object userInfo = request.getAttribute("userInfo");
        if (userInfo == null) {
            return null;
        }

        try {
            // 优先处理 UserInfoVO 类型
            if (userInfo instanceof UserInfoVO) {
                UserInfoVO vo = (UserInfoVO) userInfo;
                // 家长用户的 userId 就是 parentId
                return vo.getUserId();
            }

            // 兼容处理 Map 类型
            if (userInfo instanceof Map) {
                Map<String, Object> userMap = (Map<String, Object>) userInfo;
                Object userId = userMap.get("userId");
                if (userId != null) {
                    return Long.valueOf(userId.toString());
                }
            }
        } catch (Exception e) {
            // 解析失败，返回null
        }

        return null;
    }
}
