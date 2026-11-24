package com.lidexin.business.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 护理记录响应VO
 *
 * @author 张三
 * @since 2025-12-24
 */
@ApiModel(description = "护理记录响应VO")
public class CareRecordVO {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "护理计划ID")
    private Long carePlanId;

    @ApiModelProperty(value = "计划名称")
    private String planName;

    @ApiModelProperty(value = "宝宝ID")
    private Long babyId;

    @ApiModelProperty(value = "宝宝姓名")
    private String babyName;

    @ApiModelProperty(value = "保姆ID")
    private Long nannyId;

    @ApiModelProperty(value = "保姆姓名")
    private String nannyName;

    @ApiModelProperty(value = "记录日期")
    private LocalDate recordDate;

    @ApiModelProperty(value = "记录时间")
    private LocalTime recordTime;

    @ApiModelProperty(value = "护理类型（1-喂养，2-洗护，3-哄睡，4-早教，5-其他）")
    private Byte careType;

    @ApiModelProperty(value = "护理类型名称")
    private String careTypeName;

    @ApiModelProperty(value = "护理内容")
    private String careContent;

    @ApiModelProperty(value = "图片（JSON数组）")
    private String images;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCarePlanId() {
        return carePlanId;
    }

    public void setCarePlanId(Long carePlanId) {
        this.carePlanId = carePlanId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
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

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public LocalTime getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(LocalTime recordTime) {
        this.recordTime = recordTime;
    }

    public Byte getCareType() {
        return careType;
    }

    public void setCareType(Byte careType) {
        this.careType = careType;
    }

    public String getCareTypeName() {
        return careTypeName;
    }

    public void setCareTypeName(String careTypeName) {
        this.careTypeName = careTypeName;
    }

    public String getCareContent() {
        return careContent;
    }

    public void setCareContent(String careContent) {
        this.careContent = careContent;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
