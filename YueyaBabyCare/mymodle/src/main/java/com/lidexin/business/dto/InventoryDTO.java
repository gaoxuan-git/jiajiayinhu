package com.lidexin.business.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 库存操作DTO
 *
 * @author 张三
 * @since 2025-12-25
 */
@ApiModel(description = "库存操作DTO")
public class InventoryDTO {

    @ApiModelProperty(value = "商品ID", required = true)
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @ApiModelProperty(value = "操作类型（1-入库，2-出库，3-盘点）", required = true)
    @NotNull(message = "操作类型不能为空")
    private Byte operateType;

    @ApiModelProperty(value = "操作数量", required = true)
    @NotNull(message = "操作数量不能为空")
    @Min(value = 1, message = "操作数量必须大于0")
    private Integer quantity;

    @ApiModelProperty(value = "盘点后库存（盘点时使用）")
    private Integer afterStock;

    @ApiModelProperty(value = "备注")
    private String remark;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Byte getOperateType() {
        return operateType;
    }

    public void setOperateType(Byte operateType) {
        this.operateType = operateType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getAfterStock() {
        return afterStock;
    }

    public void setAfterStock(Integer afterStock) {
        this.afterStock = afterStock;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
