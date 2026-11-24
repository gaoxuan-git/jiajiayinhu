package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lidexin.business.dto.ProductDTO;
import com.lidexin.business.mapper.ProductMapper;
import com.lidexin.business.po.Institution;
import com.lidexin.business.po.Product;
import com.lidexin.business.po.ProductCategory;
import com.lidexin.business.service.IInstitutionService;
import com.lidexin.business.service.IProductCategoryService;
import com.lidexin.business.service.IProductService;
import com.lidexin.business.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    @Autowired
    @Lazy
    private IInstitutionService institutionService;

    @Autowired
    private IProductCategoryService productCategoryService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public Long createProduct(Long institutionId, ProductDTO dto) {
        // 验证分类是否存在
        ProductCategory category = productCategoryService.getById(dto.getCategoryId());
        if (category == null || category.getDeleted() == 1) {
            throw new RuntimeException("商品分类不存在");
        }

        Product product = new Product();
        product.setInstitutionId(institutionId);
        product.setCategoryId(dto.getCategoryId());
        product.setName(dto.getName());
        product.setCoverImg(dto.getCoverImg());
        product.setImages(convertListToJson(dto.getImages()));
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setOriginalPrice(dto.getOriginalPrice());
        product.setStock(dto.getStock());
        product.setSales(0);
        product.setUnit(dto.getUnit());
        product.setSort(dto.getSort() != null ? dto.getSort() : 0);
        product.setStatus(dto.getStatus() != null ? dto.getStatus() : (byte) 1);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        product.setDeleted((byte) 0);

        save(product);
        return product.getId();
    }

    @Override
    public ProductVO getProductById(Long id) {
        Product product = getById(id);
        if (product == null || product.getDeleted() == 1) {
            return null;
        }
        return convertToVO(product);
    }

    @Override
    @Transactional
    public boolean updateProduct(Long id, Long institutionId, ProductDTO dto) {
        Product product = getById(id);
        if (product == null || product.getDeleted() == 1) {
            return false;
        }

        // 验证是否属于该机构
        if (!product.getInstitutionId().equals(institutionId)) {
            throw new RuntimeException("无权修改此商品");
        }

        // 验证分类是否存在
        if (dto.getCategoryId() != null) {
            ProductCategory category = productCategoryService.getById(dto.getCategoryId());
            if (category == null || category.getDeleted() == 1) {
                throw new RuntimeException("商品分类不存在");
            }
            product.setCategoryId(dto.getCategoryId());
        }

        if (dto.getName() != null) {
            product.setName(dto.getName());
        }
        if (dto.getCoverImg() != null) {
            product.setCoverImg(dto.getCoverImg());
        }
        if (dto.getImages() != null) {
            product.setImages(convertListToJson(dto.getImages()));
        }
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }
        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }
        if (dto.getOriginalPrice() != null) {
            product.setOriginalPrice(dto.getOriginalPrice());
        }
        if (dto.getStock() != null) {
            product.setStock(dto.getStock());
        }
        if (dto.getUnit() != null) {
            product.setUnit(dto.getUnit());
        }
        if (dto.getSort() != null) {
            product.setSort(dto.getSort());
        }
        if (dto.getStatus() != null) {
            product.setStatus(dto.getStatus());
        }

        product.setUpdateTime(LocalDateTime.now());
        return updateById(product);
    }

    @Override
    @Transactional
    public boolean deleteProduct(Long id, Long institutionId) {
        Product product = getById(id);
        if (product == null || product.getDeleted() == 1) {
            return false;
        }

        // 验证是否属于该机构
        if (!product.getInstitutionId().equals(institutionId)) {
            throw new RuntimeException("无权删除此商品");
        }

        product.setDeleted((byte) 1);
        product.setUpdateTime(LocalDateTime.now());
        return updateById(product);
    }

    @Override
    public IPage<ProductVO> getProductList(Long categoryId, String keyword, Byte status, Integer pageNum, Integer pageSize) {
        Page<Product> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getDeleted, 0);

        // 分类筛选（包含子分类）
        if (categoryId != null) {
            List<Long> categoryIds = getAllChildCategoryIds(categoryId);
            categoryIds.add(categoryId);
            wrapper.in(Product::getCategoryId, categoryIds);
        }

        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Product::getName, keyword)
                    .or().like(Product::getDescription, keyword));
        }

        // 状态筛选
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }

        wrapper.orderByAsc(Product::getSort).orderByDesc(Product::getCreateTime);

        IPage<Product> productPage = page(page, wrapper);
        return convertToVOPage(productPage);
    }

    @Override
    public IPage<ProductVO> searchProducts(String keyword, Integer pageNum, Integer pageSize) {
        Page<Product> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getDeleted, 0)
                .eq(Product::getStatus, 1); // 只搜索上架商品

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Product::getName, keyword)
                    .or().like(Product::getDescription, keyword));
        }

        wrapper.orderByDesc(Product::getSales).orderByDesc(Product::getCreateTime);

        IPage<Product> productPage = page(page, wrapper);
        return convertToVOPage(productPage);
    }

    @Override
    public IPage<ProductVO> getInstitutionProducts(Long institutionId, Byte status, Integer pageNum, Integer pageSize) {
        Page<Product> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getDeleted, 0)
                .eq(Product::getInstitutionId, institutionId);

        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }

        wrapper.orderByAsc(Product::getSort).orderByDesc(Product::getCreateTime);

        IPage<Product> productPage = page(page, wrapper);
        return convertToVOPage(productPage);
    }

    @Override
    @Transactional
    public boolean updateProductStatus(Long id, Long institutionId, Byte status) {
        Product product = getById(id);
        if (product == null || product.getDeleted() == 1) {
            return false;
        }

        // 验证是否属于该机构
        if (!product.getInstitutionId().equals(institutionId)) {
            throw new RuntimeException("无权修改此商品状态");
        }

        product.setStatus(status);
        product.setUpdateTime(LocalDateTime.now());
        return updateById(product);
    }

    @Override
    public List<ProductVO> getRecommendProducts(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getDeleted, 0)
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getSales)
                .last("LIMIT " + limit);

        List<Product> products = list(wrapper);
        return products.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean uploadProductImages(Long id, Long institutionId, List<String> imageUrls) {
        Product product = getById(id);
        if (product == null || product.getDeleted() == 1) {
            return false;
        }

        // 验证是否属于该机构
        if (!product.getInstitutionId().equals(institutionId)) {
            throw new RuntimeException("无权修改此商品");
        }

        // 获取现有图片列表
        List<String> existingImages = convertJsonToList(product.getImages());
        if (existingImages == null) {
            existingImages = new ArrayList<>();
        }

        // 添加新图片
        existingImages.addAll(imageUrls);

        product.setImages(convertListToJson(existingImages));
        product.setUpdateTime(LocalDateTime.now());
        return updateById(product);
    }

    // ==================== 管理员专用方法 ====================

    @Override
    public IPage<ProductVO> adminGetProductList(Long institutionId, Long categoryId, String keyword, Byte status, Integer pageNum, Integer pageSize) {
        Page<Product> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getDeleted, 0);

        // 按机构筛选
        if (institutionId != null) {
            wrapper.eq(Product::getInstitutionId, institutionId);
        }

        // 分类筛选（包含子分类）
        if (categoryId != null) {
            List<Long> categoryIds = getAllChildCategoryIds(categoryId);
            categoryIds.add(categoryId);
            wrapper.in(Product::getCategoryId, categoryIds);
        }

        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Product::getName, keyword)
                    .or().like(Product::getDescription, keyword));
        }

        // 状态筛选
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }

        wrapper.orderByAsc(Product::getSort).orderByDesc(Product::getCreateTime);

        IPage<Product> productPage = page(page, wrapper);
        return convertToVOPage(productPage);
    }

    @Override
    @Transactional
    public boolean adminUpdateProduct(Long id, ProductDTO dto) {
        Product product = getById(id);
        if (product == null || product.getDeleted() == 1) {
            return false;
        }

        // 验证分类是否存在
        if (dto.getCategoryId() != null) {
            ProductCategory category = productCategoryService.getById(dto.getCategoryId());
            if (category == null || category.getDeleted() == 1) {
                throw new RuntimeException("商品分类不存在");
            }
            product.setCategoryId(dto.getCategoryId());
        }

        if (dto.getName() != null) {
            product.setName(dto.getName());
        }
        if (dto.getCoverImg() != null) {
            product.setCoverImg(dto.getCoverImg());
        }
        if (dto.getImages() != null) {
            product.setImages(convertListToJson(dto.getImages()));
        }
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }
        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }
        if (dto.getOriginalPrice() != null) {
            product.setOriginalPrice(dto.getOriginalPrice());
        }
        if (dto.getStock() != null) {
            product.setStock(dto.getStock());
        }
        if (dto.getUnit() != null) {
            product.setUnit(dto.getUnit());
        }
        if (dto.getSort() != null) {
            product.setSort(dto.getSort());
        }
        if (dto.getStatus() != null) {
            product.setStatus(dto.getStatus());
        }

        product.setUpdateTime(LocalDateTime.now());
        return updateById(product);
    }

    @Override
    @Transactional
    public boolean adminDeleteProduct(Long id) {
        Product product = getById(id);
        if (product == null || product.getDeleted() == 1) {
            return false;
        }

        product.setDeleted((byte) 1);
        product.setUpdateTime(LocalDateTime.now());
        return updateById(product);
    }

    @Override
    @Transactional
    public boolean adminUpdateProductStatus(Long id, Byte status) {
        Product product = getById(id);
        if (product == null || product.getDeleted() == 1) {
            return false;
        }

        product.setStatus(status);
        product.setUpdateTime(LocalDateTime.now());
        return updateById(product);
    }

    @Override
    @Transactional
    public boolean adminBatchDeleteProducts(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        for (Long id : ids) {
            Product product = getById(id);
            if (product != null && product.getDeleted() == 0) {
                product.setDeleted((byte) 1);
                product.setUpdateTime(LocalDateTime.now());
                updateById(product);
            }
        }
        return true;
    }

    @Override
    @Transactional
    public boolean adminBatchUpdateStatus(List<Long> ids, Byte status) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        for (Long id : ids) {
            Product product = getById(id);
            if (product != null && product.getDeleted() == 0) {
                product.setStatus(status);
                product.setUpdateTime(LocalDateTime.now());
                updateById(product);
            }
        }
        return true;
    }

    // ==================== 私有方法 ====================

    /**
     * 获取所有子分类ID
     */
    private List<Long> getAllChildCategoryIds(Long parentId) {
        List<Long> result = new ArrayList<>();
        List<ProductCategory> children = productCategoryService.list(
                new LambdaQueryWrapper<ProductCategory>()
                        .eq(ProductCategory::getParentId, parentId)
                        .eq(ProductCategory::getDeleted, 0)
        );

        for (ProductCategory child : children) {
            result.add(child.getId());
            result.addAll(getAllChildCategoryIds(child.getId()));
        }

        return result;
    }

    /**
     * 转换为VO
     */
    private ProductVO convertToVO(Product product) {
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setInstitutionId(product.getInstitutionId());
        vo.setCategoryId(product.getCategoryId());
        vo.setName(product.getName());
        vo.setCoverImg(product.getCoverImg());
        vo.setImages(convertJsonToList(product.getImages()));
        vo.setDescription(product.getDescription());
        vo.setPrice(product.getPrice());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setStock(product.getStock());
        vo.setSales(product.getSales());
        vo.setUnit(product.getUnit());
        vo.setSort(product.getSort());
        vo.setStatus(product.getStatus());
        vo.setStatusName(product.getStatus() == 1 ? "上架" : "下架");
        vo.setCreateTime(product.getCreateTime());
        vo.setUpdateTime(product.getUpdateTime());

        // 获取机构名称
        Institution institution = institutionService.getById(product.getInstitutionId());
        if (institution != null) {
            vo.setInstitutionName(institution.getName());
        }

        // 获取分类名称
        ProductCategory category = productCategoryService.getById(product.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }

        return vo;
    }

    /**
     * 转换分页结果为VO分页
     */
    private IPage<ProductVO> convertToVOPage(IPage<Product> productPage) {
        Page<ProductVO> voPage = new Page<>(productPage.getCurrent(), productPage.getSize(), productPage.getTotal());
        List<ProductVO> voList = productPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * List转JSON字符串
     */
    private String convertListToJson(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * JSON字符串转List
     */
    private List<String> convertJsonToList(String json) {
        if (!StringUtils.hasText(json)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }
}
