package com.lidexin.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lidexin.business.po.Institution;
import com.lidexin.business.po.Nanny;
import com.lidexin.business.service.IInstitutionService;
import com.lidexin.business.vo.NannyPerformanceVO;
import com.lidexin.business.vo.UserInfoVO;
import com.lidexin.mymodle.common.R;
import com.lidexin.mymodle.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 机构表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-22
 */
@Slf4j
@Api(tags = "机构管理")
@RestController
@RequestMapping("/api/institution")
public class InstitutionController {

    @Autowired
    private IInstitutionService institutionService;

    @Autowired
    private AuthService authService;

    /**
     * 机构登录
     */
    @ApiOperation("机构登录")
    @PostMapping("/login")
    public R<Map<String, String>> login(@RequestBody Institution institution) {
        String token = institutionService.login(institution.getUsername(), institution.getPassword());
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        return R.success(data);
    }

    /**
     * 机构登出
     */
    @ApiOperation("机构登出")
    @PostMapping("/logout")
    public R<String> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        institutionService.logout(token);
        return R.success("登出成功", null);
    }

    /**
     * 获取当前登录机构的完整信息（从数据库获取最新数据）
     */
    @ApiOperation("获取机构信息")
    @GetMapping("/profile")
    public R<Institution> getProfile(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        // 从数据库获取完整机构信息
        Institution institution = institutionService.getInstitutionByToken(token);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        // 隐藏密码
        institution.setPassword(null);
        return R.success(institution);
    }

    /**
     * 修改机构信息
     */
    @ApiOperation("修改机构信息")
    @PutMapping("/profile")
    public R<String> updateProfile(@RequestBody Institution institution,
                                    @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        Institution current = institutionService.getInstitutionByToken(token);
        if (current == null) {
            return R.fail("未登录或登录已过期");
        }
        
        log.info("更新机构信息，ID: {}, 数据: {}", current.getId(), institution);
        
        // 只能修改自己的信息
        institution.setId(current.getId());
        institutionService.updateProfile(institution);
        return R.success("修改成功", null);
    }

    /**
     * 获取机构下的保姆列表（分页）
     */
    @ApiOperation("获取保姆列表")
    @GetMapping("/nannies")
    public R<Page<Nanny>> getNannyList(
            @ApiParam("审核状态（0-待审核，1-已审核，不传查全部）") @RequestParam(required = false) Byte status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        Institution current = institutionService.getInstitutionByToken(token);
        if (current == null) {
            return R.fail("未登录或登录已过期");
        }
        
        Page<Nanny> page = institutionService.getNannyList(current.getId(), status, pageNum, pageSize);
        return R.success(page);
    }

    /**
     * 审核保姆
     */
    @ApiOperation("审核保姆")
    @PutMapping("/nannies/{nannyId}/audit")
    public R<String> auditNanny(
            @ApiParam("保姆ID") @PathVariable Long nannyId,
            @ApiParam("审核状态（1-通过，2-拒绝）") @RequestParam Byte status,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        Institution current = institutionService.getInstitutionByToken(token);
        if (current == null) {
            return R.fail("未登录或登录已过期");
        }
        
        institutionService.auditNanny(current.getId(), nannyId, status);
        return R.success("审核成功", null);
    }
    
    /**
     * 修改保姆信息（机构专用）
     */
    @ApiOperation("修改保姆信息")
    @PutMapping("/nanny/{nannyId}/info")
    public R<String> updateNannyInfo(
            @ApiParam("保姆ID") @PathVariable Long nannyId,
            @RequestBody Nanny nanny,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        Institution current = institutionService.getInstitutionByToken(token);
        if (current == null) {
            return R.fail("未登录或登录已过期");
        }
        
        institutionService.updateNannyInfo(current.getId(), nannyId, nanny);
        return R.success("修改成功", null);
    }
    
    /**
     * 删除保姆（机构解雇）
     */
    @ApiOperation("删除保姆")
    @DeleteMapping("/nanny/{nannyId}")
    public R<String> deleteNanny(
            @ApiParam("保姆ID") @PathVariable Long nannyId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        Institution current = institutionService.getInstitutionByToken(token);
        if (current == null) {
            return R.fail("未登录或登录已过期");
        }
        
        institutionService.deleteNanny(current.getId(), nannyId);
        return R.success("删除成功", null);
    }
    
    /**
     * 获取保姆绩效数据
     */
    @ApiOperation("获取保姆绩效")
    @GetMapping("/nanny/{nannyId}/performance")
    public R<NannyPerformanceVO> getNannyPerformance(
            @ApiParam("保姆ID") @PathVariable Long nannyId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        Institution current = institutionService.getInstitutionByToken(token);
        if (current == null) {
            return R.fail("未登录或登录已过期");
        }
        
        NannyPerformanceVO performance = institutionService.getNannyPerformance(current.getId(), nannyId);
        return R.success(performance);
    }

    /**
     * 获取指定机构信息（公开接口，无需认证）
     */
    @ApiOperation("获取机构信息")
    @GetMapping("/{id}")
    public R<Institution> getInstitutionById(@ApiParam("机构ID") @PathVariable Long id) {
        Institution institution = institutionService.getById(id);
        if (institution == null || institution.getDeleted() == 1) {
            return R.fail("机构不存在");
        }
        
        // 隐藏敏感信息
        institution.setPassword(null);
        institution.setUsername(null);
        
        return R.success(institution);
    }

    /**
     * 获取机构员工数（保姆数量）
     */
    @ApiOperation("获取机构员工数")
    @GetMapping("/{id}/staff-count")
    public R<Long> getStaffCount(@ApiParam("机构ID") @PathVariable Long id) {
        Long count = institutionService.getStaffCount(id);
        return R.success(count);
    }

    /**
     * 获取机构服务订单数
     */
    @ApiOperation("获取机构服务订单数")
    @GetMapping("/{id}/service-count")
    public R<Long> getServiceCount(@ApiParam("机构ID") @PathVariable Long id) {
        Long count = institutionService.getServiceCount(id);
        return R.success(count);
    }

    /**
     * 获取机构下的保姆列表（公开接口，家长查看）
     */
    @ApiOperation("获取机构保姆列表")
    @GetMapping("/{id}/nannies")
    public R<Page<Nanny>> getInstitutionNannies(
            @ApiParam("机构ID") @PathVariable Long id,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        // 只返回审核通过的保姆
        Page<Nanny> page = institutionService.getNannyList(id, (byte) 1, pageNum, pageSize);
        return R.success(page);
    }

    /**
     * 搜索机构（家长查看）
     */
    @ApiOperation("搜索机构")
    @GetMapping("/search")
    public R<Page<Institution>> searchInstitutions(
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Institution> page = institutionService.searchInstitutions(keyword, pageNum, pageSize);
        return R.success(page);
    }

    // ==================== 管理员接口 ====================

    /**
     * 获取所有机构列表（分页）- 管理员使用
     */
    @ApiOperation("获取机构列表（管理员）")
    @GetMapping("/admin/list")
    public R<Page<Institution>> getInstitutions(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Institution> page = institutionService.getInstitutionPage(pageNum, pageSize);
        return R.success(page);
    }
    /**
     * 获取所有机构列表（不分页）- 管理员使用，用于下拉选择
     */
    @ApiOperation("获取所有机构列表（管理员）")
    @GetMapping("/admin/all")
    public R<List<Institution>> getAllInstitutions() {
        List<Institution> list = institutionService.getAllInstitutions();
        return R.success(list);
    }


    /**
     * 审核机构（修改机构状态）- 管理员使用
     */
    @ApiOperation("审核机构（管理员）")
    @PutMapping("/admin/{id}/status")
    public R<String> auditInstitution(
            @ApiParam("机构ID") @PathVariable Long id,
            @ApiParam("状态（0-禁用，1-正常）") @RequestParam Byte status) {
        
        log.info("管理员审核机构，ID: {}, 状态: {}", id, status);
        
        institutionService.auditInstitution(id, status);
        return R.success("审核成功", null);
    }
}
