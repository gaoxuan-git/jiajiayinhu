package com.lidexin.business.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 护理记录请求DTO
 *
 * @author 张三
 * @since 2025-12-24
 */
@ApiModel(description = "护理记录请求DTO")
public class CareRecordDTO {

    @ApiModelProperty(value = "护理类型（1-喂养，2-洗护，3-哄睡，4-早教，5-其他）", required = true)
    @NotNull(message = "护理类型不能为空")
    private Byte careType;

    @ApiModelProperty(value = "护理内容", required = true)
    @NotBlank(message = "护理内容不能为空")
    private String careContent;

    @ApiModelProperty(value = "图片（JSON数组）")
    private String images;

    @ApiModelProperty(value = "备注")
    private String remark;

    public Byte getCareType() {
        return careType;
    }

    public void setCareType(Byte careType) {
        this.careType = careType;
    }

    public String getCareContent() {
        return careContent;
    }

    public void setCareContent(String careContent) {
        this.careContent = careContent;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
