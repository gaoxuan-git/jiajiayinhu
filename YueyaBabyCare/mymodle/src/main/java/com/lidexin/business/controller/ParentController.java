package com.lidexin.business.controller;

import com.lidexin.business.dto.ChangePasswordDTO;
import com.lidexin.business.dto.LoginDTO;
import com.lidexin.business.dto.ParentProfileDTO;
import com.lidexin.business.dto.ParentRegisterDTO;
import com.lidexin.business.service.IParentService;
import com.lidexin.business.vo.LoginVO;
import com.lidexin.business.vo.ParentProfileVO;
import com.lidexin.business.vo.UserInfoVO;
import com.lidexin.mymodle.common.R;
import com.lidexin.mymodle.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;

/**
 * <p>
 * 家长信息管理 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Api(tags = "家长信息管理")
@RestController
@RequestMapping("/api/parent")
public class ParentController {

    @Autowired
    private IParentService parentService;

    @Autowired
    private AuthService authService;

    @Value("${mymodle.jwt.header:Authorization}")
    private String header;

    @Value("${mymodle.jwt.prefix:Bearer }")
    private String prefix;

    /**
     * 家长登录
     */
    @ApiOperation("家长登录")
    @PostMapping("/login")
    public R<LoginVO> login(@Validated @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = parentService.login(loginDTO.getUsername(), loginDTO.getPassword());
        return R.success(loginVO);
    }

    /**
     * 家长登出
     */
    @ApiOperation("家长登出")
    @PostMapping("/logout")
    public R<Void> logout(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (StringUtils.isNotBlank(token)) {
            authService.logout(token);
        }
        return R.success();
    }

    /**
     * 家长注册
     */
    @ApiOperation("家长注册")
    @PostMapping("/register")
    public R<LoginVO> register(@Validated @RequestBody ParentRegisterDTO registerDTO) {
        LoginVO loginVO = parentService.register(registerDTO);
        return R.success(loginVO);
    }

    /**
     * 获取家长个人信息
     */
    @ApiOperation("获取家长个人信息")
    @GetMapping("/profile")
    public R<ParentProfileVO> getProfile(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        ParentProfileVO profile = parentService.getProfile(userId);
        return R.success(profile);
    }

    /**
     * 修改家长个人信息
     */
    @ApiOperation("修改家长个人信息")
    @PutMapping("/profile")
    public R<Void> updateProfile(@RequestBody ParentProfileDTO profileDTO, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        parentService.updateProfile(userId, profileDTO);
        return R.success();
    }

    /**
     * 修改头像
     */
    @ApiOperation("修改头像")
    @PutMapping("/avatar")
    public R<Void> updateAvatar(
            @ApiParam("头像URL") @RequestParam String avatarUrl,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        parentService.updateAvatar(userId, avatarUrl);
        return R.success();
    }

    /**
     * 修改密码
     */
    @ApiOperation("修改密码")
    @PostMapping("/change-password")
    public R<Void> changePassword(@Validated @RequestBody ChangePasswordDTO changePasswordDTO, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        parentService.changePassword(userId, changePasswordDTO);
        return R.success();
    }

    /**
     * 从请求中获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        Object userInfoObj = request.getAttribute("userInfo");
        if (userInfoObj instanceof UserInfoVO) {
            return ((UserInfoVO) userInfoObj).getUserId();
        } else if (userInfoObj instanceof LinkedHashMap) {
            // Redis反序列化可能返回LinkedHashMap
            LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>) userInfoObj;
            Object userId = map.get("userId");
            if (userId instanceof Number) {
                return ((Number) userId).longValue();
            }
        }
        throw new RuntimeException("无法获取当前用户信息");
    }

    /**
     * 从请求中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (StringUtils.isNotBlank(token) && token.startsWith(prefix)) {
            // 兼容 "Bearer  <token>"（多空格）等情况
            return token.substring(prefix.length()).trim();
        }
        return null;
    }
}
