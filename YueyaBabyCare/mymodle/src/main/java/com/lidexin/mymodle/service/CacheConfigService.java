package com.lidexin.mymodle.service;

import com.lidexin.mymodle.dto.CacheConfigDTO;

import java.util.List;

/**
 * 缓存配置管理服务
 * 
 * @author 张三
 */
public interface CacheConfigService {
    
    /**
     * 获取所有缓存配置
     */
    List<CacheConfigDTO> getAllCacheConfigs();
    
    /**
     * 获取指定缓存配置
     */
    CacheConfigDTO getCacheConfig(String cacheName);
    
    /**
     * 更新缓存配置
     */
    void updateCacheConfig(CacheConfigDTO config);
    
    /**
     * 清空指定缓存
     */
    void clearCache(String cacheName);
    
    /**
     * 清空所有缓存
     */
    void clearAllCaches();
    
    /**
     * 获取缓存统计信息
     */
    CacheConfigDTO getCacheStats(String cacheName);
    
    /**
     * 获取 Redis 信息
     */
    String getRedisInfo();
}

