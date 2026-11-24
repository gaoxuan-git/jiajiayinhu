package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lidexin.business.dto.ChangePasswordDTO;
import com.lidexin.business.dto.ParentProfileDTO;
import com.lidexin.business.dto.ParentRegisterDTO;
import com.lidexin.business.enums.UserTypeEnum;
import com.lidexin.business.mapper.ParentMapper;
import com.lidexin.business.po.Parent;
import com.lidexin.business.service.IParentService;
import com.lidexin.business.vo.LoginVO;
import com.lidexin.business.vo.ParentProfileVO;
import com.lidexin.business.vo.UserInfoVO;
import com.lidexin.mymodle.exception.BusinessException;
import com.lidexin.mymodle.service.AuthService;
import com.lidexin.mymodle.util.Md5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 家长表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Service
public class ParentServiceImpl extends ServiceImpl<ParentMapper, Parent> implements IParentService {

    @Autowired
    private AuthService authService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO register(ParentRegisterDTO registerDTO) {
        // 1. 参数校验
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 2. 检查用户名是否已存在
        if (getByUsername(registerDTO.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 3. 检查手机号是否已存在
        if (getByPhone(registerDTO.getPhone()) != null) {
            throw new BusinessException("手机号已被注册");
        }

        // 4. 创建家长记录
        Parent parent = new Parent();
        parent.setUsername(registerDTO.getUsername());
        parent.setPassword(Md5Util.encryptWithSalt(registerDTO.getPassword()));
        parent.setPhone(registerDTO.getPhone());
        parent.setRealName(registerDTO.getRealName());
        parent.setEmail(registerDTO.getEmail());
        parent.setStatus((byte) 1);
        parent.setDeleted((byte) 0);

        // 5. 保存到数据库
        this.save(parent);

        // 6. 注册成功后自动登录
        return doLogin(parent);
    }

    @Override
    public LoginVO login(String username, String password) {
        // 1. 查询用户
        Parent parent = getByUsername(username);
        if (parent == null) {
            throw new BusinessException(1001, "用户不存在");
        }

        // 2. 校验密码
        if (!Md5Util.verify(password, parent.getPassword())) {
            throw new BusinessException(1003, "密码错误");
        }

        // 3. 检查账号状态
        if (parent.getStatus() == null || parent.getStatus() == 0) {
            throw new BusinessException(1002, "账号已被禁用");
        }

        // 4. 执行登录
        return doLogin(parent);
    }

    /**
     * 执行登录操作，生成token并存储到Redis
     */
    private LoginVO doLogin(Parent parent) {
        // 构建用户信息VO（存入Redis）
        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setUserId(parent.getId());
        userInfo.setUsername(parent.getUsername());
        userInfo.setRealName(parent.getRealName());
        userInfo.setPhone(parent.getPhone());
        userInfo.setEmail(parent.getEmail());
        userInfo.setAvatar(parent.getAvatar());
        userInfo.setGender(parent.getGender());
        userInfo.setUserType(UserTypeEnum.PARENT.getCode());
        userInfo.setUserTypeName(UserTypeEnum.PARENT.getName());
        userInfo.setStatus(parent.getStatus());
        userInfo.setCreateTime(parent.getCreateTime());

        // 调用AuthService登录，生成token并存入Redis
        String token = authService.login(parent.getId(), parent.getUsername(), userInfo);

        // 构建登录响应
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(parent.getId());
        loginVO.setUsername(parent.getUsername());
        loginVO.setRealName(parent.getRealName());
        loginVO.setAvatar(parent.getAvatar());
        loginVO.setUserType(UserTypeEnum.PARENT.getCode());
        loginVO.setUserTypeName(UserTypeEnum.PARENT.getName());

        return loginVO;
    }

    @Override
    public Parent getByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        return this.getOne(new LambdaQueryWrapper<Parent>()
                .eq(Parent::getUsername, username)
                .eq(Parent::getDeleted, 0));
    }

    @Override
    public Parent getByPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            return null;
        }
        return this.getOne(new LambdaQueryWrapper<Parent>()
                .eq(Parent::getPhone, phone)
                .eq(Parent::getDeleted, 0));
    }

    @Override
    public ParentProfileVO getProfile(Long id) {
        Parent parent = this.getById(id);
        if (parent == null || parent.getDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }

        ParentProfileVO vo = new ParentProfileVO();
        vo.setId(parent.getId());
        vo.setUsername(parent.getUsername());
        vo.setRealName(parent.getRealName());
        vo.setPhone(desensitizePhone(parent.getPhone()));
        vo.setEmail(parent.getEmail());
        vo.setAvatar(parent.getAvatar());
        vo.setGender(parent.getGender());
        vo.setGenderName(getGenderName(parent.getGender()));
        vo.setIdCard(desensitizeIdCard(parent.getIdCard()));
        vo.setStatus(parent.getStatus());
        vo.setCreateTime(parent.getCreateTime());

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProfile(Long id, ParentProfileDTO profileDTO) {
        Parent parent = this.getById(id);
        if (parent == null || parent.getDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }

        // 检查手机号是否被其他用户使用
        if (StringUtils.isNotBlank(profileDTO.getPhone()) && !profileDTO.getPhone().equals(parent.getPhone())) {
            Parent existParent = getByPhone(profileDTO.getPhone());
            if (existParent != null && !existParent.getId().equals(id)) {
                throw new BusinessException("手机号已被其他用户使用");
            }
        }

        // 更新信息
        Parent updateParent = new Parent();
        updateParent.setId(id);
        if (StringUtils.isNotBlank(profileDTO.getRealName())) {
            updateParent.setRealName(profileDTO.getRealName());
        }
        if (StringUtils.isNotBlank(profileDTO.getPhone())) {
            updateParent.setPhone(profileDTO.getPhone());
        }
        if (StringUtils.isNotBlank(profileDTO.getEmail())) {
            updateParent.setEmail(profileDTO.getEmail());
        }
        if (profileDTO.getGender() != null) {
            updateParent.setGender(profileDTO.getGender());
        }
        if (StringUtils.isNotBlank(profileDTO.getIdCard())) {
            updateParent.setIdCard(profileDTO.getIdCard());
        }

        return this.updateById(updateParent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAvatar(Long id, String avatarUrl) {
        if (StringUtils.isBlank(avatarUrl)) {
            throw new BusinessException("头像URL不能为空");
        }

        Parent parent = new Parent();
        parent.setId(id);
        parent.setAvatar(avatarUrl);
        return this.updateById(parent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Long id, ChangePasswordDTO changePasswordDTO) {
        // 1. 参数校验
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入的新密码不一致");
        }

        // 2. 查询用户
        Parent parent = this.getById(id);
        if (parent == null || parent.getDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }

        // 3. 验证旧密码
        if (!Md5Util.verify(changePasswordDTO.getOldPassword(), parent.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 4. 更新密码
        Parent updateParent = new Parent();
        updateParent.setId(id);
        updateParent.setPassword(Md5Util.encryptWithSalt(changePasswordDTO.getNewPassword()));
        return this.updateById(updateParent);
    }

    /**
     * 手机号脱敏
     */
    private String desensitizePhone(String phone) {
        if (StringUtils.isBlank(phone) || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 身份证号脱敏
     */
    private String desensitizeIdCard(String idCard) {
        if (StringUtils.isBlank(idCard) || idCard.length() < 8) {
            return idCard;
        }
        return idCard.substring(0, 4) + "**********" + idCard.substring(idCard.length() - 4);
    }

    @Override
    public Parent getParentByToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        try {
            UserInfoVO userInfo = authService.getUserByToken(token, UserInfoVO.class);
            if (userInfo == null || userInfo.getUserId() == null) {
                return null;
            }
            return this.getById(userInfo.getUserId());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取性别名称
     */
    private String getGenderName(Byte gender) {
        if (gender == null) {
            return "未知";
        }
        switch (gender) {
            case 1:
                return "男";
            case 2:
                return "女";
            default:
                return "未知";
        }
    }
}
