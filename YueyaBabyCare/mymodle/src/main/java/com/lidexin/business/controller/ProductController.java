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

import com.lidexin.business.vo.UserInfoVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-24
 */
@Api(tags = "商品管理")
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private IProductService productService;

    @ApiOperation("新增商品（机构）")
    @PostMapping
    public R<Long> createProduct(HttpServletRequest request, @Validated @RequestBody ProductDTO dto) {
        Long institutionId = getInstitutionIdFromRequest(request);
        if (institutionId == null) {
            return R.fail("请先登录机构账号");
        }
        try {
            Long id = productService.createProduct(institutionId, dto);
            return R.success(id);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("获取商品详情")
    @GetMapping("/{id}")
    public R<ProductVO> getProductById(
            @ApiParam(value = "商品ID", required = true) @PathVariable Long id) {
        ProductVO vo = productService.getProductById(id);
        if (vo == null) {
            return R.fail("商品不存在");
        }
        return R.success(vo);
    }

    @ApiOperation("修改商品（机构）")
    @PutMapping("/{id}")
    public R<Boolean> updateProduct(
            HttpServletRequest request,
            @ApiParam(value = "商品ID", required = true) @PathVariable Long id,
            @Validated @RequestBody ProductDTO dto) {
        Long institutionId = getInstitutionIdFromRequest(request);
        if (institutionId == null) {
            return R.fail("请先登录机构账号");
        }
        try {
            boolean success = productService.updateProduct(id, institutionId, dto);
            if (!success) {
                return R.fail("商品不存在");
            }
            return R.success(true);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("删除商品（机构）")
    @DeleteMapping("/{id}")
    public R<Boolean> deleteProduct(
            HttpServletRequest request,
            @ApiParam(value = "商品ID", required = true) @PathVariable Long id) {
        Long institutionId = getInstitutionIdFromRequest(request);
        if (institutionId == null) {
            return R.fail("请先登录机构账号");
        }
        try {
            boolean success = productService.deleteProduct(id, institutionId);
            if (!success) {
                return R.fail("商品不存在");
            }
            return R.success(true);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("获取商品列表（带分页、筛选）")
    @GetMapping("/list")
    public R<IPage<ProductVO>> getProductList(
            @ApiParam(value = "分类ID") @RequestParam(required = false) Long categoryId,
            @ApiParam(value = "关键词") @RequestParam(required = false) String keyword,
            @ApiParam(value = "状态（0-下架，1-上架）") @RequestParam(required = false) Byte status,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<ProductVO> page = productService.getProductList(categoryId, keyword, status, pageNum, pageSize);
        return R.success(page);
    }

    @ApiOperation("搜索商品")
    @GetMapping("/search")
    public R<IPage<ProductVO>> searchProducts(
            @ApiParam(value = "关键词") @RequestParam(required = false) String keyword,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<ProductVO> page = productService.searchProducts(keyword, pageNum, pageSize);
        return R.success(page);
    }

    @ApiOperation("获取机构的商品列表（机构）")
    @GetMapping("/institution/products")
    public R<IPage<ProductVO>> getInstitutionProducts(
            HttpServletRequest request,
            @ApiParam(value = "状态（0-下架，1-上架）") @RequestParam(required = false) Byte status,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
        Long institutionId = getInstitutionIdFromRequest(request);
        if (institutionId == null) {
            return R.fail("请先登录机构账号");
        }
        IPage<ProductVO> page = productService.getInstitutionProducts(institutionId, status, pageNum, pageSize);
        return R.success(page);
    }

    @ApiOperation("上架/下架商品（机构）")
    @PutMapping("/{id}/status")
    public R<Boolean> updateProductStatus(
            HttpServletRequest request,
            @ApiParam(value = "商品ID", required = true) @PathVariable Long id,
            @ApiParam(value = "状态（0-下架，1-上架）", required = true) @RequestParam Byte status) {
        Long institutionId = getInstitutionIdFromRequest(request);
        if (institutionId == null) {
            return R.fail("请先登录机构账号");
        }
        try {
            boolean success = productService.updateProductStatus(id, institutionId, status);
            if (!success) {
                return R.fail("商品不存在");
            }
            return R.success(true);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @ApiOperation("获取推荐商品")
    @GetMapping("/recommend")
    public R<List<ProductVO>> getRecommendProducts(
            @ApiParam(value = "数量限制", defaultValue = "10") @RequestParam(defaultValue = "10") Integer limit) {
        List<ProductVO> list = productService.getRecommendProducts(limit);
        return R.success(list);
    }

    @ApiOperation("上传商品图片（机构）")
    @PostMapping("/{id}/images")
    public R<Boolean> uploadProductImages(
            HttpServletRequest request,
            @ApiParam(value = "商品ID", required = true) @PathVariable Long id,
            @RequestBody List<String> imageUrls) {
        Long institutionId = getInstitutionIdFromRequest(request);
        if (institutionId == null) {
            return R.fail("请先登录机构账号");
        }
        try {
            boolean success = productService.uploadProductImages(id, institutionId, imageUrls);
            if (!success) {
                return R.fail("商品不存在");
            }
            return R.success(true);
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 从请求中获取机构ID
     * 对于机构用户（userType=3），直接从 userInfo 中获取 institutionId
     */
    @SuppressWarnings("unchecked")
    private Long getInstitutionIdFromRequest(HttpServletRequest request) {
        Object userInfo = request.getAttribute("userInfo");
        if (userInfo == null) {
            return null;
        }

        try {
            // 优先处理 UserInfoVO 类型（新登录的用户）
            if (userInfo instanceof UserInfoVO) {
                UserInfoVO vo = (UserInfoVO) userInfo;
                return vo.getInstitutionId();
            }

            // 兼容处理 Map 类型（从 Redis 反序列化的可能是 LinkedHashMap）
            if (userInfo instanceof Map) {
                Map<String, Object> userMap = (Map<String, Object>) userInfo;

                // 直接获取 institutionId（机构登录时已设置此字段）
                Object institutionId = userMap.get("institutionId");
                if (institutionId != null) {
                    return Long.valueOf(institutionId.toString());
                }

                // 兼容：如果没有 institutionId，检查 userType=3 时使用 userId
                Object userType = userMap.get("userType");
                if (userType != null && Integer.valueOf(userType.toString()) == 3) {
                    Object userId = userMap.get("userId");
                    if (userId != null) {
                        return Long.valueOf(userId.toString());
                    }
                }
            }
        } catch (Exception e) {
            // 解析失败，返回null
        }

        return null;
    }
}
