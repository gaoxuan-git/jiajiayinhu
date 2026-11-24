package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lidexin.business.mapper.CareEvaluationMapper;
import com.lidexin.business.mapper.CarePlanMapper;
import com.lidexin.business.mapper.InstitutionMapper;
import com.lidexin.business.mapper.NannyMapper;
import com.lidexin.business.po.CareEvaluation;
import com.lidexin.business.po.CarePlan;
import com.lidexin.business.po.Institution;
import com.lidexin.business.po.Nanny;
import com.lidexin.business.service.IInstitutionService;
import com.lidexin.business.vo.NannyPerformanceVO;
import com.lidexin.business.vo.UserInfoVO;
import com.lidexin.mymodle.exception.BusinessException;
import com.lidexin.mymodle.service.AuthService;
import com.lidexin.mymodle.util.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 机构表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Slf4j
@Service
public class InstitutionServiceImpl extends ServiceImpl<InstitutionMapper, Institution> implements IInstitutionService {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private NannyMapper nannyMapper;
    
    @Autowired
    private CarePlanMapper carePlanMapper;
    
    @Autowired
    private CareEvaluationMapper careEvaluationMapper;

    @Override
    public List<Institution> getAllInstitutions() {
        LambdaQueryWrapper<Institution> wrapper = new LambdaQueryWrapper<>();
        // 只返回未删除的机构
        wrapper.eq(Institution::getDeleted, 0);
        // 按创建时间倒序排序
        wrapper.orderByDesc(Institution::getCreateTime);
        List<Institution> institutions = this.list(wrapper);
        // 隐藏密码
        institutions.forEach(inst -> inst.setPassword(null));
        return institutions;
    }

    @Override
    public String login(String username, String password) {
        // 1. 查询机构
        LambdaQueryWrapper<Institution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Institution::getUsername, username);
        Institution institution = this.getOne(wrapper);
        if (institution == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 验证密码
        if (!Md5Util.verify(password, institution.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 检查状态
        if (institution.getStatus() == null || institution.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 4. 构建统一的用户信息对象（包含userType）
        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setUserId(institution.getId());
        userInfo.setUsername(institution.getUsername());
        userInfo.setRealName(institution.getContactName());
        userInfo.setPhone(institution.getContactPhone());
        userInfo.setEmail(institution.getEmail());
        userInfo.setAvatar(institution.getLogo());
        userInfo.setUserType(3); // 机构类型
        userInfo.setUserTypeName("机构");
        userInfo.setStatus(institution.getStatus());
        userInfo.setInstitutionId(institution.getId());
        userInfo.setInstitutionName(institution.getName());
        userInfo.setCreateTime(institution.getCreateTime());

        // 5. 调用认证服务进行登录
        return authService.login(institution.getId(), username, userInfo);
    }

    @Override
    public Institution getInstitutionByToken(String token) {
        // 从 Redis 获取 UserInfoVO（统一的用户信息格式）
        UserInfoVO userInfo = authService.getUserByToken(token, UserInfoVO.class);
        if (userInfo == null || userInfo.getInstitutionId() == null) {
            return null;
        }
        // 根据机构ID从数据库获取完整的机构信息
        return this.getById(userInfo.getInstitutionId());
    }

    @Override
    public void logout(String token) {
        authService.logout(token);
    }

    @Override
    public boolean updateProfile(Institution institution) {
        if (institution.getId() == null) {
            throw new BusinessException("机构ID不能为空");
        }
        
        // 检查手机号是否被其他机构使用
        if (institution.getContactPhone() != null) {
            LambdaQueryWrapper<Institution> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Institution::getContactPhone, institution.getContactPhone())
                   .ne(Institution::getId, institution.getId())
                   .eq(Institution::getDeleted, 0);
            long count = this.count(wrapper);
            if (count > 0) {
                throw new BusinessException("该手机号已被其他机构使用");
            }
        }
        
        // 不允许修改密码和用户名
        institution.setPassword(null);
        institution.setUsername(null);
        
        return this.updateById(institution);
    }

    @Override
    public Page<Nanny> getNannyList(Long institutionId, Byte status, Integer pageNum, Integer pageSize) {
        Page<Nanny> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Nanny> wrapper = new LambdaQueryWrapper<>();
        
        // 查询该机构下的保姆
        wrapper.eq(Nanny::getInstitutionId, institutionId);
        
        // 根据状态筛选
        if (status != null) {
            wrapper.eq(Nanny::getStatus, status);
        }
        
        // 只查询未删除的
        wrapper.eq(Nanny::getDeleted, 0);
        
        // 按创建时间倒序排序
        wrapper.orderByDesc(Nanny::getCreateTime);
        
        // 使用 nannyMapper 进行分页查询
        Page<Nanny> result = nannyMapper.selectPage(page, wrapper);
        
        // 隐藏密码
        result.getRecords().forEach(nanny -> nanny.setPassword(null));
        
        return result;
    }

    @Override
    public boolean auditNanny(Long institutionId, Long nannyId, Byte status) {
        // 查询保姆
        Nanny nanny = nannyMapper.selectById(nannyId);
        if (nanny == null) {
            throw new BusinessException("保姆不存在");
        }
        
        // 验证保姆是否属于该机构
        if (!institutionId.equals(nanny.getInstitutionId())) {
            throw new BusinessException("无权操作其他机构的保姆");
        }
        
        // 更新状态
        nanny.setStatus(status);
        int rows = nannyMapper.updateById(nanny);
        
        log.info("机构 {} 审核保姆 {}，状态改为 {}", institutionId, nannyId, status);
        
        return rows > 0;
    }

    @Override
    public Long getStaffCount(Long institutionId) {
        LambdaQueryWrapper<Nanny> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Nanny::getInstitutionId, institutionId)
               .eq(Nanny::getDeleted, 0)
               .eq(Nanny::getStatus, 1); // 只统计审核通过的保姆
        return nannyMapper.selectCount(wrapper);
    }

    @Override
    public Long getServiceCount(Long institutionId) {
        // TODO: 订单模块未实现，暂时返回0
        // 后续需要查询 order 表中 institution_id = institutionId 的订单数量
        return 0L;
    }

    @Override
    public Page<Institution> getInstitutionPage(Integer pageNum, Integer pageSize) {
        Page<Institution> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Institution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Institution::getDeleted, 0)
               .orderByDesc(Institution::getCreateTime);
        
        Page<Institution> result = this.page(page, wrapper);
        
        // 隐藏密码
        result.getRecords().forEach(institution -> institution.setPassword(null));
        
        return result;
    }

    @Override
    public boolean auditInstitution(Long institutionId, Byte status) {
        if (institutionId == null) {
            throw new BusinessException("机构ID不能为空");
        }
        
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值无效，只能为0（禁用）或1（正常）");
        }
        
        Institution institution = this.getById(institutionId);
        if (institution == null) {
            throw new BusinessException("机构不存在");
        }
        
        institution.setStatus(status);
        boolean result = this.updateById(institution);
        
        log.info("管理员审核机构 {}，状态改为 {}", institutionId, status);
        
        return result;
    }

    @Override
    public Page<Institution> searchInstitutions(String keyword, Integer pageNum, Integer pageSize) {
        Page<Institution> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Institution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Institution::getDeleted, 0)
               .eq(Institution::getStatus, 1); // 只返回正常状态的机构
        
        // 如果有搜索关键词，按机构名称模糊查询
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Institution::getName, keyword.trim());
        }
        
        wrapper.orderByDesc(Institution::getCreateTime);
        
        Page<Institution> result = this.page(page, wrapper);
        
        // 隐藏密码和敏感信息
        result.getRecords().forEach(institution -> {
            institution.setPassword(null);
            institution.setUsername(null);
        });
        
        return result;
    }
    
    @Override
    public boolean updateNannyInfo(Long institutionId, Long nannyId, Nanny nanny) {
        // 查询保姆
        Nanny existingNanny = nannyMapper.selectById(nannyId);
        if (existingNanny == null || existingNanny.getDeleted() == 1) {
            throw new BusinessException("保姆不存在");
        }
        
        // 验证保姆是否属于该机构
        if (!institutionId.equals(existingNanny.getInstitutionId())) {
            throw new BusinessException("无权操作其他机构的保姆");
        }
        
        // 设置ID，确保更新正确的记录
        nanny.setId(nannyId);
        
        // 不允许修改敏感字段
        nanny.setPassword(null);
        nanny.setUsername(null);
        nanny.setInstitutionId(null); // 不允许更改所属机构
        nanny.setDeleted(null);
        
        int rows = nannyMapper.updateById(nanny);
        
        log.info("机构 {} 修改保姆 {} 信息", institutionId, nannyId);
        
        return rows > 0;
    }
    
    @Override
    public boolean deleteNanny(Long institutionId, Long nannyId) {
        // 查询保姆
        Nanny nanny = nannyMapper.selectById(nannyId);
        if (nanny == null || nanny.getDeleted() == 1) {
            throw new BusinessException("保姆不存在");
        }
        
        // 验证保姆是否属于该机构
        if (!institutionId.equals(nanny.getInstitutionId())) {
            throw new BusinessException("无权操作其他机构的保姆");
        }
        
        // 检查保姆是否有进行中的服务计划
        LambdaQueryWrapper<CarePlan> planWrapper = new LambdaQueryWrapper<>();
        planWrapper.eq(CarePlan::getNannyId, nannyId)
                   .eq(CarePlan::getDeleted, 0)
                   .in(CarePlan::getStatus, 0, 1); // 待执行或进行中
        long ongoingPlans = carePlanMapper.selectCount(planWrapper);
        if (ongoingPlans > 0) {
            throw new BusinessException("该保姆还有未完成的服务计划，无法删除");
        }
        
        // 软删除
        nanny.setDeleted((byte) 1);
        int rows = nannyMapper.updateById(nanny);
        
        log.info("机构 {} 删除保姆 {}", institutionId, nannyId);
        
        return rows > 0;
    }
    
    @Override
    public NannyPerformanceVO getNannyPerformance(Long institutionId, Long nannyId) {
        // 查询保姆
        Nanny nanny = nannyMapper.selectById(nannyId);
        if (nanny == null || nanny.getDeleted() == 1) {
            throw new BusinessException("保姆不存在");
        }
        
        // 验证保姆是否属于该机构
        if (!institutionId.equals(nanny.getInstitutionId())) {
            throw new BusinessException("无权查看其他机构的保姆绩效");
        }
        
        NannyPerformanceVO performance = new NannyPerformanceVO();
        performance.setNannyId(nannyId);
        performance.setNannyName(nanny.getRealName());
        
        // 查询服务计划统计
        LambdaQueryWrapper<CarePlan> planWrapper = new LambdaQueryWrapper<>();
        planWrapper.eq(CarePlan::getNannyId, nannyId)
                   .eq(CarePlan::getDeleted, 0);
        List<CarePlan> plans = carePlanMapper.selectList(planWrapper);
        
        // 服务家庭数（去重babyId）
        long familyCount = plans.stream()
                .map(CarePlan::getBabyId)
                .distinct()
                .count();
        performance.setServiceFamilyCount(familyCount);
        
        // 完成计划数（状态=2）
        long completedCount = plans.stream()
                .filter(p -> p.getStatus() != null && p.getStatus() == 2)
                .count();
        performance.setCompletedPlanCount(completedCount);
        
        // 进行中计划数（状态=0或1）
        long ongoingCount = plans.stream()
                .filter(p -> p.getStatus() != null && (p.getStatus() == 0 || p.getStatus() == 1))
                .count();
        performance.setOngoingPlanCount(ongoingCount);
        
        // 查询评价统计
        LambdaQueryWrapper<CareEvaluation> evalWrapper = new LambdaQueryWrapper<>();
        evalWrapper.eq(CareEvaluation::getNannyId, nannyId)
                   .eq(CareEvaluation::getDeleted, 0);
        List<CareEvaluation> evaluations = careEvaluationMapper.selectList(evalWrapper);
        
        long totalEvaluations = evaluations.size();
        performance.setTotalEvaluations(totalEvaluations);
        
        if (totalEvaluations > 0) {
            // 各星级数量
            long fiveStars = evaluations.stream().filter(e -> e.getScore() != null && e.getScore() == 5).count();
            long fourStars = evaluations.stream().filter(e -> e.getScore() != null && e.getScore() == 4).count();
            long threeStars = evaluations.stream().filter(e -> e.getScore() != null && e.getScore() == 3).count();
            long twoStars = evaluations.stream().filter(e -> e.getScore() != null && e.getScore() == 2).count();
            long oneStars = evaluations.stream().filter(e -> e.getScore() != null && e.getScore() == 1).count();
            
            performance.setFiveStarCount(fiveStars);
            performance.setFourStarCount(fourStars);
            performance.setThreeStarCount(threeStars);
            performance.setTwoStarCount(twoStars);
            performance.setOneStarCount(oneStars);
            
            // 平均评分
            double avgScore = evaluations.stream()
                    .filter(e -> e.getScore() != null)
                    .mapToInt(e -> e.getScore().intValue())
                    .average()
                    .orElse(0.0);
            performance.setAverageScore(BigDecimal.valueOf(avgScore).setScale(1, RoundingMode.HALF_UP));
            
            // 好评率（4-5星）
            long positiveCount = fiveStars + fourStars;
            double positiveRate = (double) positiveCount / totalEvaluations * 100;
            performance.setPositiveRate(BigDecimal.valueOf(positiveRate).setScale(1, RoundingMode.HALF_UP));
        } else {
            performance.setFiveStarCount(0L);
            performance.setFourStarCount(0L);
            performance.setThreeStarCount(0L);
            performance.setTwoStarCount(0L);
            performance.setOneStarCount(0L);
            performance.setAverageScore(BigDecimal.ZERO);
            performance.setPositiveRate(BigDecimal.ZERO);
        }
        
        return performance;
    }
}
