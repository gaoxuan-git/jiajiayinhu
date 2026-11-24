package com.lidexin.mymodle.util;

import cn.hutool.crypto.digest.DigestUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * MD5加密工具类
 * 用于密码加密等场景
 * 
 * @author 张三
 */
public class Md5Util {

    /**
     * 盐值（用于增强密码安全性）
     */
    private static final String SALT = "mymodle_salt_2024";

    /**
     * MD5加密
     * 
     * @param str 待加密字符串
     * @return 加密后的字符串
     */
    public static String encrypt(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return DigestUtil.md5Hex(str);
    }

    /**
     * MD5加密（带盐值）
     * 
     * @param str 待加密字符串
     * @return 加密后的字符串
     */
    public static String encryptWithSalt(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return DigestUtil.md5Hex(str + SALT);
    }

    /**
     * MD5加密（自定义盐值）
     * 
     * @param str 待加密字符串
     * @param salt 盐值
     * @return 加密后的字符串
     */
    public static String encryptWithSalt(String str, String salt) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return DigestUtil.md5Hex(str + (StringUtils.isNotBlank(salt) ? salt : SALT));
    }

    /**
     * 验证密码
     * 
     * @param inputPassword 用户输入的密码
     * @param encryptedPassword 数据库中加密的密码
     * @return 是否匹配
     */
    public static boolean verify(String inputPassword, String encryptedPassword) {
        if (StringUtils.isBlank(inputPassword) || StringUtils.isBlank(encryptedPassword)) {
            return false;
        }
        return encryptedPassword.equals(encryptWithSalt(inputPassword));
    }
}

