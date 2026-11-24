package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lidexin.business.mapper.*;
import com.lidexin.business.po.*;
import com.lidexin.business.service.IAdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理员数据分析服务实现类
 *
 * @author 张三
 * @since 2025-12-25
 */
@Service
public class AdminDashboardServiceImpl implements IAdminDashboardService {

    @Autowired
    private ParentMapper parentMapper;

    @Autowired
    private NannyMapper nannyMapper;

    @Autowired
    private InstitutionMapper institutionMapper;

    @Autowired
    private BabyMapper babyMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Override
    public DashboardVO getDashboardData() {
        DashboardVO vo = new DashboardVO();

        // 家长总数
        LambdaQueryWrapper<Parent> parentWrapper = new LambdaQueryWrapper<>();
        parentWrapper.eq(Parent::getDeleted, 0);
        vo.setTotalParents(parentMapper.selectCount(parentWrapper).intValue());

        // 保姆总数
        LambdaQueryWrapper<Nanny> nannyWrapper = new LambdaQueryWrapper<>();
        nannyWrapper.eq(Nanny::getDeleted, 0);
        vo.setTotalNannies(nannyMapper.selectCount(nannyWrapper).intValue());

        // 机构总数
        LambdaQueryWrapper<Institution> institutionWrapper = new LambdaQueryWrapper<>();
        institutionWrapper.eq(Institution::getDeleted, 0);
        vo.setTotalInstitutions(institutionMapper.selectCount(institutionWrapper).intValue());

        // 宝宝总数
        LambdaQueryWrapper<Baby> babyWrapper = new LambdaQueryWrapper<>();
        babyWrapper.eq(Baby::getDeleted, 0);
        vo.setTotalBabies(babyMapper.selectCount(babyWrapper).intValue());

        // 订单总数
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(Order::getDeleted, 0);
        vo.setTotalOrders(orderMapper.selectCount(orderWrapper).intValue());

        // 总收入（已完成订单）
        LambdaQueryWrapper<Order> revenueWrapper = new LambdaQueryWrapper<>();
        revenueWrapper.eq(Order::getDeleted, 0);
        revenueWrapper.eq(Order::getStatus, 3); // 已完成
        List<Order> completedOrders = orderMapper.selectList(revenueWrapper);
        BigDecimal totalRevenue = completedOrders.stream()
                .map(o -> o.getPayAmount() != null ? o.getPayAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalRevenue(totalRevenue);

        // 今日订单
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LambdaQueryWrapper<Order> todayOrderWrapper = new LambdaQueryWrapper<>();
        todayOrderWrapper.eq(Order::getDeleted, 0);
        todayOrderWrapper.between(Order::getCreateTime, todayStart, todayEnd);
        vo.setTodayOrders(orderMapper.selectCount(todayOrderWrapper).intValue());

        // 今日收入
        LambdaQueryWrapper<Order> todayRevenueWrapper = new LambdaQueryWrapper<>();
        todayRevenueWrapper.eq(Order::getDeleted, 0);
        todayRevenueWrapper.eq(Order::getStatus, 3);
        todayRevenueWrapper.between(Order::getPayTime, todayStart, todayEnd);
        List<Order> todayCompletedOrders = orderMapper.selectList(todayRevenueWrapper);
        BigDecimal todayRevenue = todayCompletedOrders.stream()
                .map(o -> o.getPayAmount() != null ? o.getPayAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTodayRevenue(todayRevenue);

        // 待处理订单（待发货）
        LambdaQueryWrapper<Order> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(Order::getDeleted, 0);
        pendingWrapper.eq(Order::getStatus, 1); // 待发货
        vo.setPendingOrders(orderMapper.selectCount(pendingWrapper).intValue());

        // 商品总数
        LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
        productWrapper.eq(Product::getDeleted, 0);
        vo.setTotalProducts(productMapper.selectCount(productWrapper).intValue());

        // 预约总数
        LambdaQueryWrapper<Appointment> appointmentWrapper = new LambdaQueryWrapper<>();
        appointmentWrapper.eq(Appointment::getDeleted, 0);
        vo.setTotalAppointments(appointmentMapper.selectCount(appointmentWrapper).intValue());

        // 待处理预约
        LambdaQueryWrapper<Appointment> pendingAppointmentWrapper = new LambdaQueryWrapper<>();
        pendingAppointmentWrapper.eq(Appointment::getDeleted, 0);
        pendingAppointmentWrapper.eq(Appointment::getStatus, 0); // 待确认
        vo.setPendingAppointments(appointmentMapper.selectCount(pendingAppointmentWrapper).intValue());

        return vo;
    }

    @Override
    public UserStatsVO getUserStats() {
        UserStatsVO vo = new UserStatsVO();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime weekStart = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.MIN);
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);

        // 家长统计
        LambdaQueryWrapper<Parent> parentWrapper = new LambdaQueryWrapper<>();
        parentWrapper.eq(Parent::getDeleted, 0);
        vo.setTotalParents(parentMapper.selectCount(parentWrapper).intValue());

        // 活跃家长（有订单的家长）
        LambdaQueryWrapper<Order> activeParentWrapper = new LambdaQueryWrapper<>();
        activeParentWrapper.eq(Order::getDeleted, 0);
        List<Order> orders = orderMapper.selectList(activeParentWrapper);
        long activeParents = orders.stream().map(Order::getParentId).distinct().count();
        vo.setActiveParents((int) activeParents);

        // 今日新增家长
        LambdaQueryWrapper<Parent> todayParentWrapper = new LambdaQueryWrapper<>();
        todayParentWrapper.eq(Parent::getDeleted, 0);
        todayParentWrapper.between(Parent::getCreateTime, todayStart, now);
        vo.setNewParentsToday(parentMapper.selectCount(todayParentWrapper).intValue());

        // 本周新增家长
        LambdaQueryWrapper<Parent> weekParentWrapper = new LambdaQueryWrapper<>();
        weekParentWrapper.eq(Parent::getDeleted, 0);
        weekParentWrapper.between(Parent::getCreateTime, weekStart, now);
        vo.setNewParentsWeek(parentMapper.selectCount(weekParentWrapper).intValue());

        // 本月新增家长
        LambdaQueryWrapper<Parent> monthParentWrapper = new LambdaQueryWrapper<>();
        monthParentWrapper.eq(Parent::getDeleted, 0);
        monthParentWrapper.between(Parent::getCreateTime, monthStart, now);
        vo.setNewParentsMonth(parentMapper.selectCount(monthParentWrapper).intValue());

        // 保姆统计
        LambdaQueryWrapper<Nanny> nannyWrapper = new LambdaQueryWrapper<>();
        nannyWrapper.eq(Nanny::getDeleted, 0);
        vo.setTotalNannies(nannyMapper.selectCount(nannyWrapper).intValue());

        // 在职保姆
        LambdaQueryWrapper<Nanny> activeNannyWrapper = new LambdaQueryWrapper<>();
        activeNannyWrapper.eq(Nanny::getDeleted, 0);
        activeNannyWrapper.eq(Nanny::getStatus, 1);
        vo.setActiveNannies(nannyMapper.selectCount(activeNannyWrapper).intValue());

        // 本月新增保姆
        LambdaQueryWrapper<Nanny> monthNannyWrapper = new LambdaQueryWrapper<>();
        monthNannyWrapper.eq(Nanny::getDeleted, 0);
        monthNannyWrapper.between(Nanny::getCreateTime, monthStart, now);
        vo.setNewNanniesMonth(nannyMapper.selectCount(monthNannyWrapper).intValue());

        // 家长增长趋势（最近7天）
        vo.setParentTrend(getUserTrend(parentMapper, 7));

        // 保姆增长趋势（最近7天）
        vo.setNannyTrend(getNannyTrend(7));

        return vo;
    }

    @Override
    public InstitutionStatsVO getInstitutionStats() {
        InstitutionStatsVO vo = new InstitutionStatsVO();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);

        // 机构总数
        LambdaQueryWrapper<Institution> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(Institution::getDeleted, 0);
        vo.setTotalInstitutions(institutionMapper.selectCount(totalWrapper).intValue());

        // 正常机构
        LambdaQueryWrapper<Institution> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.eq(Institution::getDeleted, 0);
        activeWrapper.eq(Institution::getStatus, 1);
        vo.setActiveInstitutions(institutionMapper.selectCount(activeWrapper).intValue());

        // 待审核机构
        LambdaQueryWrapper<Institution> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(Institution::getDeleted, 0);
        pendingWrapper.eq(Institution::getStatus, 2);
        vo.setPendingInstitutions(institutionMapper.selectCount(pendingWrapper).intValue());

        // 禁用机构
        LambdaQueryWrapper<Institution> disabledWrapper = new LambdaQueryWrapper<>();
        disabledWrapper.eq(Institution::getDeleted, 0);
        disabledWrapper.eq(Institution::getStatus, 0);
        vo.setDisabledInstitutions(institutionMapper.selectCount(disabledWrapper).intValue());

        // 本月新增机构
        LambdaQueryWrapper<Institution> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.eq(Institution::getDeleted, 0);
        monthWrapper.between(Institution::getCreateTime, monthStart, now);
        vo.setNewInstitutionsMonth(institutionMapper.selectCount(monthWrapper).intValue());

        // 机构增长趋势
        vo.setInstitutionTrend(getInstitutionTrend(7));

        // 机构排行榜（按订单数）
        vo.setTopInstitutions(getTopInstitutions(10));

        return vo;
    }

    @Override
    public OrderStatsVO getOrderStats() {
        OrderStatsVO vo = new OrderStatsVO();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime weekStart = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.MIN);
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);

        // 订单总数
        LambdaQueryWrapper<Order> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(Order::getDeleted, 0);
        vo.setTotalOrders(orderMapper.selectCount(totalWrapper).intValue());

        // 各状态订单数
        vo.setPendingPayOrders(getOrderCountByStatus((byte) 0));
        vo.setPendingDeliveryOrders(getOrderCountByStatus((byte) 1));
        vo.setPendingReceiveOrders(getOrderCountByStatus((byte) 2));
        vo.setCompletedOrders(getOrderCountByStatus((byte) 3));
        vo.setCancelledOrders(getOrderCountByStatus((byte) 4));
        vo.setRefundedOrders(getOrderCountByStatus((byte) 5));

        // 今日订单
        LambdaQueryWrapper<Order> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(Order::getDeleted, 0);
        todayWrapper.between(Order::getCreateTime, todayStart, now);
        vo.setTodayOrders(orderMapper.selectCount(todayWrapper).intValue());

        // 本周订单
        LambdaQueryWrapper<Order> weekWrapper = new LambdaQueryWrapper<>();
        weekWrapper.eq(Order::getDeleted, 0);
        weekWrapper.between(Order::getCreateTime, weekStart, now);
        vo.setWeekOrders(orderMapper.selectCount(weekWrapper).intValue());

        // 本月订单
        LambdaQueryWrapper<Order> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.eq(Order::getDeleted, 0);
        monthWrapper.between(Order::getCreateTime, monthStart, now);
        vo.setMonthOrders(orderMapper.selectCount(monthWrapper).intValue());

        // 订单趋势（最近7天）
        vo.setOrderTrend(getOrderTrend(7));

        // 订单状态分布
        vo.setOrderStatusDist(getOrderStatusDistribution());

        return vo;
    }

    @Override
    public RevenueStatsVO getRevenueStats() {
        RevenueStatsVO vo = new RevenueStatsVO();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime weekStart = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.MIN);
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);

        // 总收入
        vo.setTotalRevenue(getRevenueByTimeRange(null, null));

        // 今日收入
        vo.setTodayRevenue(getRevenueByTimeRange(todayStart, now));

        // 本周收入
        vo.setWeekRevenue(getRevenueByTimeRange(weekStart, now));

        // 本月收入
        vo.setMonthRevenue(getRevenueByTimeRange(monthStart, now));

        // 平均订单金额
        LambdaQueryWrapper<Order> avgWrapper = new LambdaQueryWrapper<>();
        avgWrapper.eq(Order::getDeleted, 0);
        avgWrapper.eq(Order::getStatus, 3);
        List<Order> completedOrders = orderMapper.selectList(avgWrapper);
        if (!completedOrders.isEmpty()) {
            BigDecimal total = completedOrders.stream()
                    .map(o -> o.getPayAmount() != null ? o.getPayAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setAvgOrderAmount(total.divide(BigDecimal.valueOf(completedOrders.size()), 2, RoundingMode.HALF_UP));
        } else {
            vo.setAvgOrderAmount(BigDecimal.ZERO);
        }

        // 收入趋势（最近7天）
        vo.setRevenueTrend(getRevenueTrend(7));

        // 支付方式分布
        vo.setPayTypeDist(getPayTypeDistribution());

        return vo;
    }

    @Override
    public ReportVO getReports(String type, String startDate, String endDate) {
        ReportVO vo = new ReportVO();
        vo.setReportType(type);
        vo.setStartDate(startDate);
        vo.setEndDate(endDate);

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        LocalDateTime startTime = LocalDateTime.of(start, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<Map<String, Object>> data = new ArrayList<>();

        switch (type) {
            case "order":
                data = getOrderReport(startTime, endTime);
                break;
            case "revenue":
                data = getRevenueReport(startTime, endTime);
                break;
            case "user":
                data = getUserReport(startTime, endTime);
                break;
            default:
                break;
        }

        vo.setData(data);
        return vo;
    }

    // ==================== 私有辅助方法 ====================

    private Integer getOrderCountByStatus(Byte status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getDeleted, 0);
        wrapper.eq(Order::getStatus, status);
        return orderMapper.selectCount(wrapper).intValue();
    }

    private BigDecimal getRevenueByTimeRange(LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getDeleted, 0);
        wrapper.eq(Order::getStatus, 3);
        if (start != null && end != null) {
            wrapper.between(Order::getPayTime, start, end);
        }
        List<Order> orders = orderMapper.selectList(wrapper);
        return orders.stream()
                .map(o -> o.getPayAmount() != null ? o.getPayAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<Map<String, Object>> getUserTrend(ParentMapper mapper, int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            LambdaQueryWrapper<Parent> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Parent::getDeleted, 0);
            wrapper.between(Parent::getCreateTime, dayStart, dayEnd);
            int count = mapper.selectCount(wrapper).intValue();

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("count", count);
            trend.add(item);
        }
        return trend;
    }

    private List<Map<String, Object>> getNannyTrend(int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            LambdaQueryWrapper<Nanny> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Nanny::getDeleted, 0);
            wrapper.between(Nanny::getCreateTime, dayStart, dayEnd);
            int count = nannyMapper.selectCount(wrapper).intValue();

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("count", count);
            trend.add(item);
        }
        return trend;
    }

    private List<Map<String, Object>> getInstitutionTrend(int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            LambdaQueryWrapper<Institution> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Institution::getDeleted, 0);
            wrapper.between(Institution::getCreateTime, dayStart, dayEnd);
            int count = institutionMapper.selectCount(wrapper).intValue();

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("count", count);
            trend.add(item);
        }
        return trend;
    }

    private List<Map<String, Object>> getTopInstitutions(int limit) {
        List<Map<String, Object>> result = new ArrayList<>();

        LambdaQueryWrapper<Institution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Institution::getDeleted, 0);
        wrapper.eq(Institution::getStatus, 1);
        List<Institution> institutions = institutionMapper.selectList(wrapper);

        for (Institution institution : institutions) {
            LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(Order::getDeleted, 0);
            orderWrapper.eq(Order::getInstitutionId, institution.getId());
            int orderCount = orderMapper.selectCount(orderWrapper).intValue();

            LambdaQueryWrapper<Order> revenueWrapper = new LambdaQueryWrapper<>();
            revenueWrapper.eq(Order::getDeleted, 0);
            revenueWrapper.eq(Order::getInstitutionId, institution.getId());
            revenueWrapper.eq(Order::getStatus, 3);
            List<Order> orders = orderMapper.selectList(revenueWrapper);
            BigDecimal revenue = orders.stream()
                    .map(o -> o.getPayAmount() != null ? o.getPayAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> item = new HashMap<>();
            item.put("id", institution.getId());
            item.put("name", institution.getName());
            item.put("orderCount", orderCount);
            item.put("revenue", revenue);
            result.add(item);
        }

        result.sort((a, b) -> ((Integer) b.get("orderCount")).compareTo((Integer) a.get("orderCount")));
        return result.stream().limit(limit).collect(Collectors.toList());
    }

    private List<Map<String, Object>> getOrderTrend(int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getDeleted, 0);
            wrapper.between(Order::getCreateTime, dayStart, dayEnd);
            int count = orderMapper.selectCount(wrapper).intValue();

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("count", count);
            trend.add(item);
        }
        return trend;
    }

    private List<Map<String, Object>> getOrderStatusDistribution() {
        List<Map<String, Object>> dist = new ArrayList<>();
        String[] statusNames = {"待付款", "待发货", "待收货", "已完成", "已取消", "已退款"};

        for (int i = 0; i <= 5; i++) {
            int count = getOrderCountByStatus((byte) i);
            Map<String, Object> item = new HashMap<>();
            item.put("status", i);
            item.put("name", statusNames[i]);
            item.put("count", count);
            dist.add(item);
        }
        return dist;
    }

    private List<Map<String, Object>> getRevenueTrend(int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);
            BigDecimal revenue = getRevenueByTimeRange(dayStart, dayEnd);

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("revenue", revenue);
            trend.add(item);
        }
        return trend;
    }

    private List<Map<String, Object>> getPayTypeDistribution() {
        List<Map<String, Object>> dist = new ArrayList<>();
        String[] payTypeNames = {"", "微信", "支付宝", "余额"};

        for (int i = 1; i <= 3; i++) {
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getDeleted, 0);
            wrapper.eq(Order::getPayType, i);
            wrapper.isNotNull(Order::getPayTime);
            int count = orderMapper.selectCount(wrapper).intValue();

            Map<String, Object> item = new HashMap<>();
            item.put("payType", i);
            item.put("name", payTypeNames[i]);
            item.put("count", count);
            dist.add(item);
        }
        return dist;
    }

    private List<Map<String, Object>> getOrderReport(LocalDateTime startTime, LocalDateTime endTime) {
        List<Map<String, Object>> data = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate start = startTime.toLocalDate();
        LocalDate end = endTime.toLocalDate();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getDeleted, 0);
            wrapper.between(Order::getCreateTime, dayStart, dayEnd);
            int totalOrders = orderMapper.selectCount(wrapper).intValue();

            LambdaQueryWrapper<Order> completedWrapper = new LambdaQueryWrapper<>();
            completedWrapper.eq(Order::getDeleted, 0);
            completedWrapper.eq(Order::getStatus, 3);
            completedWrapper.between(Order::getCreateTime, dayStart, dayEnd);
            int completedOrders = orderMapper.selectCount(completedWrapper).intValue();

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("totalOrders", totalOrders);
            item.put("completedOrders", completedOrders);
            data.add(item);
        }
        return data;
    }

    private List<Map<String, Object>> getRevenueReport(LocalDateTime startTime, LocalDateTime endTime) {
        List<Map<String, Object>> data = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate start = startTime.toLocalDate();
        LocalDate end = endTime.toLocalDate();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            BigDecimal revenue = getRevenueByTimeRange(dayStart, dayEnd);

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("revenue", revenue);
            data.add(item);
        }
        return data;
    }

    private List<Map<String, Object>> getUserReport(LocalDateTime startTime, LocalDateTime endTime) {
        List<Map<String, Object>> data = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate start = startTime.toLocalDate();
        LocalDate end = endTime.toLocalDate();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            LambdaQueryWrapper<Parent> parentWrapper = new LambdaQueryWrapper<>();
            parentWrapper.eq(Parent::getDeleted, 0);
            parentWrapper.between(Parent::getCreateTime, dayStart, dayEnd);
            int newParents = parentMapper.selectCount(parentWrapper).intValue();

            LambdaQueryWrapper<Nanny> nannyWrapper = new LambdaQueryWrapper<>();
            nannyWrapper.eq(Nanny::getDeleted, 0);
            nannyWrapper.between(Nanny::getCreateTime, dayStart, dayEnd);
            int newNannies = nannyMapper.selectCount(nannyWrapper).intValue();

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("newParents", newParents);
            item.put("newNannies", newNannies);
            data.add(item);
        }
        return data;
    }
}