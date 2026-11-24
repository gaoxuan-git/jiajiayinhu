package com.lidexin.business.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lidexin.business.po.Institution;
import com.lidexin.business.po.Nanny;
import com.lidexin.business.po.Parent;
import com.lidexin.business.po.SysUser;
import com.lidexin.business.service.IInstitutionService;
import com.lidexin.business.service.INannyService;
import com.lidexin.business.service.IParentService;
import com.lidexin.business.service.ISysUserService;
import com.lidexin.business.vo.UserInfoVO;
import com.lidexin.mymodle.common.PageRequest;
import com.lidexin.mymodle.common.PageResult;
import com.lidexin.mymodle.common.R;
import com.lidexin.mymodle.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-11-05
 */
@Slf4j
@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/admin")
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private IInstitutionService institutionService;

    @Autowired
    private IParentService parentService;

    @Autowired
    private INannyService nannyService;

    /**
     * 用户登录
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public R<Map<String, String>> login(@RequestBody SysUser user) {
        String token = userService.login(user.getUsername(), user.getPassword());
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        return R.success(data);
    }

    /**
     * 用户登出
     */
    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public R<String> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        // 去掉 "Bearer " 前缀，允许 authorization 为空
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        userService.logout(token);
        return R.success("登出成功", null);
    }
    
    /**
     * 获取当前登录用户信息（从 Redis）
     */
    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public R<UserInfoVO> getUserInfo(@RequestHeader(value = "Authorization", required = false) String authorization) {
        // 去掉 "Bearer " 前缀，允许 authorization 为空
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        // 从 Redis 获取 UserInfoVO（包含 userType 等信息）
        UserInfoVO userInfo = authService.getUserByToken(token, UserInfoVO.class);
        if (userInfo == null) {
            return R.fail("用户未登录或登录已过期");
        }
        return R.success(userInfo);
    }

    /**
     * 用户注册
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public R<String> register(@RequestBody SysUser user) {
        userService.register(user);
        return R.success("注册成功", null);
    }

    /**
     * 分页查询用户列表
     */
    @ApiOperation("分页查询用户列表")
    @GetMapping("/page")
    public R<PageResult<SysUser>> page(PageRequest pageRequest, String username) {
        PageResult<SysUser> result = userService.pageUsers(pageRequest, username);
        return R.success(result);
    }

    /**
     * 根据ID查询用户
     */
    @ApiOperation("根据ID查询用户")
    @GetMapping("/{id}")
    public R<SysUser> getById(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        if (user != null) {
            user.setPassword(null); // 隐藏密码
        }
        return R.success(user);
    }

    /**
     * 新增用户
     */
    @ApiOperation("新增用户")
    @PostMapping
    public R<String> save(@RequestBody SysUser user) {
        userService.save(user);
        return R.success("保存成功", null);
    }

    /**
     * 更新用户
     */
    @ApiOperation("更新用户")
    @PutMapping
    public R<String> update(@RequestBody SysUser user) {
        userService.updateById(user);
        return R.success("更新成功", null);
    }

    /**
     * 删除用户
     */
    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        userService.removeById(id);
        return R.success("删除成功", null);
    }

    // ==================== 家长管理 ====================

    /**
     * 分页查询家长列表
     */
    @ApiOperation("获取家长列表")
    @GetMapping("/manage/parents")
    public R<Page<Parent>> getParents(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword) {
        
        Page<Parent> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Parent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Parent::getDeleted, 0);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w
                .like(Parent::getUsername, keyword)
                .or().like(Parent::getRealName, keyword)
                .or().like(Parent::getPhone, keyword)
            );
        }
        
        wrapper.orderByDesc(Parent::getCreateTime);
        Page<Parent> result = parentService.page(page, wrapper);
        
        // 隐藏密码
        result.getRecords().forEach(parent -> parent.setPassword(null));
        
        return R.success(result);
    }

    /**
     * 根据ID查询家长详情
     */
    @ApiOperation("查询家长详情")
    @GetMapping("/manage/parents/{id}")
    public R<Parent> getParentById(@ApiParam("家长ID") @PathVariable Long id) {
        Parent parent = parentService.getById(id);
        if (parent == null) {
            return R.fail("家长不存在");
        }
        
        // 隐藏密码
        parent.setPassword(null);
        
        return R.success(parent);
    }

    /**
     * 更新家长状态
     */
    @ApiOperation("更新家长状态")
    @PutMapping("/manage/parents/{id}/status")
    public R<String> updateParentStatus(
            @ApiParam("家长ID") @PathVariable Long id,
            @ApiParam("状态") @RequestParam Byte status) {
        
        Parent parent = parentService.getById(id);
        if (parent == null) {
            return R.fail("家长不存在");
        }
        
        parent.setStatus(status);
        parentService.updateById(parent);
        
        log.info("更新家长状态，ID: {}, 状态: {}", id, status);
        return R.success("更新成功", null);
    }

    /**
     * 删除家长
     */
    @ApiOperation("删除家长")
    @DeleteMapping("/manage/parents/{id}")
    public R<String> deleteParent(@ApiParam("家长ID") @PathVariable Long id) {
        Parent parent = parentService.getById(id);
        if (parent == null) {
            return R.fail("家长不存在");
        }

        // 使用 removeById 执行逻辑删除（MyBatis-Plus 会自动处理 @TableLogic 字段）
        parentService.removeById(id);

        log.info("删除家长，ID: {}", id);
        return R.success("删除成功", null);
    }

    // ==================== 机构管理 ====================

    /**
     * 分页查询机构列表
     */
    @ApiOperation("获取机构列表")
    @GetMapping("/manage/institutions")
    public R<Page<Institution>> getInstitutions(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword) {
        
        Page<Institution> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Institution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Institution::getDeleted, 0);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w
                .like(Institution::getUsername, keyword)
                .or().like(Institution::getName, keyword)
                .or().like(Institution::getContactPhone, keyword)
            );
        }
        
        wrapper.orderByDesc(Institution::getCreateTime);
        Page<Institution> result = institutionService.page(page, wrapper);
        
        // 隐藏密码
        result.getRecords().forEach(institution -> institution.setPassword(null));
        
        return R.success(result);
    }

    /**
     * 根据ID查询机构详情
     */
    @ApiOperation("查询机构详情")
    @GetMapping("/manage/institutions/{id}")
    public R<Institution> getInstitutionById(@ApiParam("机构ID") @PathVariable Long id) {
        Institution institution = institutionService.getById(id);
        if (institution == null) {
            return R.fail("机构不存在");
        }
        
        // 隐藏密码
        institution.setPassword(null);
        
        return R.success(institution);
    }

    /**
     * 更新机构状态
     */
    @ApiOperation("更新机构状态")
    @PutMapping("/manage/institutions/{id}/status")
    public R<String> updateInstitutionStatus(
            @ApiParam("机构ID") @PathVariable Long id,
            @ApiParam("状态") @RequestParam Byte status) {
        
        institutionService.auditInstitution(id, status);
        
        log.info("更新机构状态，ID: {}, 状态: {}", id, status);
        return R.success("更新成功", null);
    }

    /**
     * 删除机构
     */
    @ApiOperation("删除机构")
    @DeleteMapping("/manage/institutions/{id}")
    public R<String> deleteInstitution(@ApiParam("机构ID") @PathVariable Long id) {
        Institution institution = institutionService.getById(id);
        if (institution == null) {
            return R.fail("机构不存在");
        }

        // 使用 removeById 执行逻辑删除（MyBatis-Plus 会自动处理 @TableLogic 字段）
        institutionService.removeById(id);

        log.info("删除机构，ID: {}", id);
        return R.success("删除成功", null);
    }

    // ==================== 保姆管理 ====================

    /**
     * 分页查询保姆列表
     */
    @ApiOperation("获取保姆列表")
    @GetMapping("/manage/nannies")
    public R<Page<Nanny>> getNannies(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword,
            @ApiParam("机构ID") @RequestParam(required = false) Long institutionId) {
        
        Page<Nanny> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Nanny> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Nanny::getDeleted, 0);
        
        if (institutionId != null) {
            wrapper.eq(Nanny::getInstitutionId, institutionId);
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w
                .like(Nanny::getUsername, keyword)
                .or().like(Nanny::getRealName, keyword)
                .or().like(Nanny::getPhone, keyword)
            );
        }
        
        wrapper.orderByDesc(Nanny::getCreateTime);
        Page<Nanny> result = nannyService.page(page, wrapper);
        
        // 隐藏密码
        result.getRecords().forEach(nanny -> nanny.setPassword(null));
        
        return R.success(result);
    }

    /**
     * 根据ID查询保姆详情
     */
    @ApiOperation("查询保姆详情")
    @GetMapping("/manage/nannies/{id}")
    public R<Nanny> getNannyById(@ApiParam("保姆ID") @PathVariable Long id) {
        Nanny nanny = nannyService.getById(id);
        if (nanny == null) {
            return R.fail("保姆不存在");
        }
        
        // 隐藏密码
        nanny.setPassword(null);
        
        return R.success(nanny);
    }

    /**
     * 更新保姆状态
     */
    @ApiOperation("更新保姆状态")
    @PutMapping("/manage/nannies/{id}/status")
    public R<String> updateNannyStatus(
            @ApiParam("保姆ID") @PathVariable Long id,
            @ApiParam("状态") @RequestParam Byte status) {
        
        Nanny nanny = nannyService.getById(id);
        if (nanny == null) {
            return R.fail("保姆不存在");
        }
        
        nanny.setStatus(status);
        nannyService.updateById(nanny);
        
        log.info("更新保姆状态，ID: {}, 状态: {}", id, status);
        return R.success("更新成功", null);
    }

    /**
     * 删除保姆
     */
    @ApiOperation("删除保姆")
    @DeleteMapping("/manage/nannies/{id}")
    public R<String> deleteNanny(@ApiParam("保姆ID") @PathVariable Long id) {
        Nanny nanny = nannyService.getById(id);
        if (nanny == null) {
            return R.fail("保姆不存在");
        }

        // 使用 removeById 执行逻辑删除（MyBatis-Plus 会自动处理 @TableLogic 字段）
        nannyService.removeById(id);

        log.info("删除保姆，ID: {}", id);
        return R.success("删除成功", null);
    }
}
