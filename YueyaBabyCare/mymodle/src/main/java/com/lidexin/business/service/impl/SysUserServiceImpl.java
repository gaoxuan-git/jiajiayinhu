package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lidexin.business.mapper.SysUserMapper;
import com.lidexin.business.po.SysUser;
import com.lidexin.business.service.ISysUserService;
import com.lidexin.business.vo.UserInfoVO;
import com.lidexin.mymodle.common.PageRequest;
import com.lidexin.mymodle.common.PageResult;
import com.lidexin.mymodle.exception.BusinessException;
import com.lidexin.mymodle.service.AuthService;
import com.lidexin.mymodle.util.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-11-05
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private AuthService authService;

    @Override
    public String login(String username, String password) {
        // 1. 查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = this.getOne(wrapper);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        // 2. 验证密码
        if (!Md5Util.verify(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        // 3. 检查用户状态
        if (!user.getStatus()) {
            throw new BusinessException("账号已被禁用");
        }
        // 4. 构建用户信息对象（包含userType=4表示管理员）
        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getNickname());
        userInfo.setPhone(user.getPhone());
        userInfo.setEmail(user.getEmail());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setUserType(4); // 管理员类型
        userInfo.setUserTypeName("管理员");
        userInfo.setStatus(user.getStatus() ? (byte) 1 : (byte) 0);
        userInfo.setCreateTime(user.getCreateTime());

        // 5. 调用认证服务进行登录
        return authService.login(user.getId(), username, userInfo);
    }

    @Override
    public SysUser getUserByToken(String token) {
        // 调用系统层认证服务获取用户信息
        return authService.getUserByToken(token, SysUser.class);
    }

    @Override
    public void logout(String token) {
        // 调用系统层认证服务进行登出
        authService.logout(token);
    }

    @Override
    public boolean register(SysUser user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, user.getUsername());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        // 密码加密
        user.setPassword(Md5Util.encryptWithSalt(user.getPassword()));
        // 设置默认值
        if (user.getStatus() == null) {
            // 默认启用
            user.setStatus(true);
        }
        return this.save(user);
    }

    /**
     * 重写 updateById，更新后清除用户列表缓存
     */
    @Override
    @CacheEvict(value = "sysUserCache", allEntries = true)
    public boolean updateById(SysUser entity) {
        return super.updateById(entity);
    }

    /**
     * 重写 save，新增后清除用户列表缓存
     */
    @Override
    @CacheEvict(value = "sysUserCache", allEntries = true)
    public boolean save(SysUser entity) {
        return super.save(entity);
    }

    /**
     * 重写 removeById，删除后清除用户列表缓存
     */
    @Override
    @CacheEvict(value = "sysUserCache", allEntries = true)
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
    }

    @Override
    @Cacheable(value = "sysUserCache", key = "#pageRequest.current + '-' + #pageRequest.size + '-' + #username")
    public PageResult<SysUser> pageUsers(PageRequest pageRequest, String username) {
        // 构建分页对象
        Page<SysUser> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        // 构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(username), SysUser::getUsername, username).orderByDesc(SysUser::getCreateTime);
        // 执行分页查询
        IPage<SysUser> result = this.page(page, wrapper);
        // 隐藏密码字段
        result.getRecords().forEach(user -> user.setPassword(null));
        // 转换为PageResult返回
        return PageResult.of(result);
    }
}
