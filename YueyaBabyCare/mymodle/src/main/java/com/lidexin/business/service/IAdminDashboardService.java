package com.lidexin.business.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 管理员数据分析服务接口
 *
 * @author 张三
 * @since 2025-12-25
 */
public interface IAdminDashboardService {

    /**
     * 获取管理后台首页数据
     */
    DashboardVO getDashboardData();

    /**
     * 获取用户统计
     */
    UserStatsVO getUserStats();

    /**
     * 获取机构统计
     */
    InstitutionStatsVO getInstitutionStats();

    /**
     * 获取订单统计
     */
    OrderStatsVO getOrderStats();

    /**
     * 获取收入统计
     */
    RevenueStatsVO getRevenueStats();

    /**
     * 生成报表数据
     */
    ReportVO getReports(String type, String startDate, String endDate);

    // ==================== VO类定义 ====================

    /**
     * 首页仪表盘数据
     */
    class DashboardVO {
        private Integer totalParents;       // 家长总数
        private Integer totalNannies;       // 保姆总数
        private Integer totalInstitutions;  // 机构总数
        private Integer totalBabies;        // 宝宝总数
        private Integer totalOrders;        // 订单总数
        private BigDecimal totalRevenue;    // 总收入
        private Integer todayOrders;        // 今日订单
        private BigDecimal todayRevenue;    // 今日收入
        private Integer pendingOrders;      // 待处理订单
        private Integer totalProducts;      // 商品总数
        private Integer totalAppointments;  // 预约总数
        private Integer pendingAppointments;// 待处理预约

        public Integer getTotalParents() { return totalParents; }
        public void setTotalParents(Integer totalParents) { this.totalParents = totalParents; }
        public Integer getTotalNannies() { return totalNannies; }
        public void setTotalNannies(Integer totalNannies) { this.totalNannies = totalNannies; }
        public Integer getTotalInstitutions() { return totalInstitutions; }
        public void setTotalInstitutions(Integer totalInstitutions) { this.totalInstitutions = totalInstitutions; }
        public Integer getTotalBabies() { return totalBabies; }
        public void setTotalBabies(Integer totalBabies) { this.totalBabies = totalBabies; }
        public Integer getTotalOrders() { return totalOrders; }
        public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
        public Integer getTodayOrders() { return todayOrders; }
        public void setTodayOrders(Integer todayOrders) { this.todayOrders = todayOrders; }
        public BigDecimal getTodayRevenue() { return todayRevenue; }
        public void setTodayRevenue(BigDecimal todayRevenue) { this.todayRevenue = todayRevenue; }
        public Integer getPendingOrders() { return pendingOrders; }
        public void setPendingOrders(Integer pendingOrders) { this.pendingOrders = pendingOrders; }
        public Integer getTotalProducts() { return totalProducts; }
        public void setTotalProducts(Integer totalProducts) { this.totalProducts = totalProducts; }
        public Integer getTotalAppointments() { return totalAppointments; }
        public void setTotalAppointments(Integer totalAppointments) { this.totalAppointments = totalAppointments; }
        public Integer getPendingAppointments() { return pendingAppointments; }
        public void setPendingAppointments(Integer pendingAppointments) { this.pendingAppointments = pendingAppointments; }
    }

    /**
     * 用户统计
     */
    class UserStatsVO {
        private Integer totalParents;           // 家长总数
        private Integer activeParents;          // 活跃家长数
        private Integer newParentsToday;        // 今日新增家长
        private Integer newParentsWeek;         // 本周新增家长
        private Integer newParentsMonth;        // 本月新增家长
        private Integer totalNannies;           // 保姆总数
        private Integer activeNannies;          // 在职保姆数
        private Integer newNanniesMonth;        // 本月新增保姆
        private List<Map<String, Object>> parentTrend;  // 家长增长趋势
        private List<Map<String, Object>> nannyTrend;   // 保姆增长趋势

        public Integer getTotalParents() { return totalParents; }
        public void setTotalParents(Integer totalParents) { this.totalParents = totalParents; }
        public Integer getActiveParents() { return activeParents; }
        public void setActiveParents(Integer activeParents) { this.activeParents = activeParents; }
        public Integer getNewParentsToday() { return newParentsToday; }
        public void setNewParentsToday(Integer newParentsToday) { this.newParentsToday = newParentsToday; }
        public Integer getNewParentsWeek() { return newParentsWeek; }
        public void setNewParentsWeek(Integer newParentsWeek) { this.newParentsWeek = newParentsWeek; }
        public Integer getNewParentsMonth() { return newParentsMonth; }
        public void setNewParentsMonth(Integer newParentsMonth) { this.newParentsMonth = newParentsMonth; }
        public Integer getTotalNannies() { return totalNannies; }
        public void setTotalNannies(Integer totalNannies) { this.totalNannies = totalNannies; }
        public Integer getActiveNannies() { return activeNannies; }
        public void setActiveNannies(Integer activeNannies) { this.activeNannies = activeNannies; }
        public Integer getNewNanniesMonth() { return newNanniesMonth; }
        public void setNewNanniesMonth(Integer newNanniesMonth) { this.newNanniesMonth = newNanniesMonth; }
        public List<Map<String, Object>> getParentTrend() { return parentTrend; }
        public void setParentTrend(List<Map<String, Object>> parentTrend) { this.parentTrend = parentTrend; }
        public List<Map<String, Object>> getNannyTrend() { return nannyTrend; }
        public void setNannyTrend(List<Map<String, Object>> nannyTrend) { this.nannyTrend = nannyTrend; }
    }

    /**
     * 机构统计
     */
    class InstitutionStatsVO {
        private Integer totalInstitutions;      // 机构总数
        private Integer activeInstitutions;     // 正常机构数
        private Integer pendingInstitutions;    // 待审核机构数
        private Integer disabledInstitutions;   // 禁用机构数
        private Integer newInstitutionsMonth;   // 本月新增机构
        private List<Map<String, Object>> institutionTrend;     // 机构增长趋势
        private List<Map<String, Object>> topInstitutions;      // 机构排行榜

        public Integer getTotalInstitutions() { return totalInstitutions; }
        public void setTotalInstitutions(Integer totalInstitutions) { this.totalInstitutions = totalInstitutions; }
        public Integer getActiveInstitutions() { return activeInstitutions; }
        public void setActiveInstitutions(Integer activeInstitutions) { this.activeInstitutions = activeInstitutions; }
        public Integer getPendingInstitutions() { return pendingInstitutions; }
        public void setPendingInstitutions(Integer pendingInstitutions) { this.pendingInstitutions = pendingInstitutions; }
        public Integer getDisabledInstitutions() { return disabledInstitutions; }
        public void setDisabledInstitutions(Integer disabledInstitutions) { this.disabledInstitutions = disabledInstitutions; }
        public Integer getNewInstitutionsMonth() { return newInstitutionsMonth; }
        public void setNewInstitutionsMonth(Integer newInstitutionsMonth) { this.newInstitutionsMonth = newInstitutionsMonth; }
        public List<Map<String, Object>> getInstitutionTrend() { return institutionTrend; }
        public void setInstitutionTrend(List<Map<String, Object>> institutionTrend) { this.institutionTrend = institutionTrend; }
        public List<Map<String, Object>> getTopInstitutions() { return topInstitutions; }
        public void setTopInstitutions(List<Map<String, Object>> topInstitutions) { this.topInstitutions = topInstitutions; }
    }

    /**
     * 订单统计
     */
    class OrderStatsVO {
        private Integer totalOrders;            // 订单总数
        private Integer pendingPayOrders;       // 待付款订单
        private Integer pendingDeliveryOrders;  // 待发货订单
        private Integer pendingReceiveOrders;   // 待收货订单
        private Integer completedOrders;        // 已完成订单
        private Integer cancelledOrders;        // 已取消订单
        private Integer refundedOrders;         // 已退款订单
        private Integer todayOrders;            // 今日订单
        private Integer weekOrders;             // 本周订单
        private Integer monthOrders;            // 本月订单
        private List<Map<String, Object>> orderTrend;       // 订单趋势
        private List<Map<String, Object>> orderStatusDist;  // 订单状态分布

        public Integer getTotalOrders() { return totalOrders; }
        public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
        public Integer getPendingPayOrders() { return pendingPayOrders; }
        public void setPendingPayOrders(Integer pendingPayOrders) { this.pendingPayOrders = pendingPayOrders; }
        public Integer getPendingDeliveryOrders() { return pendingDeliveryOrders; }
        public void setPendingDeliveryOrders(Integer pendingDeliveryOrders) { this.pendingDeliveryOrders = pendingDeliveryOrders; }
        public Integer getPendingReceiveOrders() { return pendingReceiveOrders; }
        public void setPendingReceiveOrders(Integer pendingReceiveOrders) { this.pendingReceiveOrders = pendingReceiveOrders; }
        public Integer getCompletedOrders() { return completedOrders; }
        public void setCompletedOrders(Integer completedOrders) { this.completedOrders = completedOrders; }
        public Integer getCancelledOrders() { return cancelledOrders; }
        public void setCancelledOrders(Integer cancelledOrders) { this.cancelledOrders = cancelledOrders; }
        public Integer getRefundedOrders() { return refundedOrders; }
        public void setRefundedOrders(Integer refundedOrders) { this.refundedOrders = refundedOrders; }
        public Integer getTodayOrders() { return todayOrders; }
        public void setTodayOrders(Integer todayOrders) { this.todayOrders = todayOrders; }
        public Integer getWeekOrders() { return weekOrders; }
        public void setWeekOrders(Integer weekOrders) { this.weekOrders = weekOrders; }
        public Integer getMonthOrders() { return monthOrders; }
        public void setMonthOrders(Integer monthOrders) { this.monthOrders = monthOrders; }
        public List<Map<String, Object>> getOrderTrend() { return orderTrend; }
        public void setOrderTrend(List<Map<String, Object>> orderTrend) { this.orderTrend = orderTrend; }
        public List<Map<String, Object>> getOrderStatusDist() { return orderStatusDist; }
        public void setOrderStatusDist(List<Map<String, Object>> orderStatusDist) { this.orderStatusDist = orderStatusDist; }
    }

    /**
     * 收入统计
     */
    class RevenueStatsVO {
        private BigDecimal totalRevenue;        // 总收入
        private BigDecimal todayRevenue;        // 今日收入
        private BigDecimal weekRevenue;         // 本周收入
        private BigDecimal monthRevenue;        // 本月收入
        private BigDecimal avgOrderAmount;      // 平均订单金额
        private List<Map<String, Object>> revenueTrend;     // 收入趋势
        private List<Map<String, Object>> payTypeDist;      // 支付方式分布

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
        public List<Map<String, Object>> getRevenueTrend() { return revenueTrend; }
        public void setRevenueTrend(List<Map<String, Object>> revenueTrend) { this.revenueTrend = revenueTrend; }
        public List<Map<String, Object>> getPayTypeDist() { return payTypeDist; }
        public void setPayTypeDist(List<Map<String, Object>> payTypeDist) { this.payTypeDist = payTypeDist; }
    }

    /**
     * 报表数据
     */
    class ReportVO {
        private String reportType;              // 报表类型
        private String startDate;               // 开始日期
        private String endDate;                 // 结束日期
        private List<Map<String, Object>> data; // 报表数据

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
