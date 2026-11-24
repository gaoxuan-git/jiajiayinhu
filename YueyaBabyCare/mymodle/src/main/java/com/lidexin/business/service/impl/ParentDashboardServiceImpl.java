package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lidexin.business.mapper.*;
import com.lidexin.business.po.*;
import com.lidexin.business.service.IParentDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 家长数据统计服务实现类
 *
 * @author 张三
 * @since 2025-12-25
 */
@Service
public class ParentDashboardServiceImpl implements IParentDashboardService {

    @Autowired
    private BabyMapper babyMapper;

    @Autowired
    private BabyGrowthRecordMapper babyGrowthRecordMapper;

    @Autowired
    private BabyHealthRecordMapper babyHealthRecordMapper;

    @Autowired
    private CareRecordMapper careRecordMapper;

    @Autowired
    private CarePlanMapper carePlanMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private CareEvaluationMapper careEvaluationMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private InstitutionMapper institutionMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public BabyStatsVO getBabyStats(Long parentId) {
        BabyStatsVO vo = new BabyStatsVO();

        // 获取家长的所有宝宝
        LambdaQueryWrapper<Baby> babyWrapper = new LambdaQueryWrapper<>();
        babyWrapper.eq(Baby::getDeleted, 0);
        babyWrapper.eq(Baby::getParentId, parentId);
        List<Baby> babies = babyMapper.selectList(babyWrapper);

        vo.setTotalBabies(babies.size());

        // 获取宝宝ID列表
        List<Long> babyIds = babies.stream().map(Baby::getId).collect(Collectors.toList());

        // 构建宝宝信息列表
        List<BabyInfoVO> babyInfoList = new ArrayList<>();
        int totalGrowthRecords = 0;
        int totalHealthRecords = 0;

        for (Baby baby : babies) {
            BabyInfoVO babyInfo = new BabyInfoVO();
            babyInfo.setId(baby.getId());
            babyInfo.setName(baby.getName());
            babyInfo.setNickname(baby.getNickname());
            babyInfo.setGender(baby.getGender());
            babyInfo.setBirthDate(baby.getBirthDate());
            babyInfo.setAvatar(baby.getAvatar());

            // 计算月龄
            if (baby.getBirthDate() != null) {
                Period period = Period.between(baby.getBirthDate(), LocalDate.now());
                int months = period.getYears() * 12 + period.getMonths();
                babyInfo.setAgeMonths(months);
            } else {
                babyInfo.setAgeMonths(0);
            }

            // 成长记录数
            LambdaQueryWrapper<BabyGrowthRecord> growthWrapper = new LambdaQueryWrapper<>();
            growthWrapper.eq(BabyGrowthRecord::getDeleted, 0);
            growthWrapper.eq(BabyGrowthRecord::getBabyId, baby.getId());
            int growthCount = babyGrowthRecordMapper.selectCount(growthWrapper).intValue();
            babyInfo.setGrowthRecordCount(growthCount);
            totalGrowthRecords += growthCount;

            // 健康档案数
            LambdaQueryWrapper<BabyHealthRecord> healthWrapper = new LambdaQueryWrapper<>();
            healthWrapper.eq(BabyHealthRecord::getDeleted, 0);
            healthWrapper.eq(BabyHealthRecord::getBabyId, baby.getId());
            int healthCount = babyHealthRecordMapper.selectCount(healthWrapper).intValue();
            babyInfo.setHealthRecordCount(healthCount);
            totalHealthRecords += healthCount;

            babyInfoList.add(babyInfo);
        }

        vo.setBabies(babyInfoList);
        vo.setTotalGrowthRecords(totalGrowthRecords);
        vo.setTotalHealthRecords(totalHealthRecords);

        // 成长记录趋势（近7天）
        vo.setGrowthTrend(getGrowthTrend(babyIds, 7));

        // 记录类型分布
        vo.setRecordTypeDist(getRecordTypeDistribution(babyIds));

        return vo;
    }

    @Override
    public CareStatsVO getCareStats(Long parentId) {
        CareStatsVO vo = new CareStatsVO();

        // 获取家长的所有宝宝ID
        LambdaQueryWrapper<Baby> babyWrapper = new LambdaQueryWrapper<>();
        babyWrapper.eq(Baby::getDeleted, 0);
        babyWrapper.eq(Baby::getParentId, parentId);
        List<Baby> babies = babyMapper.selectList(babyWrapper);
        List<Long> babyIds = babies.stream().map(Baby::getId).collect(Collectors.toList());

        if (babyIds.isEmpty()) {
            vo.setTotalCareRecords(0);
            vo.setTotalCarePlans(0);
            vo.setActiveCarePlans(0);
            vo.setCompletedCarePlans(0);
            vo.setTotalAppointments(0);
            vo.setPendingAppointments(0);
            vo.setCompletedAppointments(0);
            vo.setAvgRating(0.0);
            vo.setTotalEvaluations(0);
            vo.setCareTypeDist(new ArrayList<>());
            vo.setCareTrend(new ArrayList<>());
            vo.setAppointmentStatusDist(new ArrayList<>());
            return vo;
        }

        // 护理记录总数
        LambdaQueryWrapper<CareRecord> careRecordWrapper = new LambdaQueryWrapper<>();
        careRecordWrapper.eq(CareRecord::getDeleted, 0);
        careRecordWrapper.in(CareRecord::getBabyId, babyIds);
        vo.setTotalCareRecords(careRecordMapper.selectCount(careRecordWrapper).intValue());

        // 护理计划统计
        LambdaQueryWrapper<CarePlan> carePlanWrapper = new LambdaQueryWrapper<>();
        carePlanWrapper.eq(CarePlan::getDeleted, 0);
        carePlanWrapper.in(CarePlan::getBabyId, babyIds);
        List<CarePlan> carePlans = carePlanMapper.selectList(carePlanWrapper);
        vo.setTotalCarePlans(carePlans.size());

        // 进行中的护理计划（状态为1）
        int activePlans = (int) carePlans.stream()
                .filter(p -> p.getStatus() != null && p.getStatus() == 1)
                .count();
        vo.setActiveCarePlans(activePlans);

        // 已完成的护理计划（状态为2）
        int completedPlans = (int) carePlans.stream()
                .filter(p -> p.getStatus() != null && p.getStatus() == 2)
                .count();
        vo.setCompletedCarePlans(completedPlans);

        // 预约统计
        LambdaQueryWrapper<Appointment> appointmentWrapper = new LambdaQueryWrapper<>();
        appointmentWrapper.eq(Appointment::getDeleted, 0);
        appointmentWrapper.eq(Appointment::getParentId, parentId);
        List<Appointment> appointments = appointmentMapper.selectList(appointmentWrapper);
        vo.setTotalAppointments(appointments.size());

        // 待处理预约（状态为0）
        int pendingAppointments = (int) appointments.stream()
                .filter(a -> a.getStatus() != null && a.getStatus() == 0)
                .count();
        vo.setPendingAppointments(pendingAppointments);

        // 已完成预约（状态为2）
        int completedAppointments = (int) appointments.stream()
                .filter(a -> a.getStatus() != null && a.getStatus() == 2)
                .count();
        vo.setCompletedAppointments(completedAppointments);

        // 评价统计
        LambdaQueryWrapper<CareEvaluation> evalWrapper = new LambdaQueryWrapper<>();
        evalWrapper.eq(CareEvaluation::getDeleted, 0);
        evalWrapper.eq(CareEvaluation::getParentId, parentId);
        List<CareEvaluation> evaluations = careEvaluationMapper.selectList(evalWrapper);
        vo.setTotalEvaluations(evaluations.size());

        // 平均评分
        if (!evaluations.isEmpty()) {
            double avgRating = evaluations.stream()
                    .mapToInt(e -> e.getScore() != null ? e.getScore() : 0)
                    .average()
                    .orElse(0.0);
            vo.setAvgRating(Math.round(avgRating * 10) / 10.0);
        } else {
            vo.setAvgRating(0.0);
        }

        // 护理类型分布
        vo.setCareTypeDist(getCareTypeDistribution(babyIds));

        // 护理记录趋势（近7天）
        vo.setCareTrend(getCareTrend(babyIds, 7));

        // 预约状态分布
        vo.setAppointmentStatusDist(getAppointmentStatusDistribution(parentId));

        return vo;
    }

    @Override
    public Page<OrderHistoryVO> getOrderHistory(Long parentId, Integer pageNum, Integer pageSize) {
        // 查询订单
        Page<Order> orderPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(Order::getDeleted, 0);
        orderWrapper.eq(Order::getParentId, parentId);
        orderWrapper.orderByDesc(Order::getCreateTime);
        orderPage = orderMapper.selectPage(orderPage, orderWrapper);

        // 转换为VO
        Page<OrderHistoryVO> resultPage = new Page<>(pageNum, pageSize);
        resultPage.setTotal(orderPage.getTotal());
        resultPage.setPages(orderPage.getPages());

        List<OrderHistoryVO> voList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Order order : orderPage.getRecords()) {
            OrderHistoryVO vo = new OrderHistoryVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setInstitutionId(order.getInstitutionId());
            vo.setTotalAmount(order.getTotalAmount());
            vo.setPayAmount(order.getPayAmount());
            vo.setStatus(order.getStatus());
            vo.setStatusName(getOrderStatusName(order.getStatus()));
            vo.setPayType(order.getPayType());
            vo.setPayTypeName(getPayTypeName(order.getPayType()));
            vo.setCreateTime(order.getCreateTime() != null ? order.getCreateTime().format(formatter) : null);
            vo.setPayTime(order.getPayTime() != null ? order.getPayTime().format(formatter) : null);

            // 获取机构名称
            if (order.getInstitutionId() != null) {
                Institution institution = institutionMapper.selectById(order.getInstitutionId());
                vo.setInstitutionName(institution != null ? institution.getName() : "未知机构");
            }

            // 获取订单商品项
            vo.setItems(getOrderItems(order.getId()));

            voList.add(vo);
        }

        resultPage.setRecords(voList);
        return resultPage;
    }

    // ==================== 私有辅助方法 ====================

    private List<Map<String, Object>> getGrowthTrend(List<Long> babyIds, int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        if (babyIds.isEmpty()) {
            return trend;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);

            LambdaQueryWrapper<BabyGrowthRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BabyGrowthRecord::getDeleted, 0);
            wrapper.in(BabyGrowthRecord::getBabyId, babyIds);
            wrapper.eq(BabyGrowthRecord::getRecordDate, date);
            int count = babyGrowthRecordMapper.selectCount(wrapper).intValue();

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("count", count);
            trend.add(item);
        }
        return trend;
    }

    private List<Map<String, Object>> getRecordTypeDistribution(List<Long> babyIds) {
        List<Map<String, Object>> dist = new ArrayList<>();
        if (babyIds.isEmpty()) {
            return dist;
        }

        String[] typeNames = {"", "饮食", "睡眠", "排便", "活动", "其他"};

        for (int i = 1; i <= 5; i++) {
            LambdaQueryWrapper<BabyGrowthRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BabyGrowthRecord::getDeleted, 0);
            wrapper.in(BabyGrowthRecord::getBabyId, babyIds);
            wrapper.eq(BabyGrowthRecord::getRecordType, i);
            int count = babyGrowthRecordMapper.selectCount(wrapper).intValue();

            Map<String, Object> item = new HashMap<>();
            item.put("type", i);
            item.put("name", typeNames[i]);
            item.put("count", count);
            dist.add(item);
        }
        return dist;
    }

    private List<Map<String, Object>> getCareTypeDistribution(List<Long> babyIds) {
        List<Map<String, Object>> dist = new ArrayList<>();
        if (babyIds.isEmpty()) {
            return dist;
        }

        String[] typeNames = {"", "喂养", "洗护", "哄睡", "早教", "其他"};

        for (int i = 1; i <= 5; i++) {
            LambdaQueryWrapper<CareRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CareRecord::getDeleted, 0);
            wrapper.in(CareRecord::getBabyId, babyIds);
            wrapper.eq(CareRecord::getCareType, i);
            int count = careRecordMapper.selectCount(wrapper).intValue();

            Map<String, Object> item = new HashMap<>();
            item.put("type", i);
            item.put("name", typeNames[i]);
            item.put("count", count);
            dist.add(item);
        }
        return dist;
    }

    private List<Map<String, Object>> getCareTrend(List<Long> babyIds, int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        if (babyIds.isEmpty()) {
            return trend;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);

            LambdaQueryWrapper<CareRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CareRecord::getDeleted, 0);
            wrapper.in(CareRecord::getBabyId, babyIds);
            wrapper.eq(CareRecord::getRecordDate, date);
            int count = careRecordMapper.selectCount(wrapper).intValue();

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("count", count);
            trend.add(item);
        }
        return trend;
    }

    private List<Map<String, Object>> getAppointmentStatusDistribution(Long parentId) {
        List<Map<String, Object>> dist = new ArrayList<>();
        String[] statusNames = {"待确认", "已确认", "已完成", "已取消"};

        for (int i = 0; i <= 3; i++) {
            LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Appointment::getDeleted, 0);
            wrapper.eq(Appointment::getParentId, parentId);
            wrapper.eq(Appointment::getStatus, i);
            int count = appointmentMapper.selectCount(wrapper).intValue();

            Map<String, Object> item = new HashMap<>();
            item.put("status", i);
            item.put("name", statusNames[i]);
            item.put("count", count);
            dist.add(item);
        }
        return dist;
    }

    private List<OrderItemVO> getOrderItems(Long orderId) {
        List<OrderItemVO> items = new ArrayList<>();

        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> orderItems = orderItemMapper.selectList(wrapper);

        for (OrderItem orderItem : orderItems) {
            OrderItemVO vo = new OrderItemVO();
            vo.setProductId(orderItem.getProductId());
            vo.setProductName(orderItem.getProductName());
            vo.setProductImage(orderItem.getProductImg());
            vo.setPrice(orderItem.getPrice());
            vo.setQuantity(orderItem.getQuantity());
            vo.setSubtotal(orderItem.getTotalAmount());
            items.add(vo);
        }

        return items;
    }

    private String getOrderStatusName(Byte status) {
        if (status == null) return "未知";
        String[] statusNames = {"待付款", "待发货", "待收货", "已完成", "已取消", "已退款"};
        return status >= 0 && status < statusNames.length ? statusNames[status] : "未知";
    }

    private String getPayTypeName(Byte payType) {
        if (payType == null) return "未支付";
        String[] payTypeNames = {"", "微信", "支付宝", "余额"};
        return payType >= 1 && payType < payTypeNames.length ? payTypeNames[payType] : "未知";
    }
}
