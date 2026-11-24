package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lidexin.business.dto.BabyDTO;
import com.lidexin.business.mapper.BabyMapper;
import com.lidexin.business.po.Baby;
import com.lidexin.business.po.Nanny;
import com.lidexin.business.service.IBabyService;
import com.lidexin.business.service.INannyService;
import com.lidexin.business.vo.BabyVO;
import com.lidexin.mymodle.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 婴幼儿信息表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Slf4j
@Service
public class BabyServiceImpl extends ServiceImpl<BabyMapper, Baby> implements IBabyService {

    @Autowired
    private INannyService nannyService;

    @Override
    public boolean addBaby(BabyDTO dto, Long parentId) {
        if (parentId == null) {
            throw new BusinessException("家长ID不能为空");
        }
        Baby baby = new Baby();
        BeanUtils.copyProperties(dto, baby);
        baby.setParentId(parentId);
        baby.setDeleted((byte) 0);
        return this.save(baby);
    }

    @Override
    public BabyVO getBabyById(Long id, Long parentId) {
        Baby baby = this.getById(id);
        if (baby == null || baby.getDeleted() == 1) {
            throw new BusinessException("婴幼儿不存在");
        }
        // 权限校验：如果传入了parentId，则校验是否为该家长的宝宝
        if (parentId != null && !baby.getParentId().equals(parentId)) {
            throw new BusinessException("无权查看该婴幼儿信息");
        }
        return convertToVO(baby);
    }

    @Override
    public boolean updateBaby(BabyDTO dto, Long parentId) {
        if (dto.getId() == null) {
            throw new BusinessException("婴幼儿ID不能为空");
        }
        Baby baby = this.getById(dto.getId());
        if (baby == null || baby.getDeleted() == 1) {
            throw new BusinessException("婴幼儿不存在");
        }
        // 权限校验
        if (parentId != null && !baby.getParentId().equals(parentId)) {
            throw new BusinessException("无权修改该婴幼儿信息");
        }
        // 更新信息
        BeanUtils.copyProperties(dto, baby);
        // 不允许修改parentId
        baby.setParentId(null);
        return this.updateById(baby);
    }

    @Override
    public boolean deleteBaby(Long id, Long parentId) {
        Baby baby = this.getById(id);
        if (baby == null || baby.getDeleted() == 1) {
            throw new BusinessException("婴幼儿不存在");
        }
        // 权限校验
        if (parentId != null && !baby.getParentId().equals(parentId)) {
            throw new BusinessException("无权删除该婴幼儿信息");
        }
        // 使用 removeById 执行逻辑删除（MyBatis-Plus 会自动处理 @TableLogic 字段）
        return this.removeById(id);
    }

    @Override
    public List<BabyVO> listByParentId(Long parentId) {
        if (parentId == null) {
            throw new BusinessException("家长ID不能为空");
        }
        LambdaQueryWrapper<Baby> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Baby::getParentId, parentId)
               .eq(Baby::getDeleted, 0)
               .orderByDesc(Baby::getCreateTime);
        List<Baby> babies = this.list(wrapper);
        return babies.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<?> getCaregivers(Long babyId, Long parentId) {
        // 先校验权限
        Baby baby = this.getById(babyId);
        if (baby == null || baby.getDeleted() == 1) {
            throw new BusinessException("婴幼儿不存在");
        }
        if (parentId != null && !baby.getParentId().equals(parentId)) {
            throw new BusinessException("无权查看该婴幼儿的保姆列表");
        }
        // TODO: 根据实际业务逻辑查询关联的保姆列表
        // 这里需要根据订单或服务记录表来查询为该宝宝服务过的保姆
        return new ArrayList<>();
    }

    @Override
    public boolean updateAvatar(Long id, String avatarUrl, Long parentId) {
        Baby baby = this.getById(id);
        if (baby == null || baby.getDeleted() == 1) {
            throw new BusinessException("婴幼儿不存在");
        }
        // 权限校验
        if (parentId != null && !baby.getParentId().equals(parentId)) {
            throw new BusinessException("无权修改该婴幼儿头像");
        }
        baby.setAvatar(avatarUrl);
        return this.updateById(baby);
    }

    @Override
    public List<BabyVO> adminListByParentId(Long parentId) {
        if (parentId == null) {
            throw new BusinessException("家长ID不能为空");
        }
        LambdaQueryWrapper<Baby> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Baby::getParentId, parentId)
               .eq(Baby::getDeleted, 0)
               .orderByDesc(Baby::getCreateTime);
        List<Baby> babies = this.list(wrapper);
        return babies.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<BabyVO> adminListAll() {
        LambdaQueryWrapper<Baby> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Baby::getDeleted, 0)
               .orderByDesc(Baby::getCreateTime);
        List<Baby> babies = this.list(wrapper);
        return babies.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    /**
     * 将Baby实体转换为BabyVO
     */
    private BabyVO convertToVO(Baby baby) {
        BabyVO vo = new BabyVO();
        BeanUtils.copyProperties(baby, vo);
        // 设置性别名称
        if (baby.getGender() != null) {
            switch (baby.getGender()) {
                case 1:
                    vo.setGenderName("男");
                    break;
                case 2:
                    vo.setGenderName("女");
                    break;
                default:
                    vo.setGenderName("未知");
            }
        }
        // 计算年龄（月）
        if (baby.getBirthDate() != null) {
            Period period = Period.between(baby.getBirthDate(), LocalDate.now());
            int months = period.getYears() * 12 + period.getMonths();
            vo.setAgeInMonths(months);
        }
        return vo;
    }
}
