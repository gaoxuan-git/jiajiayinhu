package com.lidexin.mymodle.controller;

import com.lidexin.mymodle.common.R;
import com.lidexin.mymodle.dto.CacheConfigDTO;
import com.lidexin.mymodle.service.CacheConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 缓存配置管理控制器
 * 
 * @author 张三
 */
@Slf4j
@Api(tags = "缓存配置管理")
@RestController
@RequestMapping("/api/cache-config")
public class CacheConfigController {
    
    @Autowired
    private CacheConfigService cacheConfigService;
    
    /**
     * 获取所有缓存配置
     */
    @ApiOperation("获取所有缓存配置")
    @GetMapping("/list")
    public R<List<CacheConfigDTO>> getAllCacheConfigs() {
        List<CacheConfigDTO> configs = cacheConfigService.getAllCacheConfigs();
        return R.success(configs);
    }
    
    /**
     * 获取指定缓存配置
     */
    @ApiOperation("获取指定缓存配置")
    @GetMapping("/{cacheName}")
    public R<CacheConfigDTO> getCacheConfig(@PathVariable String cacheName) {
        CacheConfigDTO config = cacheConfigService.getCacheConfig(cacheName);
        return R.success(config);
    }
    
    /**
     * 更新缓存配置
     */
    @ApiOperation("更新缓存配置")
    @PutMapping
    public R<String> updateCacheConfig(@RequestBody CacheConfigDTO config) {
        cacheConfigService.updateCacheConfig(config);
        return R.success("配置更新成功", null);
    }
    
    /**
     * 清空指定缓存
     */
    @ApiOperation("清空指定缓存")
    @DeleteMapping("/{cacheName}")
    public R<String> clearCache(@PathVariable String cacheName) {
        cacheConfigService.clearCache(cacheName);
        return R.success("缓存清空成功", null);
    }
    
    /**
     * 清空所有缓存
     */
    @ApiOperation("清空所有缓存")
    @DeleteMapping("/clear-all")
    public R<String> clearAllCaches() {
        cacheConfigService.clearAllCaches();
        return R.success("所有缓存已清空", null);
    }
    
    /**
     * 获取 Redis 信息
     */
    @ApiOperation("获取 Redis 信息")
    @GetMapping("/redis-info")
    public R<String> getRedisInfo() {
        String info = cacheConfigService.getRedisInfo();
        return R.success(info);
    }
}

