package com.lidexin.business.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 婴幼儿健康记录请求DTO
 *
 * @author 张三
 * @since 2025-12-23
 */
@ApiModel(description = "婴幼儿健康记录请求参数")
public class BabyHealthRecordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录ID（修改时必填）")
    private Long id;

    @ApiModelProperty(value = "记录日期", required = true)
    @NotNull(message = "记录日期不能为空")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
