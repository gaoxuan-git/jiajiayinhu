package com.lidexin.mymodle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传结果DTO
 * 
 * @author 张三
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "文件上传结果")
public class FileUploadResult {

    @ApiModelProperty(value = "文件名（原始文件名）")
    private String fileName;

    @ApiModelProperty(value = "文件访问URL")
    private String fileUrl;

    @ApiModelProperty(value = "文件大小（字节）")
    private Long fileSize;

    @ApiModelProperty(value = "文件类型")
    private String fileType;

    @ApiModelProperty(value = "上传时间")
    private String uploadTime;
}
