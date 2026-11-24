package com.lidexin.business.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * 商品分类请求DTO
 *
 * @author 张三
 * @since 2025-12-24
 */
@ApiModel(description = "商品分类请求DTO")
public class ProductCategoryDTO {

    @ApiModelProperty(value = "父分类ID（0表示顶级分类）")
    private Long parentId;

    @ApiModelProperty(value = "分类名称", required = true)
    @NotBlank(message = "请输入分类名称")
    private String name;

    @ApiModelProperty(value = "分类图标")
    private String icon;

    @ApiModelProperty(value = "排序（数字越小越靠前）")
    private Integer sort;

    @ApiModelProperty(value = "状态（0-禁用，1-正常）")
    private Byte status;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}
