package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lidexin.business.dto.OrderDTO;
import com.lidexin.business.mapper.OrderMapper;
import com.lidexin.business.po.*;
import com.lidexin.business.service.*;
import com.lidexin.business.vo.CartVO;
import com.lidexin.business.vo.OrderItemVO;
import com.lidexin.business.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-24
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ICartService cartService;

    @Autowired
    private IOrderItemService orderItemService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IParentService parentService;

    @Autowired
    private IInstitutionService institutionService;

    @Override
    @Transactional
    public Long createOrder(Long parentId, OrderDTO dto) {
        // 1. 获取购物车中选中的商品
        List<Long> cartIds = dto.getCartIds();
        if (cartIds == null || cartIds.isEmpty()) {
            throw new RuntimeException("请选择要结算的商品");
        }

        List<CartVO> cartItems = cartService.getCartList(parentId);
        List<CartVO> selectedItems = cartItems.stream()
                .filter(item -> cartIds.contains(item.getId()) && item.getValid() && item.getSelected() == 1)
                .collect(Collectors.toList());

        if (selectedItems.isEmpty()) {
            throw new RuntimeException("没有可结算的商品");
        }

        // 2. 按机构分组（如果商品来自不同机构，需要创建多个订单）
        Map<Long, List<CartVO>> groupByInstitution = new HashMap<>();
        for (CartVO item : selectedItems) {
            Product product = productService.getById(item.getProductId());
            if (product != null) {
                Long institutionId = product.getInstitutionId();
                groupByInstitution.computeIfAbsent(institutionId, k -> new ArrayList<>()).add(item);
            }
        }

        // 3. 为每个机构创建订单
        Long firstOrderId = null;
        for (Map.Entry<Long, List<CartVO>> entry : groupByInstitution.entrySet()) {
            Long institutionId = entry.getKey();
            List<CartVO> items = entry.getValue();

            // 计算订单总金额
            BigDecimal totalAmount = BigDecimal.ZERO;
            int totalQuantity = 0;
            for (CartVO item : items) {
                totalAmount = totalAmount.add(item.getSubtotal());
                totalQuantity += item.getQuantity();
            }

            // 创建订单
            Order order = new Order();
            order.setOrderNo(generateOrderNo());
            order.setParentId(parentId);
            order.setInstitutionId(institutionId);
            order.setTotalAmount(totalAmount);
            order.setPayAmount(totalAmount); // 暂无优惠
            order.setFreightAmount(BigDecimal.ZERO); // 免运费
            order.setDiscountAmount(BigDecimal.ZERO);
            order.setStatus((byte) 0); // 待付款
            order.setReceiverName(dto.getReceiverName());
            order.setReceiverPhone(dto.getReceiverPhone());
            order.setReceiverAddress(dto.getReceiverAddress());
            order.setRemark(dto.getRemark());
            order.setCreateTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            order.setDeleted((byte) 0);
            this.save(order);

            if (firstOrderId == null) {
                firstOrderId = order.getId();
            }

            // 创建订单项
            for (CartVO cartItem : items) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                orderItem.setProductId(cartItem.getProductId());
                orderItem.setProductName(cartItem.getProductName());
                orderItem.setProductImg(cartItem.getProductCoverImg());
                orderItem.setPrice(cartItem.getProductPrice());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setTotalAmount(cartItem.getSubtotal());
                orderItem.setCreateTime(LocalDateTime.now());
                orderItem.setUpdateTime(LocalDateTime.now());
                orderItemService.save(orderItem);

                // 扣减库存
                Product product = productService.getById(cartItem.getProductId());
                if (product != null) {
                    int newStock = product.getStock() - cartItem.getQuantity();
                    if (newStock < 0) {
                        throw new RuntimeException("商品 " + product.getName() + " 库存不足");
                    }
                    product.setStock(newStock);
                    product.setSales(product.getSales() + cartItem.getQuantity());
                    productService.updateById(product);
                }
            }
        }

        // 4. 删除已结算的购物车商品
        cartService.batchDeleteCartItems(parentId, cartIds);

        return firstOrderId;
    }

    @Override
    public OrderVO getOrderById(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null || order.getDeleted() == 1) {
            return null;
        }
        return convertToVO(order);
    }

    @Override
    public OrderVO getOrderByIdForParent(Long parentId, Long orderId) {
        Order order = this.getById(orderId);
        if (order == null || order.getDeleted() == 1 || !order.getParentId().equals(parentId)) {
            return null;
        }
        return convertToVO(order);
    }

    @Override
    public IPage<OrderVO> getParentOrders(Long parentId, Byte status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getParentId, parentId)
                .eq(Order::getDeleted, 0);
        if (status != null) {
            queryWrapper.eq(Order::getStatus, status);
        }
        queryWrapper.orderByDesc(Order::getCreateTime);

        Page<Order> page = new Page<>(pageNum, pageSize);
        IPage<Order> orderPage = this.page(page, queryWrapper);

        return orderPage.convert(this::convertToVO);
    }

    @Override
    public IPage<OrderVO> getInstitutionOrders(Long institutionId, Byte status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getInstitutionId, institutionId)
                .eq(Order::getDeleted, 0);
        if (status != null) {
            queryWrapper.eq(Order::getStatus, status);
        }
        queryWrapper.orderByDesc(Order::getCreateTime);

        Page<Order> page = new Page<>(pageNum, pageSize);
        IPage<Order> orderPage = this.page(page, queryWrapper);

        return orderPage.convert(this::convertToVO);
    }

    @Override
    @Transactional
    public boolean payOrder(Long parentId, Long orderId, Byte payType) {
        Order order = this.getById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getParentId().equals(parentId)) {
            throw new RuntimeException("无权操作此订单");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不正确，无法支付");
        }

        // 模拟支付成功
        order.setStatus((byte) 1); // 待发货
        order.setPayType(payType);
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        return this.updateById(order);
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long parentId, Long orderId) {
        Order order = this.getById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getParentId().equals(parentId)) {
            throw new RuntimeException("无权操作此订单");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("只有待付款订单可以取消");
        }

        // 恢复库存
        restoreStock(orderId);

        order.setStatus((byte) 4); // 已取消
        order.setUpdateTime(LocalDateTime.now());
        return this.updateById(order);
    }

    @Override
    @Transactional
    public boolean addLogistics(Long institutionId, Long orderId, String deliveryCompany, String deliveryNo) {
        Order order = this.getById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getInstitutionId().equals(institutionId)) {
            throw new RuntimeException("无权操作此订单");
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("只有待发货订单可以添加物流信息");
        }

        order.setDeliveryCompany(deliveryCompany);
        order.setDeliveryNo(deliveryNo);
        order.setDeliveryTime(LocalDateTime.now());
        order.setStatus((byte) 2); // 待收货
        order.setUpdateTime(LocalDateTime.now());
        return this.updateById(order);
    }

    @Override
    @Transactional
    public boolean confirmReceipt(Long parentId, Long orderId) {
        Order order = this.getById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getParentId().equals(parentId)) {
            throw new RuntimeException("无权操作此订单");
        }
        if (order.getStatus() != 2) {
            throw new RuntimeException("只有待收货订单可以确认收货");
        }

        order.setStatus((byte) 3); // 已完成
        order.setReceiveTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        return this.updateById(order);
    }

    @Override
    @Transactional
    public boolean updateOrderStatus(Long orderId, Byte status) {
        LambdaUpdateWrapper<Order> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Order::getId, orderId)
                .set(Order::getStatus, status)
                .set(Order::getUpdateTime, LocalDateTime.now());
        return this.update(updateWrapper);
    }

    @Override
    public OrderStatsVO getOrderStats(Long institutionId) {
        OrderStatsVO stats = new OrderStatsVO();

        LambdaQueryWrapper<Order> baseQuery = new LambdaQueryWrapper<>();
        baseQuery.eq(Order::getDeleted, 0);
        if (institutionId != null) {
            baseQuery.eq(Order::getInstitutionId, institutionId);
        }

        // 总订单数
        stats.setTotalCount((int) this.count(baseQuery));

        // 各状态订单数
        stats.setPendingPayCount(countByStatus(institutionId, (byte) 0));
        stats.setPendingDeliveryCount(countByStatus(institutionId, (byte) 1));
        stats.setPendingReceiveCount(countByStatus(institutionId, (byte) 2));
        stats.setCompletedCount(countByStatus(institutionId, (byte) 3));
        stats.setCancelledCount(countByStatus(institutionId, (byte) 4));

        // 总金额（已完成订单）
        LambdaQueryWrapper<Order> amountQuery = new LambdaQueryWrapper<>();
        amountQuery.eq(Order::getDeleted, 0).eq(Order::getStatus, 3);
        if (institutionId != null) {
            amountQuery.eq(Order::getInstitutionId, institutionId);
        }
        List<Order> completedOrders = this.list(amountQuery);
        BigDecimal totalAmount = completedOrders.stream()
                .map(Order::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalAmount(totalAmount);

        return stats;
    }

    @Override
    public Map<String, Integer> getParentOrderStats(Long parentId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("pendingPay", countByStatusForParent(parentId, (byte) 0));
        stats.put("pendingDelivery", countByStatusForParent(parentId, (byte) 1));
        stats.put("pendingReceive", countByStatusForParent(parentId, (byte) 2));
        stats.put("completed", countByStatusForParent(parentId, (byte) 3));
        return stats;
    }

    // ==================== 私有方法 ====================

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return "ORD" + timestamp + random;
    }

    private void restoreStock(Long orderId) {
        LambdaQueryWrapper<OrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> items = orderItemService.list(queryWrapper);

        for (OrderItem item : items) {
            Product product = productService.getById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                product.setSales(Math.max(0, product.getSales() - item.getQuantity()));
                productService.updateById(product);
            }
        }
    }

    private int countByStatus(Long institutionId, Byte status) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getDeleted, 0).eq(Order::getStatus, status);
        if (institutionId != null) {
            queryWrapper.eq(Order::getInstitutionId, institutionId);
        }
        return (int) this.count(queryWrapper);
    }

    private int countByStatusForParent(Long parentId, Byte status) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getParentId, parentId)
                .eq(Order::getDeleted, 0)
                .eq(Order::getStatus, status);
        return (int) this.count(queryWrapper);
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setParentId(order.getParentId());
        vo.setInstitutionId(order.getInstitutionId());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setFreightAmount(order.getFreightAmount());
        vo.setDiscountAmount(order.getDiscountAmount());
        vo.setPayType(order.getPayType());
        vo.setPayTypeName(OrderVO.getPayTypeName(order.getPayType()));
        vo.setPayTime(order.getPayTime());
        vo.setStatus(order.getStatus());
        vo.setStatusName(OrderVO.getStatusName(order.getStatus()));
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setDeliveryCompany(order.getDeliveryCompany());
        vo.setDeliveryNo(order.getDeliveryNo());
        vo.setDeliveryTime(order.getDeliveryTime());
        vo.setReceiveTime(order.getReceiveTime());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        vo.setUpdateTime(order.getUpdateTime());

        // 获取家长信息
        Parent parent = parentService.getById(order.getParentId());
        if (parent != null) {
            vo.setParentName(parent.getRealName());
        }

        // 获取机构信息
        Institution institution = institutionService.getById(order.getInstitutionId());
        if (institution != null) {
            vo.setInstitutionName(institution.getName());
        }

        // 获取订单项
        LambdaQueryWrapper<OrderItem> itemQuery = new LambdaQueryWrapper<>();
        itemQuery.eq(OrderItem::getOrderId, order.getId());
        List<OrderItem> items = orderItemService.list(itemQuery);

        List<OrderItemVO> itemVOs = new ArrayList<>();
        int totalQuantity = 0;
        for (OrderItem item : items) {
            OrderItemVO itemVO = new OrderItemVO();
            itemVO.setId(item.getId());
            itemVO.setOrderId(item.getOrderId());
            itemVO.setProductId(item.getProductId());
            itemVO.setProductName(item.getProductName());
            itemVO.setProductImg(item.getProductImg());
            itemVO.setPrice(item.getPrice());
            itemVO.setQuantity(item.getQuantity());
            itemVO.setTotalAmount(item.getTotalAmount());
            itemVO.setCreateTime(item.getCreateTime());
            itemVOs.add(itemVO);
            totalQuantity += item.getQuantity();
        }
        vo.setItems(itemVOs);
        vo.setTotalQuantity(totalQuantity);

        return vo;
    }
}
