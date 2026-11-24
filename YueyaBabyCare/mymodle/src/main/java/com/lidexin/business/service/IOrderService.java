package com.lidexin.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lidexin.business.dto.OrderDTO;
import com.lidexin.business.po.Order;
import com.lidexin.business.vo.OrderVO;

import java.util.Map;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-24
 */
public interface IOrderService extends IService<Order> {

    /**
     * 创建订单（家长）
     * @param parentId 家长ID
     * @param dto 订单信息
     * @return 订单ID
     */
    Long createOrder(Long parentId, OrderDTO dto);

    /**
     * 获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderVO getOrderById(Long orderId);

    /**
     * 获取订单详情（验证归属）
     * @param parentId 家长ID
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderVO getOrderByIdForParent(Long parentId, Long orderId);

    /**
     * 获取家长的订单列表
     * @param parentId 家长ID
     * @param status 订单状态（可选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 订单分页列表
     */
    IPage<OrderVO> getParentOrders(Long parentId, Byte status, Integer pageNum, Integer pageSize);

    /**
     * 获取机构的订单列表
     * @param institutionId 机构ID
     * @param status 订单状态（可选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 订单分页列表
     */
    IPage<OrderVO> getInstitutionOrders(Long institutionId, Byte status, Integer pageNum, Integer pageSize);

    /**
     * 支付订单（家长）
     * @param parentId 家长ID
     * @param orderId 订单ID
     * @param payType 支付方式（1-微信，2-支付宝，3-余额）
     * @return 是否成功
     */
    boolean payOrder(Long parentId, Long orderId, Byte payType);

    /**
     * 取消订单（家长）
     * @param parentId 家长ID
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean cancelOrder(Long parentId, Long orderId);

    /**
     * 添加物流信息（机构）
     * @param institutionId 机构ID
     * @param orderId 订单ID
     * @param deliveryCompany 物流公司
     * @param deliveryNo 物流单号
     * @return 是否成功
     */
    boolean addLogistics(Long institutionId, Long orderId, String deliveryCompany, String deliveryNo);

    /**
     * 确认收货（家长）
     * @param parentId 家长ID
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean confirmReceipt(Long parentId, Long orderId);

    /**
     * 修改订单状态（管理员）
     * @param orderId 订单ID
     * @param status 订单状态
     * @return 是否成功
     */
    boolean updateOrderStatus(Long orderId, Byte status);

    /**
     * 获取订单统计
     * @param institutionId 机构ID（可选，为空则统计全部）
     * @return 统计信息
     */
    OrderStatsVO getOrderStats(Long institutionId);

    /**
     * 获取家长的订单统计
     * @param parentId 家长ID
     * @return 各状态订单数量
     */
    Map<String, Integer> getParentOrderStats(Long parentId);

    /**
     * 订单统计VO
     */
    class OrderStatsVO {
        private Integer totalCount;          // 总订单数
        private Integer pendingPayCount;     // 待付款数
        private Integer pendingDeliveryCount;// 待发货数
        private Integer pendingReceiveCount; // 待收货数
        private Integer completedCount;      // 已完成数
        private Integer cancelledCount;      // 已取消数
        private java.math.BigDecimal totalAmount;  // 总金额

        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public Integer getPendingPayCount() {
            return pendingPayCount;
        }

        public void setPendingPayCount(Integer pendingPayCount) {
            this.pendingPayCount = pendingPayCount;
        }

        public Integer getPendingDeliveryCount() {
            return pendingDeliveryCount;
        }

        public void setPendingDeliveryCount(Integer pendingDeliveryCount) {
            this.pendingDeliveryCount = pendingDeliveryCount;
        }

        public Integer getPendingReceiveCount() {
            return pendingReceiveCount;
        }

        public void setPendingReceiveCount(Integer pendingReceiveCount) {
            this.pendingReceiveCount = pendingReceiveCount;
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

        public java.math.BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(java.math.BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }
    }
}
