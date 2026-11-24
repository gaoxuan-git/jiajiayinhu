package com.lidexin.mymodle.dto;

import lombok.Data;
import java.util.List;

/**
 * 添加缓存注解请求DTO
 */
@Data
public class AddCacheAnnotationRequest {
    
    /**
     * 要添加缓存注解的方法列表
     */
    private List<CacheMethodItem> methods;
    
    @Data
    public static class CacheMethodItem {
        /**
         * 类名（全限定名）
         */
        private String className;
        
        /**
         * 方法名
         */
        private String methodName;
        
        /**
         * 缓存注解类型：Cacheable, CachePut, CacheEvict
         */
        private String annotationType;
        
        /**
         * 缓存名称
         */
        private String cacheName;
        
        /**
         * 缓存key表达式（可选，默认自动生成）
         */
        private String cacheKey;
        
        /**
         * 源文件路径
         */
        private String filePath;
    }
}

