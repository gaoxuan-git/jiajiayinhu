package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lidexin.business.dto.ProductCategoryDTO;
import com.lidexin.business.mapper.ProductCategoryMapper;
import com.lidexin.business.po.ProductCategory;
import com.lidexin.business.service.IProductCategoryService;
import com.lidexin.business.vo.ProductCategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品分类表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements IProductCategoryService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(ProductCategoryDTO dto) {
        // 校验分类名称是否重复
        LambdaQueryWrapper<ProductCategory> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(ProductCategory::getName, dto.getName())
                   .eq(ProductCategory::getDeleted, 0);
        if (this.count(nameWrapper) > 0) {
            throw new RuntimeException("分类名称已存在");
        }

        // 如果有父分类，校验父分类是否存在
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            ProductCategory parent = this.getById(dto.getParentId());
            if (parent == null || parent.getDeleted() == 1) {
                throw new RuntimeException("父分类不存在");
            }
        }

        // 创建分类
        ProductCategory category = new ProductCategory();
        category.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        category.setName(dto.getName());
        category.setIcon(dto.getIcon());
        category.setSort(dto.getSort() != null ? dto.getSort() : 0);
        category.setStatus(dto.getStatus() != null ? dto.getStatus() : (byte) 1);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setDeleted((byte) 0);

        this.save(category);
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCategory(Long id, ProductCategoryDTO dto) {
        ProductCategory category = this.getById(id);
        if (category == null || category.getDeleted() == 1) {
            return false;
        }

        // 校验分类名称是否重复（排除自己）
        LambdaQueryWrapper<ProductCategory> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(ProductCategory::getName, dto.getName())
                   .eq(ProductCategory::getDeleted, 0)
                   .ne(ProductCategory::getId, id);
        if (this.count(nameWrapper) > 0) {
            throw new RuntimeException("分类名称已存在");
        }

        // 如果有父分类，校验父分类是否存在，且不能是自己或自己的子分类
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            if (dto.getParentId().equals(id)) {
                throw new RuntimeException("父分类不能是自己");
            }
            ProductCategory parent = this.getById(dto.getParentId());
            if (parent == null || parent.getDeleted() == 1) {
                throw new RuntimeException("父分类不存在");
            }
            // 检查是否形成循环
            if (isChildCategory(id, dto.getParentId())) {
                throw new RuntimeException("不能将分类移动到其子分类下");
            }
        }

        // 更新分类
        if (dto.getParentId() != null) {
            category.setParentId(dto.getParentId());
        }
        category.setName(dto.getName());
        if (dto.getIcon() != null) {
            category.setIcon(dto.getIcon());
        }
        if (dto.getSort() != null) {
            category.setSort(dto.getSort());
        }
        if (dto.getStatus() != null) {
            category.setStatus(dto.getStatus());
        }
        category.setUpdateTime(LocalDateTime.now());

        return this.updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategory(Long id) {
        ProductCategory category = this.getById(id);
        if (category == null || category.getDeleted() == 1) {
            return false;
        }

        // 检查是否有子分类
        LambdaQueryWrapper<ProductCategory> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(ProductCategory::getParentId, id)
                    .eq(ProductCategory::getDeleted, 0);
        if (this.count(childWrapper) > 0) {
            throw new RuntimeException("该分类下存在子分类，无法删除");
        }

        // TODO: 检查是否有关联商品，如果有则不能删除

        // 逻辑删除
        category.setDeleted((byte) 1);
        category.setUpdateTime(LocalDateTime.now());
        return this.updateById(category);
    }

    @Override
    public ProductCategoryVO getCategoryById(Long id) {
        ProductCategory category = this.getById(id);
        if (category == null || category.getDeleted() == 1) {
            return null;
        }
        return convertToVO(category, 1);
    }

    @Override
    public List<ProductCategoryVO> getCategoryTree() {
        // 获取所有未删除的分类
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductCategory::getDeleted, 0)
               .orderByAsc(ProductCategory::getSort)
               .orderByAsc(ProductCategory::getId);
        List<ProductCategory> allCategories = this.list(wrapper);

        // 转换为VO
        List<ProductCategoryVO> allVOs = allCategories.stream()
                .map(c -> convertToVO(c, 1))
                .collect(Collectors.toList());

        // 构建树形结构
        return buildTree(allVOs);
    }

    @Override
    public List<ProductCategoryVO> getCategoryList(Byte status) {
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductCategory::getDeleted, 0);
        if (status != null) {
            wrapper.eq(ProductCategory::getStatus, status);
        }
        wrapper.orderByAsc(ProductCategory::getSort)
               .orderByAsc(ProductCategory::getId);

        List<ProductCategory> categories = this.list(wrapper);
        return categories.stream()
                .map(c -> {
                    int level = calculateLevel(c.getParentId(), categories);
                    return convertToVO(c, level);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCategoryStatus(Long id, Byte status) {
        ProductCategory category = this.getById(id);
        if (category == null || category.getDeleted() == 1) {
            return false;
        }

        category.setStatus(status);
        category.setUpdateTime(LocalDateTime.now());
        return this.updateById(category);
    }

    // ==================== 私有方法 ====================

    /**
     * 检查targetId是否是parentId的子分类
     */
    private boolean isChildCategory(Long parentId, Long targetId) {
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductCategory::getParentId, parentId)
               .eq(ProductCategory::getDeleted, 0);
        List<ProductCategory> children = this.list(wrapper);

        for (ProductCategory child : children) {
            if (child.getId().equals(targetId)) {
                return true;
            }
            if (isChildCategory(child.getId(), targetId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算分类层级
     */
    private int calculateLevel(Long parentId, List<ProductCategory> allCategories) {
        if (parentId == null || parentId == 0) {
            return 1;
        }
        for (ProductCategory c : allCategories) {
            if (c.getId().equals(parentId)) {
                return calculateLevel(c.getParentId(), allCategories) + 1;
            }
        }
        return 1;
    }

    /**
     * 构建树形结构
     */
    private List<ProductCategoryVO> buildTree(List<ProductCategoryVO> allVOs) {
        // 按parentId分组
        Map<Long, List<ProductCategoryVO>> parentMap = allVOs.stream()
                .collect(Collectors.groupingBy(vo -> vo.getParentId() != null ? vo.getParentId() : 0L));

        // 设置子节点并计算层级
        for (ProductCategoryVO vo : allVOs) {
            List<ProductCategoryVO> children = parentMap.get(vo.getId());
            if (children != null && !children.isEmpty()) {
                vo.setChildren(children);
                // 设置子节点层级
                setChildrenLevel(children, vo.getLevel() + 1);
            }
        }

        // 返回顶级节点
        List<ProductCategoryVO> roots = parentMap.get(0L);
        return roots != null ? roots : new ArrayList<>();
    }

    /**
     * 递归设置子节点层级
     */
    private void setChildrenLevel(List<ProductCategoryVO> children, int level) {
        for (ProductCategoryVO child : children) {
            child.setLevel(level);
            if (child.getChildren() != null && !child.getChildren().isEmpty()) {
                setChildrenLevel(child.getChildren(), level + 1);
            }
        }
    }

    /**
     * 将PO转换为VO
     */
    private ProductCategoryVO convertToVO(ProductCategory category, int level) {
        ProductCategoryVO vo = new ProductCategoryVO();
        BeanUtils.copyProperties(category, vo);
        vo.setLevel(level);
        vo.setStatusName(category.getStatus() == 1 ? "正常" : "禁用");
        return vo;
    }
}
