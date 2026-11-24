package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lidexin.business.dto.BabyHealthRecordDTO;
import com.lidexin.business.mapper.BabyHealthRecordMapper;
import com.lidexin.business.mapper.BabyMapper;
import com.lidexin.business.mapper.NannyMapper;
import com.lidexin.business.mapper.ParentMapper;
import com.lidexin.business.po.Baby;
import com.lidexin.business.po.BabyHealthRecord;
import com.lidexin.business.po.Nanny;
import com.lidexin.business.po.Parent;
import com.lidexin.business.service.IBabyHealthRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lidexin.business.vo.BabyHealthRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 婴幼儿健康档案表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Service
public class BabyHealthRecordServiceImpl extends ServiceImpl<BabyHealthRecordMapper, BabyHealthRecord> implements IBabyHealthRecordService {

    @Autowired
    private BabyMapper babyMapper;

    @Autowired
    private ParentMapper parentMapper;

    @Autowired
    private NannyMapper nannyMapper;

    @Override
    public Long addHealthRecord(Long babyId, BabyHealthRecordDTO dto, Byte recorderType, Long recorderId) {
        BabyHealthRecord record = new BabyHealthRecord();
        BeanUtils.copyProperties(dto, record);
        record.setBabyId(babyId);
        record.setRecorderType(recorderType);
        record.setRecorderId(recorderId);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        record.setDeleted((byte) 0);

        this.save(record);
        return record.getId();
    }

    @Override
    public boolean updateHealthRecord(Long id, BabyHealthRecordDTO dto, Byte recorderType, Long recorderId) {
        BabyHealthRecord record = this.getById(id);
        if (record == null || record.getDeleted() == 1) {
            return false;
        }

        // 检查是否是记录创建者
        if (!record.getRecorderType().equals(recorderType) || !record.getRecorderId().equals(recorderId)) {
            return false;
        }

        BeanUtils.copyProperties(dto, record, "id");
        record.setUpdateTime(LocalDateTime.now());

        return this.updateById(record);
    }

    @Override
    public boolean deleteHealthRecord(Long id, Byte recorderType, Long recorderId) {
        BabyHealthRecord record = this.getById(id);
        if (record == null || record.getDeleted() == 1) {
            return false;
        }

        // 家长可以删除自己宝宝的所有健康记录
        if (recorderType == 1) {
            Baby baby = babyMapper.selectById(record.getBabyId());
            if (baby != null && baby.getParentId().equals(recorderId)) {
                // 使用 removeById 执行逻辑删除
                return this.removeById(id);
            }
        }

        // 保姆只能删除自己创建的记录
        if (recorderType == 2) {
            if (record.getRecorderType().equals(recorderType) && record.getRecorderId().equals(recorderId)) {
                return this.removeById(id);
            }
        }

        return false;
    }

    @Override
    public BabyHealthRecordVO getHealthRecordById(Long id) {
        BabyHealthRecord record = this.getById(id);
        if (record == null || record.getDeleted() == 1) {
            return null;
        }
        return convertToVO(record);
    }

    @Override
    public List<BabyHealthRecordVO> getHealthRecordsByBabyId(Long babyId) {
        LambdaQueryWrapper<BabyHealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BabyHealthRecord::getBabyId, babyId)
               .eq(BabyHealthRecord::getDeleted, 0)
               .orderByDesc(BabyHealthRecord::getRecordDate);

        List<BabyHealthRecord> records = this.list(wrapper);
        return records.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public BabyHealthRecordVO getLatestHealthRecord(Long babyId) {
        LambdaQueryWrapper<BabyHealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BabyHealthRecord::getBabyId, babyId)
               .eq(BabyHealthRecord::getDeleted, 0)
               .orderByDesc(BabyHealthRecord::getRecordDate)
               .last("LIMIT 1");

        BabyHealthRecord record = this.getOne(wrapper);
        return record != null ? convertToVO(record) : null;
    }

    @Override
    public Map<String, Object> getHealthChartData(Long babyId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<BabyHealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BabyHealthRecord::getBabyId, babyId)
               .eq(BabyHealthRecord::getDeleted, 0);

        if (startDate != null) {
            wrapper.ge(BabyHealthRecord::getRecordDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(BabyHealthRecord::getRecordDate, endDate);
        }
        wrapper.orderByAsc(BabyHealthRecord::getRecordDate);

        List<BabyHealthRecord> records = this.list(wrapper);

        Map<String, Object> chartData = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Object> heights = new ArrayList<>();
        List<Object> weights = new ArrayList<>();
        List<Object> headCircumferences = new ArrayList<>();
        List<Object> temperatures = new ArrayList<>();

        for (BabyHealthRecord record : records) {
            dates.add(record.getRecordDate().toString());
            heights.add(record.getHeight());
            weights.add(record.getWeight());
            headCircumferences.add(record.getHeadCircumference());
            temperatures.add(record.getTemperature());
        }

        chartData.put("dates", dates);
        chartData.put("heights", heights);
        chartData.put("weights", weights);
        chartData.put("headCircumferences", headCircumferences);
        chartData.put("temperatures", temperatures);

        return chartData;
    }

    @Override
    public IPage<BabyHealthRecordVO> pageHealthRecords(Page<BabyHealthRecord> page, Long babyId, Long parentId,
                                                        Byte recorderType, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<BabyHealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BabyHealthRecord::getDeleted, 0);

        if (babyId != null) {
            wrapper.eq(BabyHealthRecord::getBabyId, babyId);
        }

        if (parentId != null) {
            // 获取该家长的所有宝宝ID
            LambdaQueryWrapper<Baby> babyWrapper = new LambdaQueryWrapper<>();
            babyWrapper.eq(Baby::getParentId, parentId).eq(Baby::getDeleted, 0);
            List<Baby> babies = babyMapper.selectList(babyWrapper);
            List<Long> babyIds = babies.stream().map(Baby::getId).collect(Collectors.toList());
            if (!babyIds.isEmpty()) {
                wrapper.in(BabyHealthRecord::getBabyId, babyIds);
            } else {
                // 没有宝宝，返回空结果
                return new Page<>();
            }
        }

        if (recorderType != null) {
            wrapper.eq(BabyHealthRecord::getRecorderType, recorderType);
        }

        if (startDate != null) {
            wrapper.ge(BabyHealthRecord::getRecordDate, startDate);
        }

        if (endDate != null) {
            wrapper.le(BabyHealthRecord::getRecordDate, endDate);
        }

        wrapper.orderByDesc(BabyHealthRecord::getRecordDate);

        IPage<BabyHealthRecord> recordPage = this.page(page, wrapper);

        // 转换为VO
        Page<BabyHealthRecordVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        List<BabyHealthRecordVO> voList = recordPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public List<BabyHealthRecordVO> getHealthRecordsByParentId(Long parentId) {
        // 获取该家长的所有宝宝
        LambdaQueryWrapper<Baby> babyWrapper = new LambdaQueryWrapper<>();
        babyWrapper.eq(Baby::getParentId, parentId).eq(Baby::getDeleted, 0);
        List<Baby> babies = babyMapper.selectList(babyWrapper);

        if (babies.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> babyIds = babies.stream().map(Baby::getId).collect(Collectors.toList());

        LambdaQueryWrapper<BabyHealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BabyHealthRecord::getBabyId, babyIds)
               .eq(BabyHealthRecord::getDeleted, 0)
               .orderByDesc(BabyHealthRecord::getRecordDate);

        List<BabyHealthRecord> records = this.list(wrapper);
        return records.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public boolean hasPermission(Long babyId, Byte userType, Long userId) {
        Baby baby = babyMapper.selectById(babyId);
        if (baby == null || baby.getDeleted() == 1) {
            return false;
        }

        // 家长类型
        if (userType == 1) {
            return baby.getParentId().equals(userId);
        }

        // 保姆类型 - 需要检查是否被分配照顾该宝宝（这里简化处理，实际需要查询护理计划）
        if (userType == 2) {
            // TODO: 检查保姆是否被分配照顾该宝宝
            return true;
        }

        return false;
    }

    /**
     * 将PO转换为VO
     */
    private BabyHealthRecordVO convertToVO(BabyHealthRecord record) {
        BabyHealthRecordVO vo = new BabyHealthRecordVO();
        BeanUtils.copyProperties(record, vo);

        // 获取宝宝姓名
        Baby baby = babyMapper.selectById(record.getBabyId());
        if (baby != null) {
            vo.setBabyName(baby.getName());
        }

        // 设置记录人类型名称
        if (record.getRecorderType() != null) {
            vo.setRecorderTypeName(record.getRecorderType() == 1 ? "家长" : "保姆");
        }

        // 获取记录人姓名
        if (record.getRecorderId() != null && record.getRecorderType() != null) {
            if (record.getRecorderType() == 1) {
                Parent parent = parentMapper.selectById(record.getRecorderId());
                if (parent != null) {
                    vo.setRecorderName(parent.getRealName());
                }
            } else if (record.getRecorderType() == 2) {
                Nanny nanny = nannyMapper.selectById(record.getRecorderId());
                if (nanny != null) {
                    vo.setRecorderName(nanny.getRealName());
                }
            }
        }

        return vo;
    }
}
