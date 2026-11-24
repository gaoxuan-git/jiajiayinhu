package com.lidexin.mymodle.util;

/**
 * 密码生成工具
 * 用于生成数据库初始化密码
 * 
 * @author 张三
 */
public class PasswordGenerator {

    public static void main(String[] args) {
        // 生成密码
        String password = "123456";
        String encrypted = Md5Util.encryptWithSalt(password);
        
        System.out.println("====================================");
        System.out.println("密码加密工具");
        System.out.println("====================================");
        System.out.println("原始密码: " + password);
        System.out.println("加密密码: " + encrypted);
        System.out.println("====================================");
        System.out.println();
        System.out.println("SQL 示例:");
        System.out.println("INSERT INTO sys_user (username, password, ...) VALUES");
        System.out.println("('admin', '" + encrypted + "', ...);");
        System.out.println("====================================");
    }
}

