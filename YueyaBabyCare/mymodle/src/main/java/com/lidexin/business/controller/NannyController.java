package com.lidexin.business.controller;

import com.lidexin.business.po.Nanny;
import com.lidexin.business.service.INannyService;
import com.lidexin.mymodle.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 保姆表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Slf4j
@Api(tags = "保姆管理")
@RestController
@RequestMapping("/api/nanny")
public class NannyController {

    @Autowired
    private INannyService nannyService;

    /**
     * 保姆注册
     */
    @ApiOperation("保姆注册")
    @PostMapping("/register")
    public R<String> register(@RequestBody Nanny nanny) {
        nannyService.register(nanny);
        return R.success("注册成功", null);
    }

    /**
     * 保姆登录
     */
    @ApiOperation("保姆登录")
    @PostMapping("/login")
    public R<Map<String, String>> login(@RequestBody Nanny nanny) {
        String token = nannyService.login(nanny.getUsername(), nanny.getPassword());
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        return R.success(data);
    }

    /**
     * 保姆登出
     */
    @ApiOperation("保姆登出")
    @PostMapping("/logout")
    public R<String> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        nannyService.logout(token);
        return R.success("登出成功", null);
    }

    /**
     * 获取当前登录保姆信息（从数据库获取最新数据）
     */
    @ApiOperation("获取保姆信息")
    @GetMapping("/profile")
    public R<Nanny> getProfile(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        Nanny nannyFromCache = nannyService.getNannyByToken(token);
        if (nannyFromCache == null) {
            return R.fail("未登录或登录已过期");
        }
        
        // 从数据库获取最新的完整信息（包括证书等字段）
        Nanny nanny = nannyService.getById(nannyFromCache.getId());
        if (nanny == null) {
            return R.fail("用户信息不存在");
        }
        
        // 隐藏敏感信息
        nanny.setPassword(null);
        
        return R.success(nanny);
    }

    /**
     * 修改保姆信息
     */
    @ApiOperation("修改保姆信息")
    @PutMapping("/profile")
    public R<String> updateProfile(@RequestBody Nanny nanny,
                                    @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        Nanny current = nannyService.getNannyByToken(token);
        if (current == null) {
            return R.fail("未登录或登录已过期");
        }
        
        log.info("更新保姆信息，ID: {}, 数据: {}", current.getId(), nanny);
        // 只能修改自己的信息
        nanny.setId(current.getId());
        nannyService.updateProfile(nanny);
        return R.success("修改成功", null);
    }

    /**
     * 上传资格证书
     */
    @ApiOperation("上传资格证书")
    @PostMapping("/certificate")
    public R<String> addCertificate(@RequestBody Map<String, String> params,
                                     @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        Nanny nanny = nannyService.getNannyByToken(token);
        if (nanny == null) {
            return R.fail("未登录或登录已过期");
        }
        String certificateUrl = params.get("certificateUrl");
        nannyService.addCertificate(nanny.getId(), certificateUrl);
        return R.success("上传成功", null);
    }

    /**
     * 获取证书列表
     */
    @ApiOperation("获取证书列表")
    @GetMapping("/certificates")
    public R<List<String>> getCertificates(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        Nanny nanny = nannyService.getNannyByToken(token);
        if (nanny == null) {
            return R.fail("未登录或登录已过期");
        }
        List<String> certificates = nannyService.getCertificates(nanny.getId());
        return R.success(certificates);
    }

    /**
     * 删除证书
     */
    @ApiOperation("删除证书")
    @DeleteMapping("/certificate")
    public R<String> deleteCertificate(@RequestBody Map<String, String> params,
                                        @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        Nanny nanny = nannyService.getNannyByToken(token);
        if (nanny == null) {
            return R.fail("未登录或登录已过期");
        }
        String certificateUrl = params.get("certificateUrl");
        nannyService.deleteCertificate(nanny.getId(), certificateUrl);
        return R.success("删除成功", null);
    }

    /**
     * 获取保姆详情（家长查看）
     */
    @ApiOperation("获取保姆详情")
    @GetMapping("/{id}")
    public R<Nanny> getNannyById(@PathVariable Long id) {
        Nanny nanny = nannyService.getById(id);
        if (nanny == null) {
            return R.fail("保姆不存在");
        }
        // 隐藏敏感信息
        nanny.setPassword(null);
        nanny.setIdCard(null);
        return R.success(nanny);
    }

    /**
     * 搜索保姆（家长查看）
     */
    @ApiOperation("搜索保姆")
    @GetMapping("/search")
    public R<List<Nanny>> searchNannies(@RequestParam(required = false) Long institutionId,
                                         @RequestParam(required = false) String keyword) {
        List<Nanny> nannies = nannyService.searchNannies(institutionId, keyword);
        return R.success(nannies);
    }
}
