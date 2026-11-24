package com.lidexin.business.service;

import com.lidexin.business.dto.ProductCategoryDTO;
import com.lidexin.business.po.ProductCategory;
import com.lidexin.business.vo.ProductCategoryVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品分类表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
public interface IProductCategoryService extends IService<ProductCategory> {

    /**
     * 新增分类（管理员）
     *
     * @param dto 分类信息
     * @return 分类ID
     */
    Long createCategory(ProductCategoryDTO dto);

    /**
     * 修改分类（管理员）
     *
     * @param id  分类ID
     * @param dto 分类信息
     * @return 是否成功
     */
    boolean updateCategory(Long id, ProductCategoryDTO dto);

    /**
     * 删除分类（管理员）
     *
     * @param id 分类ID
     * @return 是否成功
     */
    boolean deleteCategory(Long id);

    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return 分类详情VO
     */
    ProductCategoryVO getCategoryById(Long id);

    /**
     * 获取分类树形结构
     *
     * @return 分类树
     */
    List<ProductCategoryVO> getCategoryTree();

    /**
     * 获取分类列表（平铺）
     *
     * @param status 状态筛选（可选）
     * @return 分类列表
     */
    List<ProductCategoryVO> getCategoryList(Byte status);

    /**
     * 修改分类状态
     *
     * @param id     分类ID
     * @param status 新状态
     * @return 是否成功
     */
    boolean updateCategoryStatus(Long id, Byte status);
}
