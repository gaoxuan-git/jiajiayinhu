package com.lidexin.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lidexin.business.dto.InventoryDTO;
import com.lidexin.business.po.Inventory;
import com.lidexin.business.po.Product;
import com.lidexin.business.vo.InventoryVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 库存表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
public interface IInventoryService extends IService<Inventory> {

    /**
     * 新增库存操作记录
     *
     * @param institutionId 机构ID
     * @param operatorId    操作人ID
     * @param dto           库存操作DTO
     * @return 是否成功
     */
    boolean addInventoryRecord(Long institutionId, Long operatorId, InventoryDTO dto);

    /**
     * 获取库存操作记录列表
     *
     * @param institutionId 机构ID
     * @param productId     商品ID（可选）
     * @param operateType   操作类型（可选）
     * @param pageNum       页码
     * @param pageSize      每页数量
     * @return 分页结果
     */
    IPage<InventoryVO> getInventoryList(Long institutionId, Long productId, Byte operateType, Integer pageNum, Integer pageSize);

    /**
     * 获取当前库存列表
     *
     * @param institutionId 机构ID
     * @param keyword       搜索关键词（可选）
     * @param pageNum       页码
     * @param pageSize      每页数量
     * @return 商品库存分页结果
     */
    IPage<Product> getCurrentStock(Long institutionId, String keyword, Integer pageNum, Integer pageSize);

    /**
     * 获取库存不足商品列表
     *
     * @param institutionId 机构ID
     * @return 库存不足商品列表
     */
    List<Product> getLowStockProducts(Long institutionId);

    /**
     * 设置商品库存预警值
     *
     * @param institutionId 机构ID
     * @param productId     商品ID
     * @param alertValue    预警值
     * @return 是否成功
     */
    boolean setStockAlert(Long institutionId, Long productId, Integer alertValue);

    /**
     * 获取库存统计数据
     *
     * @param institutionId 机构ID
     * @return 统计数据
     */
    InventoryStatsVO getInventoryStats(Long institutionId);

    /**
     * 库存统计VO
     */
    class InventoryStatsVO {
        private Integer totalProducts;      // 商品总数
        private Integer totalStock;         // 库存总量
        private Integer lowStockCount;      // 库存不足商品数
        private Integer todayInCount;       // 今日入库数量
        private Integer todayOutCount;      // 今日出库数量
        private Integer monthInCount;       // 本月入库数量
        private Integer monthOutCount;      // 本月出库数量

        public Integer getTotalProducts() {
            return totalProducts;
        }

        public void setTotalProducts(Integer totalProducts) {
            this.totalProducts = totalProducts;
        }

        public Integer getTotalStock() {
            return totalStock;
        }

        public void setTotalStock(Integer totalStock) {
            this.totalStock = totalStock;
        }

        public Integer getLowStockCount() {
            return lowStockCount;
        }

        public void setLowStockCount(Integer lowStockCount) {
            this.lowStockCount = lowStockCount;
        }

        public Integer getTodayInCount() {
            return todayInCount;
        }

        public void setTodayInCount(Integer todayInCount) {
            this.todayInCount = todayInCount;
        }

        public Integer getTodayOutCount() {
            return todayOutCount;
        }

        public void setTodayOutCount(Integer todayOutCount) {
            this.todayOutCount = todayOutCount;
        }

        public Integer getMonthInCount() {
            return monthInCount;
        }

        public void setMonthInCount(Integer monthInCount) {
            this.monthInCount = monthInCount;
        }

        public Integer getMonthOutCount() {
            return monthOutCount;
        }

        public void setMonthOutCount(Integer monthOutCount) {
            this.monthOutCount = monthOutCount;
        }
    }
}
