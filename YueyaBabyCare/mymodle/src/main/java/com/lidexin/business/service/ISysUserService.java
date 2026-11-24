package com.lidexin.business.service;

import com.lidexin.business.po.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lidexin.mymodle.common.PageRequest;
import com.lidexin.mymodle.common.PageResult;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-11-05
 */
public interface ISysUserService extends IService<SysUser> {
    
    /**
     * 用户登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return JWT token
     */
    String login(String username, String password);
    
    /**
     * 用户注册
     * 
     * @param user 用户信息
     * @return 是否成功
     */
    boolean register(SysUser user);
    
    /**
     * 分页查询用户列表
     * 
     * @param pageRequest 分页参数
     * @param username 用户名（模糊查询，可选）
     * @return 分页结果
     */
    PageResult<SysUser> pageUsers(PageRequest pageRequest, String username);
    
    /**
     * 根据 token 从 Redis 获取用户信息
     * 
     * @param token JWT token
     * @return 用户信息
     */
    SysUser getUserByToken(String token);
    
    /**
     * 用户登出
     * 
     * @param token JWT token
     */
    void logout(String token);
}
