package com.lidexin.mymodle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 代码生成配置DTO
 * 
 * @author 张三
 */
@Data
@ApiModel("代码生成配置")
public class CodeGenConfigDTO {

    @ApiModelProperty(value = "数据库地址", example = "localhost:3306", required = true)
    @NotBlank(message = "数据库地址不能为空")
    private String dbHost;

    @ApiModelProperty(value = "数据库名", example = "mydb", required = true)
    @NotBlank(message = "数据库名不能为空")
    private String dbName;

    @ApiModelProperty(value = "数据库用户名", example = "root", required = true)
    @NotBlank(message = "数据库用户名不能为空")
    private String dbUsername;

    @ApiModelProperty(value = "数据库密码", example = "123456", required = true)
    @NotBlank(message = "数据库密码不能为空")
    private String dbPassword;

    @ApiModelProperty(value = "要生成的表名列表", required = true)
    private List<String> tableNames;

    @ApiModelProperty(value = "作者名", example = "张三")
    private String author;

    @ApiModelProperty(value = "包名", example = "com.lidexin.mymodle")
    private String packageName;

    @ApiModelProperty(value = "是否覆盖已有文件", example = "false")
    private Boolean overwrite = false;
}

