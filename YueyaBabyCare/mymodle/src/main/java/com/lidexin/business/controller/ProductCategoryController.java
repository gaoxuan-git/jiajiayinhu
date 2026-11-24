package com.lidexin.business.controller;

import com.lidexin.business.dto.ProductCategoryDTO;
import com.lidexin.business.service.IProductCategoryService;
import com.lidexin.business.vo.ProductCategoryVO;
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
 * 商品分类表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Api(tags = "商品分类管理")
@RestController
@RequestMapping("/api/product-category")
public class ProductCategoryController {

    @Autowired
    private IProductCategoryService productCategoryService;

    @ApiOperation("新增分类（管理员）")
    @PostMapping
    public R<Long> createCategory(@Validated @RequestBody ProductCategoryDTO dto) {
        try {
            Long id = productCategoryService.createCategory(dto);
            return R.success(id);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("获取分类树")
    @GetMapping("/tree")
    public R<List<ProductCategoryVO>> getCategoryTree() {
        List<ProductCategoryVO> tree = productCategoryService.getCategoryTree();
        return R.success(tree);
    }

    @ApiOperation("获取分类详情")
    @GetMapping("/{id}")
    public R<ProductCategoryVO> getCategoryById(
            @ApiParam(value = "分类ID", required = true) @PathVariable Long id) {
        ProductCategoryVO vo = productCategoryService.getCategoryById(id);
        if (vo == null) {
            return R.fail("分类不存在");
        }
        return R.success(vo);
    }

    @ApiOperation("修改分类（管理员）")
    @PutMapping("/{id}")
    public R<Boolean> updateCategory(
            @ApiParam(value = "分类ID", required = true) @PathVariable Long id,
            @Validated @RequestBody ProductCategoryDTO dto) {
        try {
            boolean success = productCategoryService.updateCategory(id, dto);
            if (!success) {
                return R.fail("分类不存在");
            }
            return R.success(true);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("删除分类（管理员）")
    @DeleteMapping("/{id}")
    public R<Boolean> deleteCategory(
            @ApiParam(value = "分类ID", required = true) @PathVariable Long id) {
        try {
            boolean success = productCategoryService.deleteCategory(id);
            if (!success) {
                return R.fail("分类不存在");
            }
            return R.success(true);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("获取分类列表")
    @GetMapping("/list")
    public R<List<ProductCategoryVO>> getCategoryList(
            @ApiParam(value = "状态（0-禁用，1-正常）") @RequestParam(required = false) Byte status) {
        List<ProductCategoryVO> list = productCategoryService.getCategoryList(status);
        return R.success(list);
    }

    @ApiOperation("修改分类状态")
    @PutMapping("/{id}/status")
    public R<Boolean> updateCategoryStatus(
            @ApiParam(value = "分类ID", required = true) @PathVariable Long id,
            @ApiParam(value = "状态（0-禁用，1-正常）", required = true) @RequestParam Byte status) {
        boolean success = productCategoryService.updateCategoryStatus(id, status);
        if (!success) {
            return R.fail("分类不存在");
        }
        return R.success(true);
    }
}
