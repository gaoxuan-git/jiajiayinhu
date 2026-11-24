package com.lidexin.business.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 机构数据分析服务接口
 *
 * @author 张三
 * @since 2025-12-25
 */
public interface IInstitutionDashboardService {

    /**
     * 获取机构仪表板数据
     */
    DashboardVO getDashboardData(Long institutionId);

    /**
     * 获取保姆绩效排名
     */
    List<NannyRankVO> getNannyRanking(Long institutionId, String type);

    /**
     * 获取服务数据统计
     */
    ServiceStatsVO getServiceStats(Long institutionId);

    /**
     * 获取评价分析
     */
    EvaluationStatsVO getEvaluationStats(Long institutionId);

    /**
     * 获取财务数据
     */
    FinanceStatsVO getFinanceStats(Long institutionId);

    /**
     * 导出报表数据
     */
    ReportDataVO exportReport(Long institutionId, String type, String startDate, String endDate);

    // ==================== VO类定义 ====================

    /**
     * 机构仪表板数据
     */
    class DashboardVO {
        private Integer totalNannies;           // 保姆总数
        private Integer activeNannies;          // 在职保姆数
        private Integer totalAppointments;      // 预约总数
        private Integer pendingAppointments;    // 待处理预约
        private Integer totalOrders;            // 订单总数
        private Integer todayOrders;            // 今日订单
        private BigDecimal totalRevenue;        // 总收入
        private BigDecimal todayRevenue;        // 今日收入
        private BigDecimal monthRevenue;        // 本月收入
        private Integer totalProducts;          // 商品总数
        private Integer lowStockProducts;       // 库存预警商品数
        private Double avgRating;               // 平均评分
        private List<Map<String, Object>> orderTrend;      // 订单趋势（7天）
        private List<Map<String, Object>> revenueTrend;    // 收入趋势（7天）

        public Integer getTotalNannies() { return totalNannies; }
        public void setTotalNannies(Integer totalNannies) { this.totalNannies = totalNannies; }
        public Integer getActiveNannies() { return activeNannies; }
        public void setActiveNannies(Integer activeNannies) { this.activeNannies = activeNannies; }
        public Integer getTotalAppointments() { return totalAppointments; }
        public void setTotalAppointments(Integer totalAppointments) { this.totalAppointments = totalAppointments; }
        public Integer getPendingAppointments() { return pendingAppointments; }
        public void setPendingAppointments(Integer pendingAppointments) { this.pendingAppointments = pendingAppointments; }
        public Integer getTotalOrders() { return totalOrders; }
        public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
        public Integer getTodayOrders() { return todayOrders; }
        public void setTodayOrders(Integer todayOrders) { this.todayOrders = todayOrders; }
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
        public BigDecimal getTodayRevenue() { return todayRevenue; }
        public void setTodayRevenue(BigDecimal todayRevenue) { this.todayRevenue = todayRevenue; }
        public BigDecimal getMonthRevenue() { return monthRevenue; }
        public void setMonthRevenue(BigDecimal monthRevenue) { this.monthRevenue = monthRevenue; }
        public Integer getTotalProducts() { return totalProducts; }
        public void setTotalProducts(Integer totalProducts) { this.totalProducts = totalProducts; }
        public Integer getLowStockProducts() { return lowStockProducts; }
        public void setLowStockProducts(Integer lowStockProducts) { this.lowStockProducts = lowStockProducts; }
        public Double getAvgRating() { return avgRating; }
        public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }
        public List<Map<String, Object>> getOrderTrend() { return orderTrend; }
        public void setOrderTrend(List<Map<String, Object>> orderTrend) { this.orderTrend = orderTrend; }
        public List<Map<String, Object>> getRevenueTrend() { return revenueTrend; }
        public void setRevenueTrend(List<Map<String, Object>> revenueTrend) { this.revenueTrend = revenueTrend; }
    }

    /**
     * 保姆绩效排名
     */
    class NannyRankVO {
        private Long nannyId;
        private String nannyName;
        private String avatar;
        private Integer orderCount;         // 订单数
        private BigDecimal revenue;         // 创收金额
        private Double avgRating;           // 平均评分
        private Integer evaluationCount;    // 评价数
        private Integer rank;               // 排名

        public Long getNannyId() { return nannyId; }
        public void setNannyId(Long nannyId) { this.nannyId = nannyId; }
        public String getNannyName() { return nannyName; }
        public void setNannyName(String nannyName) { this.nannyName = nannyName; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public Integer getOrderCount() { return orderCount; }
        public void setOrderCount(Integer orderCount) { this.orderCount = orderCount; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        public Double getAvgRating() { return avgRating; }
        public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }
        public Integer getEvaluationCount() { return evaluationCount; }
        public void setEvaluationCount(Integer evaluationCount) { this.evaluationCount = evaluationCount; }
        public Integer getRank() { return rank; }
        public void setRank(Integer rank) { this.rank = rank; }
    }

    /**
     * 服务数据统计
     */
    class ServiceStatsVO {
        private Integer totalAppointments;      // 预约总数
        private Integer completedAppointments;  // 已完成预约
        private Integer cancelledAppointments;  // 已取消预约
        private Integer pendingAppointments;    // 待处理预约
        private Integer todayAppointments;      // 今日预约
        private Integer weekAppointments;       // 本周预约
        private Integer monthAppointments;      // 本月预约
        private List<Map<String, Object>> appointmentTrend;    // 预约趋势
        private List<Map<String, Object>> appointmentStatusDist; // 预约状态分布

        public Integer getTotalAppointments() { return totalAppointments; }
        public void setTotalAppointments(Integer totalAppointments) { this.totalAppointments = totalAppointments; }
        public Integer getCompletedAppointments() { return completedAppointments; }
        public void setCompletedAppointments(Integer completedAppointments) { this.completedAppointments = completedAppointments; }
        public Integer getCancelledAppointments() { return cancelledAppointments; }
        public void setCancelledAppointments(Integer cancelledAppointments) { this.cancelledAppointments = cancelledAppointments; }
        public Integer getPendingAppointments() { return pendingAppointments; }
        public void setPendingAppointments(Integer pendingAppointments) { this.pendingAppointments = pendingAppointments; }
        public Integer getTodayAppointments() { return todayAppointments; }
        public void setTodayAppointments(Integer todayAppointments) { this.todayAppointments = todayAppointments; }
        public Integer getWeekAppointments() { return weekAppointments; }
        public void setWeekAppointments(Integer weekAppointments) { this.weekAppointments = weekAppointments; }
        public Integer getMonthAppointments() { return monthAppointments; }
        public void setMonthAppointments(Integer monthAppointments) { this.monthAppointments = monthAppointments; }
        public List<Map<String, Object>> getAppointmentTrend() { return appointmentTrend; }
        public void setAppointmentTrend(List<Map<String, Object>> appointmentTrend) { this.appointmentTrend = appointmentTrend; }
        public List<Map<String, Object>> getAppointmentStatusDist() { return appointmentStatusDist; }
        public void setAppointmentStatusDist(List<Map<String, Object>> appointmentStatusDist) { this.appointmentStatusDist = appointmentStatusDist; }
    }

    /**
     * 评价分析统计
     */
    class EvaluationStatsVO {
        private Integer totalEvaluations;       // 评价总数
        private Double avgRating;               // 平均评分
        private Integer fiveStarCount;          // 五星评价数
        private Integer fourStarCount;          // 四星评价数
        private Integer threeStarCount;         // 三星评价数
        private Integer twoStarCount;           // 二星评价数
        private Integer oneStarCount;           // 一星评价数
        private List<Map<String, Object>> ratingDist;      // 评分分布
        private List<Map<String, Object>> ratingTrend;     // 评分趋势
        private List<Map<String, Object>> recentEvaluations; // 最近评价

        public Integer getTotalEvaluations() { return totalEvaluations; }
        public void setTotalEvaluations(Integer totalEvaluations) { this.totalEvaluations = totalEvaluations; }
        public Double getAvgRating() { return avgRating; }
        public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }
        public Integer getFiveStarCount() { return fiveStarCount; }
        public void setFiveStarCount(Integer fiveStarCount) { this.fiveStarCount = fiveStarCount; }
        public Integer getFourStarCount() { return fourStarCount; }
        public void setFourStarCount(Integer fourStarCount) { this.fourStarCount = fourStarCount; }
        public Integer getThreeStarCount() { return threeStarCount; }
        public void setThreeStarCount(Integer threeStarCount) { this.threeStarCount = threeStarCount; }
        public Integer getTwoStarCount() { return twoStarCount; }
        public void setTwoStarCount(Integer twoStarCount) { this.twoStarCount = twoStarCount; }
        public Integer getOneStarCount() { return oneStarCount; }
        public void setOneStarCount(Integer oneStarCount) { this.oneStarCount = oneStarCount; }
        public List<Map<String, Object>> getRatingDist() { return ratingDist; }
        public void setRatingDist(List<Map<String, Object>> ratingDist) { this.ratingDist = ratingDist; }
        public List<Map<String, Object>> getRatingTrend() { return ratingTrend; }
        public void setRatingTrend(List<Map<String, Object>> ratingTrend) { this.ratingTrend = ratingTrend; }
        public List<Map<String, Object>> getRecentEvaluations() { return recentEvaluations; }
        public void setRecentEvaluations(List<Map<String, Object>> recentEvaluations) { this.recentEvaluations = recentEvaluations; }
    }

    /**
     * 财务数据统计
     */
    class FinanceStatsVO {
        private BigDecimal totalRevenue;        // 总收入
        private BigDecimal todayRevenue;        // 今日收入
        private BigDecimal weekRevenue;         // 本周收入
        private BigDecimal monthRevenue;        // 本月收入
        private BigDecimal avgOrderAmount;      // 平均订单金额
        private Integer totalOrders;            // 订单总数
        private Integer completedOrders;        // 已完成订单
        private List<Map<String, Object>> revenueTrend;    // 收入趋势
        private List<Map<String, Object>> orderTrend;      // 订单趋势
        private List<Map<String, Object>> payTypeDist;     // 支付方式分布

        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
        public BigDecimal getTodayRevenue() { return todayRevenue; }
        public void setTodayRevenue(BigDecimal todayRevenue) { this.todayRevenue = todayRevenue; }
        public BigDecimal getWeekRevenue() { return weekRevenue; }
        public void setWeekRevenue(BigDecimal weekRevenue) { this.weekRevenue = weekRevenue; }
        public BigDecimal getMonthRevenue() { return monthRevenue; }
        public void setMonthRevenue(BigDecimal monthRevenue) { this.monthRevenue = monthRevenue; }
        public BigDecimal getAvgOrderAmount() { return avgOrderAmount; }
        public void setAvgOrderAmount(BigDecimal avgOrderAmount) { this.avgOrderAmount = avgOrderAmount; }
        public Integer getTotalOrders() { return totalOrders; }
        public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
        public Integer getCompletedOrders() { return completedOrders; }
        public void setCompletedOrders(Integer completedOrders) { this.completedOrders = completedOrders; }
        public List<Map<String, Object>> getRevenueTrend() { return revenueTrend; }
        public void setRevenueTrend(List<Map<String, Object>> revenueTrend) { this.revenueTrend = revenueTrend; }
        public List<Map<String, Object>> getOrderTrend() { return orderTrend; }
        public void setOrderTrend(List<Map<String, Object>> orderTrend) { this.orderTrend = orderTrend; }
        public List<Map<String, Object>> getPayTypeDist() { return payTypeDist; }
        public void setPayTypeDist(List<Map<String, Object>> payTypeDist) { this.payTypeDist = payTypeDist; }
    }

    /**
     * 报表数据
     */
    class ReportDataVO {
        private String reportType;
        private String startDate;
        private String endDate;
        private List<Map<String, Object>> data;

        public String getReportType() { return reportType; }
        public void setReportType(String reportType) { this.reportType = reportType; }
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
        public List<Map<String, Object>> getData() { return data; }
        public void setData(List<Map<String, Object>> data) { this.data = data; }
    }
}
