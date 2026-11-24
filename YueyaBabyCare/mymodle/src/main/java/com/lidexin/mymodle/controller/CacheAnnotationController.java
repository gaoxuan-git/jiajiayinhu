package com.lidexin.mymodle.controller;

import com.lidexin.mymodle.common.R;
import com.lidexin.mymodle.dto.AddCacheAnnotationRequest;
import com.lidexin.mymodle.dto.CacheMethodInfoDTO;
import com.lidexin.mymodle.service.CacheAnnotationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 缓存注解管理Controller
 */
@Slf4j
@Api(tags = "缓存注解管理")
@RestController
@RequestMapping("/api/cache-annotation")
public class CacheAnnotationController {
    
    @Autowired
    private CacheAnnotationService cacheAnnotationService;
    
    /**
     * 扫描项目中所有可以添加缓存的方法
     */
    @ApiOperation("扫描可缓存方法")
    @GetMapping("/scan")
    public R<List<CacheMethodInfoDTO>> scanCacheableMethods() {
        log.info("开始扫描可缓存方法");
        
        try {
            List<CacheMethodInfoDTO> methods = cacheAnnotationService.scanCacheableMethods();
            log.info("扫描完成，找到 {} 个可缓存方法", methods.size());
            return R.success("扫描成功", methods);
        } catch (Exception e) {
            log.error("扫描失败", e);
            return R.fail("扫描失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量添加缓存注解
     */
    @ApiOperation("添加缓存注解")
    @PostMapping("/add")
    public R<String> addCacheAnnotations(@RequestBody AddCacheAnnotationRequest request) {
        log.info("开始添加缓存注解，共 {} 个方法", request.getMethods().size());
        
        try {
            Integer successCount = cacheAnnotationService.addCacheAnnotations(request);
            String message = String.format("成功添加 %d/%d 个缓存注解", 
                successCount, request.getMethods().size());
            log.info(message);
            return R.success(message, null);
        } catch (Exception e) {
            log.error("添加缓存注解失败", e);
            return R.fail("添加失败：" + e.getMessage());
        }
    }
}

