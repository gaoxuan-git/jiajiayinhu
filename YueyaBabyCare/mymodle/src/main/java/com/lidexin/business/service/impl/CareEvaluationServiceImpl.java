package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lidexin.business.dto.CareEvaluationDTO;
import com.lidexin.business.mapper.CareEvaluationMapper;
import com.lidexin.business.po.*;
import com.lidexin.business.service.*;
import com.lidexin.business.vo.CareEvaluationVO;
import com.lidexin.business.vo.RatingStatsVO;
import com.lidexin.mymodle.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 护理评价表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Service
public class CareEvaluationServiceImpl extends ServiceImpl<CareEvaluationMapper, CareEvaluation> implements ICareEvaluationService {

    @Autowired
    private ICarePlanService carePlanService;

    @Autowired
    private IBabyService babyService;

    @Autowired
    private IParentService parentService;

    @Autowired
    private INannyService nannyService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addEvaluation(Long parentId, CareEvaluationDTO dto) {
        // 1. 查询护理计划
        CarePlan carePlan = carePlanService.getById(dto.getCarePlanId());
        if (carePlan == null || carePlan.getDeleted() == 1) {
            throw new BusinessException("护理计划不存在");
        }

        // 2. 校验家长是否有权评价（宝宝属于该家长）
        Baby baby = babyService.getById(carePlan.getBabyId());
        if (baby == null || !parentId.equals(baby.getParentId())) {
            throw new BusinessException("您无权评价该护理计划");
        }

        // 3. 检查是否已评价
        if (hasEvaluated(dto.getCarePlanId(), parentId)) {
            throw new BusinessException("您已经评价过该护理计划");
        }

        // 4. 创建评价
        CareEvaluation evaluation = new CareEvaluation();
        evaluation.setCarePlanId(dto.getCarePlanId());
        evaluation.setCareRecordId(dto.getCareRecordId());
        evaluation.setParentId(parentId);
        evaluation.setNannyId(carePlan.getNannyId());
        evaluation.setScore(dto.getScore());
        evaluation.setContent(dto.getContent());
        evaluation.setImages(dto.getImages());
        evaluation.setCreateTime(LocalDateTime.now());
        evaluation.setDeleted((byte) 0);

        this.save(evaluation);
        return evaluation.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateEvaluation(Long id, Long parentId, CareEvaluationDTO dto) {
        // 1. 查询评价
        CareEvaluation evaluation = this.getById(id);
        if (evaluation == null || evaluation.getDeleted() == 1) {
            throw new BusinessException("评价不存在");
        }

        // 2. 校验是否是自己的评价
        if (!parentId.equals(evaluation.getParentId())) {
            throw new BusinessException("您无权修改该评价");
        }

        // 3. 已被回复的评价不能修改
        if (evaluation.getReplyContent() != null && !evaluation.getReplyContent().isEmpty()) {
            throw new BusinessException("已被回复的评价不能修改");
        }

        // 4. 更新评价
        evaluation.setScore(dto.getScore());
        evaluation.setContent(dto.getContent());
        evaluation.setImages(dto.getImages());
        evaluation.setUpdateTime(LocalDateTime.now());

        return this.updateById(evaluation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteEvaluation(Long id, Long parentId) {
        // 1. 查询评价
        CareEvaluation evaluation = this.getById(id);
        if (evaluation == null || evaluation.getDeleted() == 1) {
            throw new BusinessException("评价不存在");
        }

        // 2. 校验是否是自己的评价
        if (!parentId.equals(evaluation.getParentId())) {
            throw new BusinessException("您无权删除该评价");
        }

        // 3. 软删除
        evaluation.setDeleted((byte) 1);
        evaluation.setUpdateTime(LocalDateTime.now());
        return this.updateById(evaluation);
    }

    @Override
    public CareEvaluationVO getEvaluationById(Long id) {
        CareEvaluation evaluation = this.getById(id);
        if (evaluation == null || evaluation.getDeleted() == 1) {
            return null;
        }
        return convertToVO(evaluation);
    }

    @Override
    public List<CareEvaluationVO> getEvaluationsByNannyId(Long nannyId) {
        List<CareEvaluation> evaluations = this.list(new LambdaQueryWrapper<CareEvaluation>()
                .eq(CareEvaluation::getNannyId, nannyId)
                .eq(CareEvaluation::getDeleted, 0)
                .orderByDesc(CareEvaluation::getCreateTime));

        return evaluations.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public RatingStatsVO getRatingStatsByNannyId(Long nannyId) {
        List<CareEvaluation> evaluations = this.list(new LambdaQueryWrapper<CareEvaluation>()
                .eq(CareEvaluation::getNannyId, nannyId)
                .eq(CareEvaluation::getDeleted, 0));

        RatingStatsVO stats = new RatingStatsVO();
        stats.setNannyId(nannyId);

        // 获取保姆信息
        Nanny nanny = nannyService.getById(nannyId);
        if (nanny != null) {
            stats.setNannyName(nanny.getRealName());
        }

        int total = evaluations.size();
        stats.setTotalCount(total);

        if (total == 0) {
            stats.setAvgScore(BigDecimal.ZERO);
            stats.setFiveStarCount(0);
            stats.setFourStarCount(0);
            stats.setThreeStarCount(0);
            stats.setTwoStarCount(0);
            stats.setOneStarCount(0);
            stats.setPositiveRate(BigDecimal.ZERO);
            return stats;
        }

        // 统计各星级数量
        int fiveStar = 0, fourStar = 0, threeStar = 0, twoStar = 0, oneStar = 0;
        int totalScore = 0;

        for (CareEvaluation e : evaluations) {
            Byte score = e.getScore();
            if (score == null) continue;
            totalScore += score;
            switch (score) {
                case 5: fiveStar++; break;
                case 4: fourStar++; break;
                case 3: threeStar++; break;
                case 2: twoStar++; break;
                case 1: oneStar++; break;
            }
        }

        stats.setFiveStarCount(fiveStar);
        stats.setFourStarCount(fourStar);
        stats.setThreeStarCount(threeStar);
        stats.setTwoStarCount(twoStar);
        stats.setOneStarCount(oneStar);

        // 计算平均分
        BigDecimal avg = BigDecimal.valueOf(totalScore)
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
        stats.setAvgScore(avg);

        // 计算好评率（4-5分）
        int positiveCount = fiveStar + fourStar;
        BigDecimal positiveRate = BigDecimal.valueOf(positiveCount * 100)
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
        stats.setPositiveRate(positiveRate);

        return stats;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean replyEvaluation(Long id, Long nannyId, String replyContent) {
        // 1. 查询评价
        CareEvaluation evaluation = this.getById(id);
        if (evaluation == null || evaluation.getDeleted() == 1) {
            throw new BusinessException("评价不存在");
        }

        // 2. 校验是否是针对自己的评价
        if (!nannyId.equals(evaluation.getNannyId())) {
            throw new BusinessException("您无权回复该评价");
        }

        // 3. 检查是否已回复
        if (evaluation.getReplyContent() != null && !evaluation.getReplyContent().isEmpty()) {
            throw new BusinessException("该评价已经回复过");
        }

        // 4. 更新回复
        evaluation.setReplyContent(replyContent);
        evaluation.setReplyTime(LocalDateTime.now());
        evaluation.setUpdateTime(LocalDateTime.now());

        return this.updateById(evaluation);
    }

    @Override
    public List<CareEvaluationVO> getEvaluationsByInstitution(Long institutionId, Long nannyId, Byte score) {
        // 先查询机构下的所有护理计划
        List<CarePlan> plans = carePlanService.list(new LambdaQueryWrapper<CarePlan>()
                .eq(CarePlan::getInstitutionId, institutionId)
                .eq(CarePlan::getDeleted, 0));

        if (plans.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> planIds = plans.stream()
                .map(CarePlan::getId)
                .collect(Collectors.toList());

        // 查询评价
        LambdaQueryWrapper<CareEvaluation> wrapper = new LambdaQueryWrapper<CareEvaluation>()
                .in(CareEvaluation::getCarePlanId, planIds)
                .eq(CareEvaluation::getDeleted, 0);

        if (nannyId != null) {
            wrapper.eq(CareEvaluation::getNannyId, nannyId);
        }
        if (score != null) {
            wrapper.eq(CareEvaluation::getScore, score);
        }

        wrapper.orderByDesc(CareEvaluation::getCreateTime);

        List<CareEvaluation> evaluations = this.list(wrapper);
        return evaluations.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CareEvaluationVO> getEvaluationsByParentId(Long parentId) {
        List<CareEvaluation> evaluations = this.list(new LambdaQueryWrapper<CareEvaluation>()
                .eq(CareEvaluation::getParentId, parentId)
                .eq(CareEvaluation::getDeleted, 0)
                .orderByDesc(CareEvaluation::getCreateTime));

        return evaluations.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasEvaluated(Long carePlanId, Long parentId) {
        return this.count(new LambdaQueryWrapper<CareEvaluation>()
                .eq(CareEvaluation::getCarePlanId, carePlanId)
                .eq(CareEvaluation::getParentId, parentId)
                .eq(CareEvaluation::getDeleted, 0)) > 0;
    }

    /**
     * 转换为VO
     */
    private CareEvaluationVO convertToVO(CareEvaluation evaluation) {
        CareEvaluationVO vo = new CareEvaluationVO();
        vo.setId(evaluation.getId());
        vo.setCareRecordId(evaluation.getCareRecordId());
        vo.setCarePlanId(evaluation.getCarePlanId());
        vo.setParentId(evaluation.getParentId());
        vo.setNannyId(evaluation.getNannyId());
        vo.setScore(evaluation.getScore());
        vo.setContent(evaluation.getContent());
        vo.setImages(evaluation.getImages());
        vo.setReplyContent(evaluation.getReplyContent());
        vo.setReplyTime(evaluation.getReplyTime());
        vo.setCreateTime(evaluation.getCreateTime());
        vo.setUpdateTime(evaluation.getUpdateTime());

        // 填充家长信息
        if (evaluation.getParentId() != null) {
            Parent parent = parentService.getById(evaluation.getParentId());
            if (parent != null) {
                vo.setParentName(parent.getRealName());
                vo.setParentAvatar(parent.getAvatar());
            }
        }

        // 填充保姆信息
        if (evaluation.getNannyId() != null) {
            Nanny nanny = nannyService.getById(evaluation.getNannyId());
            if (nanny != null) {
                vo.setNannyName(nanny.getRealName());
                vo.setNannyAvatar(nanny.getAvatar());
            }
        }

        // 填充宝宝信息
        if (evaluation.getCarePlanId() != null) {
            CarePlan carePlan = carePlanService.getById(evaluation.getCarePlanId());
            if (carePlan != null && carePlan.getBabyId() != null) {
                Baby baby = babyService.getById(carePlan.getBabyId());
                if (baby != null) {
                    vo.setBabyId(baby.getId());
                    vo.setBabyName(baby.getName());
                }
            }
        }

        return vo;
    }
}
