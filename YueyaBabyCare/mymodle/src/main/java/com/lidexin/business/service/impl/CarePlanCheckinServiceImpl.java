package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lidexin.business.dto.CheckinDTO;
import com.lidexin.business.mapper.*;
import com.lidexin.business.po.*;
import com.lidexin.business.service.ICarePlanCheckinService;
import com.lidexin.business.vo.CarePlanCheckinVO;
import com.lidexin.business.vo.CheckoutResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 预约签到表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-24
 */
@Service
public class CarePlanCheckinServiceImpl extends ServiceImpl<CarePlanCheckinMapper, CarePlanCheckin> implements ICarePlanCheckinService {

    /**
     * 签到范围（米）- 考虑GPS定位误差，设置为500米
     */
    private static final int CHECKIN_RANGE_METERS = 500;

    /**
     * 地球半径（米）
     */
    private static final double EARTH_RADIUS = 6371000;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private NannyMapper nannyMapper;

    @Autowired
    private CarePlanMapper carePlanMapper;

    /**
     * 签到类型枚举
     */
    private static final Map<Byte, String> CHECKIN_TYPE_MAP = new HashMap<>();

    static {
        CHECKIN_TYPE_MAP.put((byte) 1, "签到");
        CHECKIN_TYPE_MAP.put((byte) 2, "签退");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long checkin(Long appointmentId, Long nannyId, CheckinDTO dto) {
        // 校验预约
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null || appointment.getDeleted() == 1) {
            throw new RuntimeException("预约不存在");
        }

        // 校验保姆是否是该预约的执行人
        if (!nannyId.equals(appointment.getNannyId())) {
            throw new RuntimeException("您不是该预约的执行保姆");
        }

        // 校验预约状态（已确认或进行中可以签到）
        if (appointment.getStatus() != 1 && appointment.getStatus() != 2) {
            throw new RuntimeException("该预约状态不允许签到");
        }

        // 检查今日是否已签到未签退
        CarePlanCheckin lastCheckin = getTodayLastCheckinEntity(appointmentId);
        if (lastCheckin != null && lastCheckin.getCheckinType() == 1) {
            throw new RuntimeException("您今日已签到，请勿重复签到");
        }

        // 获取服务地址经纬度
        BigDecimal[] serviceLocation = getServiceLocation(appointment);
        if (serviceLocation == null) {
            throw new RuntimeException("无法获取服务地址信息");
        }

        // 计算距离
        int distance = calculateDistance(
                dto.getLatitude().doubleValue(), dto.getLongitude().doubleValue(),
                serviceLocation[1].doubleValue(), serviceLocation[0].doubleValue()
        );

        // 判断是否在范围内
        boolean isInRange = distance <= CHECKIN_RANGE_METERS;
        if (!isInRange) {
            throw new RuntimeException("距离服务地址" + distance + "米，超出签到范围" + CHECKIN_RANGE_METERS + "米，无法签到");
        }

        // 创建签到记录
        CarePlanCheckin checkin = new CarePlanCheckin();
        checkin.setAppointmentId(appointmentId);
        checkin.setNannyId(nannyId);
        checkin.setCheckinType((byte) 1); // 签到
        checkin.setCheckinTime(LocalDateTime.now());
        checkin.setCheckinLongitude(dto.getLongitude());
        checkin.setCheckinLatitude(dto.getLatitude());
        checkin.setCheckinAddress(dto.getAddress());
        checkin.setDistance(distance);
        checkin.setIsInRange((byte) 1);
        checkin.setRemark(dto.getRemark());
        checkin.setCreateTime(LocalDateTime.now());

        this.save(checkin);

        // 如果预约是已确认状态，改为进行中
        if (appointment.getStatus() == 1) {
            appointment.setStatus((byte) 2);
            appointment.setUpdateTime(LocalDateTime.now());
            appointmentMapper.updateById(appointment);
        }

        return checkin.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckoutResultVO checkout(Long appointmentId, Long nannyId, CheckinDTO dto) {
        // 校验预约
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null || appointment.getDeleted() == 1) {
            throw new RuntimeException("预约不存在");
        }

        // 校验保姆是否是该预约的执行人
        if (!nannyId.equals(appointment.getNannyId())) {
            throw new RuntimeException("您不是该预约的执行保姆");
        }

        // 检查今日是否已签到
        CarePlanCheckin lastCheckin = getTodayLastCheckinEntity(appointmentId);
        if (lastCheckin == null || lastCheckin.getCheckinType() != 1) {
            throw new RuntimeException("您今日尚未签到，无法签退");
        }

        // 获取服务地址经纬度
        BigDecimal[] serviceLocation = getServiceLocation(appointment);

        // 计算距离（签退不限制距离，但记录）
        int distance = 0;
        boolean isInRange = false;
        if (serviceLocation != null) {
            distance = calculateDistance(
                    dto.getLatitude().doubleValue(), dto.getLongitude().doubleValue(),
                    serviceLocation[1].doubleValue(), serviceLocation[0].doubleValue()
            );
            isInRange = distance <= CHECKIN_RANGE_METERS;
        }

        // 创建签退记录
        CarePlanCheckin checkout = new CarePlanCheckin();
        checkout.setAppointmentId(appointmentId);
        checkout.setNannyId(nannyId);
        checkout.setCheckinType((byte) 2); // 签退
        checkout.setCheckinTime(LocalDateTime.now());
        checkout.setCheckinLongitude(dto.getLongitude());
        checkout.setCheckinLatitude(dto.getLatitude());
        checkout.setCheckinAddress(dto.getAddress());
        checkout.setDistance(distance);
        checkout.setIsInRange(isInRange ? (byte) 1 : (byte) 0);
        checkout.setRemark(dto.getRemark());
        checkout.setCreateTime(LocalDateTime.now());

        this.save(checkout);

        // 更新关联的护理计划状态
        LambdaQueryWrapper<CarePlan> planWrapper = new LambdaQueryWrapper<>();
        planWrapper.eq(CarePlan::getAppointmentId, appointmentId)
                   .eq(CarePlan::getDeleted, 0)
                   .in(CarePlan::getStatus, 0, 1); // 待执行或进行中的计划
        List<CarePlan> plans = carePlanMapper.selectList(planWrapper);
        
        LocalDate today = LocalDate.now();
        StringBuilder remainingMsg = new StringBuilder();
        
        for (CarePlan plan : plans) {
            // 判断是否到达结束日期
            if (plan.getEndDate() != null && today.isBefore(plan.getEndDate())) {
                // 还没到结束日期，计算剩余天数
                long remainingDays = ChronoUnit.DAYS.between(today, plan.getEndDate());
                remainingMsg.append("【").append(plan.getPlanName()).append("】还剩").append(remainingDays).append("天；");
            } else {
                // 已到达或超过结束日期，更新为已完成
                plan.setStatus((byte) 2); // 已完成
                plan.setUpdateTime(LocalDateTime.now());
                carePlanMapper.updateById(plan);
            }
        }
        
        // 返回结果，包含签退ID和提示消息
        String message = remainingMsg.length() > 0 
            ? "以下护理计划未完成：" + remainingMsg.toString()
            : null;
        return new CheckoutResultVO(checkout.getId(), message);
    }

    @Override
    public List<CarePlanCheckinVO> getCheckinRecordsByAppointmentId(Long appointmentId) {
        LambdaQueryWrapper<CarePlanCheckin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarePlanCheckin::getAppointmentId, appointmentId)
               .orderByDesc(CarePlanCheckin::getCheckinTime);
        List<CarePlanCheckin> list = this.list(wrapper);
        return convertToVOList(list);
    }

    @Override
    public IPage<CarePlanCheckinVO> getCheckinRecordsByInstitution(Page<CarePlanCheckin> page, Long institutionId,
                                                                    Long nannyId, Long appointmentId) {
        // 先查询机构下的所有预约ID
        LambdaQueryWrapper<Appointment> appointmentWrapper = new LambdaQueryWrapper<>();
        appointmentWrapper.eq(Appointment::getInstitutionId, institutionId)
                          .eq(Appointment::getDeleted, 0);
        if (appointmentId != null) {
            appointmentWrapper.eq(Appointment::getId, appointmentId);
        }
        if (nannyId != null) {
            appointmentWrapper.eq(Appointment::getNannyId, nannyId);
        }
        List<Appointment> appointments = appointmentMapper.selectList(appointmentWrapper);
        if (appointments.isEmpty()) {
            return new Page<>(page.getCurrent(), page.getSize(), 0);
        }

        List<Long> appointmentIds = appointments.stream().map(Appointment::getId).collect(Collectors.toList());

        // 查询签到记录
        LambdaQueryWrapper<CarePlanCheckin> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CarePlanCheckin::getAppointmentId, appointmentIds)
               .orderByDesc(CarePlanCheckin::getCheckinTime);

        IPage<CarePlanCheckin> checkinPage = this.page(page, wrapper);

        // 转换为VO
        IPage<CarePlanCheckinVO> voPage = new Page<>(checkinPage.getCurrent(), checkinPage.getSize(), checkinPage.getTotal());
        voPage.setRecords(convertToVOList(checkinPage.getRecords()));
        return voPage;
    }

    @Override
    public CarePlanCheckinVO getCheckinById(Long id) {
        CarePlanCheckin checkin = this.getById(id);
        if (checkin == null) {
            return null;
        }
        return convertToVO(checkin);
    }

    @Override
    public CarePlanCheckinVO getTodayLastCheckin(Long appointmentId) {
        CarePlanCheckin checkin = getTodayLastCheckinEntity(appointmentId);
        if (checkin == null) {
            return null;
        }
        return convertToVO(checkin);
    }

    /**
     * 获取今日最后一条签到记录实体
     */
    private CarePlanCheckin getTodayLastCheckinEntity(Long appointmentId) {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        LambdaQueryWrapper<CarePlanCheckin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarePlanCheckin::getAppointmentId, appointmentId)
               .between(CarePlanCheckin::getCheckinTime, todayStart, todayEnd)
               .orderByDesc(CarePlanCheckin::getCheckinTime)
               .last("LIMIT 1");

        return this.getOne(wrapper);
    }

    /**
     * 获取服务地址经纬度
     * @return [longitude, latitude] 或 null
     */
    private BigDecimal[] getServiceLocation(Appointment appointment) {
        if (appointment.getAddressId() == null) {
            return null;
        }

        Address address = addressMapper.selectById(appointment.getAddressId());
        if (address == null || address.getLongitude() == null || address.getLatitude() == null) {
            return null;
        }

        return new BigDecimal[]{address.getLongitude(), address.getLatitude()};
    }

    /**
     * 计算两点之间的距离（Haversine公式）
     *
     * @param lat1 点1纬度
     * @param lon1 点1经度
     * @param lat2 点2纬度
     * @param lon2 点2经度
     * @return 距离（米）
     */
    private int calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) Math.round(EARTH_RADIUS * c);
    }

    /**
     * 转换为VO列表
     */
    private List<CarePlanCheckinVO> convertToVOList(List<CarePlanCheckin> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private CarePlanCheckinVO convertToVO(CarePlanCheckin checkin) {
        CarePlanCheckinVO vo = new CarePlanCheckinVO();
        BeanUtils.copyProperties(checkin, vo);

        // 设置签到类型名称
        vo.setCheckinTypeName(CHECKIN_TYPE_MAP.getOrDefault(checkin.getCheckinType(), "未知"));

        // 设置是否在范围内名称
        vo.setIsInRangeName(checkin.getIsInRange() == 1 ? "是" : "否");

        // 获取预约单号
        Appointment appointment = appointmentMapper.selectById(checkin.getAppointmentId());
        if (appointment != null) {
            vo.setAppointmentNo(appointment.getAppointmentNo());
        }

        // 获取保姆名称
        Nanny nanny = nannyMapper.selectById(checkin.getNannyId());
        if (nanny != null) {
            vo.setNannyName(nanny.getRealName());
        }

        return vo;
    }
}
