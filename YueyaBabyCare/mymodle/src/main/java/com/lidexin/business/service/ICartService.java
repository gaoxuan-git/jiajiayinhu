package com.lidexin.business.service;

import com.lidexin.business.dto.CartDTO;
import com.lidexin.business.po.Cart;
import com.lidexin.business.vo.CartVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 购物车表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
public interface ICartService extends IService<Cart> {

    /**
     * 添加商品到购物车
     * @param parentId 家长ID
     * @param dto 购物车请求参数
     * @return 购物车ID
     */
    Long addToCart(Long parentId, CartDTO dto);

    /**
     * 获取购物车列表
     * @param parentId 家长ID
     * @return 购物车列表
     */
    List<CartVO> getCartList(Long parentId);

    /**
     * 修改购物车商品数量
     * @param parentId 家长ID
     * @param cartId 购物车ID
     * @param quantity 数量
     * @return 是否成功
     */
    boolean updateQuantity(Long parentId, Long cartId, Integer quantity);

    /**
     * 删除购物车商品
     * @param parentId 家长ID
     * @param cartId 购物车ID
     * @return 是否成功
     */
    boolean deleteCartItem(Long parentId, Long cartId);

    /**
     * 批量删除购物车商品
     * @param parentId 家长ID
     * @param cartIds 购物车ID列表
     * @return 是否成功
     */
    boolean batchDeleteCartItems(Long parentId, List<Long> cartIds);

    /**
     * 清空购物车
     * @param parentId 家长ID
     * @return 是否成功
     */
    boolean clearCart(Long parentId);

    /**
     * 选中/取消选中商品
     * @param parentId 家长ID
     * @param cartId 购物车ID
     * @param selected 是否选中（0-否，1-是）
     * @return 是否成功
     */
    boolean updateSelected(Long parentId, Long cartId, Byte selected);

    /**
     * 全选/取消全选
     * @param parentId 家长ID
     * @param selected 是否选中（0-否，1-是）
     * @return 是否成功
     */
    boolean selectAll(Long parentId, Byte selected);

    /**
     * 获取购物车商品数量
     * @param parentId 家长ID
     * @return 商品数量
     */
    Integer getCartCount(Long parentId);

    /**
     * 获取购物车选中商品总额
     * @param parentId 家长ID
     * @return 总额信息
     */
    CartTotalVO getCartTotal(Long parentId);

    /**
     * 购物车总额VO
     */
    class CartTotalVO {
        private Integer totalCount;      // 选中商品数量
        private BigDecimal totalAmount;  // 选中商品总额
        private Integer invalidCount;    // 无效商品数量

        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public Integer getInvalidCount() {
            return invalidCount;
        }

        public void setInvalidCount(Integer invalidCount) {
            this.invalidCount = invalidCount;
        }
    }
}
