package com.lidexin.mymodle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置类
 * 解决前后端分离项目的跨域问题
 * 
 * @author 张三
 */
@Configuration
public class CorsConfig {

    /**
     * 配置CORS跨域过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许所有源地址访问（生产环境建议配置具体的域名）
        config.addAllowedOriginPattern("*");
        
        // 允许携带Cookie信息
        config.setAllowCredentials(true);
        
        // 允许所有请求头
        config.addAllowedHeader("*");
        
        // 允许所有HTTP方法（GET、POST、PUT、DELETE等）
        config.addAllowedMethod("*");
        
        // 暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
        config.addExposedHeader("Content-Disposition");
        config.addExposedHeader("Authorization");
        
        // 预检请求的有效期，单位为秒（可以减少预检请求的次数）
        config.setMaxAge(3600L);
        
        // 添加映射路径，拦截所有请求
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}

