package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lidexin.business.mapper.NannyMapper;
import com.lidexin.business.po.Nanny;
import com.lidexin.business.po.NannySchedule;
import com.lidexin.business.mapper.NannyScheduleMapper;
import com.lidexin.business.service.INannyScheduleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lidexin.business.vo.NannyScheduleVO;
import com.lidexin.mymodle.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 保姆排班表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Slf4j
@Service
public class NannyScheduleServiceImpl extends ServiceImpl<NannyScheduleMapper, NannySchedule> implements INannyScheduleService {
    
    @Autowired
    private NannyMapper nannyMapper;
    
    // 班次默认时间
    private static final Map<Byte, LocalTime[]> SHIFT_TIMES = new HashMap<>();
    static {
        SHIFT_TIMES.put((byte) 1, new LocalTime[]{LocalTime.of(8, 0), LocalTime.of(12, 0)});   // 早班
        SHIFT_TIMES.put((byte) 2, new LocalTime[]{LocalTime.of(12, 0), LocalTime.of(18, 0)});  // 中班
        SHIFT_TIMES.put((byte) 3, new LocalTime[]{LocalTime.of(18, 0), LocalTime.of(22, 0)});  // 晚班
        SHIFT_TIMES.put((byte) 4, new LocalTime[]{LocalTime.of(8, 0), LocalTime.of(22, 0)});   // 全天
    }
    
    @Override
    @Transactional
    public boolean createSchedule(Long institutionId, NannySchedule schedule) {
        // 验证保姆是否属于该机构
        validateNannyBelongsToInstitution(institutionId, schedule.getNannyId());
        
        // 检测冲突
        List<NannyScheduleVO> conflicts = checkConflicts(institutionId, schedule.getNannyId(), 
                schedule.getScheduleDate(), schedule.getShiftType(), null);
        if (!conflicts.isEmpty()) {
            throw new BusinessException("排班冲突，该保姆在此时间段已有排班");
        }
        
        // 设置默认时间
        setDefaultTimes(schedule);
        
        schedule.setInstitutionId(institutionId);
        schedule.setStatus((byte) 1); // 正常状态
        schedule.setDeleted((byte) 0);
        schedule.setCreateTime(LocalDateTime.now());
        schedule.setUpdateTime(LocalDateTime.now());
        
        boolean result = this.save(schedule);
        log.info("机构 {} 新增排班，保姆ID: {}，日期: {}", institutionId, schedule.getNannyId(), schedule.getScheduleDate());
        return result;
    }
    
    @Override
    @Transactional
    public boolean updateSchedule(Long institutionId, Long scheduleId, NannySchedule schedule) {
        NannySchedule existing = this.getById(scheduleId);
        if (existing == null || existing.getDeleted() == 1) {
            throw new BusinessException("排班不存在");
        }
        if (!institutionId.equals(existing.getInstitutionId())) {
            throw new BusinessException("无权修改其他机构的排班");
        }
        
        // 如果修改了保姆或时间，需要检测冲突
        if (schedule.getNannyId() != null || schedule.getScheduleDate() != null || schedule.getShiftType() != null) {
            Long nannyId = schedule.getNannyId() != null ? schedule.getNannyId() : existing.getNannyId();
            LocalDate date = schedule.getScheduleDate() != null ? schedule.getScheduleDate() : existing.getScheduleDate();
            Byte shiftType = schedule.getShiftType() != null ? schedule.getShiftType() : existing.getShiftType();
            
            if (schedule.getNannyId() != null) {
                validateNannyBelongsToInstitution(institutionId, nannyId);
            }
            
            List<NannyScheduleVO> conflicts = checkConflicts(institutionId, nannyId, date, shiftType, scheduleId);
            if (!conflicts.isEmpty()) {
                throw new BusinessException("排班冲突，该保姆在此时间段已有排班");
            }
        }
        
        schedule.setId(scheduleId);
        schedule.setInstitutionId(null); // 不允许修改机构
        schedule.setUpdateTime(LocalDateTime.now());
        
        // 如果修改了班次类型，更新默认时间
        if (schedule.getShiftType() != null) {
            setDefaultTimes(schedule);
        }
        
        return this.updateById(schedule);
    }
    
    @Override
    @Transactional
    public boolean deleteSchedule(Long institutionId, Long scheduleId) {
        NannySchedule schedule = this.getById(scheduleId);
        if (schedule == null || schedule.getDeleted() == 1) {
            throw new BusinessException("排班不存在");
        }
        if (!institutionId.equals(schedule.getInstitutionId())) {
            throw new BusinessException("无权删除其他机构的排班");
        }
        
        // 软删除
        schedule.setDeleted((byte) 1);
        schedule.setUpdateTime(LocalDateTime.now());
        return this.updateById(schedule);
    }
    
    @Override
    public Page<NannyScheduleVO> getScheduleList(Long institutionId, Long nannyId, LocalDate startDate, 
                                                  LocalDate endDate, Integer pageNum, Integer pageSize) {
        Page<NannySchedule> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<NannySchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NannySchedule::getInstitutionId, institutionId)
               .eq(NannySchedule::getDeleted, 0);
        
        if (nannyId != null) {
            wrapper.eq(NannySchedule::getNannyId, nannyId);
        }
        if (startDate != null) {
            wrapper.ge(NannySchedule::getScheduleDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(NannySchedule::getScheduleDate, endDate);
        }
        
        wrapper.orderByDesc(NannySchedule::getScheduleDate)
               .orderByAsc(NannySchedule::getShiftType);
        
        Page<NannySchedule> result = this.page(page, wrapper);
        
        // 转换为VO
        Page<NannyScheduleVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
        
        return voPage;
    }
    
    @Override
    public Map<LocalDate, List<NannyScheduleVO>> getCalendarView(Long institutionId, Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        LambdaQueryWrapper<NannySchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NannySchedule::getInstitutionId, institutionId)
               .eq(NannySchedule::getDeleted, 0)
               .ge(NannySchedule::getScheduleDate, startDate)
               .le(NannySchedule::getScheduleDate, endDate)
               .orderByAsc(NannySchedule::getScheduleDate)
               .orderByAsc(NannySchedule::getShiftType);
        
        List<NannySchedule> schedules = this.list(wrapper);
        
        return schedules.stream()
                .map(this::convertToVO)
                .collect(Collectors.groupingBy(NannyScheduleVO::getScheduleDate));
    }
    
    @Override
    public List<NannyScheduleVO> getNannySchedule(Long nannyId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<NannySchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NannySchedule::getNannyId, nannyId)
               .eq(NannySchedule::getDeleted, 0)
               .ne(NannySchedule::getStatus, 0); // 排除已取消
        
        if (startDate != null) {
            wrapper.ge(NannySchedule::getScheduleDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(NannySchedule::getScheduleDate, endDate);
        }
        
        wrapper.orderByAsc(NannySchedule::getScheduleDate)
               .orderByAsc(NannySchedule::getShiftType);
        
        return this.list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public boolean applySwap(Long nannyId, Long scheduleId, Long targetNannyId, String reason) {
        NannySchedule schedule = this.getById(scheduleId);
        if (schedule == null || schedule.getDeleted() == 1) {
            throw new BusinessException("排班不存在");
        }
        if (!nannyId.equals(schedule.getNannyId())) {
            throw new BusinessException("无权申请调班");
        }
        if (schedule.getStatus() != 1) {
            throw new BusinessException("当前排班状态不允许调班");
        }
        if (targetNannyId != null && nannyId.equals(targetNannyId)) {
            throw new BusinessException("不能与自己调班");
        }
        
        // 如果指定了目标保姆，验证是否属于同一机构
        if (targetNannyId != null) {
            validateNannyBelongsToInstitution(schedule.getInstitutionId(), targetNannyId);
        }
        
        schedule.setStatus((byte) 2); // 调班申请中
        schedule.setSwapTargetNannyId(targetNannyId);
        schedule.setSwapReason(reason);
        schedule.setUpdateTime(LocalDateTime.now());
        
        log.info("保姆 {} 申请调班，排班ID: {}，目标保姆: {}", nannyId, scheduleId, targetNannyId);
        return this.updateById(schedule);
    }
    
    @Override
    @Transactional
    public boolean approveSwap(Long institutionId, Long scheduleId, boolean approved) {
        NannySchedule schedule = this.getById(scheduleId);
        if (schedule == null || schedule.getDeleted() == 1) {
            throw new BusinessException("排班不存在");
        }
        if (!institutionId.equals(schedule.getInstitutionId())) {
            throw new BusinessException("无权审核其他机构的调班申请");
        }
        if (schedule.getStatus() != 2) {
            throw new BusinessException("该排班没有待审核的调班申请");
        }
        
        if (approved) {
            // 检测目标保姆是否有冲突
            List<NannyScheduleVO> conflicts = checkConflicts(institutionId, schedule.getSwapTargetNannyId(),
                    schedule.getScheduleDate(), schedule.getShiftType(), null);
            if (!conflicts.isEmpty()) {
                throw new BusinessException("目标保姆在此时间段已有排班，无法调班");
            }
            
            // 更换保姆
            schedule.setNannyId(schedule.getSwapTargetNannyId());
            schedule.setStatus((byte) 3); // 调班已批准
        } else {
            schedule.setStatus((byte) 1); // 恢复正常状态
            schedule.setSwapTargetNannyId(null);
            schedule.setSwapReason(null);
        }
        
        schedule.setUpdateTime(LocalDateTime.now());
        log.info("机构 {} 审核调班申请，排班ID: {}，结果: {}", institutionId, scheduleId, approved ? "批准" : "拒绝");
        return this.updateById(schedule);
    }
    
    @Override
    public Page<NannyScheduleVO> getSwapRequests(Long institutionId, Integer pageNum, Integer pageSize) {
        Page<NannySchedule> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<NannySchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NannySchedule::getInstitutionId, institutionId)
               .eq(NannySchedule::getStatus, 2) // 调班申请中
               .eq(NannySchedule::getDeleted, 0)
               .orderByDesc(NannySchedule::getUpdateTime);
        
        Page<NannySchedule> result = this.page(page, wrapper);
        
        Page<NannyScheduleVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
        
        return voPage;
    }
    
    @Override
    public List<NannyScheduleVO> checkConflicts(Long institutionId, Long nannyId, LocalDate scheduleDate, 
                                                 Byte shiftType, Long excludeId) {
        LambdaQueryWrapper<NannySchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NannySchedule::getInstitutionId, institutionId)
               .eq(NannySchedule::getNannyId, nannyId)
               .eq(NannySchedule::getScheduleDate, scheduleDate)
               .eq(NannySchedule::getDeleted, 0)
               .ne(NannySchedule::getStatus, 0); // 排除已取消
        
        if (excludeId != null) {
            wrapper.ne(NannySchedule::getId, excludeId);
        }
        
        List<NannySchedule> existingSchedules = this.list(wrapper);
        
        // 检测时间冲突
        return existingSchedules.stream()
                .filter(s -> hasTimeConflict(s.getShiftType(), shiftType))
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public int batchCreateSchedule(Long institutionId, List<NannySchedule> schedules) {
        int successCount = 0;
        for (NannySchedule schedule : schedules) {
            try {
                createSchedule(institutionId, schedule);
                successCount++;
            } catch (Exception e) {
                log.warn("批量排班失败: {}", e.getMessage());
            }
        }
        return successCount;
    }
    
    /**
     * 验证保姆是否属于机构
     */
    private void validateNannyBelongsToInstitution(Long institutionId, Long nannyId) {
        Nanny nanny = nannyMapper.selectById(nannyId);
        if (nanny == null || nanny.getDeleted() == 1) {
            throw new BusinessException("保姆不存在");
        }
        if (!institutionId.equals(nanny.getInstitutionId())) {
            throw new BusinessException("该保姆不属于您的机构");
        }
        if (nanny.getStatus() != 1) {
            throw new BusinessException("该保姆状态异常，无法排班");
        }
    }
    
    /**
     * 设置默认上下班时间
     */
    private void setDefaultTimes(NannySchedule schedule) {
        if (schedule.getShiftType() != null && SHIFT_TIMES.containsKey(schedule.getShiftType())) {
            LocalTime[] times = SHIFT_TIMES.get(schedule.getShiftType());
            if (schedule.getStartTime() == null) {
                schedule.setStartTime(times[0]);
            }
            if (schedule.getEndTime() == null) {
                schedule.setEndTime(times[1]);
            }
        }
    }
    
    /**
     * 检测班次时间冲突
     */
    private boolean hasTimeConflict(Byte existingShiftType, Byte newShiftType) {
        // 全天班与任何班次都冲突
        if (existingShiftType == 4 || newShiftType == 4) {
            return true;
        }
        // 同一班次冲突
        return existingShiftType.equals(newShiftType);
    }
    
    @Override
    public List<Nanny> getSwapNannies(Long nannyId, Long scheduleId) {
        // 获取排班信息
        NannySchedule schedule = this.getById(scheduleId);
        if (schedule == null || schedule.getDeleted() == 1) {
            throw new BusinessException("排班不存在");
        }
        if (!nannyId.equals(schedule.getNannyId())) {
            throw new BusinessException("无权操作此排班");
        }
        
        // 获取同机构内其他状态正常的保姆
        LambdaQueryWrapper<Nanny> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Nanny::getInstitutionId, schedule.getInstitutionId())
               .eq(Nanny::getStatus, 1) // 状态正常
               .eq(Nanny::getDeleted, 0)
               .ne(Nanny::getId, nannyId); // 排除自己
        
        return nannyMapper.selectList(wrapper);
    }
    
    /**
     * 转换为VO
     */
    private NannyScheduleVO convertToVO(NannySchedule schedule) {
        NannyScheduleVO vo = new NannyScheduleVO();
        vo.setId(schedule.getId());
        vo.setInstitutionId(schedule.getInstitutionId());
        vo.setNannyId(schedule.getNannyId());
        vo.setScheduleDate(schedule.getScheduleDate());
        vo.setShiftType(schedule.getShiftType());
        vo.setShiftTypeName(NannyScheduleVO.getShiftTypeName(schedule.getShiftType()));
        vo.setStartTime(schedule.getStartTime());
        vo.setEndTime(schedule.getEndTime());
        vo.setStatus(schedule.getStatus());
        vo.setStatusName(NannyScheduleVO.getStatusName(schedule.getStatus()));
        vo.setSwapTargetNannyId(schedule.getSwapTargetNannyId());
        vo.setSwapReason(schedule.getSwapReason());
        vo.setRemark(schedule.getRemark());
        
        if (schedule.getCreateTime() != null) {
            vo.setCreateTime(schedule.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        
        // 获取保姆信息
        Nanny nanny = nannyMapper.selectById(schedule.getNannyId());
        if (nanny != null) {
            vo.setNannyName(nanny.getRealName());
            vo.setNannyPhone(nanny.getPhone());
        }
        
        // 获取调班目标保姆信息
        if (schedule.getSwapTargetNannyId() != null) {
            Nanny targetNanny = nannyMapper.selectById(schedule.getSwapTargetNannyId());
            if (targetNanny != null) {
                vo.setSwapTargetNannyName(targetNanny.getRealName());
            }
        }
        
        return vo;
    }
}
