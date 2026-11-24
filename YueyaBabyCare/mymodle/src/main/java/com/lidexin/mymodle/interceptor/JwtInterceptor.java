package com.lidexin.mymodle.interceptor;

import com.lidexin.mymodle.common.ResultCode;
import com.lidexin.mymodle.exception.BusinessException;
import com.lidexin.mymodle.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT + Redis 认证拦截器
 * 验证请求中的 JWT 令牌是否在 Redis 中存在（会话管理）
 * 
 * @author 张三
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    @Value("${mymodle.jwt.header:Authorization}")
    private String header;

    @Value("${mymodle.jwt.prefix:Bearer }")
    private String prefix;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取 token
        String token = getTokenFromRequest(request);
        log.debug("拦截器获取到的token: {}", token);
        
        if (StringUtils.isBlank(token)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.code(), "未登录，请先登录");
        }

        // 2. 验证 token 是否在 Redis 中存在（已登录且未过期）
        boolean valid = authService.validateToken(token);
        log.debug("token验证结果: {}", valid);
        if (!valid) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.code(), "登录已过期或无效，请重新登录");
        }

        // 3. 从 Redis 获取用户信息（泛型方式，可适配任何用户对象）
        Object userInfo = authService.getUserByToken(token, Object.class);
        if (userInfo == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.code(), "用户信息获取失败，请重新登录");
        }
        
        // 4. 将 token 和用户信息存入 request，方便后续使用
        request.setAttribute("token", token);
        request.setAttribute("userInfo", userInfo);
        
        return true;
    }

    /**
     * 从请求中获取 token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (StringUtils.isNotBlank(token) && token.startsWith(prefix)) {
            // 兼容 "Bearer  <token>"（多空格）等情况
            return token.substring(prefix.length()).trim();
        }
        return null;
    }
}

