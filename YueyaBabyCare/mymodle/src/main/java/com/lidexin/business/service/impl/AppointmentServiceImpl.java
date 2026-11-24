package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lidexin.business.dto.AppointmentDTO;
import com.lidexin.business.mapper.*;
import com.lidexin.business.po.*;
import com.lidexin.business.service.IAppointmentService;
import com.lidexin.business.vo.AppointmentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 预约表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements IAppointmentService {

    @Autowired
    private BabyMapper babyMapper;

    @Autowired
    private ParentMapper parentMapper;

    @Autowired
    private InstitutionMapper institutionMapper;

    @Autowired
    private NannyMapper nannyMapper;

    @Autowired
    private AddressMapper addressMapper;

    /**
     * 服务类型枚举
     */
    private static final Map<Byte, String> SERVICE_TYPE_MAP = new HashMap<>();

    /**
     * 状态枚举
     */
    private static final Map<Byte, String> STATUS_MAP = new HashMap<>();

    static {
        SERVICE_TYPE_MAP.put((byte) 1, "日常护理");
        SERVICE_TYPE_MAP.put((byte) 2, "月嫂服务");
        SERVICE_TYPE_MAP.put((byte) 3, "育儿指导");
        SERVICE_TYPE_MAP.put((byte) 4, "其他");

        STATUS_MAP.put((byte) 0, "待确认");
        STATUS_MAP.put((byte) 1, "已确认");
        STATUS_MAP.put((byte) 2, "进行中");
        STATUS_MAP.put((byte) 3, "已完成");
        STATUS_MAP.put((byte) 4, "已取消");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAppointment(AppointmentDTO dto, Long parentId) {
        // 校验宝宝是否属于该家长
        Baby baby = babyMapper.selectById(dto.getBabyId());
        if (baby == null || baby.getDeleted() == 1 || !baby.getParentId().equals(parentId)) {
            throw new RuntimeException("宝宝信息不存在或无权操作");
        }

        // 校验机构是否存在
        Institution institution = institutionMapper.selectById(dto.getInstitutionId());
        if (institution == null || institution.getDeleted() == 1 || institution.getStatus() != 1) {
            throw new RuntimeException("机构不存在或未启用");
        }

        // 如果指定了保姆，校验保姆是否属于该机构
        if (dto.getNannyId() != null) {
            Nanny nanny = nannyMapper.selectById(dto.getNannyId());
            if (nanny == null || nanny.getDeleted() == 1 || !nanny.getInstitutionId().equals(dto.getInstitutionId())) {
                throw new RuntimeException("保姆不存在或不属于该机构");
            }
            if (nanny.getStatus() != 1) {
                throw new RuntimeException("保姆已禁用");
            }
        }

        // 校验预约日期不能早于今天
        if (dto.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("预约日期不能早于今天");
        }

        // 校验结束时间必须大于开始时间
        if (!dto.getEndTime().isAfter(dto.getStartTime())) {
            throw new RuntimeException("结束时间必须大于开始时间");
        }

        // 检查时间冲突
        if (dto.getNannyId() != null && hasTimeConflict(dto.getNannyId(), dto.getAppointmentDate(),
                dto.getStartTime(), dto.getEndTime(), null)) {
            throw new RuntimeException("该时间段保姆已有其他预约");
        }

        // 校验地址是否属于该家长
        Address address = addressMapper.selectById(dto.getAddressId());
        if (address == null || address.getDeleted() == 1 ||
            !address.getRelationId().equals(parentId) || address.getRelationType() != 1) {
            throw new RuntimeException("地址不存在或无权使用");
        }

        // 创建预约
        Appointment appointment = new Appointment();
        appointment.setAppointmentNo(generateAppointmentNo());
        appointment.setParentId(parentId);
        appointment.setBabyId(dto.getBabyId());
        appointment.setInstitutionId(dto.getInstitutionId());
        appointment.setNannyId(dto.getNannyId());
        appointment.setServiceType(dto.getServiceType());
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setStartTime(dto.getStartTime());
        appointment.setEndTime(dto.getEndTime());
        appointment.setRequirement(dto.getRequirement());
        appointment.setAddressId(dto.getAddressId());
        appointment.setStatus((byte) 0); // 待确认
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        appointment.setDeleted((byte) 0);

        this.save(appointment);
        return appointment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAppointment(Long id, AppointmentDTO dto, Long parentId) {
        Appointment appointment = this.getById(id);
        if (appointment == null || appointment.getDeleted() == 1) {
            return false;
        }

        // 校验是否是该家长的预约
        if (!appointment.getParentId().equals(parentId)) {
            return false;
        }

        // 只有待确认状态可以修改
        if (appointment.getStatus() != 0) {
            throw new RuntimeException("只有待确认的预约可以修改");
        }

        // 校验宝宝是否属于该家长
        Baby baby = babyMapper.selectById(dto.getBabyId());
        if (baby == null || baby.getDeleted() == 1 || !baby.getParentId().equals(parentId)) {
            throw new RuntimeException("宝宝信息不存在或无权操作");
        }

        // 校验机构
        Institution institution = institutionMapper.selectById(dto.getInstitutionId());
        if (institution == null || institution.getDeleted() == 1 || institution.getStatus() != 1) {
            throw new RuntimeException("机构不存在或未启用");
        }

        // 如果指定了保姆，校验保姆
        if (dto.getNannyId() != null) {
            Nanny nanny = nannyMapper.selectById(dto.getNannyId());
            if (nanny == null || nanny.getDeleted() == 1 || !nanny.getInstitutionId().equals(dto.getInstitutionId())) {
                throw new RuntimeException("保姆不存在或不属于该机构");
            }
            // 检查时间冲突，排除当前预约
            if (hasTimeConflict(dto.getNannyId(), dto.getAppointmentDate(),
                    dto.getStartTime(), dto.getEndTime(), id)) {
                throw new RuntimeException("该时间段保姆已有其他预约");
            }
        }

        // 校验地址是否属于该家长
        Address address = addressMapper.selectById(dto.getAddressId());
        if (address == null || address.getDeleted() == 1 ||
            !address.getRelationId().equals(parentId) || address.getRelationType() != 1) {
            throw new RuntimeException("地址不存在或无权使用");
        }

        // 更新预约信息
        appointment.setBabyId(dto.getBabyId());
        appointment.setInstitutionId(dto.getInstitutionId());
        appointment.setNannyId(dto.getNannyId());
        appointment.setServiceType(dto.getServiceType());
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setStartTime(dto.getStartTime());
        appointment.setEndTime(dto.getEndTime());
        appointment.setRequirement(dto.getRequirement());
        appointment.setAddressId(dto.getAddressId());
        appointment.setUpdateTime(LocalDateTime.now());

        return this.updateById(appointment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelAppointment(Long id, Long parentId, String cancelReason) {
        Appointment appointment = this.getById(id);
        if (appointment == null || appointment.getDeleted() == 1) {
            return false;
        }

        // 校验是否是该家长的预约
        if (!appointment.getParentId().equals(parentId)) {
            return false;
        }

        // 只有待确认和已确认状态可以取消
        if (appointment.getStatus() != 0 && appointment.getStatus() != 1) {
            throw new RuntimeException("当前状态不可取消");
        }

        appointment.setStatus((byte) 4); // 已取消
        appointment.setCancelReason(cancelReason);
        appointment.setUpdateTime(LocalDateTime.now());

        return this.updateById(appointment);
    }

    @Override
    public AppointmentVO getAppointmentById(Long id) {
        Appointment appointment = this.getById(id);
        if (appointment == null || appointment.getDeleted() == 1) {
            return null;
        }
        return convertToVO(appointment);
    }

    @Override
    public List<AppointmentVO> getAppointmentsByParentId(Long parentId, Byte status) {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getParentId, parentId)
               .eq(Appointment::getDeleted, 0);
        
        if (status != null) {
            wrapper.eq(Appointment::getStatus, status);
        }
        
        wrapper.orderByDesc(Appointment::getAppointmentDate)
               .orderByDesc(Appointment::getCreateTime);

        List<Appointment> appointments = this.list(wrapper);
        return appointments.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentVO> getAppointmentsByNannyId(Long nannyId, Byte status) {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getNannyId, nannyId)
               .eq(Appointment::getDeleted, 0);
        
        if (status != null) {
            wrapper.eq(Appointment::getStatus, status);
        }
        
        wrapper.orderByDesc(Appointment::getAppointmentDate)
               .orderByDesc(Appointment::getCreateTime);

        List<Appointment> appointments = this.list(wrapper);
        return appointments.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public IPage<AppointmentVO> getAppointmentsByInstitution(Page<Appointment> page, Long institutionId,
                                                              Byte status, Byte serviceType,
                                                              LocalDate startDate, LocalDate endDate,
                                                              String keyword) {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getInstitutionId, institutionId)
               .eq(Appointment::getDeleted, 0);

        if (status != null) {
            wrapper.eq(Appointment::getStatus, status);
        }
        if (serviceType != null) {
            wrapper.eq(Appointment::getServiceType, serviceType);
        }
        if (startDate != null) {
            wrapper.ge(Appointment::getAppointmentDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(Appointment::getAppointmentDate, endDate);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 搜索预约单号
            wrapper.and(w -> w.like(Appointment::getAppointmentNo, keyword.trim()));
        }

        wrapper.orderByDesc(Appointment::getCreateTime);

        IPage<Appointment> appointmentPage = this.page(page, wrapper);

        // 转换为VO
        Page<AppointmentVO> voPage = new Page<>(appointmentPage.getCurrent(), appointmentPage.getSize(), appointmentPage.getTotal());
        List<AppointmentVO> voList = appointmentPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAppointmentStatus(Long id, Byte status, Long institutionId) {
        Appointment appointment = this.getById(id);
        if (appointment == null || appointment.getDeleted() == 1) {
            return false;
        }

        // 校验是否是该机构的预约
        if (!appointment.getInstitutionId().equals(institutionId)) {
            return false;
        }

        // 状态流转校验
        if (!isValidStatusTransition(appointment.getStatus(), status)) {
            throw new RuntimeException("无效的状态变更");
        }

        appointment.setStatus(status);
        appointment.setUpdateTime(LocalDateTime.now());

        return this.updateById(appointment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignNanny(Long id, Long nannyId, Long institutionId) {
        Appointment appointment = this.getById(id);
        if (appointment == null || appointment.getDeleted() == 1) {
            return false;
        }

        // 校验是否是该机构的预约
        if (!appointment.getInstitutionId().equals(institutionId)) {
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

        // 检查时间冲突
        if (hasTimeConflict(nannyId, appointment.getAppointmentDate(), 
                appointment.getStartTime(), appointment.getEndTime(), id)) {
            throw new RuntimeException("该时间段保姆已有其他预约");
        }

        appointment.setNannyId(nannyId);
        appointment.setUpdateTime(LocalDateTime.now());

        return this.updateById(appointment);
    }

    @Override
    public List<String> getAvailableSlots(Long institutionId, Long nannyId, LocalDate date) {
        // 定义时间段（每2小时一个时间段）
        List<String> allSlots = Arrays.asList(
                "08:00-10:00", "10:00-12:00", "13:00-15:00", 
                "15:00-17:00", "17:00-19:00", "19:00-21:00"
        );

        // 如果日期是今天，过滤掉已经过去的时间段
        LocalTime now = LocalTime.now();
        List<String> availableSlots = new ArrayList<>();

        for (String slot : allSlots) {
            String[] times = slot.split("-");
            LocalTime startTime = LocalTime.parse(times[0]);
            LocalTime endTime = LocalTime.parse(times[1]);

            // 如果是今天，跳过已经开始的时间段
            if (date.equals(LocalDate.now()) && startTime.isBefore(now)) {
                continue;
            }

            // 检查该时间段是否有冲突
            if (nannyId != null) {
                if (!hasTimeConflict(nannyId, date, startTime, endTime, null)) {
                    availableSlots.add(slot);
                }
            } else {
                // 未指定保姆，返回所有时间段
                availableSlots.add(slot);
            }
        }

        return availableSlots;
    }

    @Override
    public boolean hasPermission(Long appointmentId, Integer userType, Long userId) {
        Appointment appointment = this.getById(appointmentId);
        if (appointment == null || appointment.getDeleted() == 1) {
            return false;
        }

        switch (userType) {
            case 1: // 家长
                return appointment.getParentId().equals(userId);
            case 2: // 保姆
                return userId.equals(appointment.getNannyId());
            case 3: // 机构
                return appointment.getInstitutionId().equals(userId);
            default:
                return false;
        }
    }

    @Override
    public AppointmentStatsVO getAppointmentStats(Long institutionId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getInstitutionId, institutionId)
               .eq(Appointment::getDeleted, 0);

        if (startDate != null) {
            wrapper.ge(Appointment::getAppointmentDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(Appointment::getAppointmentDate, endDate);
        }

        List<Appointment> appointments = this.list(wrapper);

        AppointmentStatsVO stats = new AppointmentStatsVO();
        stats.setTotalCount(appointments.size());
        stats.setPendingCount((int) appointments.stream().filter(a -> a.getStatus() == 0).count());
        stats.setConfirmedCount((int) appointments.stream().filter(a -> a.getStatus() == 1).count());
        stats.setInProgressCount((int) appointments.stream().filter(a -> a.getStatus() == 2).count());
        stats.setCompletedCount((int) appointments.stream().filter(a -> a.getStatus() == 3).count());
        stats.setCancelledCount((int) appointments.stream().filter(a -> a.getStatus() == 4).count());

        return stats;
    }

    // ==================== 私有方法 ====================

    /**
     * 生成预约单号
     * 格式：YY + yyyyMMddHHmmss + 4位随机数
     */
    private String generateAppointmentNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomStr = String.format("%04d", new Random().nextInt(10000));
        return "YY" + dateStr + randomStr;
    }

    /**
     * 检查保姆在指定时间段是否有冲突
     */
    private boolean hasTimeConflict(Long nannyId, LocalDate date, LocalTime startTime, LocalTime endTime, Long excludeId) {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getNannyId, nannyId)
               .eq(Appointment::getAppointmentDate, date)
               .eq(Appointment::getDeleted, 0)
               .in(Appointment::getStatus, Arrays.asList(0, 1, 2)); // 待确认、已确认、进行中

        if (excludeId != null) {
            wrapper.ne(Appointment::getId, excludeId);
        }

        List<Appointment> appointments = this.list(wrapper);

        for (Appointment appointment : appointments) {
            // 检查时间重叠
            if (!(endTime.compareTo(appointment.getStartTime()) <= 0 || 
                  startTime.compareTo(appointment.getEndTime()) >= 0)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 校验状态流转是否有效
     * 0(待确认) -> 1(已确认) / 4(已取消)
     * 1(已确认) -> 2(进行中) / 4(已取消)
     * 2(进行中) -> 3(已完成)
     */
    private boolean isValidStatusTransition(Byte currentStatus, Byte newStatus) {
        if (currentStatus.equals(newStatus)) {
            return false;
        }

        switch (currentStatus) {
            case 0: // 待确认
                return newStatus == 1 || newStatus == 4;
            case 1: // 已确认
                return newStatus == 2 || newStatus == 4;
            case 2: // 进行中
                return newStatus == 3;
            default:
                return false;
        }
    }

    /**
     * 将PO转换为VO
     */
    private AppointmentVO convertToVO(Appointment appointment) {
        AppointmentVO vo = new AppointmentVO();
        BeanUtils.copyProperties(appointment, vo);

        // 设置服务类型名称
        if (appointment.getServiceType() != null) {
            vo.setServiceTypeName(SERVICE_TYPE_MAP.getOrDefault(appointment.getServiceType(), "未知"));
        }

        // 设置状态名称
        if (appointment.getStatus() != null) {
            vo.setStatusName(STATUS_MAP.getOrDefault(appointment.getStatus(), "未知"));
        }

        // 获取家长信息
        Parent parent = parentMapper.selectById(appointment.getParentId());
        if (parent != null) {
            vo.setParentName(parent.getRealName());
            vo.setParentPhone(parent.getPhone());
        }

        // 获取宝宝信息
        Baby baby = babyMapper.selectById(appointment.getBabyId());
        if (baby != null) {
            vo.setBabyName(baby.getName());
            // 计算宝宝月龄
            if (baby.getBirthDate() != null) {
                Period period = Period.between(baby.getBirthDate(), LocalDate.now());
                vo.setBabyAgeMonths(period.getYears() * 12 + period.getMonths());
            }
        }

        // 获取机构信息
        Institution institution = institutionMapper.selectById(appointment.getInstitutionId());
        if (institution != null) {
            vo.setInstitutionName(institution.getName());
        }

        // 获取保姆信息
        if (appointment.getNannyId() != null) {
            Nanny nanny = nannyMapper.selectById(appointment.getNannyId());
            if (nanny != null) {
                vo.setNannyName(nanny.getRealName());
                vo.setNannyPhone(nanny.getPhone());
            }
        }

        // 获取地址信息
        if (appointment.getAddressId() != null) {
            Address address = addressMapper.selectById(appointment.getAddressId());
            if (address != null) {
                // 拼接完整地址
                StringBuilder fullAddress = new StringBuilder();
                if (address.getProvince() != null) {
                    fullAddress.append(address.getProvince());
                }
                if (address.getCity() != null) {
                    fullAddress.append(address.getCity());
                }
                if (address.getDistrict() != null) {
                    fullAddress.append(address.getDistrict());
                }
                if (address.getDetailAddress() != null) {
                    fullAddress.append(address.getDetailAddress());
                }
                vo.setAddress(fullAddress.toString());
                vo.setContactName(address.getContactName());
                vo.setContactPhone(address.getContactPhone());
            }
        }

        return vo;
    }
}
