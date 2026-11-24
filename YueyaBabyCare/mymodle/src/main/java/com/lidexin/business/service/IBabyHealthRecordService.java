package com.lidexin.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lidexin.business.dto.BabyHealthRecordDTO;
import com.lidexin.business.po.BabyHealthRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lidexin.business.vo.BabyHealthRecordVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 婴幼儿健康档案表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
public interface IBabyHealthRecordService extends IService<BabyHealthRecord> {

    /**
     * 新增健康记录
     *
     * @param babyId       宝宝ID
     * @param dto          健康记录DTO
     * @param recorderType 记录人类型（1-家长，2-保姆）
     * @param recorderId   记录人ID
     * @return 记录ID
     */
    Long addHealthRecord(Long babyId, BabyHealthRecordDTO dto, Byte recorderType, Long recorderId);

    /**
     * 修改健康记录
     *
     * @param id           记录ID
     * @param dto          健康记录DTO
     * @param recorderType 记录人类型
     * @param recorderId   记录人ID
     * @return 是否成功
     */
    boolean updateHealthRecord(Long id, BabyHealthRecordDTO dto, Byte recorderType, Long recorderId);

    /**
     * 删除健康记录
     *
     * @param id           记录ID
     * @param recorderType 记录人类型
     * @param recorderId   记录人ID
     * @return 是否成功
     */
    boolean deleteHealthRecord(Long id, Byte recorderType, Long recorderId);

    /**
     * 获取健康记录详情
     *
     * @param id 记录ID
     * @return 健康记录VO
     */
    BabyHealthRecordVO getHealthRecordById(Long id);

    /**
     * 获取宝宝的健康记录列表
     *
     * @param babyId 宝宝ID
     * @return 健康记录列表
     */
    List<BabyHealthRecordVO> getHealthRecordsByBabyId(Long babyId);

    /**
     * 获取宝宝的最新健康记录
     *
     * @param babyId 宝宝ID
     * @return 最新健康记录
     */
    BabyHealthRecordVO getLatestHealthRecord(Long babyId);

    /**
     * 获取健康数据图表数据（身高/体重曲线）
     *
     * @param babyId    宝宝ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 图表数据
     */
    Map<String, Object> getHealthChartData(Long babyId, LocalDate startDate, LocalDate endDate);

    /**
     * 分页查询健康记录（管理员用）
     *
     * @param page         分页参数
     * @param babyId       宝宝ID（可选）
     * @param parentId     家长ID（可选）
     * @param recorderType 记录人类型（可选）
     * @param startDate    开始日期（可选）
     * @param endDate      结束日期（可选）
     * @return 分页结果
     */
    IPage<BabyHealthRecordVO> pageHealthRecords(Page<BabyHealthRecord> page, Long babyId, Long parentId,
                                                 Byte recorderType, LocalDate startDate, LocalDate endDate);

    /**
     * 根据家长ID获取所有宝宝的健康记录
     *
     * @param parentId 家长ID
     * @return 健康记录列表
     */
    List<BabyHealthRecordVO> getHealthRecordsByParentId(Long parentId);

    /**
     * 检查用户是否有权限操作该宝宝的健康记录
     *
     * @param babyId       宝宝ID
     * @param userType     用户类型（1-家长，2-保姆）
     * @param userId       用户ID
     * @return 是否有权限
     */
    boolean hasPermission(Long babyId, Byte userType, Long userId);
}
