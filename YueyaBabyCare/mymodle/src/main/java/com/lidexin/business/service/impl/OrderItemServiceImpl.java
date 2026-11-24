package com.lidexin.business.service.impl;

import com.lidexin.business.po.OrderItem;
import com.lidexin.business.mapper.OrderItemMapper;
import com.lidexin.business.service.IOrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单明细表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService {

}
