package com.lidexin.mymodle.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lidexin.mymodle.service.AuthService;
import com.lidexin.mymodle.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 * 基于 JWT + Redis 的用户认证
 * 
 * @author 张三
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String USER_TOKEN_PREFIX = "auth:token:";
    private static final long TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60; // 7天（秒）
    
    @Override
    public String login(Long userId, String username, Object userInfo) {
        // 生成 JWT token
        String token = jwtUtil.generateToken(userId, username);
        log.info("生成token: {}", token);
        
        // 将用户信息存储到 Redis
        String redisKey = USER_TOKEN_PREFIX + token;
        log.info("准备存入Redis, key: {}", redisKey);
        redisTemplate.opsForValue().set(redisKey, userInfo, TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
        
        // 验证是否存入成功
        Boolean exists = redisTemplate.hasKey(redisKey);
        log.info("用户 {} (ID: {}) 登录成功，token 已存入 Redis, 验证存在: {}", username, userId, exists);
        return token;
    }
    
    @Override
    public void logout(String token) {
        if (StringUtils.isBlank(token)) {
            return;
        }
        String redisKey = USER_TOKEN_PREFIX + token;
        Boolean deleted = redisTemplate.delete(redisKey);
        if (Boolean.TRUE.equals(deleted)) {
            log.info("用户登出成功，已从 Redis 删除 token");
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getUserByToken(String token, Class<T> clazz) {
        if (StringUtils.isBlank(token)) {
            return null;
        }

        String redisKey = USER_TOKEN_PREFIX + token;
        Object obj = redisTemplate.opsForValue().get(redisKey);

        if (obj == null) {
            return null;
        }

        // 刷新过期时间（活跃用户自动续期）
        redisTemplate.expire(redisKey, TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);

        // 如果目标类型是 Object，直接返回
        if (clazz == Object.class) {
            return (T) obj;
        }

        // 如果已经是目标类型，直接返回
        if (clazz.isInstance(obj)) {
            return (T) obj;
        }

        // Redis 反序列化后可能是 LinkedHashMap，需要转换为目标类型
        try {
            log.debug("从Redis获取的原始对象类型: {}, 内容: {}", obj.getClass().getName(), obj);
            ObjectMapper objectMapper = new ObjectMapper();
            // 注册 Java 8 时间模块，支持 LocalDateTime 等类型
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            // 忽略未知属性，避免 Redis 中多余字段导致转换失败
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            T result = objectMapper.convertValue(obj, clazz);
            log.debug("转换后的对象: {}", result);
            return result;
        } catch (Exception e) {
            log.error("转换用户信息失败: {}", e.getMessage());
            return null;
        }
    }
    
    @Override
    public boolean refreshToken(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        
        String redisKey = USER_TOKEN_PREFIX + token;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            return Boolean.TRUE.equals(redisTemplate.expire(redisKey, TOKEN_EXPIRE_TIME, TimeUnit.SECONDS));
        }
        
        return false;
    }
    
    @Override
    public boolean validateToken(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        
        String redisKey = USER_TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }
}

