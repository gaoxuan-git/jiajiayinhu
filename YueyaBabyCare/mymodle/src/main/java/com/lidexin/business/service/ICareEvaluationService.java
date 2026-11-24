package com.lidexin.business.service;

import com.lidexin.business.dto.CareEvaluationDTO;
import com.lidexin.business.po.CareEvaluation;
import com.lidexin.business.vo.CareEvaluationVO;
import com.lidexin.business.vo.RatingStatsVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 护理评价表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
public interface ICareEvaluationService extends IService<CareEvaluation> {

    /**
     * 新增评价（家长）
     *
     * @param parentId 家长ID
     * @param dto 评价信息
     * @return 评价ID
     */
    Long addEvaluation(Long parentId, CareEvaluationDTO dto);

    /**
     * 修改评价（家长）
     *
     * @param id 评价ID
     * @param parentId 家长ID
     * @param dto 评价信息
     * @return 是否成功
     */
    boolean updateEvaluation(Long id, Long parentId, CareEvaluationDTO dto);

    /**
     * 删除评价（家长）
     *
     * @param id 评价ID
     * @param parentId 家长ID
     * @return 是否成功
     */
    boolean deleteEvaluation(Long id, Long parentId);

    /**
     * 获取评价详情
     *
     * @param id 评价ID
     * @return 评价详情
     */
    CareEvaluationVO getEvaluationById(Long id);

    /**
     * 获取保姆的评价列表
     *
     * @param nannyId 保姆ID
     * @return 评价列表
     */
    List<CareEvaluationVO> getEvaluationsByNannyId(Long nannyId);

    /**
     * 获取保姆的评分统计
     *
     * @param nannyId 保姆ID
     * @return 评分统计
     */
    RatingStatsVO getRatingStatsByNannyId(Long nannyId);

    /**
     * 回复评价（保姆）
     *
     * @param id 评价ID
     * @param nannyId 保姆ID
     * @param replyContent 回复内容
     * @return 是否成功
     */
    boolean replyEvaluation(Long id, Long nannyId, String replyContent);

    /**
     * 获取机构的评价列表
     *
     * @param institutionId 机构ID
     * @param nannyId 保姆ID（可选筛选）
     * @param score 评分（可选筛选）
     * @return 评价列表
     */
    List<CareEvaluationVO> getEvaluationsByInstitution(Long institutionId, Long nannyId, Byte score);

    /**
     * 获取家长的评价列表
     *
     * @param parentId 家长ID
     * @return 评价列表
     */
    List<CareEvaluationVO> getEvaluationsByParentId(Long parentId);

    /**
     * 检查护理计划是否已评价
     *
     * @param carePlanId 护理计划ID
     * @param parentId 家长ID
     * @return 是否已评价
     */
    boolean hasEvaluated(Long carePlanId, Long parentId);
}
