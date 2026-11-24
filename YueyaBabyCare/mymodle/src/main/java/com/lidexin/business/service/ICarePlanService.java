package com.lidexin.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lidexin.business.dto.CarePlanDTO;
import com.lidexin.business.po.CarePlan;
import com.lidexin.business.vo.AppointmentVO;
import com.lidexin.business.vo.CarePlanVO;

import java.util.List;

/**
 * <p>
 * 护理计划表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
public interface ICarePlanService extends IService<CarePlan> {

    /**
     * 新增护理计划（机构）
     *
     * @param dto           计划信息
     * @param institutionId 机构ID
     * @return 计划ID
     */
    Long createCarePlan(CarePlanDTO dto, Long institutionId);

    /**
     * 修改护理计划（机构）
     *
     * @param id            计划ID
     * @param dto           计划信息
     * @param institutionId 机构ID
     * @return 是否成功
     */
    boolean updateCarePlan(Long id, CarePlanDTO dto, Long institutionId);

    /**
     * 删除护理计划（机构）
     *
     * @param id            计划ID
     * @param institutionId 机构ID
     * @return 是否成功
     */
    boolean deleteCarePlan(Long id, Long institutionId);

    /**
     * 获取护理计划详情
     *
     * @param id 计划ID
     * @return 计划详情VO
     */
    CarePlanVO getCarePlanById(Long id);

    /**
     * 获取机构的护理计划列表（分页）
     *
     * @param page          分页参数
     * @param institutionId 机构ID
     * @param status        状态筛选（可选）
     * @param babyId        宝宝ID筛选（可选）
     * @param nannyId       保姆ID筛选（可选）
     * @param keyword       关键词搜索（可选）
     * @return 计划分页列表
     */
    IPage<CarePlanVO> getCarePlansByInstitution(Page<CarePlan> page, Long institutionId,
                                                 Byte status, Long babyId, Long nannyId, String keyword);

    /**
     * 获取家长的护理计划列表
     *
     * @param parentId 家长ID
     * @param status   状态筛选（可选）
     * @return 计划列表
     */
    List<CarePlanVO> getCarePlansByParentId(Long parentId, Byte status);

    /**
     * 获取保姆的护理计划列表
     *
     * @param nannyId 保姆ID
     * @param status  状态筛选（可选）
     * @return 计划列表
     */
    List<CarePlanVO> getCarePlansByNannyId(Long nannyId, Byte status);

    /**
     * 修改护理计划状态（机构）
     *
     * @param id            计划ID
     * @param status        新状态
     * @param institutionId 机构ID
     * @return 是否成功
     */
    boolean updateCarePlanStatus(Long id, Byte status, Long institutionId);

    /**
     * 为护理计划分配保姆（机构）
     *
     * @param id            计划ID
     * @param nannyId       保姆ID
     * @param institutionId 机构ID
     * @return 是否成功
     */
    boolean assignNanny(Long id, Long nannyId, Long institutionId);

    /**
     * 检查是否有权限操作护理计划
     *
     * @param planId   计划ID
     * @param userType 用户类型（1-家长，2-保姆，3-机构）
     * @param userId   用户ID
     * @return 是否有权限
     */
    boolean hasPermission(Long planId, Integer userType, Long userId);

    /**
     * 获取可创建护理计划的预约列表（已确认/进行中且未创建过计划的预约）
     *
     * @param institutionId 机构ID
     * @return 可用预约列表
     */
    List<AppointmentVO> getAvailableAppointmentsForPlan(Long institutionId);
}
