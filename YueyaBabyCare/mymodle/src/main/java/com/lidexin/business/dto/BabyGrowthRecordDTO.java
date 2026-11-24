package com.lidexin.business.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * 婴幼儿成长记录请求DTO
 *
 * @author 张三
 * @since 2025-12-23
 */
@ApiModel(description = "婴幼儿成长记录请求参数")
public class BabyGrowthRecordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录ID（修改时必填）")
    private Long id;

    @ApiModelProperty(value = "记录日期", required = true)
    @NotNull(message = "记录日期不能为空")
    private LocalDate recordDate;

    @ApiModelProperty(value = "记录类型（1-饮食，2-睡眠，3-排便，4-活动，5-其他）", required = true)
    @NotNull(message = "记录类型不能为空")
    private Byte recordType;

    @ApiModelProperty(value = "标题", required = true)
    @NotBlank(message = "标题不能为空")
    private String title;

    @ApiModelProperty(value = "记录内容")
    private String content;

    @ApiModelProperty(value = "图片列表（URL数组）")
    private List<String> images;

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

    public Byte getRecordType() {
        return recordType;
    }

    public void setRecordType(Byte recordType) {
        this.recordType = recordType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
