package com.lidexin.business.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 护理计划请求DTO
 *
 * @author 张三
 * @since 2025-12-23
 */
@ApiModel(description = "护理计划请求DTO")
public class CarePlanDTO {

    @ApiModelProperty(value = "关联预约ID", required = true)
    @NotNull(message = "请选择预约单")
    private Long appointmentId;

    @ApiModelProperty(value = "宝宝ID（可选，默认从预约获取）")
    private Long babyId;

    @ApiModelProperty(value = "指定保姆ID（可选，默认从预约获取）")
    private Long nannyId;

    @ApiModelProperty(value = "计划名称", required = true)
    @NotBlank(message = "请输入计划名称")
    private String planName;

    @ApiModelProperty(value = "计划内容", required = true)
    @NotBlank(message = "请输入计划内容")
    private String planContent;

    @ApiModelProperty(value = "开始日期", required = true)
    @NotNull(message = "请选择开始日期")
    private LocalDate startDate;

    @ApiModelProperty(value = "结束日期", required = true)
    @NotNull(message = "请选择结束日期")
    private LocalDate endDate;

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getBabyId() {
        return babyId;
    }

    public void setBabyId(Long babyId) {
        this.babyId = babyId;
    }

    public Long getNannyId() {
        return nannyId;
    }

    public void setNannyId(Long nannyId) {
        this.nannyId = nannyId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanContent() {
        return planContent;
    }

    public void setPlanContent(String planContent) {
        this.planContent = planContent;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
