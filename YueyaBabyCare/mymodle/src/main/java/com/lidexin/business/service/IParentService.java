package com.lidexin.business.service;

import com.lidexin.business.dto.ChangePasswordDTO;
import com.lidexin.business.dto.ParentProfileDTO;
import com.lidexin.business.dto.ParentRegisterDTO;
import com.lidexin.business.po.Parent;
import com.lidexin.business.vo.LoginVO;
import com.lidexin.business.vo.ParentProfileVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 家长表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
public interface IParentService extends IService<Parent> {

    /**
     * 家长注册
     *
     * @param registerDTO 注册信息
     * @return 登录结果（注册成功自动登录）
     */
    LoginVO register(ParentRegisterDTO registerDTO);

    /**
     * 家长登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    LoginVO login(String username, String password);

    /**
     * 根据用户名查询家长
     *
     * @param username 用户名
     * @return 家长信息
     */
    Parent getByUsername(String username);

    /**
     * 根据手机号查询家长
     *
     * @param phone 手机号
     * @return 家长信息
     */
    Parent getByPhone(String phone);

    /**
     * 获取家长个人信息
     *
     * @param id 家长ID
     * @return 个人信息VO
     */
    ParentProfileVO getProfile(Long id);

    /**
     * 修改家长个人信息
     *
     * @param id 家长ID
     * @param profileDTO 修改信息
     * @return 是否成功
     */
    boolean updateProfile(Long id, ParentProfileDTO profileDTO);

    /**
     * 修改头像
     *
     * @param id 家长ID
     * @param avatarUrl 头像URL
     * @return 是否成功
     */
    boolean updateAvatar(Long id, String avatarUrl);

    /**
     * 修改密码
     *
     * @param id 家长ID
     * @param changePasswordDTO 密码信息
     * @return 是否成功
     */
    boolean changePassword(Long id, ChangePasswordDTO changePasswordDTO);

    /**
     * 根据token获取家长信息
     *
     * @param token JWT token
     * @return 家长信息
     */
    Parent getParentByToken(String token);
}
