package com.lidexin.business.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 签到请求DTO
 *
 * @author 张三
 * @since 2025-12-24
 */
@Data
@ApiModel(description = "签到请求DTO")
public class CheckinDTO {

    @ApiModelProperty(value = "当前经度", required = true)
    @NotNull(message = "请获取当前位置经度")
    private BigDecimal longitude;

    @ApiModelProperty(value = "当前纬度", required = true)
    @NotNull(message = "请获取当前位置纬度")
    private BigDecimal latitude;

    @ApiModelProperty(value = "当前地址（可选）")
    private String address;

    @ApiModelProperty(value = "备注（可选）")
    private String remark;
}
