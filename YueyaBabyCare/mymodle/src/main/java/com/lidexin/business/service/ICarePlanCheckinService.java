package com.lidexin.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lidexin.business.dto.CheckinDTO;
import com.lidexin.business.po.CarePlanCheckin;
import com.lidexin.business.vo.CarePlanCheckinVO;
import com.lidexin.business.vo.CheckoutResultVO;

import java.util.List;

/**
 * <p>
 * 预约签到表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-24
 */
public interface ICarePlanCheckinService extends IService<CarePlanCheckin> {

    /**
     * 签到
     *
     * @param appointmentId 预约ID
     * @param nannyId       保姆ID
     * @param dto           签到信息
     * @return 签到记录ID
     */
    Long checkin(Long appointmentId, Long nannyId, CheckinDTO dto);

    /**
     * 签退
     *
     * @param appointmentId 预约ID
     * @param nannyId       保姆ID
     * @param dto           签退信息
     * @return 签退结果（包含签退记录ID和提示消息）
     */
    CheckoutResultVO checkout(Long appointmentId, Long nannyId, CheckinDTO dto);

    /**
     * 获取预约的签到记录列表
     *
     * @param appointmentId 预约ID
     * @return 签到记录列表
     */
    List<CarePlanCheckinVO> getCheckinRecordsByAppointmentId(Long appointmentId);

    /**
     * 获取机构的签到记录列表（分页）
     *
     * @param page          分页参数
     * @param institutionId 机构ID
     * @param nannyId       保姆ID（可选）
     * @param appointmentId 预约ID（可选）
     * @return 签到记录分页列表
     */
    IPage<CarePlanCheckinVO> getCheckinRecordsByInstitution(Page<CarePlanCheckin> page, Long institutionId,
                                                             Long nannyId, Long appointmentId);

    /**
     * 获取签到记录详情
     *
     * @param id 签到记录ID
     * @return 签到记录详情
     */
    CarePlanCheckinVO getCheckinById(Long id);

    /**
     * 获取预约今日最后一条签到记录
     *
     * @param appointmentId 预约ID
     * @return 最后一条签到记录（如果有）
     */
    CarePlanCheckinVO getTodayLastCheckin(Long appointmentId);
}
