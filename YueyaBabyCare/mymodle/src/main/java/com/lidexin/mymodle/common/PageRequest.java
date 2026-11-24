package com.lidexin.mymodle.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 分页请求参数基类
 * 所有分页查询的请求参数可继承此类
 * 
 * @author 张三
 */
@Data
@ApiModel(description = "分页请求参数")
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码（默认第1页）
     */
    @ApiModelProperty(value = "当前页码", example = "1")
    @Min(value = 1, message = "页码最小值为1")
    private Long current = 1L;

    /**
     * 每页显示条数（默认10条）
     */
    @ApiModelProperty(value = "每页显示条数", example = "10")
    @Min(value = 1, message = "每页显示条数最小值为1")
    private Long size = 10L;

    /**
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段", example = "create_time")
    private String sortField;

    /**
     * 排序方式（asc-升序，desc-降序）
     */
    @ApiModelProperty(value = "排序方式", example = "desc")
    private String sortOrder = "desc";
}

