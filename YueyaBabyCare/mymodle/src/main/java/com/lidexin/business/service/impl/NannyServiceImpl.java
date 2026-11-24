package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lidexin.business.mapper.NannyMapper;
import com.lidexin.business.po.Nanny;
import com.lidexin.business.service.INannyService;
import com.lidexin.business.vo.UserInfoVO;
import com.lidexin.mymodle.exception.BusinessException;
import com.lidexin.mymodle.service.AuthService;
import com.lidexin.mymodle.util.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 保姆表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Slf4j
@Service
public class NannyServiceImpl extends ServiceImpl<NannyMapper, Nanny> implements INannyService {

    @Autowired
    private AuthService authService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean register(Nanny nanny) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<Nanny> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Nanny::getUsername, nanny.getUsername());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        // 检查手机号是否已存在
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Nanny::getPhone, nanny.getPhone());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("手机号已被注册");
        }
        // 验证机构ID是否存在（这里可以加入机构验证逻辑）
        if (nanny.getInstitutionId() == null) {
            throw new BusinessException("请选择所属机构");
        }
        // 密码加密
        nanny.setPassword(Md5Util.encryptWithSalt(nanny.getPassword()));
        // 设置默认值 - 注册后需要机构审核
        if (nanny.getStatus() == null) {
            nanny.setStatus((byte) 0); // 默认待审核
        }
        return this.save(nanny);
    }

    @Override
    public String login(String username, String password) {
        // 1. 查询保姆
        LambdaQueryWrapper<Nanny> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Nanny::getUsername, username);
        Nanny nanny = this.getOne(wrapper);
        if (nanny == null) {
            throw new BusinessException("用户名或密码错误");
        }
        // 2. 验证密码
        if (!Md5Util.verify(password, nanny.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        // 3. 检查状态
        if (nanny.getStatus() == null || nanny.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }
        // 4. 构建用户信息对象（包含userType=2表示保姆）
        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setUserId(nanny.getId());
        userInfo.setUsername(nanny.getUsername());
        userInfo.setRealName(nanny.getRealName());
        userInfo.setPhone(nanny.getPhone());
        userInfo.setAvatar(nanny.getAvatar());
        userInfo.setGender(nanny.getGender());
        userInfo.setIdCard(nanny.getIdCard());
        userInfo.setUserType(2); // 保姆类型
        userInfo.setUserTypeName("保姆");
        userInfo.setStatus(nanny.getStatus());
        userInfo.setInstitutionId(nanny.getInstitutionId());
        userInfo.setCreateTime(nanny.getCreateTime());

        // 5. 调用认证服务进行登录
        return authService.login(nanny.getId(), username, userInfo);
    }

    @Override
    public Nanny getNannyByToken(String token) {
        // 从Redis获取UserInfoVO，再根据userId查询保姆信息
        UserInfoVO userInfo = authService.getUserByToken(token, UserInfoVO.class);
        if (userInfo == null || userInfo.getUserId() == null) {
            return null;
        }
        return this.getById(userInfo.getUserId());
    }

    @Override
    public void logout(String token) {
        authService.logout(token);
    }

    @Override
    public boolean updateProfile(Nanny nanny) {
        if (nanny.getId() == null) {
            throw new BusinessException("保姆ID不能为空");
        }
        // 不允许修改密码和用户名
        nanny.setPassword(null);
        nanny.setUsername(null);
        return this.updateById(nanny);
    }

    @Override
    public boolean addCertificate(Long nannyId, String certificateUrl) {
        Nanny nanny = this.getById(nannyId);
        if (nanny == null) {
            throw new BusinessException("保姆不存在");
        }
        try {
            List<String> certificates = getCertificates(nannyId);
            if (!certificates.contains(certificateUrl)) {
                certificates.add(certificateUrl);
                nanny.setCertificate(objectMapper.writeValueAsString(certificates));
                return this.updateById(nanny);
            }
            return true;
        } catch (Exception e) {
            log.error("添加证书失败", e);
            throw new BusinessException("添加证书失败");
        }
    }

    @Override
    public List<String> getCertificates(Long nannyId) {
        Nanny nanny = this.getById(nannyId);
        if (nanny == null) {
            throw new BusinessException("保姆不存在");
        }
        if (StringUtils.isBlank(nanny.getCertificate())) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(nanny.getCertificate(), new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.error("解析证书列表失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean deleteCertificate(Long nannyId, String certificateUrl) {
        Nanny nanny = this.getById(nannyId);
        if (nanny == null) {
            throw new BusinessException("保姆不存在");
        }
        try {
            List<String> certificates = getCertificates(nannyId);
            certificates.remove(certificateUrl);
            nanny.setCertificate(objectMapper.writeValueAsString(certificates));
            return this.updateById(nanny);
        } catch (Exception e) {
            log.error("删除证书失败", e);
            throw new BusinessException("删除证书失败");
        }
    }

    @Override
    public List<Nanny> searchNannies(Long institutionId, String keyword) {
        LambdaQueryWrapper<Nanny> wrapper = new LambdaQueryWrapper<>();
        // 只返回正常状态的保姆
        wrapper.eq(Nanny::getStatus, 1);
        // 机构过滤
        if (institutionId != null) {
            wrapper.eq(Nanny::getInstitutionId, institutionId);
        }
        // 关键词搜索（姓名或简介）
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Nanny::getRealName, keyword).or().like(Nanny::getIntroduction, keyword));
        }
        // 隐藏敏感信息
        List<Nanny> nannies = this.list(wrapper);
        nannies.forEach(n -> {
            n.setPassword(null);
            n.setIdCard(null); // 隐藏身份证号
        });
        return nannies;
    }
}
