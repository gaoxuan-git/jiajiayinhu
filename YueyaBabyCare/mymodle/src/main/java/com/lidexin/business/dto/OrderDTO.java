package com.lidexin.business.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 创建订单请求DTO
 *
 * @author 张三
 * @since 2025-12-24
 */
@ApiModel(description = "创建订单请求参数")
public class OrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "购物车ID列表（从购物车结算时使用）")
    private List<Long> cartIds;

    @ApiModelProperty(value = "收货人姓名", required = true)
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    @ApiModelProperty(value = "收货人电话", required = true)
    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;

    @ApiModelProperty(value = "收货地址", required = true)
    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;

    @ApiModelProperty(value = "订单备注")
    private String remark;

    // Getters and Setters
    public List<Long> getCartIds() {
        return cartIds;
    }

    public void setCartIds(List<Long> cartIds) {
        this.cartIds = cartIds;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
