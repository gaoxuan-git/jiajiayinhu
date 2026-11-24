package com.lidexin.mymodle.controller;

import com.lidexin.mymodle.common.R;
import com.lidexin.mymodle.dto.CodeGenConfigDTO;
import com.lidexin.mymodle.service.CodeGeneratorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 代码生成器控制器
 * 
 * @author 张三
 */
@Slf4j
@RestController
@RequestMapping("/api/codegen")
@Api(tags = "代码生成器管理")
public class CodeGeneratorController {

    @Autowired
    private CodeGeneratorService codeGeneratorService;

    @PostMapping("/generate")
    @ApiOperation("生成代码")
    public R<String> generateCode(@Validated @RequestBody CodeGenConfigDTO config) {
        log.info("收到代码生成请求: {}", config);
        String result = codeGeneratorService.generateCode(config);
        return R.success(result);
    }

    @GetMapping("/test-connection")
    @ApiOperation("测试数据库连接")
    public R<Boolean> testConnection(
            @ApiParam("数据库地址") @RequestParam String dbHost,
            @ApiParam("数据库名") @RequestParam String dbName,
            @ApiParam("用户名") @RequestParam String dbUsername,
            @ApiParam("密码") @RequestParam String dbPassword) {
        boolean connected = codeGeneratorService.testConnection(dbHost, dbName, dbUsername, dbPassword);
        if (connected) {
            return R.success("数据库连接成功", true);
        } else {
            return R.fail("数据库连接失败，请检查配置");
        }
    }

    @GetMapping("/tables")
    @ApiOperation("获取数据库表列表")
    public R<List<String>> getTables(
            @ApiParam("数据库地址") @RequestParam String dbHost,
            @ApiParam("数据库名") @RequestParam String dbName,
            @ApiParam("用户名") @RequestParam String dbUsername,
            @ApiParam("密码") @RequestParam String dbPassword) {
        List<String> tables = codeGeneratorService.getTables(dbHost, dbName, dbUsername, dbPassword);
        return R.success(tables);
    }
}

