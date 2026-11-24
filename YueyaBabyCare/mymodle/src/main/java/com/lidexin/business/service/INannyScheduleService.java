package com.lidexin.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lidexin.business.po.Nanny;
import com.lidexin.business.po.NannySchedule;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lidexin.business.vo.NannyScheduleVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 保姆排班表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
public interface INannyScheduleService extends IService<NannySchedule> {
    
    /**
     * 新增排班（机构）
     *
     * @param institutionId 机构ID
     * @param schedule 排班信息
     * @return 是否成功
     */
    boolean createSchedule(Long institutionId, NannySchedule schedule);
    
    /**
     * 修改排班（机构）
     *
     * @param institutionId 机构ID
     * @param scheduleId 排班ID
     * @param schedule 排班信息
     * @return 是否成功
     */
    boolean updateSchedule(Long institutionId, Long scheduleId, NannySchedule schedule);
    
    /**
     * 删除排班（机构）
     *
     * @param institutionId 机构ID
     * @param scheduleId 排班ID
     * @return 是否成功
     */
    boolean deleteSchedule(Long institutionId, Long scheduleId);
    
    /**
     * 获取排班列表（机构分页）
     *
     * @param institutionId 机构ID
     * @param nannyId 保姆ID（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Page<NannyScheduleVO> getScheduleList(Long institutionId, Long nannyId, LocalDate startDate, LocalDate endDate, Integer pageNum, Integer pageSize);
    
    /**
     * 获取日历视图数据（机构）
     *
     * @param institutionId 机构ID
     * @param year 年份
     * @param month 月份
     * @return 日历数据（key=日期，value=排班列表）
     */
    Map<LocalDate, List<NannyScheduleVO>> getCalendarView(Long institutionId, Integer year, Integer month);
    
    /**
     * 获取保姆的排班（保姆/机构）
     *
     * @param nannyId 保姆ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 排班列表
     */
    List<NannyScheduleVO> getNannySchedule(Long nannyId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 申请调班（保姆）
     *
     * @param nannyId 保姆ID
     * @param scheduleId 排班ID
     * @param targetNannyId 目标保姆ID
     * @param reason 调班原因
     * @return 是否成功
     */
    boolean applySwap(Long nannyId, Long scheduleId, Long targetNannyId, String reason);
    
    /**
     * 审核调班（机构）
     *
     * @param institutionId 机构ID
     * @param scheduleId 排班ID
     * @param approved 是否批准
     * @return 是否成功
     */
    boolean approveSwap(Long institutionId, Long scheduleId, boolean approved);
    
    /**
     * 获取调班申请列表（机构）
     *
     * @param institutionId 机构ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Page<NannyScheduleVO> getSwapRequests(Long institutionId, Integer pageNum, Integer pageSize);
    
    /**
     * 检测排班冲突
     *
     * @param institutionId 机构ID
     * @param nannyId 保姆ID
     * @param scheduleDate 排班日期
     * @param shiftType 班次类型
     * @param excludeId 排除的排班ID（编辑时使用）
     * @return 冲突的排班列表
     */
    List<NannyScheduleVO> checkConflicts(Long institutionId, Long nannyId, LocalDate scheduleDate, Byte shiftType, Long excludeId);
    
    /**
     * 批量新增排班（机构）
     *
     * @param institutionId 机构ID
     * @param schedules 排班列表
     * @return 成功数量
     */
    int batchCreateSchedule(Long institutionId, List<NannySchedule> schedules);
    
    /**
     * 获取可调班的保姆列表（保姆端）
     * 返回同机构内其他状态正常的保姆
     *
     * @param nannyId 当前保姆ID
     * @param scheduleId 排班ID
     * @return 可调班的保姆列表
     */
    List<Nanny> getSwapNannies(Long nannyId, Long scheduleId);
}
