package com.lidexin.mymodle.service;

/**
 * 认证服务接口
 * 提供基于 JWT + Redis 的用户认证功能
 * 
 * @author 张三
 */
public interface AuthService {
    
    /**
     * 用户登录 - 生成 token 并存储用户信息到 Redis
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param userInfo 用户信息对象（会存储到 Redis）
     * @return JWT token
     */
    String login(Long userId, String username, Object userInfo);
    
    /**
     * 用户登出 - 从 Redis 删除用户信息
     * 
     * @param token JWT token
     */
    void logout(String token);
    
    /**
     * 根据 token 从 Redis 获取用户信息
     * 
     * @param token JWT token
     * @param clazz 用户信息类型
     * @return 用户信息对象
     */
    <T> T getUserByToken(String token, Class<T> clazz);
    
    /**
     * 刷新 token 过期时间
     * 
     * @param token JWT token
     * @return 是否刷新成功
     */
    boolean refreshToken(String token);
    
    /**
     * 验证 token 是否有效（存在于 Redis 中）
     * 
     * @param token JWT token
     * @return 是否有效
     */
    boolean validateToken(String token);
}

