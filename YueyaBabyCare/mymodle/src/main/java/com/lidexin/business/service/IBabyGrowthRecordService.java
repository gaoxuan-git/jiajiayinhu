package com.lidexin.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lidexin.business.dto.BabyGrowthRecordDTO;
import com.lidexin.business.po.BabyGrowthRecord;
import com.lidexin.business.vo.BabyGrowthRecordVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 婴幼儿成长记录表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
public interface IBabyGrowthRecordService extends IService<BabyGrowthRecord> {

    /**
     * 新增成长记录
     *
     * @param babyId       宝宝ID
     * @param dto          成长记录DTO
     * @param recorderType 记录人类型（1-家长，2-保姆）
     * @param recorderId   记录人ID
     * @return 记录ID
     */
    Long addGrowthRecord(Long babyId, BabyGrowthRecordDTO dto, Byte recorderType, Long recorderId);

    /**
     * 修改成长记录
     *
     * @param id           记录ID
     * @param dto          成长记录DTO
     * @param recorderType 记录人类型
     * @param recorderId   记录人ID
     * @return 是否成功
     */
    boolean updateGrowthRecord(Long id, BabyGrowthRecordDTO dto, Byte recorderType, Long recorderId);

    /**
     * 删除成长记录
     *
     * @param id           记录ID
     * @param recorderType 记录人类型
     * @param recorderId   记录人ID
     * @return 是否成功
     */
    boolean deleteGrowthRecord(Long id, Byte recorderType, Long recorderId);

    /**
     * 获取成长记录详情
     *
     * @param id 记录ID
     * @return 成长记录VO
     */
    BabyGrowthRecordVO getGrowthRecordById(Long id);

    /**
     * 获取宝宝的成长记录列表
     *
     * @param babyId     宝宝ID
     * @param recordType 记录类型（可选）
     * @return 成长记录列表
     */
    List<BabyGrowthRecordVO> getGrowthRecordsByBabyId(Long babyId, Byte recordType);

    /**
     * 获取成长记录时间线
     *
     * @param babyId    宝宝ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 按日期分组的成长记录
     */
    Map<String, List<BabyGrowthRecordVO>> getGrowthRecordsTimeline(Long babyId, LocalDate startDate, LocalDate endDate);

    /**
     * 为成长记录上传图片
     *
     * @param id           记录ID
     * @param imageUrls    图片URL列表
     * @param recorderType 记录人类型
     * @param recorderId   记录人ID
     * @return 是否成功
     */
    boolean uploadImages(Long id, List<String> imageUrls, Byte recorderType, Long recorderId);

    /**
     * 分页查询成长记录（管理员用）
     *
     * @param page         分页参数
     * @param babyId       宝宝ID（可选）
     * @param parentId     家长ID（可选）
     * @param recordType   记录类型（可选）
     * @param recorderType 记录人类型（可选）
     * @param startDate    开始日期（可选）
     * @param endDate      结束日期（可选）
     * @return 分页结果
     */
    IPage<BabyGrowthRecordVO> pageGrowthRecords(Page<BabyGrowthRecord> page, Long babyId, Long parentId,
                                                 Byte recordType, Byte recorderType,
                                                 LocalDate startDate, LocalDate endDate);

    /**
     * 根据家长ID获取所有宝宝的成长记录
     *
     * @param parentId 家长ID
     * @return 成长记录列表
     */
    List<BabyGrowthRecordVO> getGrowthRecordsByParentId(Long parentId);

    /**
     * 检查用户是否有权限操作该宝宝的成长记录
     *
     * @param babyId   宝宝ID
     * @param userType 用户类型（1-家长，2-保姆）
     * @param userId   用户ID
     * @return 是否有权限
     */
    boolean hasPermission(Long babyId, Byte userType, Long userId);
}
