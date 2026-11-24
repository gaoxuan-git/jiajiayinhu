package com.lidexin.mymodle.dto;

import lombok.Data;

/**
 * 缓存配置 DTO
 * 
 * @author 张三
 */
@Data
public class CacheConfigDTO {
    
    /**
     * 缓存名称
     */
    private String cacheName;
    
    /**
     * 过期时间（秒），-1 表示永不过期
     */
    private Long ttl;
    
    /**
     * 最大缓存数量，-1 表示不限制
     */
    private Integer maxSize;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 当前缓存数量
     */
    private Long currentSize;
    
    /**
     * 命中次数
     */
    private Long hits;
    
    /**
     * 未命中次数
     */
    private Long misses;
    
    /**
     * 命中率
     */
    private Double hitRate;
}

