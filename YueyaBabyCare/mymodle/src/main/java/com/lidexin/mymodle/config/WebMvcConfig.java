package com.lidexin.mymodle.config;

import com.lidexin.mymodle.interceptor.JwtInterceptor;
import com.lidexin.mymodle.interceptor.LogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 配置拦截器、静态资源等
 * 
 * @author 张三
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LogInterceptor logInterceptor;

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Value("${mymodle.upload.path:./upload}")
    private String uploadPath;

    @Value("${mymodle.upload.url:/upload}")
    private String uploadUrl;

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册日志拦截器
        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/static/**",
                        "/upload/**",
                        "/favicon.ico",
                        "/error",
                        "/doc.html",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/v2/**",
                        "/v3/**"
                );

        // 注册JWT认证拦截器
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                // 排除不需要认证的接口
                .excludePathPatterns(
                        // 家长登录注册
                        "/api/parent/login",
                        "/api/parent/register",
                        // 保姆登录注册
                        "/api/nanny/login",
                        "/api/nanny/register",
                        // 保姆公开信息（家长查看）
                        "/api/nanny/*/",
                        "/api/nanny/search",
                        // 机构登录注册
                        "/api/institution/login",
                        "/api/institution/register",
                        // 机构公开信息（家长查看）
                        "/api/institution/*/",
                        "/api/institution/*/staff-count",
                        "/api/institution/*/service-count",
                        "/api/institution/*/nannies",
                        "/api/institution/search",
                        // 保姆注册时获取机构列表（无需认证）
                        "/api/institution/nanny/list",
                        // 管理员登录
                        "/api/admin/login",
                        // 机构管理（管理员，暂时不需要认证）
                        "/api/institution/admin/**",
                        // 用户登录、登出、注册和获取信息
                        "/api/user/login",
                        "/api/user/logout",
                        "/api/user/register",
                        "/api/user/info",
                        // 文件上传
                        "/api/file/upload",
                        "/api/file/upload/batch",
                        // 静态资源
                        "/static/**",
                        "/upload/**",
                        "/favicon.ico",
                        "/error"
                );
    }

    /**
     * 配置静态资源处理
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置文件上传的静态资源访问
        registry.addResourceHandler(uploadUrl + "/**")
                .addResourceLocations("file:" + uploadPath + "/");
        
        // 配置Swagger文档相关的静态资源
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}

