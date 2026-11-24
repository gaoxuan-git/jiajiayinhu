package com.lidexin.business.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 签到记录VO
 *
 * @author 张三
 * @since 2025-12-24
 */
@Data
@ApiModel(description = "签到记录VO")
public class CarePlanCheckinVO {

    @ApiModelProperty("签到记录ID")
    private Long id;

    @ApiModelProperty("预约ID")
    private Long appointmentId;

    @ApiModelProperty("预约单号")
    private String appointmentNo;

    @ApiModelProperty("保姆ID")
    private Long nannyId;

    @ApiModelProperty("保姆姓名")
    private String nannyName;

    @ApiModelProperty("签到类型（1-签到，2-签退）")
    private Byte checkinType;

    @ApiModelProperty("签到类型名称")
    private String checkinTypeName;

    @ApiModelProperty("签到时间")
    private LocalDateTime checkinTime;

    @ApiModelProperty("签到经度")
    private BigDecimal checkinLongitude;

    @ApiModelProperty("签到纬度")
    private BigDecimal checkinLatitude;

    @ApiModelProperty("签到地址")
    private String checkinAddress;

    @ApiModelProperty("距离服务地址的距离（米）")
    private Integer distance;

    @ApiModelProperty("是否在范围内（0-否，1-是）")
    private Byte isInRange;

    @ApiModelProperty("是否在范围内名称")
    private String isInRangeName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
