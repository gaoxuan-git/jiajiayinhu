package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lidexin.business.dto.CartDTO;
import com.lidexin.business.mapper.CartMapper;
import com.lidexin.business.po.Cart;
import com.lidexin.business.po.Product;
import com.lidexin.business.service.ICartService;
import com.lidexin.business.service.IProductService;
import com.lidexin.business.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 购物车表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {

    @Autowired
    private IProductService productService;

    @Override
    @Transactional
    public Long addToCart(Long parentId, CartDTO dto) {
        // 1. 检查商品是否存在
        Product product = productService.getById(dto.getProductId());
        if (product == null || product.getDeleted() == 1) {
            throw new RuntimeException("商品不存在");
        }

        // 2. 检查商品是否上架
        if (product.getStatus() != 1) {
            throw new RuntimeException("商品已下架");
        }

        // 3. 检查库存
        if (product.getStock() < dto.getQuantity()) {
            throw new RuntimeException("商品库存不足");
        }

        // 4. 检查购物车中是否已存在该商品
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Cart::getParentId, parentId)
                .eq(Cart::getProductId, dto.getProductId());
        Cart existingCart = this.getOne(queryWrapper);

        if (existingCart != null) {
            // 已存在，合并数量
            int newQuantity = existingCart.getQuantity() + dto.getQuantity();
            if (newQuantity > product.getStock()) {
                throw new RuntimeException("商品库存不足，当前库存：" + product.getStock());
            }
            existingCart.setQuantity(newQuantity);
            existingCart.setUpdateTime(LocalDateTime.now());
            this.updateById(existingCart);
            return existingCart.getId();
        } else {
            // 不存在，新增
            Cart cart = new Cart();
            cart.setParentId(parentId);
            cart.setProductId(dto.getProductId());
            cart.setQuantity(dto.getQuantity());
            cart.setSelected((byte) 1); // 默认选中
            cart.setCreateTime(LocalDateTime.now());
            cart.setUpdateTime(LocalDateTime.now());
            this.save(cart);
            return cart.getId();
        }
    }

    @Override
    public List<CartVO> getCartList(Long parentId) {
        // 1. 查询购物车列表
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Cart::getParentId, parentId)
                .orderByDesc(Cart::getCreateTime);
        List<Cart> cartList = this.list(queryWrapper);

        // 2. 转换为VO并关联商品信息
        List<CartVO> voList = new ArrayList<>();
        for (Cart cart : cartList) {
            CartVO vo = convertToVO(cart);
            voList.add(vo);
        }
        return voList;
    }

    @Override
    @Transactional
    public boolean updateQuantity(Long parentId, Long cartId, Integer quantity) {
        // 1. 检查购物车项是否存在且属于当前用户
        Cart cart = this.getById(cartId);
        if (cart == null || !cart.getParentId().equals(parentId)) {
            throw new RuntimeException("购物车商品不存在");
        }

        // 2. 检查商品库存
        Product product = productService.getById(cart.getProductId());
        if (product == null || product.getDeleted() == 1) {
            throw new RuntimeException("商品不存在");
        }
        if (quantity > product.getStock()) {
            throw new RuntimeException("商品库存不足，当前库存：" + product.getStock());
        }

        // 3. 更新数量
        cart.setQuantity(quantity);
        cart.setUpdateTime(LocalDateTime.now());
        return this.updateById(cart);
    }

    @Override
    @Transactional
    public boolean deleteCartItem(Long parentId, Long cartId) {
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Cart::getId, cartId)
                .eq(Cart::getParentId, parentId);
        return this.remove(queryWrapper);
    }

    @Override
    @Transactional
    public boolean batchDeleteCartItems(Long parentId, List<Long> cartIds) {
        if (cartIds == null || cartIds.isEmpty()) {
            return true;
        }
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Cart::getId, cartIds)
                .eq(Cart::getParentId, parentId);
        return this.remove(queryWrapper);
    }

    @Override
    @Transactional
    public boolean clearCart(Long parentId) {
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Cart::getParentId, parentId);
        return this.remove(queryWrapper);
    }

    @Override
    @Transactional
    public boolean updateSelected(Long parentId, Long cartId, Byte selected) {
        LambdaUpdateWrapper<Cart> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Cart::getId, cartId)
                .eq(Cart::getParentId, parentId)
                .set(Cart::getSelected, selected)
                .set(Cart::getUpdateTime, LocalDateTime.now());
        return this.update(updateWrapper);
    }

    @Override
    @Transactional
    public boolean selectAll(Long parentId, Byte selected) {
        LambdaUpdateWrapper<Cart> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Cart::getParentId, parentId)
                .set(Cart::getSelected, selected)
                .set(Cart::getUpdateTime, LocalDateTime.now());
        return this.update(updateWrapper);
    }

    @Override
    public Integer getCartCount(Long parentId) {
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Cart::getParentId, parentId);
        return (int) this.count(queryWrapper);
    }

    @Override
    public CartTotalVO getCartTotal(Long parentId) {
        CartTotalVO totalVO = new CartTotalVO();
        totalVO.setTotalCount(0);
        totalVO.setTotalAmount(BigDecimal.ZERO);
        totalVO.setInvalidCount(0);

        // 获取购物车列表
        List<CartVO> cartList = getCartList(parentId);

        int totalCount = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        int invalidCount = 0;

        for (CartVO cart : cartList) {
            if (!cart.getValid()) {
                invalidCount++;
                continue;
            }
            if (cart.getSelected() == 1) {
                totalCount += cart.getQuantity();
                totalAmount = totalAmount.add(cart.getSubtotal());
            }
        }

        totalVO.setTotalCount(totalCount);
        totalVO.setTotalAmount(totalAmount);
        totalVO.setInvalidCount(invalidCount);
        return totalVO;
    }

    /**
     * 将Cart转换为CartVO，并关联商品信息
     */
    private CartVO convertToVO(Cart cart) {
        CartVO vo = new CartVO();
        vo.setId(cart.getId());
        vo.setParentId(cart.getParentId());
        vo.setProductId(cart.getProductId());
        vo.setQuantity(cart.getQuantity());
        vo.setSelected(cart.getSelected());
        vo.setCreateTime(cart.getCreateTime());
        vo.setUpdateTime(cart.getUpdateTime());

        // 关联商品信息
        Product product = productService.getById(cart.getProductId());
        if (product != null && product.getDeleted() != 1) {
            vo.setProductName(product.getName());
            vo.setProductCoverImg(product.getCoverImg());
            vo.setProductPrice(product.getPrice());
            vo.setProductOriginalPrice(product.getOriginalPrice());
            vo.setProductStock(product.getStock());
            vo.setProductStatus(product.getStatus());
            vo.setProductUnit(product.getUnit());

            // 计算小计
            vo.setSubtotal(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));

            // 判断有效性
            if (product.getStatus() != 1) {
                vo.setValid(false);
                vo.setInvalidReason("商品已下架");
            } else if (product.getStock() < cart.getQuantity()) {
                vo.setValid(false);
                vo.setInvalidReason("库存不足");
            } else {
                vo.setValid(true);
            }
        } else {
            // 商品不存在或已删除
            vo.setValid(false);
            vo.setInvalidReason("商品已失效");
            vo.setSubtotal(BigDecimal.ZERO);
        }

        return vo;
    }
}
