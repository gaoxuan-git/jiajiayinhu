package com.lidexin.mymodle.codegen;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

public class Mycontor {

    private static final String URL = "jdbc:mysql://localhost:3306/mydb?characterEncoding=utf-8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";
    private static final String PACKAGE_NAME = "com.lidexin.business";
    private static final String AUTHOR_NAME = "张三";
    
    // 自动获取项目路径（推荐）
    private static final String PROJECT_PATH = System.getProperty("user.dir");
    private static final String OUTDIR_JAVA = PROJECT_PATH + "/src/main/java";
    private static final String OUTDIR_XML = PROJECT_PATH + "/src/main/resources/mapper";

    public static void main(String[] args) {
        FastAutoGenerator.create(URL, USERNAME, PASSWORD)
                .globalConfig(builder -> {
                    builder.author(AUTHOR_NAME) // 设置作者
                            // 覆盖已生成文件
                            .outputDir(OUTDIR_JAVA) // 指定输出目录
                            .disableOpenDir();    // 禁止打开输出目录
                })
                .packageConfig(builder -> {
                    builder.parent(PACKAGE_NAME) // 设置包名
                            .entity("po")         //设置实体类包名
                            .pathInfo(Collections.singletonMap(OutputFile.valueOf(""), OUTDIR_XML)); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("") // 设置需要生成的表名
                            .controllerBuilder()   //这里写controllerBuilder，表示将开始controller配置
                            .enableRestStyle();
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}