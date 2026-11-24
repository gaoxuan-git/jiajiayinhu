package com.lidexin.business.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 预约请求DTO
 *
 * @author 张三
 * @since 2025-12-23
 */
@ApiModel(description = "预约请求DTO")
public class AppointmentDTO {

    @ApiModelProperty(value = "宝宝ID", required = true)
    @NotNull(message = "请选择宝宝")
    private Long babyId;

    @ApiModelProperty(value = "机构ID", required = true)
    @NotNull(message = "请选择机构")
    private Long institutionId;

    @ApiModelProperty(value = "指定保姆ID（可选）")
    private Long nannyId;

    @ApiModelProperty(value = "服务类型（1-日常护理，2-月嫂服务，3-育儿指导，4-其他）", required = true)
    @NotNull(message = "请选择服务类型")
    private Byte serviceType;

    @ApiModelProperty(value = "预约日期", required = true)
    @NotNull(message = "请选择预约日期")
    private LocalDate appointmentDate;

    @ApiModelProperty(value = "开始时间", required = true)
    @NotNull(message = "请选择开始时间")
    private LocalTime startTime;

    @ApiModelProperty(value = "结束时间", required = true)
    @NotNull(message = "请选择结束时间")
    private LocalTime endTime;

    @ApiModelProperty(value = "服务需求")
    private String requirement;

    @ApiModelProperty(value = "服务地址ID", required = true)
    @NotNull(message = "请选择服务地址")
    private Long addressId;

    public Long getBabyId() {
        return babyId;
    }

    public void setBabyId(Long babyId) {
        this.babyId = babyId;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public Long getNannyId() {
        return nannyId;
    }

    public void setNannyId(Long nannyId) {
        this.nannyId = nannyId;
    }

    public Byte getServiceType() {
        return serviceType;
    }

    public void setServiceType(Byte serviceType) {
        this.serviceType = serviceType;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }
}
