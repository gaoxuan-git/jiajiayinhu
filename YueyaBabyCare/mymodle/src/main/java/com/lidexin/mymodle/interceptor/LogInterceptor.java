package com.lidexin.mymodle.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志拦截器
 * 记录请求的详细信息，包括请求路径、参数、耗时等
 * 
 * @author 张三
 */
@Component
public class LogInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LogInterceptor.class);
    
    private static final String START_TIME = "startTime";
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 请求处理之前调用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME, startTime);
        
        // 获取请求信息
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String ip = getIpAddress(request);
        
        // 获取请求参数
        Map<String, String> params = getParameters(request);
        
        // 记录请求日志
        log.info("==> 请求开始: {} {} | IP: {} | 参数: {}", 
                method, uri + (queryString != null ? "?" + queryString : ""), 
                ip, objectMapper.writeValueAsString(params));
        
        return true;
    }

    /**
     * 请求处理之后调用（Controller方法执行完成后）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 这里可以添加一些后置处理逻辑
    }

    /**
     * 整个请求完成后调用（视图渲染完成后）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 计算请求耗时
        Long startTime = (Long) request.getAttribute(START_TIME);
        if (startTime != null) {
            long endTime = System.currentTimeMillis();
            long executeTime = endTime - startTime;
            
            String method = request.getMethod();
            String uri = request.getRequestURI();
            int status = response.getStatus();
            
            // 记录响应日志
            log.info("<== 请求完成: {} {} | 状态码: {} | 耗时: {}ms", 
                    method, uri, status, executeTime);
            
            // 如果请求耗时过长，记录警告日志
            if (executeTime > 3000) {
                log.warn("!!! 请求耗时过长: {} {} | 耗时: {}ms", method, uri, executeTime);
            }
        }
        
        // 如果有异常，记录异常信息
        if (ex != null) {
            log.error("!!! 请求异常: {} {} | 异常: {}", 
                    request.getMethod(), request.getRequestURI(), ex.getMessage());
        }
    }

    /**
     * 获取请求参数
     */
    private Map<String, String> getParameters(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            String value = request.getParameter(name);
            // 过滤敏感信息（密码等）
            if (name.toLowerCase().contains("password") || name.toLowerCase().contains("pwd")) {
                value = "******";
            }
            params.put(name, value);
        }
        return params;
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多级代理的情况
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}

