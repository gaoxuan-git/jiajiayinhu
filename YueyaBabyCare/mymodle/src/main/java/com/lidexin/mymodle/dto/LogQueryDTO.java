package com.lidexin.mymodle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 日志查询条件DTO
 *
 * @author 张三
 */
@Data
@ApiModel(value = "LogQueryDTO", description = "日志查询条件")
public class LogQueryDTO {

    @ApiModelProperty(value = "日志文件名", example = "app.log")
    private String fileName;

    @ApiModelProperty(value = "日志级别（ALL, TRACE, DEBUG, INFO, WARN, ERROR）", example = "INFO")
    private String level;

    @ApiModelProperty(value = "关键词搜索", example = "用户登录")
    private String keyword;

    @ApiModelProperty(value = "开始时间（格式：yyyy-MM-dd HH:mm:ss）", example = "2025-11-05 00:00:00")
    private String startTime;

    @ApiModelProperty(value = "结束时间（格式：yyyy-MM-dd HH:mm:ss）", example = "2025-11-05 23:59:59")
    private String endTime;

    @ApiModelProperty(value = "当前页码", example = "1")
    private Integer current = 1;

    @ApiModelProperty(value = "每页大小", example = "50")
    private Integer size = 50;
}

