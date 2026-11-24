package com.lidexin.mymodle.dto;

import lombok.Data;

/**
 * 缓存方法信息DTO
 */
@Data
public class CacheMethodInfoDTO {
    
    /**
     * 类名（全限定名）
     */
    private String className;
    
    /**
     * 类简称
     */
    private String simpleClassName;
    
    /**
     * 方法名
     */
    private String methodName;
    
    /**
     * 方法签名
     */
    private String methodSignature;
    
    /**
     * 返回值类型
     */
    private String returnType;
    
    /**
     * 方法类型：query, update, delete
     */
    private String methodType;
    
    /**
     * 建议的缓存注解：@Cacheable, @CachePut, @CacheEvict
     */
    private String suggestedAnnotation;
    
    /**
     * 缓存名称
     */
    private String cacheName;
    
    /**
     * 是否已有缓存注解
     */
    private Boolean hasCacheAnnotation;
    
    /**
     * 源文件路径
     */
    private String filePath;
}

