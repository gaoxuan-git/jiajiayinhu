package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lidexin.business.mapper.*;
import com.lidexin.business.po.*;
import com.lidexin.business.service.IInstitutionDashboardService;
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
 * 机构数据分析服务实现类
 *
 * @author 张三
 * @since 2025-12-25
 */
@Service
public class InstitutionDashboardServiceImpl implements IInstitutionDashboardService {

    @Autowired
    private NannyMapper nannyMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CareEvaluationMapper careEvaluationMapper;

    @Override
    public DashboardVO getDashboardData(Long institutionId) {
        DashboardVO vo = new DashboardVO();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);

        // 保姆总数
        LambdaQueryWrapper<Nanny> nannyWrapper = new LambdaQueryWrapper<>();
        nannyWrapper.eq(Nanny::getDeleted, 0);
        nannyWrapper.eq(Nanny::getInstitutionId, institutionId);
        vo.setTotalNannies(nannyMapper.selectCount(nannyWrapper).intValue());

        // 在职保姆
        LambdaQueryWrapper<Nanny> activeNannyWrapper = new LambdaQueryWrapper<>();
        activeNannyWrapper.eq(Nanny::getDeleted, 0);
        activeNannyWrapper.eq(Nanny::getInstitutionId, institutionId);
        activeNannyWrapper.eq(Nanny::getStatus, 1);
        vo.setActiveNannies(nannyMapper.selectCount(activeNannyWrapper).intValue());

        // 预约总数
        LambdaQueryWrapper<Appointment> appointmentWrapper = new LambdaQueryWrapper<>();
        appointmentWrapper.eq(Appointment::getDeleted, 0);
        appointmentWrapper.eq(Appointment::getInstitutionId, institutionId);
        vo.setTotalAppointments(appointmentMapper.selectCount(appointmentWrapper).intValue());

        // 待处理预约
        LambdaQueryWrapper<Appointment> pendingAppointmentWrapper = new LambdaQueryWrapper<>();
        pendingAppointmentWrapper.eq(Appointment::getDeleted, 0);
        pendingAppointmentWrapper.eq(Appointment::getInstitutionId, institutionId);
        pendingAppointmentWrapper.eq(Appointment::getStatus, 0);
        vo.setPendingAppointments(appointmentMapper.selectCount(pendingAppointmentWrapper).intValue());

        // 订单总数
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(Order::getDeleted, 0);
        orderWrapper.eq(Order::getInstitutionId, institutionId);
        vo.setTotalOrders(orderMapper.selectCount(orderWrapper).intValue());

        // 今日订单
        LambdaQueryWrapper<Order> todayOrderWrapper = new LambdaQueryWrapper<>();
        todayOrderWrapper.eq(Order::getDeleted, 0);
        todayOrderWrapper.eq(Order::getInstitutionId, institutionId);
        todayOrderWrapper.between(Order::getCreateTime, todayStart, now);
        vo.setTodayOrders(orderMapper.selectCount(todayOrderWrapper).intValue());

        // 总收入
        vo.setTotalRevenue(getRevenueByTimeRange(institutionId, null, null));

        // 今日收入
        vo.setTodayRevenue(getRevenueByTimeRange(institutionId, todayStart, now));

        // 本月收入
        vo.setMonthRevenue(getRevenueByTimeRange(institutionId, monthStart, now));

        // 商品总数
        LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
        productWrapper.eq(Product::getDeleted, 0);
        productWrapper.eq(Product::getInstitutionId, institutionId);
        vo.setTotalProducts(productMapper.selectCount(productWrapper).intValue());

        // 库存预警商品数
        LambdaQueryWrapper<Product> lowStockWrapper = new LambdaQueryWrapper<>();
        lowStockWrapper.eq(Product::getDeleted, 0);
        lowStockWrapper.eq(Product::getInstitutionId, institutionId);
        lowStockWrapper.apply("stock <= IFNULL(stock_alert, 10)");
        vo.setLowStockProducts(productMapper.selectCount(lowStockWrapper).intValue());

        // 平均评分
        vo.setAvgRating(getAvgRating(institutionId));

        // 订单趋势（7天）
        vo.setOrderTrend(getOrderTrend(institutionId, 7));

        // 收入趋势（7天）
        vo.setRevenueTrend(getRevenueTrend(institutionId, 7));

        return vo;
    }

    @Override
    public List<NannyRankVO> getNannyRanking(Long institutionId, String type) {
        List<NannyRankVO> result = new ArrayList<>();

        // 获取机构下所有保姆
        LambdaQueryWrapper<Nanny> nannyWrapper = new LambdaQueryWrapper<>();
        nannyWrapper.eq(Nanny::getDeleted, 0);
        nannyWrapper.eq(Nanny::getInstitutionId, institutionId);
        nannyWrapper.eq(Nanny::getStatus, 1);
        List<Nanny> nannies = nannyMapper.selectList(nannyWrapper);

        for (Nanny nanny : nannies) {
            NannyRankVO vo = new NannyRankVO();
            vo.setNannyId(nanny.getId());
            vo.setNannyName(nanny.getRealName());
            vo.setAvatar(nanny.getAvatar());

            // 评价数和平均评分
            LambdaQueryWrapper<CareEvaluation> evalWrapper = new LambdaQueryWrapper<>();
            evalWrapper.eq(CareEvaluation::getDeleted, 0);
            evalWrapper.eq(CareEvaluation::getNannyId, nanny.getId());
            List<CareEvaluation> evaluations = careEvaluationMapper.selectList(evalWrapper);
            vo.setEvaluationCount(evaluations.size());
            vo.setOrderCount(evaluations.size()); // 用评价数作为服务数
            if (!evaluations.isEmpty()) {
                double avgRating = evaluations.stream()
                        .mapToInt(e -> e.getScore() != null ? e.getScore() : 0)
                        .average()
                        .orElse(0.0);
                vo.setAvgRating(Math.round(avgRating * 10) / 10.0);
            } else {
                vo.setAvgRating(0.0);
            }
            vo.setRevenue(BigDecimal.ZERO); // 保姆没有直接关联订单收入

            result.add(vo);
        }

        // 根据类型排序
        switch (type) {
            case "order":
                result.sort((a, b) -> b.getOrderCount().compareTo(a.getOrderCount()));
                break;
            case "revenue":
                result.sort((a, b) -> b.getRevenue().compareTo(a.getRevenue()));
                break;
            case "rating":
                result.sort((a, b) -> b.getAvgRating().compareTo(a.getAvgRating()));
                break;
            default:
                result.sort((a, b) -> b.getOrderCount().compareTo(a.getOrderCount()));
        }

        // 设置排名
        for (int i = 0; i < result.size(); i++) {
            result.get(i).setRank(i + 1);
        }

        return result;
    }

    @Override
    public ServiceStatsVO getServiceStats(Long institutionId) {
        ServiceStatsVO vo = new ServiceStatsVO();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime weekStart = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.MIN);
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);

        // 预约总数
        LambdaQueryWrapper<Appointment> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(Appointment::getDeleted, 0);
        totalWrapper.eq(Appointment::getInstitutionId, institutionId);
        vo.setTotalAppointments(appointmentMapper.selectCount(totalWrapper).intValue());

        // 已完成预约
        vo.setCompletedAppointments(getAppointmentCountByStatus(institutionId, (byte) 2));

        // 已取消预约
        vo.setCancelledAppointments(getAppointmentCountByStatus(institutionId, (byte) 3));

        // 待处理预约
        vo.setPendingAppointments(getAppointmentCountByStatus(institutionId, (byte) 0));

        // 今日预约
        LambdaQueryWrapper<Appointment> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(Appointment::getDeleted, 0);
        todayWrapper.eq(Appointment::getInstitutionId, institutionId);
        todayWrapper.between(Appointment::getCreateTime, todayStart, now);
        vo.setTodayAppointments(appointmentMapper.selectCount(todayWrapper).intValue());

        // 本周预约
        LambdaQueryWrapper<Appointment> weekWrapper = new LambdaQueryWrapper<>();
        weekWrapper.eq(Appointment::getDeleted, 0);
        weekWrapper.eq(Appointment::getInstitutionId, institutionId);
        weekWrapper.between(Appointment::getCreateTime, weekStart, now);
        vo.setWeekAppointments(appointmentMapper.selectCount(weekWrapper).intValue());

        // 本月预约
        LambdaQueryWrapper<Appointment> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.eq(Appointment::getDeleted, 0);
        monthWrapper.eq(Appointment::getInstitutionId, institutionId);
        monthWrapper.between(Appointment::getCreateTime, monthStart, now);
        vo.setMonthAppointments(appointmentMapper.selectCount(monthWrapper).intValue());

        // 预约趋势
        vo.setAppointmentTrend(getAppointmentTrend(institutionId, 7));

        // 预约状态分布
        vo.setAppointmentStatusDist(getAppointmentStatusDistribution(institutionId));

        return vo;
    }

    @Override
    public EvaluationStatsVO getEvaluationStats(Long institutionId) {
        EvaluationStatsVO vo = new EvaluationStatsVO();

        // 获取机构下所有保姆的ID
        LambdaQueryWrapper<Nanny> nannyWrapper = new LambdaQueryWrapper<>();
        nannyWrapper.eq(Nanny::getDeleted, 0);
        nannyWrapper.eq(Nanny::getInstitutionId, institutionId);
        List<Nanny> nannies = nannyMapper.selectList(nannyWrapper);
        List<Long> nannyIds = nannies.stream().map(Nanny::getId).collect(Collectors.toList());

        if (nannyIds.isEmpty()) {
            vo.setTotalEvaluations(0);
            vo.setAvgRating(0.0);
            vo.setFiveStarCount(0);
            vo.setFourStarCount(0);
            vo.setThreeStarCount(0);
            vo.setTwoStarCount(0);
            vo.setOneStarCount(0);
            vo.setRatingDist(new ArrayList<>());
            vo.setRatingTrend(new ArrayList<>());
            vo.setRecentEvaluations(new ArrayList<>());
            return vo;
        }

        // 获取所有评价
        LambdaQueryWrapper<CareEvaluation> evalWrapper = new LambdaQueryWrapper<>();
        evalWrapper.eq(CareEvaluation::getDeleted, 0);
        evalWrapper.in(CareEvaluation::getNannyId, nannyIds);
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

        // 各星级评价数
        vo.setFiveStarCount((int) evaluations.stream().filter(e -> e.getScore() != null && e.getScore() == 5).count());
        vo.setFourStarCount((int) evaluations.stream().filter(e -> e.getScore() != null && e.getScore() == 4).count());
        vo.setThreeStarCount((int) evaluations.stream().filter(e -> e.getScore() != null && e.getScore() == 3).count());
        vo.setTwoStarCount((int) evaluations.stream().filter(e -> e.getScore() != null && e.getScore() == 2).count());
        vo.setOneStarCount((int) evaluations.stream().filter(e -> e.getScore() != null && e.getScore() == 1).count());

        // 评分分布
        vo.setRatingDist(getRatingDistribution(evaluations));

        // 评分趋势
        vo.setRatingTrend(getRatingTrend(nannyIds, 7));

        // 最近评价
        vo.setRecentEvaluations(getRecentEvaluations(nannyIds, 10));

        return vo;
    }

    @Override
    public FinanceStatsVO getFinanceStats(Long institutionId) {
        FinanceStatsVO vo = new FinanceStatsVO();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime weekStart = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.MIN);
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);

        // 总收入
        vo.setTotalRevenue(getRevenueByTimeRange(institutionId, null, null));

        // 今日收入
        vo.setTodayRevenue(getRevenueByTimeRange(institutionId, todayStart, now));

        // 本周收入
        vo.setWeekRevenue(getRevenueByTimeRange(institutionId, weekStart, now));

        // 本月收入
        vo.setMonthRevenue(getRevenueByTimeRange(institutionId, monthStart, now));

        // 订单总数
        LambdaQueryWrapper<Order> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(Order::getDeleted, 0);
        totalWrapper.eq(Order::getInstitutionId, institutionId);
        vo.setTotalOrders(orderMapper.selectCount(totalWrapper).intValue());

        // 已完成订单
        LambdaQueryWrapper<Order> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(Order::getDeleted, 0);
        completedWrapper.eq(Order::getInstitutionId, institutionId);
        completedWrapper.eq(Order::getStatus, 3);
        List<Order> completedOrders = orderMapper.selectList(completedWrapper);
        vo.setCompletedOrders(completedOrders.size());

        // 平均订单金额
        if (!completedOrders.isEmpty()) {
            BigDecimal total = completedOrders.stream()
                    .map(o -> o.getPayAmount() != null ? o.getPayAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setAvgOrderAmount(total.divide(BigDecimal.valueOf(completedOrders.size()), 2, RoundingMode.HALF_UP));
        } else {
            vo.setAvgOrderAmount(BigDecimal.ZERO);
        }

        // 收入趋势
        vo.setRevenueTrend(getRevenueTrend(institutionId, 7));

        // 订单趋势
        vo.setOrderTrend(getOrderTrend(institutionId, 7));

        // 支付方式分布
        vo.setPayTypeDist(getPayTypeDistribution(institutionId));

        return vo;
    }

    @Override
    public ReportDataVO exportReport(Long institutionId, String type, String startDate, String endDate) {
        ReportDataVO vo = new ReportDataVO();
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
                data = getOrderReport(institutionId, startTime, endTime);
                break;
            case "revenue":
                data = getRevenueReport(institutionId, startTime, endTime);
                break;
            case "service":
                data = getServiceReport(institutionId, startTime, endTime);
                break;
            default:
                break;
        }

        vo.setData(data);
        return vo;
    }

    // ==================== 私有辅助方法 ====================

    private BigDecimal getRevenueByTimeRange(Long institutionId, LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getDeleted, 0);
        wrapper.eq(Order::getInstitutionId, institutionId);
        wrapper.eq(Order::getStatus, 3);
        if (start != null && end != null) {
            wrapper.between(Order::getPayTime, start, end);
        }
        List<Order> orders = orderMapper.selectList(wrapper);
        return orders.stream()
                .map(o -> o.getPayAmount() != null ? o.getPayAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Double getAvgRating(Long institutionId) {
        LambdaQueryWrapper<Nanny> nannyWrapper = new LambdaQueryWrapper<>();
        nannyWrapper.eq(Nanny::getDeleted, 0);
        nannyWrapper.eq(Nanny::getInstitutionId, institutionId);
        List<Nanny> nannies = nannyMapper.selectList(nannyWrapper);
        List<Long> nannyIds = nannies.stream().map(Nanny::getId).collect(Collectors.toList());

        if (nannyIds.isEmpty()) {
            return 0.0;
        }

        LambdaQueryWrapper<CareEvaluation> evalWrapper = new LambdaQueryWrapper<>();
        evalWrapper.eq(CareEvaluation::getDeleted, 0);
        evalWrapper.in(CareEvaluation::getNannyId, nannyIds);
        List<CareEvaluation> evaluations = careEvaluationMapper.selectList(evalWrapper);

        if (evaluations.isEmpty()) {
            return 0.0;
        }

        double avg = evaluations.stream()
                .mapToInt(e -> e.getScore() != null ? e.getScore() : 0)
                .average()
                .orElse(0.0);
        return Math.round(avg * 10) / 10.0;
    }

    private Integer getAppointmentCountByStatus(Long institutionId, Byte status) {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getDeleted, 0);
        wrapper.eq(Appointment::getInstitutionId, institutionId);
        wrapper.eq(Appointment::getStatus, status);
        return appointmentMapper.selectCount(wrapper).intValue();
    }

    private List<Map<String, Object>> getOrderTrend(Long institutionId, int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getDeleted, 0);
            wrapper.eq(Order::getInstitutionId, institutionId);
            wrapper.between(Order::getCreateTime, dayStart, dayEnd);
            int count = orderMapper.selectCount(wrapper).intValue();

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("count", count);
            trend.add(item);
        }
        return trend;
    }

    private List<Map<String, Object>> getRevenueTrend(Long institutionId, int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);
            BigDecimal revenue = getRevenueByTimeRange(institutionId, dayStart, dayEnd);

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("revenue", revenue);
            trend.add(item);
        }
        return trend;
    }

    private List<Map<String, Object>> getAppointmentTrend(Long institutionId, int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Appointment::getDeleted, 0);
            wrapper.eq(Appointment::getInstitutionId, institutionId);
            wrapper.between(Appointment::getCreateTime, dayStart, dayEnd);
            int count = appointmentMapper.selectCount(wrapper).intValue();

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("count", count);
            trend.add(item);
        }
        return trend;
    }

    private List<Map<String, Object>> getAppointmentStatusDistribution(Long institutionId) {
        List<Map<String, Object>> dist = new ArrayList<>();
        String[] statusNames = {"待确认", "已确认", "已完成", "已取消"};

        for (int i = 0; i <= 3; i++) {
            int count = getAppointmentCountByStatus(institutionId, (byte) i);
            Map<String, Object> item = new HashMap<>();
            item.put("status", i);
            item.put("name", statusNames[i]);
            item.put("count", count);
            dist.add(item);
        }
        return dist;
    }

    private List<Map<String, Object>> getRatingDistribution(List<CareEvaluation> evaluations) {
        List<Map<String, Object>> dist = new ArrayList<>();
        for (int i = 5; i >= 1; i--) {
            final int rating = i;
            int count = (int) evaluations.stream()
                    .filter(e -> e.getScore() != null && e.getScore() == rating)
                    .count();
            Map<String, Object> item = new HashMap<>();
            item.put("rating", i);
            item.put("name", i + "星");
            item.put("count", count);
            dist.add(item);
        }
        return dist;
    }

    private List<Map<String, Object>> getRatingTrend(List<Long> nannyIds, int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            LambdaQueryWrapper<CareEvaluation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CareEvaluation::getDeleted, 0);
            wrapper.in(CareEvaluation::getNannyId, nannyIds);
            wrapper.between(CareEvaluation::getCreateTime, dayStart, dayEnd);
            List<CareEvaluation> evals = careEvaluationMapper.selectList(wrapper);

            double avgRating = 0.0;
            if (!evals.isEmpty()) {
                avgRating = evals.stream()
                        .mapToInt(e -> e.getScore() != null ? e.getScore() : 0)
                        .average()
                        .orElse(0.0);
                avgRating = Math.round(avgRating * 10) / 10.0;
            }

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("avgRating", avgRating);
            item.put("count", evals.size());
            trend.add(item);
        }
        return trend;
    }

    private List<Map<String, Object>> getRecentEvaluations(List<Long> nannyIds, int limit) {
        List<Map<String, Object>> result = new ArrayList<>();

        LambdaQueryWrapper<CareEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CareEvaluation::getDeleted, 0);
        wrapper.in(CareEvaluation::getNannyId, nannyIds);
        wrapper.orderByDesc(CareEvaluation::getCreateTime);
        wrapper.last("LIMIT " + limit);
        List<CareEvaluation> evaluations = careEvaluationMapper.selectList(wrapper);

        for (CareEvaluation eval : evaluations) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", eval.getId());
            item.put("rating", eval.getScore());
            item.put("content", eval.getContent());
            item.put("createTime", eval.getCreateTime());

            Nanny nanny = nannyMapper.selectById(eval.getNannyId());
            item.put("nannyName", nanny != null ? nanny.getRealName() : "未知");

            result.add(item);
        }
        return result;
    }

    private List<Map<String, Object>> getPayTypeDistribution(Long institutionId) {
        List<Map<String, Object>> dist = new ArrayList<>();
        String[] payTypeNames = {"", "微信", "支付宝", "余额"};

        for (int i = 1; i <= 3; i++) {
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getDeleted, 0);
            wrapper.eq(Order::getInstitutionId, institutionId);
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

    private List<Map<String, Object>> getOrderReport(Long institutionId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Map<String, Object>> data = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate start = startTime.toLocalDate();
        LocalDate end = endTime.toLocalDate();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getDeleted, 0);
            wrapper.eq(Order::getInstitutionId, institutionId);
            wrapper.between(Order::getCreateTime, dayStart, dayEnd);
            int totalOrders = orderMapper.selectCount(wrapper).intValue();

            LambdaQueryWrapper<Order> completedWrapper = new LambdaQueryWrapper<>();
            completedWrapper.eq(Order::getDeleted, 0);
            completedWrapper.eq(Order::getInstitutionId, institutionId);
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

    private List<Map<String, Object>> getRevenueReport(Long institutionId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Map<String, Object>> data = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate start = startTime.toLocalDate();
        LocalDate end = endTime.toLocalDate();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            BigDecimal revenue = getRevenueByTimeRange(institutionId, dayStart, dayEnd);

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("revenue", revenue);
            data.add(item);
        }
        return data;
    }

    private List<Map<String, Object>> getServiceReport(Long institutionId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Map<String, Object>> data = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate start = startTime.toLocalDate();
        LocalDate end = endTime.toLocalDate();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Appointment::getDeleted, 0);
            wrapper.eq(Appointment::getInstitutionId, institutionId);
            wrapper.between(Appointment::getCreateTime, dayStart, dayEnd);
            int totalAppointments = appointmentMapper.selectCount(wrapper).intValue();

            LambdaQueryWrapper<Appointment> completedWrapper = new LambdaQueryWrapper<>();
            completedWrapper.eq(Appointment::getDeleted, 0);
            completedWrapper.eq(Appointment::getInstitutionId, institutionId);
            completedWrapper.eq(Appointment::getStatus, 2);
            completedWrapper.between(Appointment::getCreateTime, dayStart, dayEnd);
            int completedAppointments = appointmentMapper.selectCount(completedWrapper).intValue();

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("totalAppointments", totalAppointments);
            item.put("completedAppointments", completedAppointments);
            data.add(item);
        }
        return data;
    }
}