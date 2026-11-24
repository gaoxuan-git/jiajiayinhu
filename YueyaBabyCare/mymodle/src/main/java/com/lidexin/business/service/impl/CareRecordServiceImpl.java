package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lidexin.business.dto.CareRecordDTO;
import com.lidexin.business.mapper.*;
import com.lidexin.business.po.*;
import com.lidexin.business.service.ICareRecordService;
import com.lidexin.business.vo.CareRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 护理记录表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Service
public class CareRecordServiceImpl extends ServiceImpl<CareRecordMapper, CareRecord> implements ICareRecordService {

    @Autowired
    private CarePlanMapper carePlanMapper;

    @Autowired
    private CarePlanCheckinMapper carePlanCheckinMapper;

    @Autowired
    private BabyMapper babyMapper;

    @Autowired
    private NannyMapper nannyMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    /**
     * 护理类型枚举
     */
    private static final Map<Byte, String> CARE_TYPE_MAP = new HashMap<>();

    static {
        CARE_TYPE_MAP.put((byte) 1, "喂养");
        CARE_TYPE_MAP.put((byte) 2, "洗护");
        CARE_TYPE_MAP.put((byte) 3, "哄睡");
        CARE_TYPE_MAP.put((byte) 4, "早教");
        CARE_TYPE_MAP.put((byte) 5, "其他");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addCareRecord(Long carePlanId, Long nannyId, CareRecordDTO dto) {
        // 校验护理计划
        CarePlan plan = carePlanMapper.selectById(carePlanId);
        if (plan == null || plan.getDeleted() == 1) {
            throw new RuntimeException("护理计划不存在");
        }

        // 校验保姆是否是该计划的执行人
        if (!nannyId.equals(plan.getNannyId())) {
            throw new RuntimeException("您不是该计划的执行保姆");
        }

        // 校验计划状态（待执行或1进行中可以添加记录）
        if (plan.getStatus() != 0 && plan.getStatus() != 1) {
            throw new RuntimeException("该计划状态不允许添加记录");
        }

        // 校验是否今日已签到未签退
        if (!canExecutePlan(carePlanId, nannyId)) {
            throw new RuntimeException("您今日未签到或已签退，无法添加护理记录");
        }

        // 创建护理记录
        CareRecord record = new CareRecord();
        record.setCarePlanId(carePlanId);
        record.setBabyId(plan.getBabyId());
        record.setNannyId(nannyId);
        record.setRecordDate(LocalDate.now());
        record.setRecordTime(LocalTime.now());
        record.setCareType(dto.getCareType());
        record.setCareContent(dto.getCareContent());
        record.setImages(dto.getImages());
        record.setRemark(dto.getRemark());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        record.setDeleted((byte) 0);

        this.save(record);

        // 如果计划是待执行状态，更新为进行中
        if (plan.getStatus() == 0) {
            plan.setStatus((byte) 1);
            plan.setUpdateTime(LocalDateTime.now());
            carePlanMapper.updateById(plan);
        }

        return record.getId();
    }

    @Override
    public List<CareRecordVO> getRecordsByCarePlanId(Long carePlanId) {
        LambdaQueryWrapper<CareRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CareRecord::getCarePlanId, carePlanId)
               .eq(CareRecord::getDeleted, 0)
               .orderByDesc(CareRecord::getRecordDate, CareRecord::getRecordTime);
        List<CareRecord> records = this.list(wrapper);
        return records.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public CareRecordVO getRecordById(Long id) {
        CareRecord record = this.getById(id);
        if (record == null || record.getDeleted() == 1) {
            return null;
        }
        return convertToVO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRecord(Long id, Long nannyId) {
        CareRecord record = this.getById(id);
        if (record == null || record.getDeleted() == 1) {
            return false;
        }

        // 只能删除自己的记录
        if (!nannyId.equals(record.getNannyId())) {
            throw new RuntimeException("您只能删除自己的记录");
        }

        record.setDeleted((byte) 1);
        record.setUpdateTime(LocalDateTime.now());
        return this.updateById(record);
    }

    @Override
    public boolean canExecutePlan(Long carePlanId, Long nannyId) {
        // 获取护理计划
        CarePlan plan = carePlanMapper.selectById(carePlanId);
        if (plan == null || plan.getDeleted() == 1 || plan.getAppointmentId() == null) {
            return false;
        }

        // 检查保姆是否是该计划的执行人
        if (!nannyId.equals(plan.getNannyId())) {
            return false;
        }

        // 检查今日是否已签到未签退
        Long appointmentId = plan.getAppointmentId();
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        // 查询今日最后一条签到记录
        LambdaQueryWrapper<CarePlanCheckin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarePlanCheckin::getAppointmentId, appointmentId)
               .between(CarePlanCheckin::getCheckinTime, startOfDay, endOfDay)
               .orderByDesc(CarePlanCheckin::getCheckinTime)
               .last("LIMIT 1");
        CarePlanCheckin lastCheckin = carePlanCheckinMapper.selectOne(wrapper);

        // 必须有签到记录，且最后一条是签到（不是签退）
        return lastCheckin != null && lastCheckin.getCheckinType() == 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRecord(Long id, Long nannyId, CareRecordDTO dto) {
        CareRecord record = this.getById(id);
        if (record == null || record.getDeleted() == 1) {
            throw new RuntimeException("记录不存在");
        }

        // 只能修改自己的记录
        if (!nannyId.equals(record.getNannyId())) {
            throw new RuntimeException("您只能修改自己的记录");
        }

        record.setCareType(dto.getCareType());
        record.setCareContent(dto.getCareContent());
        record.setImages(dto.getImages());
        record.setRemark(dto.getRemark());
        record.setUpdateTime(LocalDateTime.now());

        return this.updateById(record);
    }

    @Override
    public List<CareRecordVO> getRecordsByBabyId(Long babyId) {
        LambdaQueryWrapper<CareRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CareRecord::getBabyId, babyId)
               .eq(CareRecord::getDeleted, 0)
               .orderByDesc(CareRecord::getRecordDate, CareRecord::getRecordTime);
        List<CareRecord> records = this.list(wrapper);
        return records.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<CareRecordVO> getTodayRecords(Long nannyId) {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<CareRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CareRecord::getNannyId, nannyId)
               .eq(CareRecord::getRecordDate, today)
               .eq(CareRecord::getDeleted, 0)
               .orderByDesc(CareRecord::getRecordTime);
        List<CareRecord> records = this.list(wrapper);
        return records.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<CareRecordVO> getRecordsByInstitution(Long institutionId, Long nannyId, Long babyId, Byte careType) {
        // 先查询机构下的所有护理计划ID
        LambdaQueryWrapper<CarePlan> planWrapper = new LambdaQueryWrapper<>();
        planWrapper.eq(CarePlan::getInstitutionId, institutionId)
                   .eq(CarePlan::getDeleted, 0);
        List<CarePlan> plans = carePlanMapper.selectList(planWrapper);
        
        if (plans.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Long> planIds = plans.stream().map(CarePlan::getId).collect(Collectors.toList());

        // 查询护理记录
        LambdaQueryWrapper<CareRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CareRecord::getCarePlanId, planIds)
               .eq(CareRecord::getDeleted, 0);
        
        if (nannyId != null) {
            wrapper.eq(CareRecord::getNannyId, nannyId);
        }
        if (babyId != null) {
            wrapper.eq(CareRecord::getBabyId, babyId);
        }
        if (careType != null) {
            wrapper.eq(CareRecord::getCareType, careType);
        }
        
        wrapper.orderByDesc(CareRecord::getRecordDate, CareRecord::getRecordTime);
        List<CareRecord> records = this.list(wrapper);
        return records.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private CareRecordVO convertToVO(CareRecord record) {
        CareRecordVO vo = new CareRecordVO();
        BeanUtils.copyProperties(record, vo);

        // 设置护理类型名称
        if (record.getCareType() != null) {
            vo.setCareTypeName(CARE_TYPE_MAP.getOrDefault(record.getCareType(), "未知"));
        }

        // 获取计划名称
        if (record.getCarePlanId() != null) {
            CarePlan plan = carePlanMapper.selectById(record.getCarePlanId());
            if (plan != null) {
                vo.setPlanName(plan.getPlanName());
            }
        }

        // 获取宝宝名称
        if (record.getBabyId() != null) {
            Baby baby = babyMapper.selectById(record.getBabyId());
            if (baby != null) {
                vo.setBabyName(baby.getName());
            }
        }

        // 获取保姆名称
        if (record.getNannyId() != null) {
            Nanny nanny = nannyMapper.selectById(record.getNannyId());
            if (nanny != null) {
                vo.setNannyName(nanny.getRealName());
            }
        }

        return vo;
    }
}
