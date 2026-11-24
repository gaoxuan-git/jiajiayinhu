package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lidexin.business.dto.InventoryDTO;
import com.lidexin.business.mapper.InventoryMapper;
import com.lidexin.business.mapper.ProductMapper;
import com.lidexin.business.po.Inventory;
import com.lidexin.business.po.Product;
import com.lidexin.business.service.IInventoryService;
import com.lidexin.business.vo.InventoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 库存表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements IInventoryService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addInventoryRecord(Long institutionId, Long operatorId, InventoryDTO dto) {
        // 查询商品
        Product product = productMapper.selectById(dto.getProductId());
        if (product == null || !product.getInstitutionId().equals(institutionId)) {
            throw new RuntimeException("商品不存在");
        }

        Integer currentStock = product.getStock() != null ? product.getStock() : 0;
        Integer newStock;

        // 根据操作类型计算新库存
        switch (dto.getOperateType()) {
            case 1: // 入库
                newStock = currentStock + dto.getQuantity();
                break;
            case 2: // 出库
                if (currentStock < dto.getQuantity()) {
                    throw new RuntimeException("库存不足，当前库存：" + currentStock);
                }
                newStock = currentStock - dto.getQuantity();
                break;
            case 3: // 盘点
                if (dto.getAfterStock() == null) {
                    throw new RuntimeException("盘点操作需要指定盘点后库存");
                }
                newStock = dto.getAfterStock();
                break;
            default:
                throw new RuntimeException("无效的操作类型");
        }

        // 创建库存记录
        Inventory inventory = new Inventory();
        inventory.setInstitutionId(institutionId);
        inventory.setProductId(dto.getProductId());
        inventory.setOperateType(dto.getOperateType());
        inventory.setQuantity(dto.getOperateType() == 3 ? Math.abs(newStock - currentStock) : dto.getQuantity());
        inventory.setBeforeStock(currentStock);
        inventory.setAfterStock(newStock);
        inventory.setRemark(dto.getRemark());
        inventory.setOperatorId(operatorId);
        inventory.setCreateTime(LocalDateTime.now());

        // 保存库存记录
        this.save(inventory);

        // 更新商品库存
        product.setStock(newStock);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);

        return true;
    }

    @Override
    public IPage<InventoryVO> getInventoryList(Long institutionId, Long productId, Byte operateType, Integer pageNum, Integer pageSize) {
        Page<Inventory> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getInstitutionId, institutionId);
        if (productId != null) {
            wrapper.eq(Inventory::getProductId, productId);
        }
        if (operateType != null) {
            wrapper.eq(Inventory::getOperateType, operateType);
        }
        wrapper.orderByDesc(Inventory::getCreateTime);

        IPage<Inventory> inventoryPage = this.page(page, wrapper);

        // 转换为VO
        Page<InventoryVO> voPage = new Page<>(inventoryPage.getCurrent(), inventoryPage.getSize(), inventoryPage.getTotal());
        List<InventoryVO> voList = inventoryPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public IPage<Product> getCurrentStock(Long institutionId, String keyword, Integer pageNum, Integer pageSize) {
        Page<Product> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getInstitutionId, institutionId);
        wrapper.eq(Product::getDeleted, 0);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getName, keyword);
        }
        wrapper.orderByDesc(Product::getUpdateTime);

        return productMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Product> getLowStockProducts(Long institutionId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getInstitutionId, institutionId);
        wrapper.eq(Product::getDeleted, 0);
        // 库存预警：库存 <= 预警值，或者库存 <= 10（默认预警值）
        wrapper.and(w -> w
                .apply("stock <= IFNULL(stock_alert, 10)")
        );
        wrapper.orderByAsc(Product::getStock);

        return productMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setStockAlert(Long institutionId, Long productId, Integer alertValue) {
        Product product = productMapper.selectById(productId);
        if (product == null || !product.getInstitutionId().equals(institutionId)) {
            throw new RuntimeException("商品不存在");
        }

        product.setStockAlert(alertValue);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);

        return true;
    }

    @Override
    public InventoryStatsVO getInventoryStats(Long institutionId) {
        InventoryStatsVO stats = new InventoryStatsVO();

        // 商品总数
        LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
        productWrapper.eq(Product::getInstitutionId, institutionId);
        productWrapper.eq(Product::getDeleted, 0);
        Long totalProducts = productMapper.selectCount(productWrapper);
        stats.setTotalProducts(totalProducts.intValue());

        // 库存总量
        LambdaQueryWrapper<Product> stockWrapper = new LambdaQueryWrapper<>();
        stockWrapper.eq(Product::getInstitutionId, institutionId);
        stockWrapper.eq(Product::getDeleted, 0);
        List<Product> products = productMapper.selectList(stockWrapper);
        int totalStock = products.stream()
                .mapToInt(p -> p.getStock() != null ? p.getStock() : 0)
                .sum();
        stats.setTotalStock(totalStock);

        // 库存不足商品数
        List<Product> lowStockProducts = getLowStockProducts(institutionId);
        stats.setLowStockCount(lowStockProducts.size());

        // 今日入库/出库数量
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        stats.setTodayInCount(getOperateCount(institutionId, (byte) 1, todayStart, todayEnd));
        stats.setTodayOutCount(getOperateCount(institutionId, (byte) 2, todayStart, todayEnd));

        // 本月入库/出库数量
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
        LocalDateTime monthEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        stats.setMonthInCount(getOperateCount(institutionId, (byte) 1, monthStart, monthEnd));
        stats.setMonthOutCount(getOperateCount(institutionId, (byte) 2, monthStart, monthEnd));

        return stats;
    }

    /**
     * 获取指定时间范围内的操作数量
     */
    private Integer getOperateCount(Long institutionId, Byte operateType, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getInstitutionId, institutionId);
        wrapper.eq(Inventory::getOperateType, operateType);
        wrapper.between(Inventory::getCreateTime, startTime, endTime);

        List<Inventory> list = this.list(wrapper);
        return list.stream()
                .mapToInt(i -> i.getQuantity() != null ? i.getQuantity() : 0)
                .sum();
    }

    /**
     * 转换为VO
     */
    private InventoryVO convertToVO(Inventory inventory) {
        InventoryVO vo = new InventoryVO();
        vo.setId(inventory.getId());
        vo.setInstitutionId(inventory.getInstitutionId());
        vo.setProductId(inventory.getProductId());
        vo.setOperateType(inventory.getOperateType());
        vo.setQuantity(inventory.getQuantity());
        vo.setBeforeStock(inventory.getBeforeStock());
        vo.setAfterStock(inventory.getAfterStock());
        vo.setRemark(inventory.getRemark());
        vo.setOperatorId(inventory.getOperatorId());
        vo.setCreateTime(inventory.getCreateTime());

        // 设置操作类型名称
        if (inventory.getOperateType() != null) {
            switch (inventory.getOperateType()) {
                case 1:
                    vo.setOperateTypeName("入库");
                    break;
                case 2:
                    vo.setOperateTypeName("出库");
                    break;
                case 3:
                    vo.setOperateTypeName("盘点");
                    break;
            }
        }

        // 查询商品信息
        Product product = productMapper.selectById(inventory.getProductId());
        if (product != null) {
            vo.setProductName(product.getName());
            vo.setProductCoverImg(product.getCoverImg());
        }

        return vo;
    }
}
