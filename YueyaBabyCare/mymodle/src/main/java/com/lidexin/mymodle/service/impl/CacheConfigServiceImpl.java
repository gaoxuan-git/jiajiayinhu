package com.lidexin.mymodle.service.impl;

import com.lidexin.mymodle.dto.CacheConfigDTO;
import com.lidexin.mymodle.service.CacheConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 缓存配置管理服务实现
 * 
 * @author 张三
 */
@Slf4j
@Service
public class CacheConfigServiceImpl implements CacheConfigService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired(required = false)
    private CacheManager cacheManager;
    
    private static final String CACHE_CONFIG_PREFIX = "cache:config:";
    private static final Long DEFAULT_TTL = 3600L; // 默认1小时
    
    @Override
    @Cacheable(value = "systemCache", key = "'allCacheConfigs'")
    public List<CacheConfigDTO> getAllCacheConfigs() {
        log.info("查询所有缓存配置...");
        List<CacheConfigDTO> configs = new ArrayList<>();
        Set<String> cacheNames = new HashSet<>();
        
        // 方法1：从 CacheManager 获取已注册的缓存
        if (cacheManager != null) {
            cacheNames.addAll(cacheManager.getCacheNames());
        }
        
        // 方法2：从 Redis 扫描实际存在的缓存
        try {
            Set<String> redisCacheNames = scanCacheNamesFromRedis();
            cacheNames.addAll(redisCacheNames);
        } catch (Exception e) {
            log.warn("从 Redis 扫描缓存名称失败", e);
        }
        
        // 转换为配置对象
        for (String cacheName : cacheNames) {
            try {
                CacheConfigDTO config = getCacheConfig(cacheName);
                if (config != null) {
                    configs.add(config);
                }
            } catch (Exception e) {
                log.warn("获取缓存配置失败: {}", cacheName, e);
            }
        }
        
        // 按名称排序
        configs.sort(Comparator.comparing(CacheConfigDTO::getCacheName));
        
        return configs;
    }
    
    /**
     * 从 Redis 扫描缓存名称
     * Redis 缓存键格式：cacheName::key
     */
    private Set<String> scanCacheNamesFromRedis() {
        Set<String> cacheNames = new HashSet<>();
        
        try {
            // 扫描所有包含 :: 的键（Spring Cache 的格式）
            Set<String> keys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
                Set<String> result = new HashSet<>();
                ScanOptions options = ScanOptions.scanOptions()
                        .match("*::*")  // Spring Cache 格式：cacheName::key
                        .count(1000)
                        .build();
                
                Cursor<byte[]> cursor = connection.scan(options);
                while (cursor.hasNext()) {
                    String key = new String(cursor.next());
                    result.add(key);
                }
                cursor.close();
                return result;
            });
            
            if (keys != null) {
                for (String key : keys) {
                    // 提取缓存名称（:: 之前的部分）
                    int index = key.indexOf("::");
                    if (index > 0) {
                        String cacheName = key.substring(0, index);
                        cacheNames.add(cacheName);
                    }
                }
            }
            
            log.info("从 Redis 扫描到 {} 个缓存", cacheNames.size());
        } catch (Exception e) {
            log.error("扫描 Redis 缓存失败", e);
        }
        
        return cacheNames;
    }
    
    @Override
    public CacheConfigDTO getCacheConfig(String cacheName) {
        CacheConfigDTO config = new CacheConfigDTO();
        config.setCacheName(cacheName);
        
        // 从 Redis 获取配置
        String configKey = CACHE_CONFIG_PREFIX + cacheName;
        @SuppressWarnings("unchecked")
        Map<String, Object> configMap = (Map<String, Object>) redisTemplate.opsForValue().get(configKey);
        
        if (configMap != null) {
            config.setTtl(getLongValue(configMap.get("ttl"), DEFAULT_TTL));
            config.setMaxSize(getIntValue(configMap.get("maxSize"), -1));
            config.setEnabled(getBooleanValue(configMap.get("enabled"), true));
            config.setDescription((String) configMap.get("description"));
        } else {
            // 默认配置
            config.setTtl(DEFAULT_TTL);
            config.setMaxSize(-1);
            config.setEnabled(true);
            config.setDescription("缓存：" + cacheName);
        }
        
        // 获取统计信息
        CacheConfigDTO stats = getCacheStats(cacheName);
        if (stats != null) {
            config.setCurrentSize(stats.getCurrentSize());
            config.setHits(stats.getHits());
            config.setMisses(stats.getMisses());
            config.setHitRate(stats.getHitRate());
        }
        
        return config;
    }
    
    @Override
    @CacheEvict(value = "systemCache", allEntries = true)
    public void updateCacheConfig(CacheConfigDTO config) {
        String configKey = CACHE_CONFIG_PREFIX + config.getCacheName();
        
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("ttl", config.getTtl());
        configMap.put("maxSize", config.getMaxSize());
        configMap.put("enabled", config.getEnabled());
        configMap.put("description", config.getDescription());
        
        redisTemplate.opsForValue().set(configKey, configMap);
        
        log.info("缓存配置已更新: {}, 清除系统缓存", config.getCacheName());
    }
    
    @Override
    @CacheEvict(value = "systemCache", allEntries = true)
    public void clearCache(String cacheName) {
        if (cacheManager != null) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.info("已清空缓存: {}", cacheName);
            }
        }
        
        // 清空 Redis 中的缓存数据
        String pattern = cacheName + "::*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("已从 Redis 删除缓存键: {} 个", keys.size());
        }
        log.info("清除系统缓存");
    }
    
    @Override
    @CacheEvict(value = "systemCache", allEntries = true)
    public void clearAllCaches() {
        if (cacheManager != null) {
            Collection<String> cacheNames = cacheManager.getCacheNames();
            for (String cacheName : cacheNames) {
                Cache cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    cache.clear();
                }
            }
        }
        log.info("已清空所有缓存，清除系统缓存");
    }
    
    @Override
    public CacheConfigDTO getCacheStats(String cacheName) {
        CacheConfigDTO stats = new CacheConfigDTO();
        stats.setCacheName(cacheName);
        
        try {
            // 统计缓存键数量
            String pattern = cacheName + "::*";
            Long size = redisTemplate.execute((RedisCallback<Long>) connection -> {
                long count = 0;
                ScanOptions options = ScanOptions.scanOptions()
                        .match(pattern)
                        .count(1000)
                        .build();
                Cursor<byte[]> cursor = connection.scan(options);
                while (cursor.hasNext()) {
                    cursor.next();
                    count++;
                }
                cursor.close();
                return count;
            });
            
            stats.setCurrentSize(size != null ? size : 0L);
            
            // Redis 本身不直接提供命中率统计，这里返回 0
            // 如果需要精确统计，需要在应用层记录
            stats.setHits(0L);
            stats.setMisses(0L);
            stats.setHitRate(0.0);
            
        } catch (Exception e) {
            log.error("获取缓存统计失败: {}", cacheName, e);
            stats.setCurrentSize(0L);
        }
        
        return stats;
    }
    
    @Override
    public String getRedisInfo() {
        try {
            Properties info = redisTemplate.execute((RedisCallback<Properties>) RedisConnection::info);
            if (info != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Redis 服务器信息:\n");
                sb.append("版本: ").append(info.getProperty("redis_version", "未知")).append("\n");
                sb.append("运行模式: ").append(info.getProperty("redis_mode", "未知")).append("\n");
                sb.append("已使用内存: ").append(info.getProperty("used_memory_human", "未知")).append("\n");
                sb.append("连接客户端数: ").append(info.getProperty("connected_clients", "未知")).append("\n");
                sb.append("总键数: ").append(info.getProperty("db0", "未知")).append("\n");
                return sb.toString();
            }
        } catch (Exception e) {
            log.error("获取 Redis 信息失败", e);
        }
        return "无法获取 Redis 信息";
    }
    
    private Long getLongValue(Object value, Long defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        try {
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    private Integer getIntValue(Object value, Integer defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Long) return ((Long) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    private Boolean getBooleanValue(Object value, Boolean defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Boolean) return (Boolean) value;
        try {
            return Boolean.parseBoolean(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}

