package com.lidexin.business.controller;

import com.lidexin.business.dto.BabyDTO;
import com.lidexin.business.service.IBabyService;
import com.lidexin.business.vo.BabyVO;
import com.lidexin.business.vo.UserInfoVO;
import com.lidexin.mymodle.common.R;
import com.lidexin.mymodle.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 婴幼儿信息表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Api(tags = "婴幼儿管理")
@RestController
@RequestMapping("/api/baby")
public class BabyController {

    @Autowired
    private IBabyService babyService;

    @Autowired
    private AuthService authService;

    /**
     * 新增婴幼儿
     */
    @ApiOperation("新增婴幼儿")
    @PostMapping
    public R<Boolean> addBaby(@Validated @RequestBody BabyDTO dto, HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        // 只有家长可以添加婴幼儿
        if (userInfo.getUserType() == null || userInfo.getUserType() != 1) {
            return R.fail("只有家长可以添加婴幼儿");
        }
        boolean result = babyService.addBaby(dto, userInfo.getUserId());
        return result ? R.success("添加成功", true) : R.fail("添加失败");
    }

    /**
     * 获取婴幼儿信息
     */
    @ApiOperation("获取婴幼儿信息")
    @GetMapping("/{id}")
    public R<BabyVO> getBabyById(
            @ApiParam(value = "婴幼儿ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        // 家长只能查看自己的宝宝，管理员可以查看所有
        Long parentId = isAdmin(userInfo) ? null : userInfo.getUserId();
        BabyVO baby = babyService.getBabyById(id, parentId);
        return R.success(baby);
    }

    /**
     * 修改婴幼儿信息
     */
    @ApiOperation("修改婴幼儿信息")
    @PutMapping("/{id}")
    public R<Boolean> updateBaby(
            @ApiParam(value = "婴幼儿ID", required = true) @PathVariable Long id,
            @Validated @RequestBody BabyDTO dto,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        dto.setId(id);
        // 家长只能修改自己的宝宝，管理员可以修改所有
        Long parentId = isAdmin(userInfo) ? null : userInfo.getUserId();
        boolean result = babyService.updateBaby(dto, parentId);
        return result ? R.success("修改成功", true) : R.fail("修改失败");
    }

    /**
     * 删除婴幼儿
     */
    @ApiOperation("删除婴幼儿")
    @DeleteMapping("/{id}")
    public R<Boolean> deleteBaby(
            @ApiParam(value = "婴幼儿ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        // 家长只能删除自己的宝宝，管理员可以删除所有
        Long parentId = isAdmin(userInfo) ? null : userInfo.getUserId();
        boolean result = babyService.deleteBaby(id, parentId);
        return result ? R.success("删除成功", true) : R.fail("删除失败");
    }

    /**
     * 获取当前用户的婴幼儿列表
     */
    @ApiOperation("获取当前用户的婴幼儿列表")
    @GetMapping("/list")
    public R<List<BabyVO>> getBabyList(HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        // 只有家长可以获取自己的婴幼儿列表
        if (userInfo.getUserType() == null || userInfo.getUserType() != 1) {
            return R.fail("只有家长可以查看婴幼儿列表");
        }
        List<BabyVO> list = babyService.listByParentId(userInfo.getUserId());
        return R.success(list);
    }

    /**
     * 获取婴幼儿的保姆列表
     */
    @ApiOperation("获取婴幼儿的保姆列表")
    @GetMapping("/{id}/caregivers")
    public R<List<?>> getCaregivers(
            @ApiParam(value = "婴幼儿ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        // 家长只能查看自己宝宝的保姆，管理员可以查看所有
        Long parentId = isAdmin(userInfo) ? null : userInfo.getUserId();
        List<?> caregivers = babyService.getCaregivers(id, parentId);
        return R.success(caregivers);
    }

    /**
     * 修改婴幼儿头像
     */
    @ApiOperation("修改婴幼儿头像")
    @PutMapping("/{id}/avatar")
    public R<Boolean> updateAvatar(
            @ApiParam(value = "婴幼儿ID", required = true) @PathVariable Long id,
            @ApiParam(value = "头像URL", required = true) @RequestParam String avatarUrl,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        // 家长只能修改自己宝宝的头像
        Long parentId = isAdmin(userInfo) ? null : userInfo.getUserId();
        boolean result = babyService.updateAvatar(id, avatarUrl, parentId);
        return result ? R.success("修改成功", true) : R.fail("修改失败");
    }

    /**
     * 管理员根据家长ID查询婴幼儿列表
     */
    @ApiOperation("管理员根据家长ID查询婴幼儿列表")
    @GetMapping("/admin/parent/{parentId}")
    public R<List<BabyVO>> adminListByParentId(
            @ApiParam(value = "家长ID", required = true) @PathVariable Long parentId) {
        List<BabyVO> list = babyService.adminListByParentId(parentId);
        return R.success(list);
    }

    /**
     * 管理员获取所有婴幼儿列表
     */
    @ApiOperation("管理员获取所有婴幼儿列表")
    @GetMapping("/admin/all")
    public R<List<BabyVO>> adminListAll() {
        List<BabyVO> list = babyService.adminListAll();
        return R.success(list);
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
     * 判断是否为管理员
     */
    private boolean isAdmin(UserInfoVO userInfo) {
        return userInfo.getUserType() != null && userInfo.getUserType() == 4;
    }
}
