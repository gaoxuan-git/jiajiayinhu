package com.lidexin.business.vo;

import java.math.BigDecimal;

/**
 * 评分统计VO
 *
 * @author 张三
 */
public class RatingStatsVO {

    /**
     * 保姆ID
     */
    private Long nannyId;

    /**
     * 保姆姓名
     */
    private String nannyName;

    /**
     * 总评价数
     */
    private Integer totalCount;

    /**
     * 平均评分
     */
    private BigDecimal avgScore;

    /**
     * 5分评价数
     */
    private Integer fiveStarCount;

    /**
     * 4分评价数
     */
    private Integer fourStarCount;

    /**
     * 3分评价数
     */
    private Integer threeStarCount;

    /**
     * 2分评价数
     */
    private Integer twoStarCount;

    /**
     * 1分评价数
     */
    private Integer oneStarCount;

    /**
     * 好评率（4-5分占比）
     */
    private BigDecimal positiveRate;

    // Getters and Setters

    public Long getNannyId() {
        return nannyId;
    }

    public void setNannyId(Long nannyId) {
        this.nannyId = nannyId;
    }

    public String getNannyName() {
        return nannyName;
    }

    public void setNannyName(String nannyName) {
        this.nannyName = nannyName;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(BigDecimal avgScore) {
        this.avgScore = avgScore;
    }

    public Integer getFiveStarCount() {
        return fiveStarCount;
    }

    public void setFiveStarCount(Integer fiveStarCount) {
        this.fiveStarCount = fiveStarCount;
    }

    public Integer getFourStarCount() {
        return fourStarCount;
    }

    public void setFourStarCount(Integer fourStarCount) {
        this.fourStarCount = fourStarCount;
    }

    public Integer getThreeStarCount() {
        return threeStarCount;
    }

    public void setThreeStarCount(Integer threeStarCount) {
        this.threeStarCount = threeStarCount;
    }

    public Integer getTwoStarCount() {
        return twoStarCount;
    }

    public void setTwoStarCount(Integer twoStarCount) {
        this.twoStarCount = twoStarCount;
    }

    public Integer getOneStarCount() {
        return oneStarCount;
    }

    public void setOneStarCount(Integer oneStarCount) {
        this.oneStarCount = oneStarCount;
    }

    public BigDecimal getPositiveRate() {
        return positiveRate;
    }

    public void setPositiveRate(BigDecimal positiveRate) {
        this.positiveRate = positiveRate;
    }
}
