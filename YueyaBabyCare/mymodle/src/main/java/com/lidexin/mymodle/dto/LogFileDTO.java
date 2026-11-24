package com.lidexin.mymodle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 日志文件信息DTO
 *
 * @author 张三
 */
@Data
@ApiModel(value = "LogFileDTO", description = "日志文件信息")
public class LogFileDTO {

    @ApiModelProperty(value = "文件名", example = "app.log")
    private String fileName;

    @ApiModelProperty(value = "文件路径", example = "/logs/app.log")
    private String filePath;

    @ApiModelProperty(value = "文件大小（字节）", example = "1048576")
    private Long fileSize;

    @ApiModelProperty(value = "文件大小（格式化）", example = "1.00 MB")
    private String fileSizeFormatted;

    @ApiModelProperty(value = "最后修改时间", example = "2025-11-05 10:20:30")
    private String lastModified;

    @ApiModelProperty(value = "是否可读", example = "true")
    private Boolean readable;
}

