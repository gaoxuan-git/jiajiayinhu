package com.lidexin.business.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 婴幼儿健康记录响应VO
 *
 * @author 张三
 * @since 2025-12-23
 */
@ApiModel(description = "婴幼儿健康记录信息")
public class BabyHealthRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录ID")
    private Long id;

    @ApiModelProperty(value = "宝宝ID")
    private Long babyId;

    @ApiModelProperty(value = "宝宝姓名")
    private String babyName;

    @ApiModelProperty(value = "记录日期")
    private LocalDate recordDate;

    @ApiModelProperty(value = "身高（cm）")
    private BigDecimal height;

    @ApiModelProperty(value = "体重（kg）")
    private BigDecimal weight;

    @ApiModelProperty(value = "头围（cm）")
    private BigDecimal headCircumference;

    @ApiModelProperty(value = "体温（℃）")
    private BigDecimal temperature;

    @ApiModelProperty(value = "健康状况")
    private String healthStatus;

    @ApiModelProperty(value = "疫苗接种情况")
    private String vaccination;

    @ApiModelProperty(value = "疾病记录")
    private String illnessRecord;

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

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getHeadCircumference() {
        return headCircumference;
    }

    public void setHeadCircumference(BigDecimal headCircumference) {
        this.headCircumference = headCircumference;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getVaccination() {
        return vaccination;
    }

    public void setVaccination(String vaccination) {
        this.vaccination = vaccination;
    }

    public String getIllnessRecord() {
        return illnessRecord;
    }

    public void setIllnessRecord(String illnessRecord) {
        this.illnessRecord = illnessRecord;
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
