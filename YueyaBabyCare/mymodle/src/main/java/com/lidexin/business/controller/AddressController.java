package com.lidexin.business.controller;

import com.lidexin.business.dto.AddressDTO;
import com.lidexin.business.service.IAddressService;
import com.lidexin.business.vo.AddressVO;
import com.lidexin.business.vo.UserInfoVO;
import com.lidexin.mymodle.common.R;
import com.lidexin.mymodle.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 地址表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Api(tags = "地址管理")
@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private IAddressService addressService;

    @Autowired
    private AuthService authService;

    /**
     * 获取当前用户地址列表
     */
    @ApiOperation("获取当前用户地址列表")
    @GetMapping
    public R<List<AddressVO>> getAddressList(HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        Byte relationType = getUserRelationType(userInfo.getUserType());
        Long relationId = getRelationId(userInfo);

        List<AddressVO> list = addressService.getAddressList(relationType, relationId);
        return R.success(list);
    }

    /**
     * 新增地址
     */
    @ApiOperation("新增地址")
    @PostMapping
    public R<Long> addAddress(@Validated @RequestBody AddressDTO dto, HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        Byte relationType = getUserRelationType(userInfo.getUserType());
        Long relationId = getRelationId(userInfo);

        Long addressId = addressService.addAddress(relationType, relationId, dto);
        return R.success("新增成功", addressId);
    }

    /**
     * 修改地址
     */
    @ApiOperation("修改地址")
    @PutMapping("/{id}")
    public R<Boolean> updateAddress(
            @ApiParam(value = "地址ID", required = true) @PathVariable Long id,
            @Validated @RequestBody AddressDTO dto,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        Byte relationType = getUserRelationType(userInfo.getUserType());
        Long relationId = getRelationId(userInfo);

        boolean result = addressService.updateAddress(relationType, relationId, id, dto);
        return result ? R.success("修改成功", true) : R.fail("修改失败，地址不存在或无权限");
    }

    /**
     * 删除地址
     */
    @ApiOperation("删除地址")
    @DeleteMapping("/{id}")
    public R<Boolean> deleteAddress(
            @ApiParam(value = "地址ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        Byte relationType = getUserRelationType(userInfo.getUserType());
        Long relationId = getRelationId(userInfo);

        boolean result = addressService.deleteAddress(relationType, relationId, id);
        return result ? R.success("删除成功", true) : R.fail("删除失败，地址不存在或无权限");
    }

    /**
     * 设置默认地址
     */
    @ApiOperation("设置默认地址")
    @PutMapping("/{id}/set-default")
    public R<Boolean> setDefaultAddress(
            @ApiParam(value = "地址ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        Byte relationType = getUserRelationType(userInfo.getUserType());
        Long relationId = getRelationId(userInfo);

        boolean result = addressService.setDefaultAddress(relationType, relationId, id);
        return result ? R.success("设置成功", true) : R.fail("设置失败，地址不存在或无权限");
    }

    /**
     * 获取地址详情
     */
    @ApiOperation("获取地址详情")
    @GetMapping("/{id}")
    public R<AddressVO> getAddressById(
            @ApiParam(value = "地址ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        Byte relationType = getUserRelationType(userInfo.getUserType());
        Long relationId = getRelationId(userInfo);

        AddressVO address = addressService.getAddressById(relationType, relationId, id);
        return address != null ? R.success(address) : R.fail("地址不存在或无权限");
    }

    /**
     * 获取默认地址
     */
    @ApiOperation("获取默认地址")
    @GetMapping("/default")
    public R<AddressVO> getDefaultAddress(HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        Byte relationType = getUserRelationType(userInfo.getUserType());
        Long relationId = getRelationId(userInfo);

        AddressVO address = addressService.getDefaultAddress(relationType, relationId);
        return R.success(address);
    }

    /**
     * 获取附近地址（地图用）- 预留接口，待地图API提供后实现
     */
    @ApiOperation("获取附近地址（地图用）")
    @GetMapping("/nearby")
    public R<List<AddressVO>> getNearbyAddresses(
            @ApiParam(value = "经度", required = true) @RequestParam Double longitude,
            @ApiParam(value = "纬度", required = true) @RequestParam Double latitude,
            @ApiParam(value = "搜索半径（米）", defaultValue = "5000") @RequestParam(defaultValue = "5000") Integer radius) {
        // TODO: 待地图API提供后实现
        return R.fail("地图功能暂未开放，请稍后再试");
    }

    /**
     * 从请求中获取当前登录用户
     */
    private UserInfoVO getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || token.isEmpty()) {
            return null;
        }
        return authService.getUserByToken(token, UserInfoVO.class);
    }

    /**
     * 根据用户类型获取关联人类型
     * 用户类型：1-家长，2-保姆，3-机构，4-管理员
     * 关联人类型：1-家长，2-保姆，3-机构
     */
    private Byte getUserRelationType(Integer userType) {
        if (userType == null) {
            return 1; // 默认家长
        }
        switch (userType) {
            case 1:
                return 1; // 家长
            case 2:
                return 2; // 保姆
            case 3:
                return 3; // 机构
            default:
                return 1; // 管理员等其他类型默认为家长
        }
    }

    /**
     * 获取关联人ID
     * 对于机构用户，使用机构ID；其他用户使用用户ID
     */
    private Long getRelationId(UserInfoVO userInfo) {
        if (userInfo.getUserType() != null && userInfo.getUserType() == 3) {
            // 机构用户使用机构ID
            return userInfo.getInstitutionId() != null ? userInfo.getInstitutionId() : userInfo.getUserId();
        }
        return userInfo.getUserId();
    }
}
