package com.lidexin.mymodle.service;

import com.lidexin.mymodle.dto.CodeGenConfigDTO;

import java.util.List;

/**
 * 代码生成器服务接口
 * 
 * @author 张三
 */
public interface CodeGeneratorService {

    /**
     * 生成代码
     * 
     * @param config 代码生成配置
     * @return 生成结果
     */
    String generateCode(CodeGenConfigDTO config);

    /**
     * 测试数据库连接
     * 
     * @param dbHost 数据库地址
     * @param dbName 数据库名
     * @param dbUsername 用户名
     * @param dbPassword 密码
     * @return 是否连接成功
     */
    boolean testConnection(String dbHost, String dbName, String dbUsername, String dbPassword);

    /**
     * 获取数据库所有表名
     * 
     * @param dbHost 数据库地址
     * @param dbName 数据库名
     * @param dbUsername 用户名
     * @param dbPassword 密码
     * @return 表名列表
     */
    List<String> getTables(String dbHost, String dbName, String dbUsername, String dbPassword);
}

