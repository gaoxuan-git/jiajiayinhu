package com.lidexin.business.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 保姆绩效VO
 *
 * @author 张三
 * @since 2025-12-25
 */
@Data
public class NannyPerformanceVO {
    
    /**
     * 保姆ID
     */
    private Long nannyId;
    
    /**
     * 保姆姓名
     */
    private String nannyName;
    
    /**
     * 服务家庭数
     */
    private Long serviceFamilyCount;
    
    /**
     * 完成服务计划数
     */
    private Long completedPlanCount;
    
    /**
     * 进行中服务计划数
     */
    private Long ongoingPlanCount;
    
    /**
     * 总评价数
     */
    private Long totalEvaluations;
    
    /**
     * 平均评分
     */
    private BigDecimal averageScore;
    
    /**
     * 好评率（4-5星）
     */
    private BigDecimal positiveRate;
    
    /**
     * 5星评价数
     */
    private Long fiveStarCount;
    
    /**
     * 4星评价数
     */
    private Long fourStarCount;
    
    /**
     * 3星评价数
     */
    private Long threeStarCount;
    
    /**
     * 2星评价数
     */
    private Long twoStarCount;
    
    /**
     * 1星评价数
     */
    private Long oneStarCount;
}
