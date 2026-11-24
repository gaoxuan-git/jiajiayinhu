package com.lidexin.business.controller;

import com.lidexin.business.dto.CareEvaluationDTO;
import com.lidexin.business.po.Institution;
import com.lidexin.business.po.Nanny;
import com.lidexin.business.po.Parent;
import com.lidexin.business.service.ICareEvaluationService;
import com.lidexin.business.service.IInstitutionService;
import com.lidexin.business.service.INannyService;
import com.lidexin.business.service.IParentService;
import com.lidexin.business.vo.CareEvaluationVO;
import com.lidexin.business.vo.RatingStatsVO;
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
 * 护理评价表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Api(tags = "护理评价管理")
@RestController
@RequestMapping("/api")
public class CareEvaluationController {

    @Autowired
    private ICareEvaluationService careEvaluationService;

    @Autowired
    private IParentService parentService;

    @Autowired
    private INannyService nannyService;

    @Autowired
    private IInstitutionService institutionService;

    @Value("${mymodle.jwt.header:Authorization}")
    private String header;

    @Value("${mymodle.jwt.prefix:Bearer }")
    private String prefix;

    /**
     * 新增评价（家长端）
     */
    @ApiOperation("新增护理评价")
    @PostMapping("/care-evaluation")
    public R<Long> addEvaluation(@Validated @RequestBody CareEvaluationDTO dto,
                                 HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Parent parent = parentService.getParentByToken(token);
        if (parent == null) {
            return R.fail("请先登录");
        }
        try {
            Long id = careEvaluationService.addEvaluation(parent.getId(), dto);
            return R.success(id);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 修改评价（家长端）
     */
    @ApiOperation("修改护理评价")
    @PutMapping("/care-evaluation/{id}")
    public R<Boolean> updateEvaluation(@PathVariable Long id,
                                       @Validated @RequestBody CareEvaluationDTO dto,
                                       HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Parent parent = parentService.getParentByToken(token);
        if (parent == null) {
            return R.fail("请先登录");
        }
        try {
            boolean result = careEvaluationService.updateEvaluation(id, parent.getId(), dto);
            return result ? R.success(true) : R.fail("修改失败");
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 删除评价（家长端）
     */
    @ApiOperation("删除护理评价")
    @DeleteMapping("/care-evaluation/{id}")
    public R<Boolean> deleteEvaluation(@PathVariable Long id, HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Parent parent = parentService.getParentByToken(token);
        if (parent == null) {
            return R.fail("请先登录");
        }
        try {
            boolean result = careEvaluationService.deleteEvaluation(id, parent.getId());
            return result ? R.success(true) : R.fail("删除失败");
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 获取评价详情
     */
    @ApiOperation("获取评价详情")
    @GetMapping("/care-evaluation/{id}")
    public R<CareEvaluationVO> getEvaluationDetail(@PathVariable Long id) {
        CareEvaluationVO vo = careEvaluationService.getEvaluationById(id);
        if (vo == null) {
            return R.fail("评价不存在");
        }
        return R.success(vo);
    }

    /**
     * 获取保姆的评价列表
     */
    @ApiOperation("获取保姆的评价列表")
    @GetMapping("/nanny/{nannyId}/evaluations")
    public R<List<CareEvaluationVO>> getNannyEvaluations(@PathVariable Long nannyId) {
        List<CareEvaluationVO> list = careEvaluationService.getEvaluationsByNannyId(nannyId);
        return R.success(list);
    }

    /**
     * 获取保姆的评分统计
     */
    @ApiOperation("获取保姆的评分统计")
    @GetMapping("/nanny/{nannyId}/rating-stats")
    public R<RatingStatsVO> getNannyRatingStats(@PathVariable Long nannyId) {
        RatingStatsVO stats = careEvaluationService.getRatingStatsByNannyId(nannyId);
        return R.success(stats);
    }

    /**
     * 回复评价（保姆端）
     */
    @ApiOperation("回复评价")
    @PostMapping("/care-evaluation/{id}/reply")
    public R<Boolean> replyEvaluation(@PathVariable Long id,
                                      @RequestBody String replyContent,
                                      HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Nanny nanny = nannyService.getNannyByToken(token);
        if (nanny == null) {
            return R.fail("请先登录");
        }
        try {
            boolean result = careEvaluationService.replyEvaluation(id, nanny.getId(), replyContent);
            return result ? R.success(true) : R.fail("回复失败");
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 获取机构的评价列表（机构端）
     */
    @ApiOperation("获取机构的评价列表")
    @GetMapping("/institution/evaluations")
    public R<List<CareEvaluationVO>> getInstitutionEvaluations(
            @RequestParam(required = false) Long nannyId,
            @RequestParam(required = false) Byte score,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = "";
        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.replace("Bearer ", "").trim();
        }
        Institution institution = institutionService.getInstitutionByToken(token);
        if (institution == null) {
            return R.fail("未登录或登录已过期");
        }
        List<CareEvaluationVO> list = careEvaluationService.getEvaluationsByInstitution(
                institution.getId(), nannyId, score);
        return R.success(list);
    }

    /**
     * 获取家长的评价列表（家长端）
     */
    @ApiOperation("获取我的评价列表")
    @GetMapping("/care-evaluation/my-list")
    public R<List<CareEvaluationVO>> getMyEvaluations(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Parent parent = parentService.getParentByToken(token);
        if (parent == null) {
            return R.fail("请先登录");
        }
        List<CareEvaluationVO> list = careEvaluationService.getEvaluationsByParentId(parent.getId());
        return R.success(list);
    }

    /**
     * 保姆获取自己的评价列表（保姆端）
     */
    @ApiOperation("保姆获取我的评价列表")
    @GetMapping("/care-evaluation/nanny-list")
    public R<List<CareEvaluationVO>> getNannyMyEvaluations(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Nanny nanny = nannyService.getNannyByToken(token);
        if (nanny == null) {
            return R.fail("请先登录");
        }
        List<CareEvaluationVO> list = careEvaluationService.getEvaluationsByNannyId(nanny.getId());
        return R.success(list);
    }

    /**
     * 保姆获取自己的评分统计（保姆端）
     */
    @ApiOperation("保姆获取我的评分统计")
    @GetMapping("/care-evaluation/nanny-stats")
    public R<RatingStatsVO> getNannyMyStats(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Nanny nanny = nannyService.getNannyByToken(token);
        if (nanny == null) {
            return R.fail("请先登录");
        }
        RatingStatsVO stats = careEvaluationService.getRatingStatsByNannyId(nanny.getId());
        return R.success(stats);
    }

    /**
     * 检查护理计划是否已评价（家长端）
     */
    @ApiOperation("检查护理计划是否已评价")
    @GetMapping("/care-evaluation/check/{carePlanId}")
    public R<Boolean> checkEvaluated(@PathVariable Long carePlanId, HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Parent parent = parentService.getParentByToken(token);
        if (parent == null) {
            return R.fail("请先登录");
        }
        boolean hasEvaluated = careEvaluationService.hasEvaluated(carePlanId, parent.getId());
        return R.success(hasEvaluated);
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
