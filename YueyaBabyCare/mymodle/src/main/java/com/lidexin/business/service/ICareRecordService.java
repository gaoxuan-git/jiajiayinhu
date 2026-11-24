package com.lidexin.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lidexin.business.dto.CareRecordDTO;
import com.lidexin.business.po.CareRecord;
import com.lidexin.business.vo.CareRecordVO;

import java.util.List;

/**
 * <p>
 * 护理记录表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
public interface ICareRecordService extends IService<CareRecord> {

    /**
     * 添加护理记录（保姆端）
     * 校验：必须今日已签到且未签退
     *
     * @param carePlanId 护理计划ID
     * @param nannyId    保姆ID
     * @param dto        护理记录信息
     * @return 记录ID
     */
    Long addCareRecord(Long carePlanId, Long nannyId, CareRecordDTO dto);

    /**
     * 获取护理计划的记录列表
     *
     * @param carePlanId 护理计划ID
     * @return 记录列表
     */
    List<CareRecordVO> getRecordsByCarePlanId(Long carePlanId);

    /**
     * 获取记录详情
     *
     * @param id 记录ID
     * @return 记录详情
     */
    CareRecordVO getRecordById(Long id);

    /**
     * 删除记录（只能删除自己的记录）
     *
     * @param id      记录ID
     * @param nannyId 保姆ID
     * @return 是否成功
     */
    boolean deleteRecord(Long id, Long nannyId);

    /**
     * 检查保姆是否可以执行该计划（今日已签到未签退）
     *
     * @param carePlanId 护理计划ID
     * @param nannyId    保姆ID
     * @return 是否可执行
     */
    boolean canExecutePlan(Long carePlanId, Long nannyId);

    /**
     * 修改护理记录
     *
     * @param id      记录ID
     * @param nannyId 保姆ID
     * @param dto     修改内容
     * @return 是否成功
     */
    boolean updateRecord(Long id, Long nannyId, CareRecordDTO dto);

    /**
     * 获取婴幼儿的护理记录
     *
     * @param babyId 婴幼儿ID
     * @return 记录列表
     */
    List<CareRecordVO> getRecordsByBabyId(Long babyId);

    /**
     * 获取保姆今日的护理记录
     *
     * @param nannyId 保姆ID
     * @return 今日记录列表
     */
    List<CareRecordVO> getTodayRecords(Long nannyId);

    /**
     * 获取机构下的护理记录列表
     *
     * @param institutionId 机构ID
     * @param nannyId       保姆ID（可选筛选）
     * @param babyId        婴幼儿ID（可选筛选）
     * @param careType      护理类型（可选筛选）
     * @return 记录列表
     */
    List<CareRecordVO> getRecordsByInstitution(Long institutionId, Long nannyId, Long babyId, Byte careType);
}
