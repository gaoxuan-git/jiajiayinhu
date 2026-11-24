package com.lidexin.mymodle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 日志信息DTO
 *
 * @author 张三
 */
@Data
@ApiModel(value = "LogInfoDTO", description = "日志信息")
public class LogInfoDTO {

    @ApiModelProperty(value = "日志ID（行号）")
    private Long id;

    @ApiModelProperty(value = "时间戳", example = "2025-11-05 10:20:30.123")
    private String timestamp;

    @ApiModelProperty(value = "日志级别", example = "INFO")
    private String level;

    @ApiModelProperty(value = "线程名", example = "http-nio-8080-exec-1")
    private String thread;

    @ApiModelProperty(value = "日志来源（类名）", example = "com.lidexin.mymodle.controller.UserController")
    private String logger;

    @ApiModelProperty(value = "日志内容", example = "用户登录成功: admin")
    private String message;

    @ApiModelProperty(value = "完整日志（原始行）")
    private String rawLog;
}

