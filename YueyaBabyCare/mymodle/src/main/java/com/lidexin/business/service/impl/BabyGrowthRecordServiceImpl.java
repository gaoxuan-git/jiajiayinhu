package com.lidexin.business.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lidexin.business.dto.BabyGrowthRecordDTO;
import com.lidexin.business.mapper.BabyGrowthRecordMapper;
import com.lidexin.business.mapper.BabyMapper;
import com.lidexin.business.mapper.NannyMapper;
import com.lidexin.business.mapper.ParentMapper;
import com.lidexin.business.po.Baby;
import com.lidexin.business.po.BabyGrowthRecord;
import com.lidexin.business.po.Nanny;
import com.lidexin.business.po.Parent;
import com.lidexin.business.service.IBabyGrowthRecordService;
import com.lidexin.business.vo.BabyGrowthRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 婴幼儿成长记录表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Service
public class BabyGrowthRecordServiceImpl extends ServiceImpl<BabyGrowthRecordMapper, BabyGrowthRecord> implements IBabyGrowthRecordService {

    @Autowired
    private BabyMapper babyMapper;

    @Autowired
    private ParentMapper parentMapper;

    @Autowired
    private NannyMapper nannyMapper;

    /**
     * 记录类型枚举
     */
    private static final Map<Byte, String> RECORD_TYPE_MAP = new HashMap<>();

    static {
        RECORD_TYPE_MAP.put((byte) 1, "饮食");
        RECORD_TYPE_MAP.put((byte) 2, "睡眠");
        RECORD_TYPE_MAP.put((byte) 3, "排便");
        RECORD_TYPE_MAP.put((byte) 4, "活动");
        RECORD_TYPE_MAP.put((byte) 5, "其他");
    }

    @Override
    public Long addGrowthRecord(Long babyId, BabyGrowthRecordDTO dto, Byte recorderType, Long recorderId) {
        BabyGrowthRecord record = new BabyGrowthRecord();
        record.setBabyId(babyId);
        record.setRecordDate(dto.getRecordDate());
        record.setRecordType(dto.getRecordType());
        record.setTitle(dto.getTitle());
        record.setContent(dto.getContent());
        record.setRecorderType(recorderType);
        record.setRecorderId(recorderId);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        record.setDeleted((byte) 0);

        // 处理图片列表
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            record.setImages(JSON.toJSONString(dto.getImages()));
        }

        this.save(record);
        return record.getId();
    }

    @Override
    public boolean updateGrowthRecord(Long id, BabyGrowthRecordDTO dto, Byte recorderType, Long recorderId) {
        BabyGrowthRecord record = this.getById(id);
        if (record == null || record.getDeleted() == 1) {
            return false;
        }

        // 检查是否是记录创建者
        if (!record.getRecorderType().equals(recorderType) || !record.getRecorderId().equals(recorderId)) {
            return false;
        }

        record.setRecordDate(dto.getRecordDate());
        record.setRecordType(dto.getRecordType());
        record.setTitle(dto.getTitle());
        record.setContent(dto.getContent());
        record.setUpdateTime(LocalDateTime.now());

        // 处理图片列表
        if (dto.getImages() != null) {
            record.setImages(JSON.toJSONString(dto.getImages()));
        }

        return this.updateById(record);
    }

    @Override
    public boolean deleteGrowthRecord(Long id, Byte recorderType, Long recorderId) {
        BabyGrowthRecord record = this.getById(id);
        if (record == null || record.getDeleted() == 1) {
            return false;
        }

        // 家长可以删除自己宝宝的所有成长记录
        if (recorderType == 1) {
            Baby baby = babyMapper.selectById(record.getBabyId());
            if (baby != null && baby.getParentId().equals(recorderId)) {
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
    public BabyGrowthRecordVO getGrowthRecordById(Long id) {
        BabyGrowthRecord record = this.getById(id);
        if (record == null || record.getDeleted() == 1) {
            return null;
        }
        return convertToVO(record);
    }

    @Override
    public List<BabyGrowthRecordVO> getGrowthRecordsByBabyId(Long babyId, Byte recordType) {
        LambdaQueryWrapper<BabyGrowthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BabyGrowthRecord::getBabyId, babyId)
               .eq(BabyGrowthRecord::getDeleted, 0);

        if (recordType != null) {
            wrapper.eq(BabyGrowthRecord::getRecordType, recordType);
        }

        wrapper.orderByDesc(BabyGrowthRecord::getRecordDate)
               .orderByDesc(BabyGrowthRecord::getCreateTime);

        List<BabyGrowthRecord> records = this.list(wrapper);
        return records.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<BabyGrowthRecordVO>> getGrowthRecordsTimeline(Long babyId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<BabyGrowthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BabyGrowthRecord::getBabyId, babyId)
               .eq(BabyGrowthRecord::getDeleted, 0);

        if (startDate != null) {
            wrapper.ge(BabyGrowthRecord::getRecordDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(BabyGrowthRecord::getRecordDate, endDate);
        }

        wrapper.orderByDesc(BabyGrowthRecord::getRecordDate)
               .orderByDesc(BabyGrowthRecord::getCreateTime);

        List<BabyGrowthRecord> records = this.list(wrapper);

        // 按日期分组
        Map<String, List<BabyGrowthRecordVO>> timeline = new LinkedHashMap<>();
        for (BabyGrowthRecord record : records) {
            String dateKey = record.getRecordDate().toString();
            timeline.computeIfAbsent(dateKey, k -> new ArrayList<>())
                    .add(convertToVO(record));
        }

        return timeline;
    }

    @Override
    public boolean uploadImages(Long id, List<String> imageUrls, Byte recorderType, Long recorderId) {
        BabyGrowthRecord record = this.getById(id);
        if (record == null || record.getDeleted() == 1) {
            return false;
        }

        // 检查是否是记录创建者
        if (!record.getRecorderType().equals(recorderType) || !record.getRecorderId().equals(recorderId)) {
            return false;
        }

        // 获取现有图片并合并
        List<String> existingImages = new ArrayList<>();
        if (record.getImages() != null && !record.getImages().isEmpty()) {
            existingImages = JSON.parseArray(record.getImages(), String.class);
        }
        existingImages.addAll(imageUrls);

        record.setImages(JSON.toJSONString(existingImages));
        record.setUpdateTime(LocalDateTime.now());

        return this.updateById(record);
    }

    @Override
    public IPage<BabyGrowthRecordVO> pageGrowthRecords(Page<BabyGrowthRecord> page, Long babyId, Long parentId,
                                                        Byte recordType, Byte recorderType,
                                                        LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<BabyGrowthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BabyGrowthRecord::getDeleted, 0);

        if (babyId != null) {
            wrapper.eq(BabyGrowthRecord::getBabyId, babyId);
        }

        if (parentId != null) {
            // 获取该家长的所有宝宝ID
            LambdaQueryWrapper<Baby> babyWrapper = new LambdaQueryWrapper<>();
            babyWrapper.eq(Baby::getParentId, parentId).eq(Baby::getDeleted, 0);
            List<Baby> babies = babyMapper.selectList(babyWrapper);
            List<Long> babyIds = babies.stream().map(Baby::getId).collect(Collectors.toList());
            if (!babyIds.isEmpty()) {
                wrapper.in(BabyGrowthRecord::getBabyId, babyIds);
            } else {
                return new Page<>();
            }
        }

        if (recordType != null) {
            wrapper.eq(BabyGrowthRecord::getRecordType, recordType);
        }

        if (recorderType != null) {
            wrapper.eq(BabyGrowthRecord::getRecorderType, recorderType);
        }

        if (startDate != null) {
            wrapper.ge(BabyGrowthRecord::getRecordDate, startDate);
        }

        if (endDate != null) {
            wrapper.le(BabyGrowthRecord::getRecordDate, endDate);
        }

        wrapper.orderByDesc(BabyGrowthRecord::getRecordDate)
               .orderByDesc(BabyGrowthRecord::getCreateTime);

        IPage<BabyGrowthRecord> recordPage = this.page(page, wrapper);

        // 转换为VO
        Page<BabyGrowthRecordVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        List<BabyGrowthRecordVO> voList = recordPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public List<BabyGrowthRecordVO> getGrowthRecordsByParentId(Long parentId) {
        // 获取该家长的所有宝宝
        LambdaQueryWrapper<Baby> babyWrapper = new LambdaQueryWrapper<>();
        babyWrapper.eq(Baby::getParentId, parentId).eq(Baby::getDeleted, 0);
        List<Baby> babies = babyMapper.selectList(babyWrapper);

        if (babies.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> babyIds = babies.stream().map(Baby::getId).collect(Collectors.toList());

        LambdaQueryWrapper<BabyGrowthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BabyGrowthRecord::getBabyId, babyIds)
               .eq(BabyGrowthRecord::getDeleted, 0)
               .orderByDesc(BabyGrowthRecord::getRecordDate)
               .orderByDesc(BabyGrowthRecord::getCreateTime);

        List<BabyGrowthRecord> records = this.list(wrapper);
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
    private BabyGrowthRecordVO convertToVO(BabyGrowthRecord record) {
        BabyGrowthRecordVO vo = new BabyGrowthRecordVO();
        BeanUtils.copyProperties(record, vo);

        // 获取宝宝姓名
        Baby baby = babyMapper.selectById(record.getBabyId());
        if (baby != null) {
            vo.setBabyName(baby.getName());
        }

        // 设置记录类型名称
        if (record.getRecordType() != null) {
            vo.setRecordTypeName(RECORD_TYPE_MAP.getOrDefault(record.getRecordType(), "未知"));
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

        // 解析图片JSON
        if (record.getImages() != null && !record.getImages().isEmpty()) {
            try {
                List<String> imageList = JSON.parseArray(record.getImages(), String.class);
                vo.setImages(imageList);
            } catch (Exception e) {
                vo.setImages(Collections.emptyList());
            }
        } else {
            vo.setImages(Collections.emptyList());
        }

        return vo;
    }
}
