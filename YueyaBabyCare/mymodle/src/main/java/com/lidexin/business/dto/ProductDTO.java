package com.lidexin.business.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品请求DTO
 *
 * @author 张三
 * @since 2025-12-24
 */
@ApiModel(description = "商品请求DTO")
public class ProductDTO {

    @ApiModelProperty(value = "分类ID", required = true)
    @NotNull(message = "请选择商品分类")
    private Long categoryId;

    @ApiModelProperty(value = "商品名称", required = true)
    @NotBlank(message = "请输入商品名称")
    private String name;

    @ApiModelProperty(value = "封面图片")
    private String coverImg;

    @ApiModelProperty(value = "商品图片列表")
    private List<String> images;

    @ApiModelProperty(value = "商品描述")
    private String description;

    @ApiModelProperty(value = "销售价格", required = true)
    @NotNull(message = "请输入销售价格")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    @ApiModelProperty(value = "原价")
    private BigDecimal originalPrice;

    @ApiModelProperty(value = "库存数量", required = true)
    @NotNull(message = "请输入库存数量")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "排序（数字越小越靠前）")
    private Integer sort;

    @ApiModelProperty(value = "状态（0-下架，1-上架）")
    private Byte status;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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
