package com.lidexin.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lidexin.business.dto.ProductDTO;
import com.lidexin.business.service.IProductService;
import com.lidexin.business.vo.ProductVO;
import com.lidexin.mymodle.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品管理 - 管理员专用控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-24
 */
@Api(tags = "商品管理（管理员）")
@RestController
@RequestMapping("/api/admin/product")
public class AdminProductController {

    @Autowired
    private IProductService productService;

    @ApiOperation("获取所有商品列表（管理员）- 支持按机构筛选")
    @GetMapping("/list")
    public R<IPage<ProductVO>> getProductList(
            @ApiParam(value = "机构ID") @RequestParam(required = false) Long institutionId,
            @ApiParam(value = "分类ID") @RequestParam(required = false) Long categoryId,
            @ApiParam(value = "关键词") @RequestParam(required = false) String keyword,
            @ApiParam(value = "状态（0-下架，1-上架）") @RequestParam(required = false) Byte status,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<ProductVO> page = productService.adminGetProductList(institutionId, categoryId, keyword, status, pageNum, pageSize);
        return R.success(page);
    }

    @ApiOperation("获取商品详情（管理员）")
    @GetMapping("/{id}")
    public R<ProductVO> getProductById(
            @ApiParam(value = "商品ID", required = true) @PathVariable Long id) {
        ProductVO vo = productService.getProductById(id);
        if (vo == null) {
            return R.fail("商品不存在");
        }
        return R.success(vo);
    }

    @ApiOperation("修改商品（管理员）")
    @PutMapping("/{id}")
    public R<Boolean> updateProduct(
            @ApiParam(value = "商品ID", required = true) @PathVariable Long id,
            @Validated @RequestBody ProductDTO dto) {
        try {
            boolean success = productService.adminUpdateProduct(id, dto);
            if (!success) {
                return R.fail("商品不存在");
            }
            return R.success(true);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("删除商品（管理员）")
    @DeleteMapping("/{id}")
    public R<Boolean> deleteProduct(
            @ApiParam(value = "商品ID", required = true) @PathVariable Long id) {
        boolean success = productService.adminDeleteProduct(id);
        if (!success) {
            return R.fail("商品不存在");
        }
        return R.success(true);
    }

    @ApiOperation("上架/下架商品（管理员）")
    @PutMapping("/{id}/status")
    public R<Boolean> updateProductStatus(
            @ApiParam(value = "商品ID", required = true) @PathVariable Long id,
            @ApiParam(value = "状态（0-下架，1-上架）", required = true) @RequestParam Byte status) {
        boolean success = productService.adminUpdateProductStatus(id, status);
        if (!success) {
            return R.fail("商品不存在");
        }
        return R.success(true);
    }

    @ApiOperation("批量删除商品（管理员）")
    @DeleteMapping("/batch")
    public R<Boolean> batchDeleteProducts(
            @ApiParam(value = "商品ID列表", required = true) @RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return R.fail("请选择要删除的商品");
        }
        boolean success = productService.adminBatchDeleteProducts(ids);
        return R.success(success);
    }

    @ApiOperation("批量上架/下架商品（管理员）")
    @PutMapping("/batch/status")
    public R<Boolean> batchUpdateStatus(
            @ApiParam(value = "商品ID列表", required = true) @RequestBody List<Long> ids,
            @ApiParam(value = "状态（0-下架，1-上架）", required = true) @RequestParam Byte status) {
        if (ids == null || ids.isEmpty()) {
            return R.fail("请选择要操作的商品");
        }
        boolean success = productService.adminBatchUpdateStatus(ids, status);
        return R.success(success);
    }
}
