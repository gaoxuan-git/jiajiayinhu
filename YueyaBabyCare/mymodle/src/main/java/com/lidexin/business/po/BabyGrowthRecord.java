package com.lidexin.business.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 婴幼儿成长记录表
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@TableName("baby_growth_record")
public class BabyGrowthRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 宝宝ID
     */
    private Long babyId;

    /**
     * 记录日期
     */
    private LocalDate recordDate;

    /**
     * 记录类型（1-饮食，2-睡眠，3-排便，4-活动，5-其他）
     */
    private Byte recordType;

    /**
     * 标题
     */
    private String title;

    /**
     * 记录内容
     */
    private String content;

    /**
     * 图片（JSON数组）
     */
    private String images;

    /**
     * 记录人类型（1-家长，2-保姆）
     */
    private Byte recorderType;

    /**
     * 记录人ID
     */
    private Long recorderId;

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

    public Long getBabyId() {
        return babyId;
    }

    public void setBabyId(Long babyId) {
        this.babyId = babyId;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public Byte getRecordType() {
        return recordType;
    }

    public void setRecordType(Byte recordType) {
        this.recordType = recordType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Byte getRecorderType() {
        return recorderType;
    }

    public void setRecorderType(Byte recorderType) {
        this.recorderType = recorderType;
    }

    public Long getRecorderId() {
        return recorderId;
    }

    public void setRecorderId(Long recorderId) {
        this.recorderId = recorderId;
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
        return "BabyGrowthRecord{" +
            "id = " + id +
            ", babyId = " + babyId +
            ", recordDate = " + recordDate +
            ", recordType = " + recordType +
            ", title = " + title +
            ", content = " + content +
            ", images = " + images +
            ", recorderType = " + recorderType +
            ", recorderId = " + recorderId +
            ", createTime = " + createTime +
            ", updateTime = " + updateTime +
            ", deleted = " + deleted +
        "}";
    }
}
