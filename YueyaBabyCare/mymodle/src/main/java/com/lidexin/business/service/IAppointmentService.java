package com.lidexin.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lidexin.business.dto.AppointmentDTO;
import com.lidexin.business.po.Appointment;
import com.lidexin.business.vo.AppointmentVO;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 预约表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
public interface IAppointmentService extends IService<Appointment> {

    /**
     * 新增预约（家长）
     *
     * @param dto      预约信息
     * @param parentId 家长ID
     * @return 预约ID
     */
    Long createAppointment(AppointmentDTO dto, Long parentId);

    /**
     * 修改预约（家长）
     *
     * @param id       预约ID
     * @param dto      预约信息
     * @param parentId 家长ID
     * @return 是否成功
     */
    boolean updateAppointment(Long id, AppointmentDTO dto, Long parentId);

    /**
     * 取消预约（家长）
     *
     * @param id           预约ID
     * @param parentId     家长ID
     * @param cancelReason 取消原因
     * @return 是否成功
     */
    boolean cancelAppointment(Long id, Long parentId, String cancelReason);

    /**
     * 获取预约详情
     *
     * @param id 预约ID
     * @return 预约详情VO
     */
    AppointmentVO getAppointmentById(Long id);

    /**
     * 获取家长的预约列表
     *
     * @param parentId 家长ID
     * @param status   状态筛选（可选）
     * @return 预约列表
     */
    List<AppointmentVO> getAppointmentsByParentId(Long parentId, Byte status);

    /**
     * 获取保姆的预约列表
     *
     * @param nannyId 保姆ID
     * @param status  状态筛选（可选）
     * @return 预约列表
     */
    List<AppointmentVO> getAppointmentsByNannyId(Long nannyId, Byte status);

    /**
     * 获取机构的预约列表（分页）
     *
     * @param page          分页参数
     * @param institutionId 机构ID
     * @param status        状态筛选（可选）
     * @param serviceType   服务类型筛选（可选）
     * @param startDate     开始日期（可选）
     * @param endDate       结束日期（可选）
     * @param keyword       关键词搜索（可选，搜索预约单号、家长姓名、宝宝姓名）
     * @return 预约分页列表
     */
    IPage<AppointmentVO> getAppointmentsByInstitution(Page<Appointment> page, Long institutionId,
                                                       Byte status, Byte serviceType,
                                                       LocalDate startDate, LocalDate endDate,
                                                       String keyword);

    /**
     * 修改预约状态（机构）
     *
     * @param id            预约ID
     * @param status        新状态
     * @param institutionId 机构ID（用于权限校验）
     * @return 是否成功
     */
    boolean updateAppointmentStatus(Long id, Byte status, Long institutionId);

    /**
     * 为预约分配保姆（机构）
     *
     * @param id            预约ID
     * @param nannyId       保姆ID
     * @param institutionId 机构ID（用于权限校验）
     * @return 是否成功
     */
    boolean assignNanny(Long id, Long nannyId, Long institutionId);

    /**
     * 获取可预约的时间段
     *
     * @param institutionId 机构ID
     * @param nannyId       保姆ID（可选）
     * @param date          日期
     * @return 可用时间段列表，格式如 ["08:00-10:00", "10:00-12:00"]
     */
    List<String> getAvailableSlots(Long institutionId, Long nannyId, LocalDate date);

    /**
     * 检查是否有权限操作预约
     *
     * @param appointmentId 预约ID
     * @param userType      用户类型（1-家长，2-保姆，3-机构）
     * @param userId        用户ID
     * @return 是否有权限
     */
    boolean hasPermission(Long appointmentId, Integer userType, Long userId);

    /**
     * 获取预约统计数据（机构用）
     *
     * @param institutionId 机构ID
     * @param startDate     开始日期
     * @param endDate       结束日期
     * @return 统计数据
     */
    AppointmentStatsVO getAppointmentStats(Long institutionId, LocalDate startDate, LocalDate endDate);

    /**
     * 预约统计VO（内部类）
     */
    class AppointmentStatsVO {
        private Integer totalCount;      // 总预约数
        private Integer pendingCount;    // 待确认数
        private Integer confirmedCount;  // 已确认数
        private Integer inProgressCount; // 进行中数
        private Integer completedCount;  // 已完成数
        private Integer cancelledCount;  // 已取消数

        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public Integer getPendingCount() {
            return pendingCount;
        }

        public void setPendingCount(Integer pendingCount) {
            this.pendingCount = pendingCount;
        }

        public Integer getConfirmedCount() {
            return confirmedCount;
        }

        public void setConfirmedCount(Integer confirmedCount) {
            this.confirmedCount = confirmedCount;
        }

        public Integer getInProgressCount() {
            return inProgressCount;
        }

        public void setInProgressCount(Integer inProgressCount) {
            this.inProgressCount = inProgressCount;
        }

        public Integer getCompletedCount() {
            return completedCount;
        }

        public void setCompletedCount(Integer completedCount) {
            this.completedCount = completedCount;
        }

        public Integer getCancelledCount() {
            return cancelledCount;
        }

        public void setCancelledCount(Integer cancelledCount) {
            this.cancelledCount = cancelledCount;
        }
    }
}
