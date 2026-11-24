package com.lidexin.business.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 护理评价表
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@TableName("care_evaluation")
public class CareEvaluation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 保姆ID
     */
    private Long nannyId;

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

    /**
     * 删除标志（0-未删除，1-已删除）
     */
    private Byte deleted;

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

    public Long getNannyId() {
        return nannyId;
    }

    public void setNannyId(Long nannyId) {
        this.nannyId = nannyId;
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

    public Byte getDeleted() {
        return deleted;
    }

    public void setDeleted(Byte deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "CareEvaluation{" +
            "id = " + id +
            ", careRecordId = " + careRecordId +
            ", carePlanId = " + carePlanId +
            ", parentId = " + parentId +
            ", nannyId = " + nannyId +
            ", score = " + score +
            ", content = " + content +
            ", images = " + images +
            ", replyContent = " + replyContent +
            ", replyTime = " + replyTime +
            ", createTime = " + createTime +
            ", updateTime = " + updateTime +
            ", deleted = " + deleted +
        "}";
    }
}
