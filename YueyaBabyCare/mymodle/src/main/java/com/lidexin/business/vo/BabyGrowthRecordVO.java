package com.lidexin.business.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 婴幼儿成长记录响应VO
 *
 * @author 张三
 * @since 2025-12-23
 */
@ApiModel(description = "婴幼儿成长记录信息")
public class BabyGrowthRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录ID")
    private Long id;

    @ApiModelProperty(value = "宝宝ID")
    private Long babyId;

    @ApiModelProperty(value = "宝宝姓名")
    private String babyName;

    @ApiModelProperty(value = "记录日期")
    private LocalDate recordDate;

    @ApiModelProperty(value = "记录类型（1-饮食，2-睡眠，3-排便，4-活动，5-其他）")
    private Byte recordType;

    @ApiModelProperty(value = "记录类型名称")
    private String recordTypeName;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "记录内容")
    private String content;

    @ApiModelProperty(value = "图片列表")
    private List<String> images;

    @ApiModelProperty(value = "记录人类型（1-家长，2-保姆）")
    private Byte recorderType;

    @ApiModelProperty(value = "记录人类型名称")
    private String recorderTypeName;

    @ApiModelProperty(value = "记录人ID")
    private Long recorderId;

    @ApiModelProperty(value = "记录人姓名")
    private String recorderName;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

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

    public String getBabyName() {
        return babyName;
    }

    public void setBabyName(String babyName) {
        this.babyName = babyName;
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

    public String getRecordTypeName() {
        return recordTypeName;
    }

    public void setRecordTypeName(String recordTypeName) {
        this.recordTypeName = recordTypeName;
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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Byte getRecorderType() {
        return recorderType;
    }

    public void setRecorderType(Byte recorderType) {
        this.recorderType = recorderType;
    }

    public String getRecorderTypeName() {
        return recorderTypeName;
    }

    public void setRecorderTypeName(String recorderTypeName) {
        this.recorderTypeName = recorderTypeName;
    }

    public Long getRecorderId() {
        return recorderId;
    }

    public void setRecorderId(Long recorderId) {
        this.recorderId = recorderId;
    }

    public String getRecorderName() {
        return recorderName;
    }

    public void setRecorderName(String recorderName) {
        this.recorderName = recorderName;
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
