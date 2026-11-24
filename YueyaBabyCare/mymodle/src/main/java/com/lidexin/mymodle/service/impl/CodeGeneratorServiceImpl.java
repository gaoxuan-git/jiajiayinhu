package com.lidexin.mymodle.service.impl;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.lidexin.mymodle.dto.CodeGenConfigDTO;
import com.lidexin.mymodle.exception.BusinessException;
import com.lidexin.mymodle.service.CodeGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 代码生成器服务实现类
 * 
 * @author 张三
 */
@Slf4j
@Service
public class CodeGeneratorServiceImpl implements CodeGeneratorService {

    private static final String DEFAULT_AUTHOR = "张三";
    private static final String DEFAULT_PACKAGE = "com.lidexin.business";

    @Override
    public String generateCode(CodeGenConfigDTO config) {
        try {
            // 参数校验
            validateConfig(config);

            // 构建数据库连接URL
            String url = buildJdbcUrl(config.getDbHost(), config.getDbName());
            
            // 获取项目路径
            String projectPath = System.getProperty("user.dir");
            String outputDirJava = projectPath + "/src/main/java";
            String outputDirXml = projectPath + "/src/main/resources/mapper";

            // 设置默认值
            String author = StringUtils.isNotBlank(config.getAuthor()) ? config.getAuthor() : DEFAULT_AUTHOR;
            String packageName = StringUtils.isNotBlank(config.getPackageName()) ? config.getPackageName() : DEFAULT_PACKAGE;

            log.info("开始生成代码: 数据库={}, 表={}, 作者={}, 包名={}", 
                    config.getDbName(), config.getTableNames(), author, packageName);

            // 执行代码生成
            FastAutoGenerator.create(url, config.getDbUsername(), config.getDbPassword())
                    .globalConfig(builder -> {
                        builder.author(author)
                                .outputDir(outputDirJava)
                                .disableOpenDir()
                                .fileOverride(); // 根据配置决定是否覆盖
                    })
                    .packageConfig(builder -> {
                        builder.parent(packageName)
                                .entity("po")
                                .pathInfo(Collections.singletonMap(OutputFile.valueOf("xml"), outputDirXml));
                    })
                    .strategyConfig(builder -> {
                        builder.addInclude(config.getTableNames())
                                .controllerBuilder()
                                .enableRestStyle();
                    })
                    .templateEngine(new FreemarkerTemplateEngine())
                    .execute();

            log.info("代码生成成功: {}", config.getTableNames());
            return "代码生成成功！表：" + String.join(", ", config.getTableNames());

        } catch (Exception e) {
            log.error("代码生成失败", e);
            throw new BusinessException("代码生成失败：" + e.getMessage());
        }
    }

    @Override
    public boolean testConnection(String dbHost, String dbName, String dbUsername, String dbPassword) {
        String url = buildJdbcUrl(dbHost, dbName);
        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            log.error("数据库连接测试失败", e);
            return false;
        }
    }

    @Override
    public List<String> getTables(String dbHost, String dbName, String dbUsername, String dbPassword) {
        List<String> tables = new ArrayList<>();
        String url = buildJdbcUrl(dbHost, dbName);
        
        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(dbName, null, "%", new String[]{"TABLE"});
            
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                tables.add(tableName);
            }
            
            log.info("获取到 {} 个表", tables.size());
            return tables;
            
        } catch (SQLException e) {
            log.error("获取表列表失败", e);
            throw new BusinessException("获取表列表失败：" + e.getMessage());
        }
    }

    /**
     * 构建JDBC连接URL
     */
    private String buildJdbcUrl(String dbHost, String dbName) {
        return String.format("jdbc:mysql://%s/%s?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true",
                dbHost, dbName);
    }

    /**
     * 校验配置参数
     */
    private void validateConfig(CodeGenConfigDTO config) {
        if (config == null) {
            throw new BusinessException("配置信息不能为空");
        }
        if (config.getTableNames() == null || config.getTableNames().isEmpty()) {
            throw new BusinessException("请至少选择一个表");
        }
    }
}

