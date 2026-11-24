package com.lidexin.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lidexin.business.dto.ProductDTO;
import com.lidexin.business.po.Product;
import com.lidexin.business.vo.ProductVO;

import java.util.List;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
public interface IProductService extends IService<Product> {

    /**
     * 新增商品（机构）
     *
     * @param institutionId 机构ID
     * @param dto           商品信息
     * @return 商品ID
     */
    Long createProduct(Long institutionId, ProductDTO dto);

    /**
     * 获取商品详情
     *
     * @param id 商品ID
     * @return 商品详情
     */
    ProductVO getProductById(Long id);

    /**
     * 修改商品（机构）
     *
     * @param id            商品ID
     * @param institutionId 机构ID
     * @param dto           商品信息
     * @return 是否成功
     */
    boolean updateProduct(Long id, Long institutionId, ProductDTO dto);

    /**
     * 删除商品（机构）
     *
     * @param id            商品ID
     * @param institutionId 机构ID
     * @return 是否成功
     */
    boolean deleteProduct(Long id, Long institutionId);

    /**
     * 获取商品列表（带分页、筛选）
     *
     * @param categoryId 分类ID（可选）
     * @param keyword    关键词（可选）
     * @param status     状态（可选）
     * @param pageNum    页码
     * @param pageSize   每页数量
     * @return 商品分页列表
     */
    IPage<ProductVO> getProductList(Long categoryId, String keyword, Byte status, Integer pageNum, Integer pageSize);

    /**
     * 搜索商品
     *
     * @param keyword  关键词
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 商品分页列表
     */
    IPage<ProductVO> searchProducts(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 获取机构的商品列表（机构）
     *
     * @param institutionId 机构ID
     * @param status        状态（可选）
     * @param pageNum       页码
     * @param pageSize      每页数量
     * @return 商品分页列表
     */
    IPage<ProductVO> getInstitutionProducts(Long institutionId, Byte status, Integer pageNum, Integer pageSize);

    /**
     * 上架/下架商品（机构）
     *
     * @param id            商品ID
     * @param institutionId 机构ID
     * @param status        状态（0-下架，1-上架）
     * @return 是否成功
     */
    boolean updateProductStatus(Long id, Long institutionId, Byte status);

    /**
     * 获取推荐商品
     *
     * @param limit 数量限制
     * @return 推荐商品列表
     */
    List<ProductVO> getRecommendProducts(Integer limit);

    /**
     * 上传商品图片
     *
     * @param id            商品ID
     * @param institutionId 机构ID
     * @param imageUrls     图片URL列表
     * @return 是否成功
     */
    boolean uploadProductImages(Long id, Long institutionId, List<String> imageUrls);

    // ==================== 管理员专用方法 ====================

    /**
     * 获取所有商品列表（管理员）- 支持按机构筛选
     *
     * @param institutionId 机构ID（可选）
     * @param categoryId    分类ID（可选）
     * @param keyword       关键词（可选）
     * @param status        状态（可选）
     * @param pageNum       页码
     * @param pageSize      每页数量
     * @return 商品分页列表
     */
    IPage<ProductVO> adminGetProductList(Long institutionId, Long categoryId, String keyword, Byte status, Integer pageNum, Integer pageSize);

    /**
     * 修改商品（管理员）- 无需验证机构归属
     *
     * @param id  商品ID
     * @param dto 商品信息
     * @return 是否成功
     */
    boolean adminUpdateProduct(Long id, ProductDTO dto);

    /**
     * 删除商品（管理员）- 无需验证机构归属
     *
     * @param id 商品ID
     * @return 是否成功
     */
    boolean adminDeleteProduct(Long id);

    /**
     * 上架/下架商品（管理员）- 无需验证机构归属
     *
     * @param id     商品ID
     * @param status 状态（0-下架，1-上架）
     * @return 是否成功
     */
    boolean adminUpdateProductStatus(Long id, Byte status);

    /**
     * 批量删除商品（管理员）
     *
     * @param ids 商品ID列表
     * @return 是否成功
     */
    boolean adminBatchDeleteProducts(List<Long> ids);

    /**
     * 批量上架/下架商品（管理员）
     *
     * @param ids    商品ID列表
     * @param status 状态（0-下架，1-上架）
     * @return 是否成功
     */
    boolean adminBatchUpdateStatus(List<Long> ids, Byte status);
}
