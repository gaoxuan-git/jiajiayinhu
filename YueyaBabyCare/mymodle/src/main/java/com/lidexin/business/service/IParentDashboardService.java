package com.lidexin.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 家长数据统计服务接口
 *
 * @author 张三
 * @since 2025-12-25
 */
public interface IParentDashboardService {

    /**
     * 获取婴幼儿数据统计
     */
    BabyStatsVO getBabyStats(Long parentId);

    /**
     * 获取护理数据统计
     */
    CareStatsVO getCareStats(Long parentId);

    /**
     * 获取订单历史（分页）
     */
    Page<OrderHistoryVO> getOrderHistory(Long parentId, Integer pageNum, Integer pageSize);

    // ==================== VO类定义 ====================

    /**
     * 婴幼儿数据统计
     */
    class BabyStatsVO {
        private Integer totalBabies;                    // 宝宝总数
        private List<BabyInfoVO> babies;                // 宝宝列表
        private Integer totalGrowthRecords;             // 成长记录总数
        private Integer totalHealthRecords;             // 健康档案总数
        private List<Map<String, Object>> growthTrend;  // 成长记录趋势（近7天）
        private List<Map<String, Object>> recordTypeDist; // 记录类型分布

        public Integer getTotalBabies() { return totalBabies; }
        public void setTotalBabies(Integer totalBabies) { this.totalBabies = totalBabies; }
        public List<BabyInfoVO> getBabies() { return babies; }
        public void setBabies(List<BabyInfoVO> babies) { this.babies = babies; }
        public Integer getTotalGrowthRecords() { return totalGrowthRecords; }
        public void setTotalGrowthRecords(Integer totalGrowthRecords) { this.totalGrowthRecords = totalGrowthRecords; }
        public Integer getTotalHealthRecords() { return totalHealthRecords; }
        public void setTotalHealthRecords(Integer totalHealthRecords) { this.totalHealthRecords = totalHealthRecords; }
        public List<Map<String, Object>> getGrowthTrend() { return growthTrend; }
        public void setGrowthTrend(List<Map<String, Object>> growthTrend) { this.growthTrend = growthTrend; }
        public List<Map<String, Object>> getRecordTypeDist() { return recordTypeDist; }
        public void setRecordTypeDist(List<Map<String, Object>> recordTypeDist) { this.recordTypeDist = recordTypeDist; }
    }

    /**
     * 宝宝信息
     */
    class BabyInfoVO {
        private Long id;
        private String name;
        private String nickname;
        private Byte gender;
        private LocalDate birthDate;
        private String avatar;
        private Integer ageMonths;          // 月龄
        private Integer growthRecordCount;  // 成长记录数
        private Integer healthRecordCount;  // 健康档案数

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public Byte getGender() { return gender; }
        public void setGender(Byte gender) { this.gender = gender; }
        public LocalDate getBirthDate() { return birthDate; }
        public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public Integer getAgeMonths() { return ageMonths; }
        public void setAgeMonths(Integer ageMonths) { this.ageMonths = ageMonths; }
        public Integer getGrowthRecordCount() { return growthRecordCount; }
        public void setGrowthRecordCount(Integer growthRecordCount) { this.growthRecordCount = growthRecordCount; }
        public Integer getHealthRecordCount() { return healthRecordCount; }
        public void setHealthRecordCount(Integer healthRecordCount) { this.healthRecordCount = healthRecordCount; }
    }

    /**
     * 护理数据统计
     */
    class CareStatsVO {
        private Integer totalCareRecords;               // 护理记录总数
        private Integer totalCarePlans;                 // 护理计划总数
        private Integer activeCarePlans;                // 进行中的护理计划
        private Integer completedCarePlans;             // 已完成的护理计划
        private Integer totalAppointments;              // 预约总数
        private Integer pendingAppointments;            // 待处理预约
        private Integer completedAppointments;          // 已完成预约
        private Double avgRating;                       // 平均评分
        private Integer totalEvaluations;               // 评价总数
        private List<Map<String, Object>> careTypeDist; // 护理类型分布
        private List<Map<String, Object>> careTrend;    // 护理记录趋势（近7天）
        private List<Map<String, Object>> appointmentStatusDist; // 预约状态分布

        public Integer getTotalCareRecords() { return totalCareRecords; }
        public void setTotalCareRecords(Integer totalCareRecords) { this.totalCareRecords = totalCareRecords; }
        public Integer getTotalCarePlans() { return totalCarePlans; }
        public void setTotalCarePlans(Integer totalCarePlans) { this.totalCarePlans = totalCarePlans; }
        public Integer getActiveCarePlans() { return activeCarePlans; }
        public void setActiveCarePlans(Integer activeCarePlans) { this.activeCarePlans = activeCarePlans; }
        public Integer getCompletedCarePlans() { return completedCarePlans; }
        public void setCompletedCarePlans(Integer completedCarePlans) { this.completedCarePlans = completedCarePlans; }
        public Integer getTotalAppointments() { return totalAppointments; }
        public void setTotalAppointments(Integer totalAppointments) { this.totalAppointments = totalAppointments; }
        public Integer getPendingAppointments() { return pendingAppointments; }
        public void setPendingAppointments(Integer pendingAppointments) { this.pendingAppointments = pendingAppointments; }
        public Integer getCompletedAppointments() { return completedAppointments; }
        public void setCompletedAppointments(Integer completedAppointments) { this.completedAppointments = completedAppointments; }
        public Double getAvgRating() { return avgRating; }
        public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }
        public Integer getTotalEvaluations() { return totalEvaluations; }
        public void setTotalEvaluations(Integer totalEvaluations) { this.totalEvaluations = totalEvaluations; }
        public List<Map<String, Object>> getCareTypeDist() { return careTypeDist; }
        public void setCareTypeDist(List<Map<String, Object>> careTypeDist) { this.careTypeDist = careTypeDist; }
        public List<Map<String, Object>> getCareTrend() { return careTrend; }
        public void setCareTrend(List<Map<String, Object>> careTrend) { this.careTrend = careTrend; }
        public List<Map<String, Object>> getAppointmentStatusDist() { return appointmentStatusDist; }
        public void setAppointmentStatusDist(List<Map<String, Object>> appointmentStatusDist) { this.appointmentStatusDist = appointmentStatusDist; }
    }

    /**
     * 订单历史
     */
    class OrderHistoryVO {
        private Long id;
        private String orderNo;
        private Long institutionId;
        private String institutionName;
        private BigDecimal totalAmount;
        private BigDecimal payAmount;
        private Byte status;
        private String statusName;
        private Byte payType;
        private String payTypeName;
        private String createTime;
        private String payTime;
        private List<OrderItemVO> items;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getOrderNo() { return orderNo; }
        public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
        public Long getInstitutionId() { return institutionId; }
        public void setInstitutionId(Long institutionId) { this.institutionId = institutionId; }
        public String getInstitutionName() { return institutionName; }
        public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public BigDecimal getPayAmount() { return payAmount; }
        public void setPayAmount(BigDecimal payAmount) { this.payAmount = payAmount; }
        public Byte getStatus() { return status; }
        public void setStatus(Byte status) { this.status = status; }
        public String getStatusName() { return statusName; }
        public void setStatusName(String statusName) { this.statusName = statusName; }
        public Byte getPayType() { return payType; }
        public void setPayType(Byte payType) { this.payType = payType; }
        public String getPayTypeName() { return payTypeName; }
        public void setPayTypeName(String payTypeName) { this.payTypeName = payTypeName; }
        public String getCreateTime() { return createTime; }
        public void setCreateTime(String createTime) { this.createTime = createTime; }
        public String getPayTime() { return payTime; }
        public void setPayTime(String payTime) { this.payTime = payTime; }
        public List<OrderItemVO> getItems() { return items; }
        public void setItems(List<OrderItemVO> items) { this.items = items; }
    }

    /**
     * 订单商品项
     */
    class OrderItemVO {
        private Long productId;
        private String productName;
        private String productImage;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subtotal;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getProductImage() { return productImage; }
        public void setProductImage(String productImage) { this.productImage = productImage; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    }
}
