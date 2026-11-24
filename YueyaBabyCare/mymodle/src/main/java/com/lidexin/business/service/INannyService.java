package com.lidexin.business.service;

import com.lidexin.business.po.Nanny;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 保姆表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
public interface INannyService extends IService<Nanny> {
    
    /**
     * 保姆注册
     * 
     * @param nanny 保姆信息
     * @return 是否成功
     */
    boolean register(Nanny nanny);
    
    /**
     * 保姆登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return JWT token
     */
    String login(String username, String password);
    
    /**
     * 根据 token 从 Redis 获取保姆信息
     * 
     * @param token JWT token
     * @return 保姆信息
     */
    Nanny getNannyByToken(String token);
    
    /**
     * 保姆登出
     * 
     * @param token JWT token
     */
    void logout(String token);
    
    /**
     * 更新保姆信息（不包括密码）
     * 
     * @param nanny 保姆信息
     * @return 是否成功
     */
    boolean updateProfile(Nanny nanny);
    
    /**
     * 添加证书
     * 
     * @param nannyId 保姆ID
     * @param certificateUrl 证书URL
     * @return 是否成功
     */
    boolean addCertificate(Long nannyId, String certificateUrl);
    
    /**
     * 获取证书列表
     * 
     * @param nannyId 保姆ID
     * @return 证书列表
     */
    List<String> getCertificates(Long nannyId);
    
    /**
     * 删除证书
     * 
     * @param nannyId 保姆ID
     * @param certificateUrl 证书URL
     * @return 是否成功
     */
    boolean deleteCertificate(Long nannyId, String certificateUrl);
    
    /**
     * 搜索保姆（供家长查看）
     * 
     * @param institutionId 机构ID（可选）
     * @param keyword 关键词（姓名、简介）
     * @return 保姆列表
     */
    List<Nanny> searchNannies(Long institutionId, String keyword);
}
