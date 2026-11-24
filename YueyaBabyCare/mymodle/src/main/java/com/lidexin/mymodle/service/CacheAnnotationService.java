package com.lidexin.mymodle.service;

import com.lidexin.mymodle.dto.AddCacheAnnotationRequest;
import com.lidexin.mymodle.dto.CacheMethodInfoDTO;

import java.util.List;

/**
 * 缓存注解管理服务接口
 */
public interface CacheAnnotationService {
    
    /**
     * 扫描项目中所有可以添加缓存的方法
     * 
     * @return 方法信息列表
     */
    List<CacheMethodInfoDTO> scanCacheableMethods();
    
    /**
     * 批量添加缓存注解到源文件
     * 
     * @param request 添加请求
     * @return 成功添加的数量
     */
    Integer addCacheAnnotations(AddCacheAnnotationRequest request);
}

