package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lidexin.business.dto.CarePlanDTO;
import com.lidexin.business.mapper.*;
import com.lidexin.business.po.*;
import com.lidexin.business.service.ICarePlanService;
import com.lidexin.business.vo.AppointmentVO;
import com.lidexin.business.vo.CarePlanVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 护理计划表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Service
public class CarePlanServiceImpl extends ServiceImpl<CarePlanMapper, CarePlan> implements ICarePlanService {

    @Autowired
    private BabyMapper babyMapper;

    @Autowired
    private ParentMapper parentMapper;

    @Autowired
    private InstitutionMapper institutionMapper;

    @Autowired
    private NannyMapper nannyMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    /**
     * 状态枚举
     */
    private static final Map<Byte, String> STATUS_MAP = new HashMap<>();

    static {
        STATUS_MAP.put((byte) 0, "待执行");
        STATUS_MAP.put((byte) 1, "进行中");
        STATUS_MAP.put((byte) 2, "已完成");
        STATUS_MAP.put((byte) 3, "已取消");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCarePlan(CarePlanDTO dto, Long institutionId) {
        // 校验预约是否存在且属于该机构
        Appointment appointment = appointmentMapper.selectById(dto.getAppointmentId());
        if (appointment == null || appointment.getDeleted() == 1) {
            throw new RuntimeException("预约不存在");
        }
        if (!appointment.getInstitutionId().equals(institutionId)) {
            throw new RuntimeException("无权操作该预约");
        }
        // 预约必须是已确认或进行中状态才能创建护理计划
        if (appointment.getStatus() != 1 && appointment.getStatus() != 2) {
            throw new RuntimeException("只能为已确认或进行中的预约创建护理计划");
        }

        // 检查该预约是否已经有护理计划
        LambdaQueryWrapper<CarePlan> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(CarePlan::getAppointmentId, dto.getAppointmentId())
                    .eq(CarePlan::getDeleted, 0);
        if (this.count(existWrapper) > 0) {
            throw new RuntimeException("该预约已经存在护理计划");
        }

        // 从预约获取宝宝ID和保姆ID（如果DTO没有提供）
        Long babyId = dto.getBabyId() != null ? dto.getBabyId() : appointment.getBabyId();
        Long nannyId = dto.getNannyId() != null ? dto.getNannyId() : appointment.getNannyId();

        // 校验宝宝是否存在
        Baby baby = babyMapper.selectById(babyId);
        if (baby == null || baby.getDeleted() == 1) {
            throw new RuntimeException("宝宝信息不存在");
        }

        // 如果指定了保姆，校验保姆是否属于该机构
        if (nannyId != null) {
            Nanny nanny = nannyMapper.selectById(nannyId);
            if (nanny == null || nanny.getDeleted() == 1 || !nanny.getInstitutionId().equals(institutionId)) {
                throw new RuntimeException("保姆不存在或不属于该机构");
            }
            if (nanny.getStatus() != 1) {
                throw new RuntimeException("保姆已禁用");
            }
        }

        // 校验日期
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new RuntimeException("结束日期不能早于开始日期");
        }

        // 创建护理计划
        CarePlan plan = new CarePlan();
        plan.setInstitutionId(institutionId);
        plan.setAppointmentId(dto.getAppointmentId());
        plan.setBabyId(babyId);
        plan.setNannyId(nannyId);
        plan.setPlanName(dto.getPlanName());
        plan.setPlanContent(dto.getPlanContent());
        plan.setStartDate(dto.getStartDate());
        plan.setEndDate(dto.getEndDate());
        plan.setStatus((byte) 0); // 待执行
        plan.setCreateTime(LocalDateTime.now());
        plan.setUpdateTime(LocalDateTime.now());
        plan.setDeleted((byte) 0);

        this.save(plan);
        return plan.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCarePlan(Long id, CarePlanDTO dto, Long institutionId) {
        CarePlan plan = this.getById(id);
        if (plan == null || plan.getDeleted() == 1) {
            return false;
        }

        // 校验是否是该机构的计划
        if (!plan.getInstitutionId().equals(institutionId)) {
            return false;
        }

        // 只有待执行状态可以修改
        if (plan.getStatus() != 0) {
            throw new RuntimeException("只有待执行的计划可以修改");
        }

        // 预约关联不允许修改，忽略dto中的appointmentId

        // 如果提供了babyId，校验宝宝
        Long babyId = dto.getBabyId();
        if (babyId != null) {
            Baby baby = babyMapper.selectById(babyId);
            if (baby == null || baby.getDeleted() == 1) {
                throw new RuntimeException("宝宝信息不存在");
            }
            plan.setBabyId(babyId);
        }

        // 如果指定了保姆，校验保姆
        if (dto.getNannyId() != null) {
            Nanny nanny = nannyMapper.selectById(dto.getNannyId());
            if (nanny == null || nanny.getDeleted() == 1 || !nanny.getInstitutionId().equals(institutionId)) {
                throw new RuntimeException("保姆不存在或不属于该机构");
            }
            plan.setNannyId(dto.getNannyId());
        }

        // 校验日期
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new RuntimeException("结束日期不能早于开始日期");
        }

        // 更新计划信息
        plan.setPlanName(dto.getPlanName());
        plan.setPlanContent(dto.getPlanContent());
        plan.setStartDate(dto.getStartDate());
        plan.setEndDate(dto.getEndDate());
        plan.setUpdateTime(LocalDateTime.now());

        return this.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCarePlan(Long id, Long institutionId) {
        CarePlan plan = this.getById(id);
        if (plan == null || plan.getDeleted() == 1) {
            return false;
        }

        // 校验是否是该机构的计划
        if (!plan.getInstitutionId().equals(institutionId)) {
            return false;
        }

        // 进行中的计划不能删除
        if (plan.getStatus() == 1) {
            throw new RuntimeException("进行中的计划不能删除");
        }

        return this.removeById(id);
    }

    @Override
    public CarePlanVO getCarePlanById(Long id) {
        CarePlan plan = this.getById(id);
        if (plan == null || plan.getDeleted() == 1) {
            return null;
        }
        return convertToVO(plan);
    }

    @Override
    public IPage<CarePlanVO> getCarePlansByInstitution(Page<CarePlan> page, Long institutionId,
                                                        Byte status, Long babyId, Long nannyId, String keyword) {
        LambdaQueryWrapper<CarePlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarePlan::getInstitutionId, institutionId)
               .eq(CarePlan::getDeleted, 0);

        if (status != null) {
            wrapper.eq(CarePlan::getStatus, status);
        }
        if (babyId != null) {
            wrapper.eq(CarePlan::getBabyId, babyId);
        }
        if (nannyId != null) {
            wrapper.eq(CarePlan::getNannyId, nannyId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(CarePlan::getPlanName, keyword.trim());
        }

        wrapper.orderByDesc(CarePlan::getCreateTime);

        IPage<CarePlan> planPage = this.page(page, wrapper);

        // 转换为VO
        Page<CarePlanVO> voPage = new Page<>(planPage.getCurrent(), planPage.getSize(), planPage.getTotal());
        List<CarePlanVO> voList = planPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public List<CarePlanVO> getCarePlansByParentId(Long parentId, Byte status) {
        // 获取家长的所有宝宝
        LambdaQueryWrapper<Baby> babyWrapper = new LambdaQueryWrapper<>();
        babyWrapper.eq(Baby::getParentId, parentId).eq(Baby::getDeleted, 0);
        List<Baby> babies = babyMapper.selectList(babyWrapper);

        if (babies.isEmpty()) {
            return null;
        }

        List<Long> babyIds = babies.stream().map(Baby::getId).collect(Collectors.toList());

        LambdaQueryWrapper<CarePlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CarePlan::getBabyId, babyIds)
               .eq(CarePlan::getDeleted, 0);

        if (status != null) {
            wrapper.eq(CarePlan::getStatus, status);
        }

        wrapper.orderByDesc(CarePlan::getCreateTime);

        List<CarePlan> plans = this.list(wrapper);
        return plans.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<CarePlanVO> getCarePlansByNannyId(Long nannyId, Byte status) {
        LambdaQueryWrapper<CarePlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarePlan::getNannyId, nannyId)
               .eq(CarePlan::getDeleted, 0);

        if (status != null) {
            wrapper.eq(CarePlan::getStatus, status);
        }

        wrapper.orderByDesc(CarePlan::getCreateTime);

        List<CarePlan> plans = this.list(wrapper);
        return plans.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCarePlanStatus(Long id, Byte status, Long institutionId) {
        CarePlan plan = this.getById(id);
        if (plan == null || plan.getDeleted() == 1) {
            return false;
        }

        // 校验是否是该机构的计划
        if (!plan.getInstitutionId().equals(institutionId)) {
            return false;
        }

        // 状态流转校验
        if (!isValidStatusTransition(plan.getStatus(), status)) {
            throw new RuntimeException("无效的状态变更");
        }

        plan.setStatus(status);
        plan.setUpdateTime(LocalDateTime.now());

        return this.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignNanny(Long id, Long nannyId, Long institutionId) {
        CarePlan plan = this.getById(id);
        if (plan == null || plan.getDeleted() == 1) {
            return false;
        }

        // 校验是否是该机构的计划
        if (!plan.getInstitutionId().equals(institutionId)) {
            return false;
        }

        // 校验保姆是否属于该机构
        Nanny nanny = nannyMapper.selectById(nannyId);
        if (nanny == null || nanny.getDeleted() == 1 || !nanny.getInstitutionId().equals(institutionId)) {
            throw new RuntimeException("保姆不存在或不属于该机构");
        }
        if (nanny.getStatus() != 1) {
            throw new RuntimeException("保姆已禁用");
        }

        plan.setNannyId(nannyId);
        plan.setUpdateTime(LocalDateTime.now());

        return this.updateById(plan);
    }

    @Override
    public boolean hasPermission(Long planId, Integer userType, Long userId) {
        CarePlan plan = this.getById(planId);
        if (plan == null || plan.getDeleted() == 1) {
            return false;
        }

        switch (userType) {
            case 1: // 家长 - 检查宝宝是否属于该家长
                Baby baby = babyMapper.selectById(plan.getBabyId());
                return baby != null && baby.getParentId().equals(userId);
            case 2: // 保姆
                return userId.equals(plan.getNannyId());
            case 3: // 机构
                return plan.getInstitutionId().equals(userId);
            default:
                return false;
        }
    }

    @Override
    public List<AppointmentVO> getAvailableAppointmentsForPlan(Long institutionId) {
        // 查询该机构下已确认(1)或进行中(2)状态的预约
        LambdaQueryWrapper<Appointment> appointmentWrapper = new LambdaQueryWrapper<>();
        appointmentWrapper.eq(Appointment::getInstitutionId, institutionId)
                          .eq(Appointment::getDeleted, 0)
                          .in(Appointment::getStatus, 1, 2)
                          .orderByDesc(Appointment::getCreateTime);
        List<Appointment> appointments = appointmentMapper.selectList(appointmentWrapper);

        if (appointments.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询已经关联护理计划的预约ID
        List<Long> appointmentIds = appointments.stream().map(Appointment::getId).collect(Collectors.toList());
        LambdaQueryWrapper<CarePlan> planWrapper = new LambdaQueryWrapper<>();
        planWrapper.in(CarePlan::getAppointmentId, appointmentIds)
                   .eq(CarePlan::getDeleted, 0);
        List<CarePlan> existingPlans = this.list(planWrapper);
        List<Long> usedAppointmentIds = existingPlans.stream()
                .map(CarePlan::getAppointmentId)
                .collect(Collectors.toList());

        // 过滤掉已经创建过护理计划的预约
        List<Appointment> availableAppointments = appointments.stream()
                .filter(a -> !usedAppointmentIds.contains(a.getId()))
                .collect(Collectors.toList());

        // 转换为VO
        return availableAppointments.stream().map(this::convertAppointmentToVO).collect(Collectors.toList());
    }

    // ==================== 私有方法 ====================

    /**
     * 校验状态流转是否有效
     * 0(待执行) -> 1(进行中) / 3(已取消)
     * 1(进行中) -> 2(已完成) / 3(已取消)
     */
    private boolean isValidStatusTransition(Byte currentStatus, Byte newStatus) {
        if (currentStatus.equals(newStatus)) {
            return false;
        }

        switch (currentStatus) {
            case 0: // 待执行
                return newStatus == 1 || newStatus == 3;
            case 1: // 进行中
                return newStatus == 2 || newStatus == 3;
            default:
                return false;
        }
    }

    /**
     * 将PO转换为VO
     */
    private CarePlanVO convertToVO(CarePlan plan) {
        CarePlanVO vo = new CarePlanVO();
        BeanUtils.copyProperties(plan, vo);

        // 设置状态名称
        if (plan.getStatus() != null) {
            vo.setStatusName(STATUS_MAP.getOrDefault(plan.getStatus(), "未知"));
        }

        // 获取机构信息
        Institution institution = institutionMapper.selectById(plan.getInstitutionId());
        if (institution != null) {
            vo.setInstitutionName(institution.getName());
        }

        // 获取宝宝和家长信息
        Baby baby = babyMapper.selectById(plan.getBabyId());
        if (baby != null) {
            vo.setBabyName(baby.getName());
            vo.setParentId(baby.getParentId());

            Parent parent = parentMapper.selectById(baby.getParentId());
            if (parent != null) {
                vo.setParentName(parent.getRealName());
                vo.setParentPhone(parent.getPhone());
            }
        }

        // 获取保姆信息
        if (plan.getNannyId() != null) {
            Nanny nanny = nannyMapper.selectById(plan.getNannyId());
            if (nanny != null) {
                vo.setNannyName(nanny.getRealName());
                vo.setNannyPhone(nanny.getPhone());
            }
        }

        // 获取预约单号
        if (plan.getAppointmentId() != null) {
            Appointment appointment = appointmentMapper.selectById(plan.getAppointmentId());
            if (appointment != null) {
                vo.setAppointmentNo(appointment.getAppointmentNo());
            }
        }

        // 计算执行进度
        if (plan.getStartDate() != null && plan.getEndDate() != null) {
            long totalDays = ChronoUnit.DAYS.between(plan.getStartDate(), plan.getEndDate()) + 1;
            vo.setTotalDays((int) totalDays);

            LocalDate today = LocalDate.now();
            if (today.isBefore(plan.getStartDate())) {
                vo.setExecutionDays(0);
                vo.setProgressPercent(0);
            } else if (today.isAfter(plan.getEndDate())) {
                vo.setExecutionDays((int) totalDays);
                vo.setProgressPercent(100);
            } else {
                long executionDays = ChronoUnit.DAYS.between(plan.getStartDate(), today) + 1;
                vo.setExecutionDays((int) executionDays);
                vo.setProgressPercent((int) (executionDays * 100 / totalDays));
            }
        }

        return vo;
    }

    /**
     * 将预约PO转换为VO
     */
    private AppointmentVO convertAppointmentToVO(Appointment appointment) {
        AppointmentVO vo = new AppointmentVO();
        BeanUtils.copyProperties(appointment, vo);

        // 设置状态名称
        String[] statusNames = {"待确认", "已确认", "进行中", "已完成", "已取消"};
        if (appointment.getStatus() != null && appointment.getStatus() < statusNames.length) {
            vo.setStatusName(statusNames[appointment.getStatus()]);
        }

        // 设置服务类型名称
        String[] serviceTypeNames = {"", "日常护理", "月嫂服务", "育儿指导", "其他"};
        if (appointment.getServiceType() != null && appointment.getServiceType() < serviceTypeNames.length) {
            vo.setServiceTypeName(serviceTypeNames[appointment.getServiceType()]);
        }

        // 获取宝宝和家长信息
        Baby baby = babyMapper.selectById(appointment.getBabyId());
        if (baby != null) {
            vo.setBabyName(baby.getName());
            // 计算月龄
            if (baby.getBirthDate() != null) {
                long months = ChronoUnit.MONTHS.between(baby.getBirthDate(), LocalDate.now());
                vo.setBabyAgeMonths((int) months);
            }

            Parent parent = parentMapper.selectById(baby.getParentId());
            if (parent != null) {
                vo.setParentId(parent.getId());
                vo.setParentName(parent.getRealName());
                vo.setParentPhone(parent.getPhone());
            }
        }

        // 获取保姆信息
        if (appointment.getNannyId() != null) {
            Nanny nanny = nannyMapper.selectById(appointment.getNannyId());
            if (nanny != null) {
                vo.setNannyName(nanny.getRealName());
                vo.setNannyPhone(nanny.getPhone());
            }
        }

        return vo;
    }
}
