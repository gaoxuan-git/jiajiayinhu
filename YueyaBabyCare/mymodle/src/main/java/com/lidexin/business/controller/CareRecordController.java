package com.lidexin.business.controller;

import com.lidexin.business.dto.CareRecordDTO;
import com.lidexin.business.po.Institution;
import com.lidexin.business.po.Nanny;
import com.lidexin.business.service.ICareRecordService;
import com.lidexin.business.service.IInstitutionService;
import com.lidexin.business.service.INannyService;
import com.lidexin.business.vo.CareRecordVO;
import com.lidexin.mymodle.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 护理记录表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Api(tags = "护理记录管理")
@RestController
@RequestMapping("/api")
public class CareRecordController {

    @Autowired
    private ICareRecordService careRecordService;

    @Autowired
    private INannyService nannyService;

    @Autowired
    private IInstitutionService institutionService;

    @Value("${mymodle.jwt.header:Authorization}")
    private String header;

    @Value("${mymodle.jwt.prefix:Bearer }")
    private String prefix;

    /**
     * 添加护理记录（保姆端）
     */
    @ApiOperation("添加护理记录")
    @PostMapping("/care-plan/{carePlanId}/record")
    public R<Long> addRecord(@PathVariable Long carePlanId,
                             @Validated @RequestBody CareRecordDTO dto,
                             HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Nanny nanny = nannyService.getNannyByToken(token);
        if (nanny == null) {
            return R.fail("请先登录");
        }
        try {
            Long recordId = careRecordService.addCareRecord(carePlanId, nanny.getId(), dto);
            return R.success(recordId);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 获取护理计划的记录列表
     */
    @ApiOperation("获取护理计划的记录列表")
    @GetMapping("/care-plan/{carePlanId}/records")
    public R<List<CareRecordVO>> getRecords(@PathVariable Long carePlanId) {
        List<CareRecordVO> records = careRecordService.getRecordsByCarePlanId(carePlanId);
        return R.success(records);
    }

    /**
     * 获取记录详情
     */
    @ApiOperation("获取护理记录详情")
    @GetMapping("/care-record/{id}")
    public R<CareRecordVO> getRecordDetail(@PathVariable Long id) {
        CareRecordVO record = careRecordService.getRecordById(id);
        if (record == null) {
            return R.fail("记录不存在");
        }
        return R.success(record);
    }

    /**
     * 删除记录（保姆端）
     */
    @ApiOperation("删除护理记录")
    @DeleteMapping("/care-record/{id}")
    public R<Boolean> deleteRecord(@PathVariable Long id, HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Nanny nanny = nannyService.getNannyByToken(token);
        if (nanny == null) {
            return R.fail("请先登录");
        }
        try {
            boolean result = careRecordService.deleteRecord(id, nanny.getId());
            return result ? R.success(true) : R.fail("删除失败");
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 检查是否可以执行计划（保姆端）
     */
    @ApiOperation("检查是否可以执行计划")
    @GetMapping("/care-plan/{carePlanId}/can-execute")
    public R<Boolean> canExecutePlan(@PathVariable Long carePlanId, HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Nanny nanny = nannyService.getNannyByToken(token);
        if (nanny == null) {
            return R.fail("请先登录");
        }
        boolean canExecute = careRecordService.canExecutePlan(carePlanId, nanny.getId());
        return R.success(canExecute);
    }

    /**
     * 修改护理记录（保姆端）
     */
    @ApiOperation("修改护理记录")
    @PutMapping("/care-record/{id}")
    public R<Boolean> updateRecord(@PathVariable Long id,
                                   @Validated @RequestBody CareRecordDTO dto,
                                   HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Nanny nanny = nannyService.getNannyByToken(token);
        if (nanny == null) {
            return R.fail("请先登录");
        }
        try {
            boolean result = careRecordService.updateRecord(id, nanny.getId(), dto);
            return result ? R.success(true) : R.fail("修改失败");
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 获取婴幼儿的护理记录
     */
    @ApiOperation("获取婴幼儿的护理记录")
    @GetMapping("/baby/{babyId}/care-records")
    public R<List<CareRecordVO>> getRecordsByBaby(@PathVariable Long babyId) {
        List<CareRecordVO> records = careRecordService.getRecordsByBabyId(babyId);
        return R.success(records);
    }

    /**
     * 获取保姆今日的护理记录（保姆端）
     */
    @ApiOperation("获取今日护理记录")
    @GetMapping("/care-record/today")
    public R<List<CareRecordVO>> getTodayRecords(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Nanny nanny = nannyService.getNannyByToken(token);
        if (nanny == null) {
            return R.fail("请先登录");
        }
        List<CareRecordVO> records = careRecordService.getTodayRecords(nanny.getId());
        return R.success(records);
    }

    /**
     * 获取机构下的护理记录列表（机构端）
     */
    @ApiOperation("获取机构护理记录列表")
    @GetMapping("/institution/care-records")
    public R<List<CareRecordVO>> getInstitutionRecords(
            @RequestParam(required = false) Long nannyId,
            @RequestParam(required = false) Long babyId,
            @RequestParam(required = false) Byte careType,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        // 从 token 获取当前登录机构
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        Institution institution = institutionService.getInstitutionByToken(token);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        List<CareRecordVO> records = careRecordService.getRecordsByInstitution(institution.getId(), nannyId, babyId, careType);
        return R.success(records);
    }

    /**
     * 从请求中获取 token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (token != null && token.startsWith(prefix)) {
            return token.substring(prefix.length()).trim();
        }
        return null;
    }

}
