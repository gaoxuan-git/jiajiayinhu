package com.lidexin.business.vo;

import java.time.LocalDateTime;

/**
 * 护理评价VO
 *
 * @author 张三
 */
public class CareEvaluationVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 护理记录ID
     */
    private Long careRecordId;

    /**
     * 护理计划ID
     */
    private Long carePlanId;

    /**
     * 家长ID
     */
    private Long parentId;

    /**
     * 家长姓名
     */
    private String parentName;

    /**
     * 家长头像
     */
    private String parentAvatar;

    /**
     * 保姆ID
     */
    private Long nannyId;

    /**
     * 保姆姓名
     */
    private String nannyName;

    /**
     * 保姆头像
     */
    private String nannyAvatar;

    /**
     * 宝宝ID
     */
    private Long babyId;

    /**
     * 宝宝姓名
     */
    private String babyName;

    /**
     * 评分（1-5分）
     */
    private Byte score;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 图片（JSON数组）
     */
    private String images;

    /**
     * 回复内容
     */
    private String replyContent;

    /**
     * 回复时间
     */
    private LocalDateTime replyTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCareRecordId() {
        return careRecordId;
    }

    public void setCareRecordId(Long careRecordId) {
        this.careRecordId = careRecordId;
    }

    public Long getCarePlanId() {
        return carePlanId;
    }

    public void setCarePlanId(Long carePlanId) {
        this.carePlanId = carePlanId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentAvatar() {
        return parentAvatar;
    }

    public void setParentAvatar(String parentAvatar) {
        this.parentAvatar = parentAvatar;
    }

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

    public String getNannyAvatar() {
        return nannyAvatar;
    }

    public void setNannyAvatar(String nannyAvatar) {
        this.nannyAvatar = nannyAvatar;
    }

    public Long getBabyId() {
        return babyId;
    }

    public void setBabyId(Long babyId) {
        this.babyId = babyId;
    }

    public String getBabyName() {
        return babyName;
    }

    public void setBabyName(String babyName) {
        this.babyName = babyName;
    }

    public Byte getScore() {
        return score;
    }

    public void setScore(Byte score) {
        this.score = score;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public LocalDateTime getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(LocalDateTime replyTime) {
        this.replyTime = replyTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
