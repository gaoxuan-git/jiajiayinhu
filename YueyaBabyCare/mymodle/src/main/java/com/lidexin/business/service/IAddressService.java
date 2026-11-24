package com.lidexin.business.service;

import com.lidexin.business.dto.AddressDTO;
import com.lidexin.business.po.Address;
import com.lidexin.business.vo.AddressVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 地址表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
public interface IAddressService extends IService<Address> {

    /**
     * 获取当前用户地址列表
     *
     * @param relationType 关联人类型（1-家长，2-保姆，3-机构）
     * @param relationId   关联人ID
     * @return 地址列表
     */
    List<AddressVO> getAddressList(Byte relationType, Long relationId);

    /**
     * 新增地址
     *
     * @param relationType 关联人类型
     * @param relationId   关联人ID
     * @param dto          地址信息
     * @return 新增的地址ID
     */
    Long addAddress(Byte relationType, Long relationId, AddressDTO dto);

    /**
     * 修改地址
     *
     * @param relationType 关联人类型
     * @param relationId   关联人ID
     * @param id           地址ID
     * @param dto          地址信息
     * @return 是否成功
     */
    boolean updateAddress(Byte relationType, Long relationId, Long id, AddressDTO dto);

    /**
     * 删除地址
     *
     * @param relationType 关联人类型
     * @param relationId   关联人ID
     * @param id           地址ID
     * @return 是否成功
     */
    boolean deleteAddress(Byte relationType, Long relationId, Long id);

    /**
     * 设置默认地址
     *
     * @param relationType 关联人类型
     * @param relationId   关联人ID
     * @param id           地址ID
     * @return 是否成功
     */
    boolean setDefaultAddress(Byte relationType, Long relationId, Long id);

    /**
     * 获取地址详情
     *
     * @param relationType 关联人类型
     * @param relationId   关联人ID
     * @param id           地址ID
     * @return 地址详情
     */
    AddressVO getAddressById(Byte relationType, Long relationId, Long id);

    /**
     * 获取默认地址
     *
     * @param relationType 关联人类型
     * @param relationId   关联人ID
     * @return 默认地址
     */
    AddressVO getDefaultAddress(Byte relationType, Long relationId);
}
